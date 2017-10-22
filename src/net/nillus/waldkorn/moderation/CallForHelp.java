package net.nillus.waldkorn.moderation;

import java.util.Date;

import net.nillus.waldkorn.net.SerializableObject;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.spaces.Space;
import net.nillus.waldkorn.users.User;

/**
 * CallForHelp represents a 'cry for help' submitted by a logged in User. Moderating Users are supposed to handle this CallForHelp and inform the calling User how to deal with the reported issue.
 * 
 * @author Nillus
 */
public class CallForHelp implements SerializableObject
{
	/**
	 * The database ID of this CallForHelp.
	 */
	public final int ID;
	/**
	 * The User object of the calling user.
	 */
	private User m_sender;
	/**
	 * The Space object representing
	 */
	private Space m_space;
	/**
	 * The java.util.Date representing the moment this CallForHelp was sent.
	 */
	public final Date timeStamp;
	/**
	 * The message (eg, 'The Hotel is on fire') sent by the calling User.
	 */
	public String text;
	
	public CallForHelp(int callID)
	{
		this.ID = callID;
		this.timeStamp = new Date();
	}
	
	public void setSender(User usr)
	{
		m_sender = usr;
	}
	
	public User getSender()
	{
		return m_sender;
	}
	
	public void setSpace(Space space)
	{
		m_space = space;
	}
	
	public Space getSpace()
	{
		return m_space;
	}
	
	@Override
	public void serialize(ServerMessage msg)
	{
		if (this.getSender() != null)
		{
			msg.appendNewArgument("User: " + this.getSender().name + " @ " + this.timeStamp);
			msg.appendNewArgument(ModerationCenter.craftChatlogUrl(this.ID));
			if (this.getSpace() != null)
			{
				msg.appendKV2Argument("id", Integer.toString(this.getSpace().ID));
				msg.appendKV2Argument("name", "Space: \"" + this.getSpace().name + "\" (id: " + this.getSpace().ID + ", owner: " + this.getSpace().owner + ")");
				msg.appendKV2Argument("type", (this.getSpace().isUserFlat()) ? "private" : "public");
				msg.appendKV2Argument("port", "30000");
			}
			msg.appendKV2Argument("text", this.text);
		}
	}
}
