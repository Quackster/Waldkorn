package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;

public class GETFLATINFO extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get Space object
		int flatID = Integer.parseInt(msg.nextArgument('/'));
		Space flat = m_session.getServer().getSpaceAdmin().getSpaceInfo(flatID);
		
		// Valid Space?
		if (flat != null && flat.ownerID == m_session.getUserObject().ID)
		{
			 //SendData index, "SETFLATINFO" & Chr(13) & "/" & ModifyID & "/description=" & RoomDesc & Chr(13) & "password=" & RoomPass & Chr(13) & "allsuperuser=" & Allrights & Chr(13) & "wordfilter_disable=" & Chr(13)

			 m_response.set("SETFLATINFO");
			 m_response.appendNewArgument(Integer.toString(flat.ID));
			 m_response.appendKVArgument("name", flat.name);
			 m_response.appendKVArgument("description", flat.description);
			 m_response.appendKVArgument("password", flat.password);
			 m_response.appendKVArgument("allsuperuser", flat.superUsers ? "1" : "0");
			 m_response.appendKVArgument("worldfilter_disable", "");
			sendResponse();
		}
	}
}
