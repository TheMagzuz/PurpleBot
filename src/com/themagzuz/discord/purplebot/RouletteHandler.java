package com.themagzuz.discord.purplebot;

import java.util.HashMap;
import java.util.Map;

import com.themagzuz.discord.purplebot.datatypes.GamblingMessage;
import com.themagzuz.lib.biasedarrayselection.BiasedArraySelect;
import com.themagzuz.lib.biasedarrayselection.InvalidChancesArrayException;
import com.themagzuz.lib.biasedarrayselection.InvalidPercentageException;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class RouletteHandler implements Runnable
{

	// The delay between each roulette
	public volatile long delay;

	public Map<User, Integer> bets = new HashMap<User, Integer>();

	// public static Set<PUser> points = new HashSet<PUser>();
	public static Map<String, Integer> users = new HashMap<String, Integer>();

	public MessageChannel gamblingChannel;

	public RouletteHandler(long rouletteDelay, BotListener creator)
	{
		delay = rouletteDelay;
		creator.rh = this;
		SqlHandler.Setup();
	}

	@Override
	public synchronized void run()
	{
		while (true)
		{
			try
			{
				Thread.sleep(delay);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			// System.out.println("run");
			if (gamblingChannel != null)
				gamble();
		}
	}

	/**
	 * Starts the gamble, including animation and giving points.
	 */
	public void gamble()
	{
		// Check if anyone has bet, if not, cancel the gambling and send the
		// message.
		if (bets.values().isEmpty())
		{
			gamblingChannel.sendMessage("The roulette did not happen because no one bet").queue();
			return;
		}
		// Message out the chances
		gamblingChannel.sendMessage("Chances: ").queue();

		// Create a hashmap containing all the users and the chance that they'll
		// win.
		Map<User, Double> chances = new HashMap<User, Double>();

		// Count up how much has been bet in total.
		int total = 0;
		for (int i : bets.values())
		{
			total += i;
		}

		String chancesMsg = "";

		// Calcute the chance of each user winning, populate the map and send
		// out the message containing the chance
		// TODO: Probably turn all of these messages into one message to reduce
		// the messages sent
		for (User u : bets.keySet())
		{
			double chance = (double) ((double) bets.get(u) / total);
			chances.put(u, chance);
			chance *= 100;
			String chanceMsg = String.format("%s: %.2f %%\n", u.getAsMention(), chance);
			chancesMsg = chancesMsg + chanceMsg;
		}
		gamblingChannel.sendMessage(chancesMsg).queue();

		// The users that are playing
		User[] users = chances.keySet().toArray(new User[chances.size()]);

		// An array containing all the chances
		double[] chancesA = toPrimitiveArray(chances.values().toArray(new Double[chances.size()]));
		User winner = null;

		// Pick a winner using the BAS library
		try
		{
			winner = BiasedArraySelect.SelectRandomFromArrayChance(users, chancesA);
		} catch (InvalidChancesArrayException | InvalidPercentageException e)
		{
			e.printStackTrace();
		}
		gamblingChannel.sendMessage("Picking the winner...").complete();
		new GamblingMessage(this, 1).run();
		if (winner == null)
		{
			gamblingChannel.sendMessage("Something went wrong").queue();
			return;
		}
		// Announce the winner
		gamblingChannel.sendMessage(winner.getAsMention() + " has won " + total + " points!").queue();

		// Give the winner the correct amount of points
		int current = RouletteHandler.users.get(winner.getAsMention());
		RouletteHandler.users.put(winner.getAsMention(), current + total);

		// Clear the bets and announce it
		bets.clear();
		gamblingChannel.sendMessage("All bets have been cleared").queue();
	}

	double[] toPrimitiveArray(Double[] wrappedArray)
	{
		double[] array = new double[wrappedArray.length];
		for (int i = 0; i < wrappedArray.length; i++)
			array[i] = wrappedArray[i].doubleValue();
		return array;
	}

}
