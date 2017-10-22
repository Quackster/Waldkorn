package net.nillus.waldkorn.sessions.commands.spaces;

import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.users.User;
import net.nillus.waldkorn.util.KeyValueStringReader;
import net.nillus.waldkorn.util.SecurityUtil;

public class UPDATE extends SessionCommandHandler
{
	public void handle(ClientMessage msg) throws Exception
	{
		// 'Decode' user object sent by client
		KeyValueStringReader obj = new KeyValueStringReader(msg.getBody(), "=");
		User usr = m_session.getUserObject();
		
		// Apply changes
		if(obj.read("ph_figure", null) == null)
		{
			if(obj.read("password") != null)
			{
				usr.password = obj.read("password");
			}
			usr.email = obj.read("email");
			usr.dateOfBirth = obj.read("birthday");
			usr.phoneNumber = obj.read("phoneNumber");
			usr.motto = SecurityUtil.filterInput(obj.read("customData"));
			usr.figure = obj.read("figure");
			usr.sex = (obj.read("sex").equals("Male")) ? 'M' : 'F';
		}
		else
		{
			usr.poolFigure = obj.read("ph_figure");
		}
		
		// Update user object
		usr.updateInfo(m_session.getServer().getDatabase());
	}
}
