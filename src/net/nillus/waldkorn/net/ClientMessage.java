package net.nillus.waldkorn.net;

import net.nillus.waldkorn.util.ArrayUtil;

/**
 * ClientMessage represents a network message sent from the Habbo client to the server.
 * ClientMessage provides methods for identifying the message and reading it's body.
 * 
 * @author Nillus
 */
public class ClientMessage
{
	private final String m_type;
	private final char[] m_body;
	private int m_cursor;
	
	/**
	 * Constructs a ClientMessage object that represents a received message.
	 * 
	 * @param type The type of the message that was sent, eg, VERSIONCHECK, GET_CREDITS, STATUSOK
	 * etc.
	 * @param body The array of characters representing the body of the message.
	 */
	public ClientMessage(String type, char[] body)
	{
		// Set type
		m_type = (type != null) ? type : "";
		
		// Set body
		m_body = (body != null) ? body : new char[0];
	}
	
	/**
	 * Resets the messagebody cursor, so the message can be read from the start again.
	 */
	public void reset()
	{
		m_cursor = 0;
	}
	
	public void advance(int n)
	{
		m_cursor += n;
	}
	
	/**
	 * Returns the type of this message. (eg, VERSIONCHECK, GET_CREDITS, STATUSOK etc.)
	 */
	public String getType()
	{
		return m_type;
	}
	
	/**
	 * Returns the total body of this message as a string.
	 */
	public String getBody()
	{
		return new String(m_body);
	}
	
	/**
	 * Returns the remaining body of this message as a string. An empty string is returned if there
	 * is no body (anymore).
	 */
	public String getRemainingBody()
	{
		int remaining = this.remainingBodyLength();
		String s;
		
		if (remaining > 0)
		{
			s = String.valueOf(m_body, m_cursor, remaining);
			m_cursor += remaining;
		}
		else
		{
			s = "";
		}
		
		return s;
	}
	
	/**
	 * Finds the index of the next occurrence of a given character in the character array of the
	 * remaining message body. -1 is returned if the char is not found in the remaining message
	 * body.
	 * 
	 * @param c The character to find the index of.
	 */
	private int remainingBodyIndexOf(char c)
	{
		for (int i = m_cursor; i < m_body.length; i++)
		{
			if (m_body[i] == c)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Gets the next argument in the message body as a string. (ending with a whitespace character)
	 * 
	 * @see nextArgument(char delimiter)
	 */
	public String nextArgument()
	{
		return nextArgument(' ');
	}
	
	/**
	 * Gets the next argument in the message body as a string.
	 * 
	 * @param delimiter The character to read 'to'. TODO: fix this crappy doc
	 */
	public String nextArgument(char delimiter)
	{
		int remaining = (m_body.length - m_cursor);
		if (remaining <= 0)
			return "";
		else
		{
			// "arg1 arg2 arg3"
			int delimiterIndex = this.remainingBodyIndexOf(delimiter);
			if (delimiterIndex == -1) // All remaining data!
			{
				return this.getRemainingBody();
			}
			else
			{
				if (delimiterIndex == 0)
				{
					m_cursor++;
					delimiterIndex = this.remainingBodyIndexOf(delimiter);
					if (delimiterIndex == -1)
					{
						delimiterIndex = remaining;
					}
				}
				
				String arg = new String(m_body, m_cursor, (delimiterIndex - m_cursor));
				
				m_cursor += arg.length() + 1;
				return arg;
			}
		}
	}
	
	/**
	 * Returns the total, original length of the message body.
	 */
	public int bodyLength()
	{
		return m_body.length;
	}
	
	/**
	 * Returns the length of the remaining message body.
	 */
	public int remainingBodyLength()
	{
		return m_body.length - m_cursor;
	}
	
	/**
	 * Returns the string representation of this ClientMessage.
	 */
	@Override
	public String toString()
	{
		if (bodyLength() > 0)
		{
			return getType() + " " + getBody();
		}
		else
		{
			return getType();
		}
	}
	
	/**
	 * Attempts to parse a ClientMessage object from a given array of unicode characters.
	 * 
	 * @param data The input array of characters.
	 * @return The ClientMessage object if parsing succeeded. Null is returned if parsing fails for
	 * whatever reason.
	 */
	public static ClientMessage parse(char[] data)
	{
			// Find first index of whitespace in data
			int indexOfWhiteSpace = ArrayUtil.indexOfChar(data, 0, data.length, ' ');
			if (indexOfWhiteSpace != -1) // This ClientMessage has body
			{
				String msgType = new String(data, 0, indexOfWhiteSpace);
				char[] msgBody = ArrayUtil.chompArray(data, indexOfWhiteSpace + 1, data.length);
				
				return new ClientMessage(msgType, msgBody);
			}
			else
			{
				// This ClientMessage has no body, stackData = msgtype
				return new ClientMessage(new String(data), null);
			}
	}
}