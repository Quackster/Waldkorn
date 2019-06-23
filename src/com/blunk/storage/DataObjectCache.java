package com.blunk.storage;

import java.util.Hashtable;
import java.util.Vector;

/**
 * DataObjectCache temporarily stores DataObjects in the system memory.\rEach DataObject has a unique cachekey, and the time they stay in the DataObjectCache is depending on the access rate of the DataObjects.
 * @author Nillus
 *
 */
public class DataObjectCache
{
	//private Vector<DataObject>[] m_cache;
	private Hashtable<String, Vector<DataObject>> m_cache;
	public DataObjectCache()
	{
		m_cache = new Hashtable<String, Vector<DataObject>>();
	}
	
	/*
	public boolean cache(DataObject obj)
	{
		Vector<DataObject> cache = this.getCacheForObject(obj);
		if(cache != null)
		{
			cache.add(obj);
			return true;
		}
		
		Log.error("DataObjectCache: could not add to cache: DataObject #" + obj.getCacheKey() + ".");
		return false;
	}
	
	public boolean remove(DataObject obj)
	{
		Vector<DataObject> cache = this.getCacheForObject(obj);
		if(cache != null)
		{
			cache.remove(obj);
			return true;
		}
		
		Log.error("DataObjectCache: could not remove from cache: DataObject #" + obj.getCacheKey() + ".");
		return false;
	}
	
	public boolean lookup(DataObject obj)
	{
		if(obj != null)
		{
			Vector<DataObject> cache = this.getCacheForObject(obj);
			if(cache != null)
			{		
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public boolean registerDataObjectClass(String className)
	{
		try
		{
			Class<DataObject> c = (Class<DataObject>)Class.forName(className);
			if(c != null)
			{
				if(this.getCacheForObjectClass(c) != null)
				{
					return true;
				}
			}
		}
		catch (Exception ex)
		{
			Log.error("Could not register DataObjectClass \"" + className + "\". Verify if you specified the correct fully qualified class name etc.");
		}
		
		return false;
	}
	
	private Vector<DataObject> getCacheForObject(Object obj)
	{
		if(obj != null && obj instanceof DataObject)
		{
			// Get the DataObject vector for this class name (eg, SQLUser > User)
			String implName = obj.getClass().getSuperclass().getName();
			Vector<DataObject> cache = m_cache.get(implName);
			
			// Create a Vector if it doesn't exist yet
			if(cache == null)
			{
				cache = new Vector<DataObject>();
				m_cache.put(implName, cache);
			}
			
			// Return the vector
			return cache;
		}
		else
		{
			Log.error("")
			return null;
		}
	}*/
}
