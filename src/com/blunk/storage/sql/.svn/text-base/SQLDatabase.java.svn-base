package com.blunk.storage.sql;

import java.rmi.Naming;
import java.sql.Connection;
import java.util.Vector;

import com.blunk.Environment;
import com.blunk.Log;
import com.blunk.storage.DataObject;
import com.blunk.storage.DataQuery;
import com.blunk.storage.Database;

/**
 * SQLDatabase is a Database that can store SQLDataObjects in a SQL database over JDBC.
 * 
 * @author Nillus
 */
public class SQLDatabase implements Database
{
	/**
	 * The SQLDatabaseConnectionManager that manages connections for the endpoint database.
	 */
	private SQLDatabaseConnectionManager m_connMgr;
	
	/**
	 * TODO: document me!
	 */
	public SQLDatabase()
	{
		m_connMgr = new SQLDatabaseConnectionManager();
	}
	
	public boolean prepare()
	{
		String propertiesFile = Environment.getPropBox().get("db.props");
		return m_connMgr.prepare(propertiesFile);
	}
	
	@Override
	public boolean insert(DataObject obj)
	{
		if (obj != null)
		{
			Connection conn = m_connMgr.getConnection();
			if (conn != null)
			{
				try
				{
					return ((SQLDataObject)obj).insert(conn);
				}
				catch (Exception ex)
				{
					Log.error("Failed to insert new SQLDataObject into SQLDatabase!", ex);
				}
				finally
				{
					m_connMgr.releaseConnection(conn);
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean delete(DataObject obj)
	{
		if (obj != null)
		{
			Connection conn = m_connMgr.getConnection();
			if (conn != null)
			{
				try
				{
					return ((SQLDataObject)obj).delete(conn);
				}
				catch (Exception ex)
				{
					Log.error("Failed to delete existing SQLDataObject from SQLDatabase!", ex);
				}
				finally
				{
					m_connMgr.releaseConnection(conn);
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean update(DataObject obj)
	{
		if (obj != null)
		{
			Connection conn = m_connMgr.getConnection();
			if (conn != null)
			{
				try
				{
					return ((SQLDataObject)obj).update(conn);
				}
				catch (Exception ex)
				{
					Log.error("Failed to update existing SQLDataObject to SQLDatabase!", ex);
				}
				finally
				{
					m_connMgr.releaseConnection(conn);
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean load(DataObject obj)
	{
		if (obj != null)
		{
			Connection conn = m_connMgr.getConnection();
			if (conn != null)
			{
				try
				{
					return ((SQLDataObject)obj).load(conn);
				}
				catch (Exception ex)
				{
					Log.error("Failed to load existing SQLDataObject from Database using helper!", ex);
				}
				finally
				{
					m_connMgr.releaseConnection(conn);
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void execute(DataQuery dbQuery)
	{
		if (dbQuery != null)
		{
			Connection conn = m_connMgr.getConnection();
			if (conn != null)
			{
				try
				{
					((SQLDataQuery)dbQuery).execute(conn);
				}
				catch (Exception ex)
				{
					Log.error("Failed to execute SQLDataQuery against SQLDatabase!", ex);
				}
				finally
				{
					m_connMgr.releaseConnection(conn);
				}
			}
		}
	}
	
	@Override
	public Vector<?> query(DataQuery dbQuery)
	{
		if (dbQuery != null)
		{
			Vector<?> results = null;
			Connection conn = m_connMgr.getConnection();
			if (conn != null)
			{
				try
				{
					results = ((SQLDataQuery)dbQuery).query(conn);
				}
				catch (Exception ex)
				{
					Log.error("Failed to execute SQLDataQuery against SQLDatabase!", ex);
					results = new Vector<Object>(); // Empty results
				}
				finally
				{
					m_connMgr.releaseConnection(conn);
				}
			}
			
			return results;
		}
		
		return new Vector<Object>(0);
	}
	
	public SQLDatabaseConnectionManager getConnectionManager()
	{
		return m_connMgr;
	}
	
	/**
	 * Initializes the Blunk environment, starts up a remote SQLDatabase instance and binds it to a RMI url.
	 * 
	 * @param args arg0 = Path to blunk.properties (environment). arg1 = RMI url
	 */
	public static void main(String[] args)
	{
		try
		{
			// Initialize Environment with blunk.properties (arg0)
			Environment.init(args[0]);
			
			// Now create new instance!
			SQLDatabase instance = new SQLDatabase();
			
			// OK?
			if (instance.prepare())
			{
				// And now bind the new instance of SQLDatabase to the RMI uri
				// (arg1)
				Naming.bind(args[1], instance);
				
				// We are up and running!
				Log.info("SQLDatabase instance up and running and bound to " + args[2]);
			}
			else
			{
				Log.error("Failed to start new SQLDatabase instance!");
			}
		}
		catch (Exception ex)
		{
			Log.error("Failed to start new SQLDatabase instance!", ex);
			Log.info("Start this class with 'java com.blunk.storage.sql.SQLDatabase [blunk.properties] [RMI url]");
		}
	}
}
