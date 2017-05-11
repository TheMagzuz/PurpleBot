package com.themagzuz.discord.purplebot.datatypes;

public class PUser
{

	public String mention;

	/*
	 * The amount of points that the user has
	 */
	public int points;

	public PUser(String ment)
	{
		mention = ment;
		points = 0;
	}

	public PUser(String ment, int p)
	{
		mention = ment;
		points = p;
	}

}
