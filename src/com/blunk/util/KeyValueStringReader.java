package com.blunk.util;

import java.util.Hashtable;

public class KeyValueStringReader
{
	private Hashtable<String, String> values;
	
	public KeyValueStringReader(String data, String kvDelim)
	{
		this.values = new Hashtable<String, String>(5);
		
		String[] args = data.split("\r");
		for (int i = 0; i < args.length; i++)
		{
			String[] curArg = args[i].split(kvDelim, 2);
			if (curArg.length == 2)
			{
				this.values.put(curArg[0], curArg[1]);
			}
		}
	}
	
	public String read(String paramName)
	{
		return this.read(paramName, "");
	}
	public String read(String paramName, String def)
	{
		if(this.values.containsKey(paramName))
			return this.values.get(paramName);
		else
		{
			return def;
		}
	}
	
	public int readInt(String paramName)
	{
		try
		{
			return Integer.parseInt(this.read(paramName));
		}
		catch (NumberFormatException ex)
		{
			
		}
		
		return 0;
	}
}
