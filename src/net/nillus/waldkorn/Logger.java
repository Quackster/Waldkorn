package net.nillus.waldkorn;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger
{
	// Default log settings
	private String m_name;
	public boolean logToFile = true;
	public boolean logErrorsToFile = true;
	public boolean logDebug = true;
	
	private SimpleDateFormat m_formatter = new SimpleDateFormat();
	private BufferedWriter m_logFile;
	private BufferedWriter m_errorFile;

	public Logger(String name)
	{
		m_name = name;
		
		// Create the files
		String fileName = name + "_" + new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		try
		{
			setM_logFile(new BufferedWriter(new FileWriter(fileName + ".log")));
			setM_errorFile(new BufferedWriter(new FileWriter(fileName + "_errors.log")));
		}
		catch (Exception ex)
		{
			
		}
	}
	
	private String getNow()
	{
		return m_formatter.format(new Date());
	}
	
	public void info(Object system, Object obj)
	{
		// Print to out
		System.out.println(this.getNow() + " -- [" + system + "] -- " + obj.toString());
		
		// Print to file?
		if (this.logToFile)
		{
			
		}
	}
	
	public void error(Object system, Object obj, Exception ex)
	{
		// Print to out
		System.out.println(this.getNow() + " -- [" + system + "] -- ## ERROR: " + obj.toString());
		
		// Print to file?
		if(this.logErrorsToFile)
		{
			
		}
	}
	
	public void debug(Object system, Object obj)
	{
		if(this.logDebug)
		{
			// Print to out
			System.out.println(this.getNow() + " -- [" + system + "] -- " + obj.toString());
		}
	}
	
	public String getName()
	{
		return m_name;
	}

	public void setM_errorFile(BufferedWriter m_errorFile)
	{
		this.m_errorFile = m_errorFile;
	}

	public BufferedWriter getM_errorFile()
	{
		return m_errorFile;
	}

	public void setM_logFile(BufferedWriter m_logFile)
	{
		this.m_logFile = m_logFile;
	}

	public BufferedWriter getM_logFile()
	{
		return m_logFile;
	}
}

