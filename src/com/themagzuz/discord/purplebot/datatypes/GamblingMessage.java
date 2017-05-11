package com.themagzuz.discord.purplebot.datatypes;

import java.util.Arrays;

import com.themagzuz.discord.purplebot.RouletteHandler;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class GamblingMessage implements Runnable
{

	public volatile boolean stop = false;

	RouletteHandler rh;

	int loops = 0;

	String[] messages = { ":hearts: :spades: :diamonds: :clubs:", ":clubs: :hearts: :spades: :diamonds:",
			":diamonds: :clubs: :hearts: :spades:", ":spades: :diamonds: :clubs: :hearts:" };

	Message[] msgs = new Message[messages.length];

	public GamblingMessage(RouletteHandler handler, int animationLoops)
	{
		MessageBuilder mb;
		rh = handler;
		for (int i = 0; i < messages.length; i++)
		{
			mb = new MessageBuilder();
			mb.append(messages[i]);
			msgs[i] = mb.build();
		}
		loops = animationLoops;
	}

	@Override
	public void run()
	{
		while (!stop)
		{
			MessageBuilder mb = new MessageBuilder();

			Message send = mb.append(messages[messages.length - 1]).build();
			// System.out.println(send.getRawContent() + " : " + id);
			Message sent = rh.gamblingChannel.sendMessage(send).complete();
			String id = sent.getId();
			for (; loops > 0; loops--)
			{
				for (String msg : messages)
				{
					if (msg == null || send == null)
					{
						System.out.println("PANIC!");
					}
					System.out.println(Arrays.asList(messages).indexOf(msg));
					rh.gamblingChannel.editMessageById(id, msg).complete();
					try
					{
						Thread.sleep(100);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
			rh.gamblingChannel.editMessageById(id, messages[1]).queue();
			stop = true;
		}

	}
}
