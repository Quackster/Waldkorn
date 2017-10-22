package net.nillus.waldkorn.messenger;

import java.util.Date;

import net.nillus.waldkorn.net.SerializableObject;
import net.nillus.waldkorn.net.ServerMessage;

/**
 * MessengerBuddy represents a user in another users buddy list.
 * @author Nillus
 *
 */
public class MessengerBuddy implements SerializableObject
{
	public int ID;
	public String name;
	public String messengerMotto;
	public Date lastActivity;
	public String location;
	
	@Override
	public void serialize(ServerMessage msg)
	{
		msg.appendNewArgument(Integer.toString(this.ID));
		msg.appendTabArgument(this.name);
		msg.appendTabArgument(this.messengerMotto);
		msg.appendNewArgument(this.location);
		msg.appendTabArgument(this.lastActivity.toString());
	}
}
