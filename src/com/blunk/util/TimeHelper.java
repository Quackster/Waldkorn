package com.blunk.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides functions for working with date and time.
 * 
 * @author Nillus
 */

public class TimeHelper
{
	private static SimpleDateFormat m_momentFormat = new SimpleDateFormat("dd-MM-yyyy hh:MM:ss");
	
	public static long getTime()
	{
		return System.currentTimeMillis();
	}
	
	public static Date getDateTime()
	{
		return new Date(TimeHelper.getTime());
	}
	
	public static String formatDateTime()
	{
		return TimeHelper.formatDateTime(TimeHelper.getDateTime());
	}
	
	public static String formatDateTime(Date d)
	{
		return m_momentFormat.format(d);
	}
	
	public static long calculateDaysElapsed(Date d)
	{
		long diff = TimeHelper.getTime() - d.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return diffDays;
	}
	
	public static SimpleDateFormat getMomentFormatter()
	{
		return m_momentFormat;
	}
}
