package net.nillus.waldkorn.messenger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.nillus.waldkorn.net.SerializableObject;
import net.nillus.waldkorn.net.ServerMessage;


/**
 * MessengerMessage is is a textmessage sent on the ingame messenger ('Console'), from one user to another. Users can also receive campaign and staff messages.
 * Messages are stored in database even when they are marked as 'read', so moderation can include checking out those messages y0.
 * @author Nillus
 *
 */
public class MessengerMessage implements SerializableObject
{
	/**
	 * The unique ID of this messenger message. Each individual message has it's own ID, which is unique in the database.
	 */
	public int ID;
	/**
	 * The ID of the user that sent this message.
	 */
	public int senderID;
	/**
	 * The ID of the user that this message is sent to.
	 */
	public int receiverID;
	/**
	 * The java.util.Date representing the moment this message was sent.
	 */
	public Date timestamp;
	/**
	 * The actual text in this message. ('body')
	 */
	public String text;
	/**
	 * True if the receiver has read this message and marked it as 'read', False otherwise. (doh) 
	 */
	public boolean read;
	/**
	 * The String representing the figure of the sender for displaying the head of the user next to the message in client. This is retrieved from a separate location than the message location.
	 */
	public String senderFigure;
	
	@Override
	public void serialize(ServerMessage msg)
	{
		msg.appendNewArgument(Integer.toString(this.ID));
		msg.appendNewArgument(Integer.toString(this.senderID));
		msg.appendNewArgument("[2490046]");
		msg.appendNewArgument(this.timestamp.toString());
		msg.appendNewArgument(this.text);
		msg.appendNewArgument(this.senderFigure);
		
		/*
		 * 
		 *  SandboxHandler.TCP.SendToClient("# MESSENGER_MSG " + Convert.ToChar(13) + "139750967" + Convert.ToChar(13) + "17" + Convert.ToChar(13) + "[2490046]" + Convert.ToChar(13) + "03-03-2003 23:57:11" + Convert.ToChar(13) + "i hear u liek console messages nillus?!" + Convert.ToChar(13) + "sd=001/0&hr=002/224,224,224&hd=002/255,204,153&ey=666/0&fc=001/255,204,153&bd=001/255,204,153&lh=001/255,204,153&rh=001/255,204,153&ch=010/76,136,43&ls=001/76,136,43&rs=001/76,136,43&lg=006/255,255,255&sh=002/255,255,255##");
		 *  
		 *  MESSENGER_MSG[]Message_ID[]USER_ID[]Unsure...[]TIMESTAMP[]MESSAGE[]FIGURE
		 *  */
	}

	public static MessengerMessage parse(ResultSet row) throws SQLException
	{
		MessengerMessage msg = new MessengerMessage();
		msg.ID = row.getInt("id");
		msg.senderID = row.getInt("senderid");
		msg.senderFigure = row.getString("figure");
		msg.timestamp = new Date(row.getTimestamp("timestamp").getTime());
		msg.text = row.getString("text");
		return msg;
	}
}
