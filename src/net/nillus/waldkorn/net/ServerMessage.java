package net.nillus.waldkorn.net;


public class ServerMessage
{
	private StringBuilder m_data;
	
	public ServerMessage()
	{
		m_data = new StringBuilder();
	}
	
	public ServerMessage(int initialCapacity)
	{
		m_data = new StringBuilder(initialCapacity);
	}
	
	public ServerMessage(String msgType)
	{
		this();
		this.set(msgType);
	}
	
	public void clear()
	{
		m_data.setLength(0);
	}
	
	public void set(String msgType)
	{
		this.clear();
		m_data.append('#');
		m_data.append(' ');
		m_data.append(msgType);
	}
	
	public void append(String s)
	{
		m_data.append(s);
	}
	
	public void appendArgument(String arg)
	{
		appendArgument(arg, ' ');
	}
	
	public void appendNewArgument(String arg)
	{
		appendArgument(arg, (char)13);
	}
	
	public void appendTextLine(String text)
	{
		appendArgument(text, (char)10);
	}
	
	public void appendPartArgument(String arg)
	{
		appendArgument(arg, '/');
	}
	
	public void appendTabArgument(String arg)
	{
		appendArgument(arg, (char)9);
	}
	
	public void appendKVArgument(String key, String value)
	{
		m_data.append((char)13);
		m_data.append(key);
		m_data.append('=');
		m_data.append(value);
	}
	public void appendKV2Argument(String key, String value)
	{
		m_data.append((char)13);
		m_data.append(key);
		m_data.append(':');
		m_data.append(value);
	}
	
	public void appendArgument(String arg, char delimiter)
	{
		m_data.append(delimiter);
		m_data.append(arg);
	}
	
	public void appendObject(SerializableObject obj)
	{
		if(obj != null)
		{
			obj.serialize(this);
		}
		else
		{
			this.appendArgument("null");
		}
	}
	
	public String getResult()
	{
		m_data.append("##");
		return m_data.toString();
	}
	
	public String toString()
	{
		return m_data.toString();
	}
}
