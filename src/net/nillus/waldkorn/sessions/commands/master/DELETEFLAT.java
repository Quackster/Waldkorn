package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;

public class DELETEFLAT extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get flat to delete
		int flatID = Integer.parseInt(msg.nextArgument('/'));
		Space flat = m_session.getServer().getSpaceAdmin().getSpaceInfo(flatID);
		
		// Owner of the flat?
		if(flat != null && flat.ownerID == m_session.getUserObject().ID)
		{
			// Kill any instances running in flatservers
			m_session.getServer().getSpaceServer().destroyFlatInstance(flatID);
			
			// Delete flat and all content etc
			
		}
	}
}
