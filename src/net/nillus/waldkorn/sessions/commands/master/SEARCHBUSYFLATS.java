package net.nillus.waldkorn.sessions.commands.master;

import java.util.ArrayList;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;

public class SEARCHBUSYFLATS extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Determine the range to show
		int start, amount;
		if(msg.bodyLength() == 0)
		{
			start = 0;
			amount = 11;
		}
		else
		{
			String[] parts = msg.nextArgument('/').split(",");
			start = Integer.parseInt(parts[0]);
			amount = Integer.parseInt(parts[1]);
		}
		
		// Get the popular flats
		ArrayList<Space> popular = m_session.getServer().getSpaceServer().getPopularFlats();
		
		// Impose limits
		int available = popular.size();
		if(available == 0)
		{
			start = 0;
			amount = 0;
		}
		else
		{
			if(amount > available) amount = available;
		}
		
		// Send only the ones within the range
		m_response.set("BUSY_FLAT_RESULTS");
		for(int x = start; x < start + amount && x < available; x++)
		{
			m_response.appendObject(popular.get(x));
		}
		sendResponse();
	}
}
