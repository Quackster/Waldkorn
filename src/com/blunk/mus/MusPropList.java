package com.blunk.mus;

import com.blunk.util.BitUtil;

public class MusPropList
{
	private String[] m_symbols;
	private short[] m_data_types;
	private byte[][] m_data;
	
	public MusPropList(int length)
	{
		m_symbols = new String[length];
		m_data_types = new short[length];
		m_data = new byte[length][0];
	}
	
	public boolean setPropAsBytes(String symbol, short type, byte[] data)
	{
		for(int i = 0; i < m_symbols.length; i++)
		{
			if(m_symbols[i] == null)
			{
				m_symbols[i] = symbol;
				m_data_types[i] = type;
				m_data[i] = data;
				
				return true;
			}
		}
		
		// No space
		return false;
	}
	
	public boolean setPropAsInt(String symbol, int i)
	{
		byte[] data = BitUtil.intToBytes(i);
		return this.setPropAsBytes(symbol, MusTypes.Integer, data);
	}
	
	public boolean setPropAsString(String symbol, String str)
	{
		byte[] data = str.getBytes();
		return this.setPropAsBytes(symbol, MusTypes.String, data);
	}
	
	public short getPropType(String symbol)
	{
		for(int i = 0; i < m_symbols.length; i++)
		{
			if(m_symbols[i] != null)
			{
				if(m_symbols[i].equals(symbol))
				{
					return m_data_types[i];
				}
			}
		}
		
		return MusTypes.Void;
	}
	
	public byte[] getPropAsBytes(String symbol)
	{
		for(int i = 0; i < m_symbols.length; i++)
		{
			if(m_symbols[i] != null)
			{
				if(m_symbols[i].equals(symbol))
				{
					return m_data[i];
				}
			}
		}
		
		return new byte[0];
	}
	
	public int getPropAsInt(String symbol)
	{
		byte[] bytes = this.getPropAsBytes(symbol);
		if(bytes.length == 0)
		{
			return -1;
		}
		else
		{
			return BitUtil.bytesToInt(bytes);
		}
	}
	
	public String getPropAsString(String symbol)
	{
		byte[] bytes = this.getPropAsBytes(symbol);
		return new String(bytes);
	}
	
	public String getSymbolAt(int slot)
	{
		return m_symbols[slot];
	}
	
	public short getDataTypeAt(int slot)
	{
		return m_data_types[slot];
	}
	
	public byte[] getDataAt(int slot)
	{
		return m_data[slot];
	}
	
	public int length()
	{
		return m_symbols.length;
	}
}
