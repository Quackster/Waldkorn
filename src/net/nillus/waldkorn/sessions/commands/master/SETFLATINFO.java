package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;
import net.nillus.waldkorn.util.KeyValueStringReader;
import net.nillus.waldkorn.util.SecurityUtil;

public class SETFLATINFO extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get Space object
		int flatID = Integer.parseInt(msg.nextArgument('/'));
		Space flat = m_session.getServer().getSpaceAdmin().getSpaceInfo(flatID);
		
		// Can edit this flat?
		if (flat != null && flat.ownerID == m_session.getUserObject().ID)
		{
			// Evaluate new data
			KeyValueStringReader props = new KeyValueStringReader(msg.getRemainingBody(), "=");
			flat.description = SecurityUtil.filterInput(props.read("description"));
			flat.password = props.read("password");
			flat.superUsers = (props.read("allsuperuser").equals("1"));
			
			// Update data
			flat.update(m_session.getServer().getDatabase());
		}
	}
}
