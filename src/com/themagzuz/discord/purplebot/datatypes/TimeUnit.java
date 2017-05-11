package com.themagzuz.discord.purplebot.datatypes;

public enum TimeUnit
{

	MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS, INVALID;

	public static long toMilliseconds(long time, TimeUnit unit)
	{
		switch (unit)
		{
		case DAYS:
			return time * 86400000;
		case HOURS:
			return time * 360000;
		case MINUTES:
			return time * 60000;
		case SECONDS:
			return time * 1000;
		case MILLISECONDS:
			return time;
		default:
			return time;
		}
	}

	public static long toSeconds(long time, TimeUnit unit)
	{
		switch (unit)
		{
		case DAYS:
			return time * 86400;
		case HOURS:
			return time * 3600;
		case MINUTES:
			return time * 60;
		case SECONDS:
			return time;
		case MILLISECONDS:
			return time / 1000;
		default:
			return time;
		}
	}

	public static long toMinutes(long time, TimeUnit unit)
	{
		switch (unit)
		{
		case DAYS:
			return time * 1440;
		case HOURS:
			return time * 60;
		case MINUTES:
			return time;
		case SECONDS:
			return time / 60;
		case MILLISECONDS:
			return time / 60000;
		default:
			return time;
		}
	}

	public static long toHours(long time, TimeUnit unit)
	{
		switch (unit)
		{
		case DAYS:
			return time * 24;
		case HOURS:
			return time;
		case MINUTES:
			return time / 60;
		case SECONDS:
			return time / 3600;
		case MILLISECONDS:
			return time / 360000;
		default:
			return time;
		}
	}

	public static long toDays(long time, TimeUnit unit)
	{
		switch (unit)
		{
		case DAYS:
			return time;
		case HOURS:
			return time / 60;
		case MINUTES:
			return time / 3600;
		case SECONDS:
			return time / 86400;
		case MILLISECONDS:
			return time / 8640000;
		default:
			return time;
		}
	}

	public static TimeUnit parse(String str)
	{
		switch (str.toLowerCase())
		{
		case "days":
		case "day":
		case "d":
			return TimeUnit.DAYS;
		case "hour":
		case "hours":
		case "h":
			return TimeUnit.HOURS;
		case "minutes":
		case "minute":
		case "mins":
		case "min":
		case "m":
			return TimeUnit.MINUTES;
		case "seconds":
		case "second":
		case "secs":
		case "sec":
		case "s":
			return TimeUnit.SECONDS;
		case "milliseconds":
		case "millisecond":
		case "ms":
			return TimeUnit.MILLISECONDS;
		default:
			return TimeUnit.INVALID;
		}
	}

}
