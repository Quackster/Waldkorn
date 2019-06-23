package com.blunk.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;

import com.blunk.storage.DataObject;
import com.blunk.storage.DatabaseException;

/**
 * SQLDataObject is a DataObject that is stored in a SQL database. SQLDataObject is inserteable,
 * deleteable and updateable.
 * 
 * @author Nillus
 * @see DataObject
 */
public interface SQLDataObject extends DataObject
{
	/**
	 * Inserts a new SQLDataObject in the SQLDatabase.
	 * 
	 * @param conn The open java.sql.Connection (JDBC) to use when executing.
	 * @return True if inserting failed, False otherwise.
	 * @throws DatabaseException When whatever error occurs.
	 */
	public boolean insert(Connection conn) throws SQLException;
	
	/**
	 * Deletes an existing SQLDataObject from the SQLDatabase.
	 * 
	 * @param conn The open java.sql.Connection (JDBC) to use when executing.
	 * @return True if deletion succeeded, False otherwise.
	 * @throws DatabaseException When whatever error occurs.
	 */
	public boolean delete(Connection conn) throws SQLException;
	
	/**
	 * Fully updates an existing SQLDataObject in the SQLDatabase.
	 * 
	 * @param conn The open java.sql.Connection (JDBC) to use when executing.
	 * @return True if updateing succeeded, False otherwise.
	 * @throws DatabaseException When whatever error occurs.
	 */
	public boolean update(Connection conn) throws SQLException;
	
	/**
	 * 'Completes' a partially constructed SQLDataObject by loading remaining information from the SQLDatabase.
	 * @param conn The open java.sql.Connection (JDBC) to use when executing.
	 * @return True if the object was completed with data from the SQLDatabase, False otherwise.
	 * @throws SQLException
	 */
	public boolean load(Connection conn) throws SQLException;
}