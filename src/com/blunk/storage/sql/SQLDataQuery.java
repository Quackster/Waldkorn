package com.blunk.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.blunk.storage.DataQuery;
import com.blunk.storage.DatabaseException;

/**
 * SQLDataQuery executes against a SQL database over a JDBC connection and optionally returns the
 * result.
 * 
 * @author Nillus
 */
public interface SQLDataQuery extends DataQuery
{
	/**
	 * Executes this SQLDataQuery against a java.sql.Connection and returns the results.
	 * 
	 * @param conn The open java.sql.Connection (JDBC) to use when executing.
	 * @return The results of the query as a DataQueryResults<?> object.
	 * @throws SQLException When SQL component related error occurs.
	 */
	public Vector<?> query(Connection conn) throws SQLException;
	
	/**
	 * Executes this SQLDataQuery against a java.sql.Connection.
	 * 
	 * @param conn The open java.sql.Connection (JDBC) to use when executing.
	 * @throws DatabaseException When whatever error occurs.
	 */
	public void execute(Connection conn) throws SQLException;
}