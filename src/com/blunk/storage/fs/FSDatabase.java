package com.blunk.storage.fs;

import java.util.Vector;

import com.blunk.storage.DataObject;
import com.blunk.storage.DataQuery;
import com.blunk.storage.Database;

/**
 * FSDatabase is a Database that can store FSDataObjects as .bfs files in the local filesystem.
 * 
 * @author Nillus
 * @see Database
 */
public class FSDatabase implements Database
{
	@Override
	public boolean prepare()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean delete(DataObject obj)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void execute(DataQuery queryBean)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean insert(DataObject obj)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean load(DataObject obj)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Vector<?> query(DataQuery queryBean)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean update(DataObject obj)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
