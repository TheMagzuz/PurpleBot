package com.themagzuz.discord.purplebot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.Driver;
import com.themagzuz.discord.purplebot.datatypes.PUser;
import com.themagzuz.util.configload.Config;

public class SqlHandler
{

	static final String URL = "jdbc:mysql://localhost/discord";

	/**
	 * DO NOT CHANGE THIS VARIABLE. THE ONLY REASON THIS IS NOT FINAL IS SO THAT
	 * IT CAN BE SET TO A VALUE FROM CONFIG
	 */
	static String USER;
	/**
	 * DO NOT CHANGE THIS VARIABLE. THE ONLY REASON THIS IS NOT FINAL IS SO THAT
	 * IT CAN BE SET TO A VALUE FROM CONFIG
	 */
	static String PASSWORD;

	public static volatile Connection conn;

	public static void Setup()
	{

		PASSWORD = Config.GetString("password");
		USER = Config.GetString("username");
		Statement stmt = null;
		try
		{
			Driver driver = new Driver();

			System.out.println("[SQLHandler] Registering driver");
			DriverManager.registerDriver(driver);

			System.out.println("[SQLHandler] Connecting to server");
			conn = DriverManager.getConnection(URL, USER, PASSWORD);

			System.out.println("[SQLHandler] Fetching user data");
			stmt = conn.createStatement();
			String sql = "SELECT * FROM users";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				RouletteHandler.users.put(rs.getString("mention"), (Integer) rs.getInt("points"));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			stmt.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static boolean doesUserExist(PUser u)
	{
		try
		{
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM users WHERE mention = " + u.mention;
			ResultSet rs = stmt.executeQuery(sql);
			boolean rt = rs.first();
			rs.close();
			stmt.close();
			return rt;
		} catch (Exception e)
		{

		}
		return false;
	}

	public static void setUser(PUser u)
	{
		try
		{
			Statement stmt = conn.createStatement();
			String sql = String.format(
					"INSERT INTO users VALUES (\'%s\', %s) ON DUPLICATE KEY UPDATE mention=\'%s\', points=%s",
					u.mention, u.points, u.mention, u.points);
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void saveAll()
	{
		for (String s : RouletteHandler.users.keySet())
		{
			setUser(new PUser(s, RouletteHandler.users.get(s)));
		}
	}

}
