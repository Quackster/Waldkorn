package com.blunk.storage;

import java.rmi.Remote;
import java.util.Vector;

/**
 * Database is a highlevel remote interface to a database that can store DataObjects.
 * 
 * @author Nillus
 * @see DataObject
 */
public interface Database extends Remote
{
	/**
	 * Prepares the Database. Handles initial connection, handshake, security etc.
	 * @return True if preparations succeeded, false otherwise.
	 */
	public boolean prepare();
	
	/**
	 * Inserts a new DataObject in the database.
	 * 
	 * @param obj The DataObject to insert.
	 */
	public boolean insert(DataObject obj);
	
	/**
	 * Deletes an existing DataObject from the database.
	 * 
	 * @param obj The DataObject to delete.
	 */
	public boolean delete(DataObject obj);
	
	/**
	 * Updates an existing DataObject in the database.
	 * 
	 * @param obj The DataObject to update.
	 */
	public boolean update(DataObject obj);
	
	/**
	 * Completes data of a partially constructed DataObject (eg, just ID) by loading the full data from the Database and completing the DataObject with some form of parsing.
	 * 
	 * @param obj The partially constructed DataObject to complete. This is usually just having it's 'ID field' set etc.
	 * @return True if the DataObject was completed with data from the Database, False if there was no result data or failed to parse this data.
	 */
	public boolean load(DataObject obj);
	
	/**
	 * Executes one or more queries in a DataQuery against the database and returns the results.
	 * 
	 * @param queryBean The DataQuery object that performs the actual query against the database.
	 * @return A Vector<?> with the results of the query.
	 */
	public Vector<?> query(DataQuery queryBean);
	
	/**
	 * Executes one or more queries in a DataQuery against the database.
	 * 
	 * @param queryBean The DataQuery object that performs the actual query against the database.
	 */
	public void execute(DataQuery queryBean);
}
