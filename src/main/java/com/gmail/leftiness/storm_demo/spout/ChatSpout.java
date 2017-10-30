package com.gmail.leftiness.storm_demo.spout;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import com.gmail.leftiness.storm_demo.bot.QueueingListener;

public class ChatSpout extends BaseRichSpout
{
  private SpoutOutputCollector        collector;
  private BlockingQueue<MessageEvent> queue;

  public ChatSpout () {}

  @Override
  public void open ( Map conf, TopologyContext context, SpoutOutputCollector collector )
  {
    this.collector = collector;
    this.queue     = new LinkedBlockingQueue<>();

    Configuration config = new Configuration.Builder()
      .setName(System.getenv("BOT_NAME"))
      .addServer(System.getenv("BOT_SERVER"))
      .addAutoJoinChannel(System.getenv("BOT_CHANNEL"))
      .addListener(new QueueingListener(queue))
      .buildConfiguration();

    PircBotX bot = new PircBotX(config);

    try {
      bot.startBot();
    } catch ( Exception e ) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close () {}

  @Override
  public void nextTuple ()
  {
    MessageEvent event;

    try {
      event = this.queue.take();
    } catch ( InterruptedException e ) {
      throw new RuntimeException(e);
    }

    Values values = new Values(
      event.getChannelSource(),
      event.getUser(),
      event.getMessage()
    );

    this.collector.emit(values);
  }

  @Override
  public void ack ( Object msgId ) {}

  @Override
  public void fail ( Object msgId ) {}

  @Override
  public void declareOutputFields ( OutputFieldsDeclarer declarer )
  {
    declarer.declare(new Fields("channel", "user", "message"));
  }

  @Override
  public Map<String, Object> getComponentConfiguration ()
  {
    return null;
  }
}
