package com.blunk.util;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * PropertiesBox is a java.util.Properties utility that can load Properties from file and return
 * them.
 * 
 * @author Nillus
 */
public final class PropertiesBox
{
	private Properties m_props;
	
	public PropertiesBox()
	{
		m_props = new Properties();
	}
	
	public boolean load(String filePath)
	{
		try
		{
			m_props.load(new FileInputStream(filePath));
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Returns the value of a property of a given name.
	 * 
	 * @param propName The name of the property to get the value of.
	 * @return The value of the requested property. Null is returned if the property is not defined.
	 */
	public String get(String propName)
	{
		return this.get(propName, null);
	}
	
	/**
	 * Returns the value of a property of a given name.
	 * 
	 * @param propName The name of the property to get the value of.
	 * @param valDefault This value is returned if the property is not defined.
	 * @return The value of the requested property. valDefault is returned if the property is not
	 * defined.
	 */
	public String get(String propName, String valDefault)
	{
		return m_props.getProperty(propName, valDefault);
	}
	
	/**
	 * Returns the value of an property of a given name, after parsing it to an integer.
	 * 
	 * @param propName The name of the property to get the value of.
	 * @return The value of the requested property. Null is returned if the property is not defined.
	 */
	public int getInt(String propName)
	{
		return this.getInt(propName, 0);
	}
	
	/**
	 * Returns the value of a property of a given name, after parsing it to an integer.
	 * 
	 * @param propName The name of the property to get the value of.
	 * @param valDefault This value is returned if the property is not defined or is not parseable
	 * to integer.
	 * @return The value of the requested property. valDefault is returned if the property is not
	 * defined or is not parseable to integer.
	 */
	public int getInt(String propName, int valDefault)
	{
		try
		{
			return Integer.parseInt(this.get(propName));
		}
		catch (NumberFormatException ex)
		{
			return valDefault;
		}
	}
	
	public int size()
	{
		return m_props.size();
	}
}
