package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;
import net.nillus.waldkorn.util.SecurityUtil;

public class UPDATEFLAT extends SessionCommandHandler
{
	public void handle(ClientMessage msg) throws Exception
	{
		// Get flat data
		int flatID = Integer.parseInt(msg.nextArgument('/'));
		Space flat = m_session.getServer().getSpaceAdmin().getSpaceInfo(flatID);
		
		// Flat found and owner of this flat?
		if (flat != null && flat.ownerID == m_session.getUserObject().ID)
		{
			// Evaluate new data
			flat.name = SecurityUtil.filterInput(msg.nextArgument('/'));
			flat.accessType = msg.nextArgument('/');;
			
			if(flat.accessType.toString().isEmpty())
			{
				flat.accessType = "open";
			}
			
			flat.showOwner = (msg.nextArgument('/').equals("1"));
			
			// Update the data object
			flat.update(m_session.getServer().getDatabase());
		}
	}
}
