package com.blunk;

import java.util.Random;

import com.blunk.storage.Database;
import com.blunk.storage.DatabaseProxy;
import com.blunk.util.PropertiesBox;

/**
 * Environment is a central static class that holds environment properties/variables.
 * 
 * @author Nillus / Mike
 */
public class Environment
{
	private static Random m_random = new Random();
	private static PropertiesBox m_propBox = new PropertiesBox();
	private static DatabaseProxy m_databaseProxy;
	
	/**
	 * Initalizes the static Blunk environment by loading up properties from a .properties file at a
	 * given path. (blunk.properties)
	 * 
	 * @param propertiesFile The path to the blunk.properties file as a string.
	 */
	public static boolean init(String propertiesFile)
	{
		System.out.println();
		System.out.println("############################################");
		System.out.println("##  Blunk: multi user server environment  ##");
		System.out.println("##  Copyright (C) 2009                    ##");
		System.out.println("##  Nils [nillus] / Mike [office.boy]     ##");
		System.out.println("##  NILLUS.NET / SCRIPT-O-MATIC.NET       ##");
		System.out.println("############################################");
		System.out.println();
		
		System.out.println("Java executing directory=" + System.getProperty("user.dir"));
		System.out.println();
		
		Log.info("Initializing Blunk environment...");
		
		// Initialize properites
		if(initPropBox(propertiesFile))
		{
			System.out.println();
			
			// Initialize log
			initLog();
			
			if(initDatabaseImpl())
			{
				System.out.println();
				
				// Start timed garbage collector
				GarbageCollector gcer = new GarbageCollector();
				gcer.start(25);
				
				Log.info("Initialized Blunk environment.");
				
				// Ready
				return true;
			}
		}
		
		// Not OK...
		return false;
	}
	
	private static boolean initPropBox(String propertiesFile)
	{
		Log.info("Locating .properties file for Blunk environment...");
		m_propBox = new PropertiesBox();
		if(m_propBox.load(propertiesFile))
		{
			Log.info("Initialized properties for Blunk environment, " + m_propBox.size() + " properties loaded.");
			return true;
		}
		else
		{
			Log.error("Could not load Blunk environment .properties file " + propertiesFile);
			return false;
		}
	}
	
	private static void initLog()
	{
		// Initialize log with settings from config
		boolean logDebug = (m_propBox.get("system.log.debug").equals("1"));
		boolean logInfoToFile = (m_propBox.get("system.log.info.save").equals("1"));
		boolean logErrorsToFile = (m_propBox.get("system.log.errors.save").equals("1"));
		Log.init(logInfoToFile, logErrorsToFile, logDebug);
	}
	
	@SuppressWarnings("unchecked")
	private static boolean initDatabaseImpl()
	{
		// Setup database proxy
		m_databaseProxy = new DatabaseProxy();
		
		// Eg, 'SQL', 'FS', 'XML'
		String dbImplName = m_propBox.get("db.impl", "WARNING: NOTDEFINED").toUpperCase();
		String dbImplClassName = "com.blunk.storage." + dbImplName.toLowerCase() + "." + dbImplName + "Database";
		Log.info("Database implementation name: " + dbImplName);
		Log.info("Database classname: " + dbImplClassName);
		
		// Try to get Database implementation class
		Class databaseImplClass = null;
		try
		{
			databaseImplClass = Class.forName(dbImplClassName);
		}
		catch (ClassNotFoundException ex)
		{
			Log.error("The class " + dbImplClassName + " is not found or not a valid com.blunk.storage.Database implementation!");
			return false;
		}
		
		// Get RMI url
		String dbRmiUrl = m_propBox.get("db.rmiUrl", "WARNING: NOTDEFINED");
		Log.info("Database RMI url: " + dbRmiUrl);
		
		// Determine if to link to internal or remote Database
		if (dbRmiUrl.equals("internal")) // Set up internal database instance
		{
			Database dbInstance = null;
			try
			{
				dbInstance = (Database)databaseImplClass.newInstance();
				if(dbInstance.prepare())
				{
					Log.info("Successfully instantiated and prepared Database implementation " + dbImplName + "Database.");
				}
				else
				{
					Log.error("Database implementation " + dbImplName + "Database was instantiated, but preparations failed. Please check the configuration of " + dbImplName + "Database.");
					return false;
				}
			}
			catch (Exception ex)
			{
				Log.error("Could not setup internal Database instance!", ex);
				return false;
			}
			
			m_databaseProxy.setInternalReference(dbInstance);
		}
		else
		{
			m_databaseProxy.setRemoteReference(dbRmiUrl);
		}
		
		return true;
	}
	

	
	/**
	 * Returns the java.util.Random instance for generating random numbers.
	 */
	public static Random getRandom()
	{
		return m_random;
	}
	
	/**
	 * Returns the com.blunk.util.PropertiesBox instance that holds environment variables.
	 */
	public static PropertiesBox getPropBox()
	{
		return m_propBox;
	}
	
	/**
	 * Returns the DatabaseProxy, used for
	 * Environment.getDatabaseProxy().getDatabase().query(stuff);
	 * 
	 * @return The instance of the DatabaseProxy
	 */
	public static DatabaseProxy getDatabaseProxy()
	{
		return m_databaseProxy;
	}
	
	/**
	 * Returns the Database implementation instance.\r
	 * This is identical to calling getDatabaseProxy().getDatabase()
	 * @see getDatabaseProxy()
	 * @return The instance of the Database initialized in the Environment
	 */
	public static Database getDatabase()
	{
		return m_databaseProxy.getDatabase();
	}
}
