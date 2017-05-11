package com.themagzuz.discord.purplebot.datatypes;

import com.themagzuz.discord.purplebot.UserHandler;

public class UserThread implements Runnable
{

	public volatile boolean givePoints = true;

	@Override
	public void run()
	{
		while (givePoints)
		{
			synchronized (UserHandler.monitor)
			{
				try
				{
					this.wait(TimeUnit.toMilliseconds(10, TimeUnit.MINUTES));
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				if (givePoints)
				{
					
				}
			}
		}
	}

}
