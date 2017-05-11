package com.themagzuz.discord.purplebot;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.themagzuz.discord.purplebot.datatypes.GamblingMessage;
import com.themagzuz.discord.purplebot.datatypes.PUser;
import com.themagzuz.discord.purplebot.datatypes.TimeUnit;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter
{

	public RouletteHandler rh;

	@Override
	public void onMessageReceived(MessageReceivedEvent e)
	{

		String msg = e.getMessage().getRawContent();
		User sender = e.getAuthor();
		Member member = e.getMember();

		//System.out.println(msg);
		if (sender.isBot())
			return;

		if (msg.equalsIgnoreCase("!points"))
		{
			if (RouletteHandler.users.get(sender.getAsMention()) == null)
			{
				RouletteHandler.users.put(sender.getAsMention(), 0);
			}
			e.getChannel().sendMessage(e.getAuthor().getAsMention() + " has "
					+ RouletteHandler.users.get(sender.getAsMention()) + " points").queue();
		} else if (msg.startsWith("!bet"))
		{

			if (msg.contains(" ") && NumberUtils.isCreatable((msg.split(" ")[1])))
			{
				int betsize = -1;
				try
				{
					betsize = Integer.parseInt(msg.split(" ")[1]);
				} catch (NumberFormatException ex)
				{
					e.getChannel().sendMessage(
							"Oi! " + sender.getAsMention() + "! That number is too large you rich piece of shit!")
							.queue();
					return;
				}
				if (betsize == -1)
				{
					e.getChannel().sendMessage("Something went wrong!").queue();
					return;
				}
				if (RouletteHandler.users.get(sender.getAsMention()) == null)
				{
					RouletteHandler.users.put(sender.getAsMention(), 0);
				}
				if (RouletteHandler.users.get(sender.getAsMention()) < betsize)
				{
					e.getChannel().sendMessage(sender.getAsMention() + "! You can't afford to bet that much!").queue();
					return;
				}
				int current = RouletteHandler.users.get(sender.getAsMention());
				RouletteHandler.users.put(sender.getAsMention(), current - betsize);
				if (rh.bets.containsKey(sender))
				{
					int oldBet = rh.bets.get(sender);
					rh.bets.put(sender, oldBet + betsize);
				} else
				{
					rh.bets.put(sender, betsize);
				}
				e.getChannel().sendMessage(
						sender.getAsMention() + " bet " + betsize + ". Your total bet is now " + rh.bets.get(sender))
						.queue();

			} else
			{
				e.getChannel().sendMessage(e.getAuthor().getAsMention() + ", that is not a number").queue();
			}

		} else if (msg.equalsIgnoreCase("!gamblehere"))
		{
			if (member.hasPermission(Permission.ADMINISTRATOR))
			{
				MessageChannel c = e.getChannel();
				c.sendMessage("Now gambling in channel " + c.getName()).queue();
				rh.gamblingChannel = c;
			} else
			{
				MessageChannel c = e.getChannel();
				c.sendMessage("You do not have permission to do that, " + sender.getAsMention() + "!").queue();
			}
		} else if (msg.equalsIgnoreCase("!gamblenow"))
		{
			MessageChannel c = e.getChannel();
			if (member.hasPermission(Permission.ADMINISTRATOR))
			{
				if (rh.gamblingChannel != null)
				{
					rh.gamble();
				} else
				{
					c.sendMessage("There is no gambling channel designated").queue();
				}
			} else
			{

				c.sendMessage("You do not have permission to do that, " + sender.getAsMention() + "!").queue();
			}
		} else if (msg.equalsIgnoreCase("!regme"))
		{
			PUser u = new PUser(sender.getAsMention());
			if (!SqlHandler.doesUserExist(u))
			{
				RouletteHandler.users.put(u.mention, u.points);
				SqlHandler.setUser(u);
			} else
			{
				e.getChannel().sendMessage(sender.getAsMention() + ", you're already registered with "
						+ RouletteHandler.users.get(sender.getAsMention()) + " points").queue();
			}
		} else if (msg.startsWith("!addpoints") && member.hasPermission(Permission.ADMINISTRATOR) && msg.contains(" "))
		{
			MessageChannel c = e.getChannel();
			if (NumberUtils.isCreatable(msg.split(" ")[1]))
			{
				int current = 0;
				int add = Integer.parseInt(msg.split(" ")[1]);
				if (RouletteHandler.users.containsKey(sender.getAsMention()))
				{
					current = RouletteHandler.users.get(sender.getAsMention());
				}

				RouletteHandler.users.put(sender.getAsMention(), current + add);
				c.sendMessage("Gave " + sender.getAsMention() + " " + add + " points");
			} else
			{
				c.sendMessage(sender.getAsMention() + ", that's not a number!");
			}
		} else if (msg.equalsIgnoreCase("!saveall") && member.hasPermission(Permission.ADMINISTRATOR))
		{
			SqlHandler.saveAll();
			e.getChannel().sendMessage("Saved all users").queue();
		} else if (msg.equalsIgnoreCase("!lbets") && member.hasPermission(Permission.ADMINISTRATOR))
		{
			for (User u : rh.bets.keySet())
			{
				e.getChannel().sendMessage(u.getAsMention() + ": " + rh.bets.get(u)).queue();
			}
			e.getChannel().sendMessage("Chances: ").queue();
			Map<User, Double> chances = new HashMap<User, Double>();
			int total = 0;
			for (int i : rh.bets.values())
			{
				total += i;
			}
			for (User u : rh.bets.keySet())
			{
				double chance = (double) ((double) rh.bets.get(u) / total);
				System.out.println(rh.bets.get(u) + " " + total);
				chances.put(u, chance);
				chance *= 100;
				String chanceMsg = String.format("%s: %s %%", u.getAsMention(), chance);
				System.out.println(chance);
				e.getChannel().sendMessage(chanceMsg).queue();
			}
		} else if (msg.startsWith("!interval ") && member.hasPermission(Permission.ADMINISTRATOR))
		{
			if (msg.split(" ").length != 3)
			{
				e.getChannel().sendMessage("Invalid usage of command").queue();
				;
				return;
			}
			String[] splits = msg.split(" ");
			int time = 0;
			if (NumberUtils.isCreatable(splits[1].replaceAll(" ", "")))
			{
				time = Integer.parseInt(splits[1].replaceAll(" ", ""));
			} else
			{
				e.getChannel().sendMessage("Invalid usage of command").queue();
				;
				return;
			}
			TimeUnit unit = TimeUnit.parse(splits[2].replaceAll(" ", ""));
			if (unit == TimeUnit.INVALID)
			{
				e.getChannel().sendMessage("Invalid time unit").queue();
				return;
			}
			rh.delay = TimeUnit.toMilliseconds(time, unit);
			e.getChannel().sendMessage("Set interval to " + time + " " + unit.toString().toLowerCase()).queue();
		} else if (msg.equalsIgnoreCase("!msg") && member.hasPermission(Permission.ADMINISTRATOR))
		{
			new Thread(new GamblingMessage(rh, 1)).start();
		}
	}

	@Override
	public void onReady(ReadyEvent event)
	{
		new Thread(new RouletteHandler(TimeUnit.toMilliseconds(10, TimeUnit.SECONDS), this)).start();
	}

}
