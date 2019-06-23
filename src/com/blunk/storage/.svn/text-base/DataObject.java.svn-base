package com.blunk.storage;

/**
 * DataObject is a persistent piece of data that can be inserted, deleted and updated against a
 * database. DataObjects need to specify their database instance-unique 'cache key' when they are
 * cachable.
 * 
 * @author Nillus
 */
public interface DataObject
{
	/**
	 * Returns the cache key of this DataObject. This is a value that is unique to a Database
	 * instance. A cache key -1 means that the object is not cachable.
	 */
	public long getCacheKey();
}