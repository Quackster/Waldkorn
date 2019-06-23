package com.blunk.mus;

/**
 * MusMessage is a message in the MUS protocol sent by either a MUS client or a MUS server.
 * 
 * @author Nillus
 */
public class MusMessage
{
	/**
	 * The size of this message in bytes.
	 */
	public int size;
	/**
	 * The error code that was returned by the handler of this message. 0 if no errors.
	 */
	public int errorCode;
	/**
	 * The UNIX time in milliseconds representing the timestamp this message was handled on.
	 */
	public int timeStamp;
	
	/**
	 * The subject String of this message.
	 */
	public String subject;
	/**
	 * The ID of the sender of this message, usually a user Identity of some sort.
	 */
	public String senderID;
	/**
	 * A String array holding receivers of this message.
	 */
	public String[] receivers;
	
	public short contentType;
	public int contentInt;
	public String contentString;
	public MusPropList contentPropList;
	
	public String toString()
	{
		if (this.contentString != null)
		{
			return this.subject + ":\"" + this.contentString + "\"";
			
		}
		else
		{
			return this.subject + ":\"\"";
		}
	}
}
