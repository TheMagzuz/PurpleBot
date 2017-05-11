package com.themagzuz.discord.purplebot;

import java.io.File;

import javax.security.auth.login.LoginException;

import com.themagzuz.util.configload.Config;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class Bot
{

	public static JDA jda;

	/**
	 * DO NOT CHANGE THIS VARIABLE. THE ONLY REASON THIS IS NOT FINAL IS SO THAT
	 * IT CAN BE SET TO A VALUE FROM CONFIG
	 */
	public static String TOKEN;

	public static void main(String[] args)
	{
		LoadConfig();
		new UserHandler();

		try
		{
			Runtime.getRuntime().addShutdownHook(new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						SqlHandler.saveAll();
						SqlHandler.conn.close();

					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
			jda = new JDABuilder(AccountType.BOT).addEventListener(new BotListener()).setToken(TOKEN).buildBlocking();
		} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException e)
		{
			e.printStackTrace();
		}

	}

	static void LoadConfig()
	{
		Config.SetConfigFile(System.getProperty("user.dir") + "\\..\\config.cfg");
		TOKEN = Config.GetString("Token");
	}
}
