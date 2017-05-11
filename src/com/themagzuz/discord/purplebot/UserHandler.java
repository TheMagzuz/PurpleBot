package com.themagzuz.discord.purplebot;

import java.util.HashMap;
import java.util.Map;

import com.themagzuz.discord.purplebot.datatypes.UserThread;

public class UserHandler
{

	public static UserHandler instance;
	
	public Map<String, UserThread> userThreads = new HashMap<String, UserThread>();

	public static Object monitor = new Object();
	
	public UserHandler()
	{
		instance = this;
	}

	public void AddUser(String user)
	{
		if (userThreads.containsKey(user))
			return;
		userThreads.put(user, new UserThread());
		new Thread(userThreads.get(user)).start();;
	}
	
}
