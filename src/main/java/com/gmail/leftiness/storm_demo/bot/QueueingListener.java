package com.gmail.leftiness.storm_demo.bot;

import java.util.Queue;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class QueueingListener extends ListenerAdapter
{
  private Queue<MessageEvent> queue;

  public QueueingListener ( Queue<MessageEvent> queue )
  {
    this.queue = queue;
  }

  @Override
  public void onMessage ( MessageEvent event )
  {
    this.queue.add(event);
  }
}
