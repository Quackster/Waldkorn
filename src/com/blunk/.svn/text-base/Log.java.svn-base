package com.blunk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import com.blunk.util.TimeHelper;

/**
 * Log is a static class for printing all kinds of information to the standard output stream.
 * 
 * @author Nillus & Mike
 */
public class Log
{
	private static SimpleDateFormat m_formatter = new SimpleDateFormat();
	private static FileWriter m_fileWriter;
	private static BufferedWriter m_fileOut;
	
	private static FileWriter m_errorWriter;
	private static BufferedWriter m_errorOut;
	
	// Default log settings
	private static boolean m_logInfoToFile;
	private static boolean m_logErrorsToFile;
	private static boolean m_logDebug;
	
	/**
	 * Init Logging - Constructs the class and sets up the Writer for output to file.
	 */
	public static void init(boolean logInfoToFile, boolean logErrorsToFile, boolean logDebug)
	{
		// Set error modes
		m_logInfoToFile = logInfoToFile;
		m_logErrorsToFile = logErrorsToFile;
		m_logDebug = logDebug;
		
		// Create formatter for datetime
		SimpleDateFormat pFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String tFileName = pFormatter.format(TimeHelper.getDateTime()) + ".log";
		
		try
		{
			m_fileWriter = new FileWriter(tFileName);
			m_fileOut = new BufferedWriter(m_fileWriter);
			
			m_errorWriter = new FileWriter(tFileName + "_errors.log");
			m_errorOut = new BufferedWriter(m_errorWriter);
			
			m_fileOut.write("Server logging started @ " + getNow() + "\r\n");
			m_fileOut.flush();
			
			m_errorOut.write("Error logging started @ " + getNow() + "\r\n\r\n");
		}
		catch (Exception ex)
		{
			logInfoToFile = false;
			logErrorsToFile = false;
			
			error("Could not set file output stream (logging) to '" + tFileName + "'!", ex);
		}
	}
	
	/**
	 * Returns the current date and time as a string.
	 */
	private static String getNow()
	{
		return m_formatter.format(TimeHelper.getDateTime());
	}
	
	/**
	 * Appends a String to the current file output stream.
	 * 
	 * @param str The String to append.
	 */
	private static void appendStringToFile(String str)
	{
		try
		{
			m_fileOut.write(str);
			m_fileOut.flush();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private static void appendErrorToFile(String str)
	{
		try
		{
			m_errorOut.write(str);
			m_errorOut.flush();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static String stackTraceToString(Exception ex)
	{
		StringWriter sw = new StringWriter();
		if (ex != null)
		{
			ex.printStackTrace(new PrintWriter(sw));
		}
		return sw.toString();
	}
	
	public static void printStackTrace(Exception ex)
	{
		StringWriter sw = new StringWriter();
		if(ex != null)
		{
			ex.printStackTrace(new PrintWriter(sw));
		}
		
		System.out.println(sw.toString());
	}
	
	/**
	 * Prints the current date and time and a given string to the standard output stream.
	 * 
	 * @param eventMsg The text to print.
	 * @throws IOException
	 */
	public static void info(String eventMsg)
	{
		// Print to out
		System.out.println(getNow() + " -- " + eventMsg);
		
		// Print to file?
		if (m_logInfoToFile)
		{
			appendStringToFile(getNow() + " -- " + eventMsg + "\r\n");
		}
	}
	
	/**
	 * Prints the current date and time and a given error message to the standard output stream.
	 * 
	 * @param eventMsg The 'human' description of the error to print.
	 */
	public static void error(String eventMsg)
	{
		error(eventMsg, null);
	}
	
	/**
	 * Prints the current date and time, a given error message and the string representation of a
	 * given exception to the standard output stream.
	 * 
	 * @param eventMsg The 'human' description of the error to print.
	 * @param ex The Exception object that contains all information about the exception.
	 */
	public static void error(String eventMsg, Exception ex)
	{
		// Print to out
		System.out.println(getNow() + " -- ## ERROR: " + eventMsg);
		if (ex != null)
		{
			Log.printStackTrace(ex);
		}
		
		// Print to file?
		if (m_logErrorsToFile)
		{
			//appendStringToFile(getNow());
			appendErrorToFile(getNow());
			
			if (ex != null)
			{
				appendErrorToFile(stackTraceToString(ex) + "\r\n\r\n");
			}
		}
	}
	
	public static void debug(Object obj)
	{
		// Log this or not?
		if (m_logDebug && obj != null)
		{
			System.out.println(getNow() + " -- ## DEBUG: " + obj.toString());
		}
	}
}
