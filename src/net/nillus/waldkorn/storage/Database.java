package net.nillus.waldkorn.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.nillus.waldkorn.Logger;
import net.nillus.waldkorn.util.PropertiesBox;

public class Database
{
	private Logger m_logger;
	private DatabaseEndpoint m_endpoint;
	private DatabaseConnectionManager m_connectionManager;
	
	public Database(String propsFile, Logger logger)
	{
		m_logger = logger;
		
		// Load properties
		PropertiesBox props = new PropertiesBox();
		if(props.load(propsFile))
		{
			m_endpoint = new DatabaseEndpoint(props.get("db.uid"), props.get("db.pwd"), props.get("db.url"));
			m_connectionManager = new DatabaseConnectionManager(m_endpoint, props.get("db.driver"), props.getInt("db.maxconnections", 0));
		}
	}
	
	public Logger getLogger()
	{
		return m_logger;
	}
	
	public DatabaseEndpoint getEndpoint()
	{
		return m_endpoint;
	}
	
	public DatabaseConnectionManager getConnectionManager()
	{
		return m_connectionManager;
	}
	
	public Connection getConnection()
	{
		return m_connectionManager.getConnection();
	}
	
	public void releaseConnection(Connection connection)
	{
		m_connectionManager.releaseConnection(connection);
	}
	
	public Statement createStatement() throws SQLException
	{
		return m_connectionManager.getConnection().createStatement();
	}
	
	public int executeUpdate(String sql) throws SQLException
	{
		Statement query = this.createStatement();
		try
		{
			return query.executeUpdate(sql);
		}
		finally
		{
			this.releaseStatement(query);
		}
	}
	
	public boolean executeExists(String sql) throws Exception
	{
		Statement query = this.createStatement();
		try
		{
			return query.executeQuery(sql).next();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			this.releaseStatement(query);
		}
		
		return false;
	}
	
	public ResultSet executeQuery(String sql) throws Exception
	{
		Connection connection = m_connectionManager.getConnection();
		if(connection != null)
		{
			Statement query = connection.createStatement();
			return query.executeQuery(sql);
		}
		
		return null;
	}
	
	public PreparedStatement prepareStatement(String sql) throws Exception
	{
		Connection connection = m_connectionManager.getConnection();
		return connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
	}
	
	public void releaseStatement(ResultSet result)
	{
		try
		{
			this.releaseStatement(result.getStatement());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void releaseStatement(Statement statement)
	{
		try
		{
			statement.close();
			m_connectionManager.releaseConnection(statement.getConnection());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
