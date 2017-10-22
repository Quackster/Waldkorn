package net.nillus.waldkorn.sessions.commands.master;

import java.util.Date;

import net.nillus.waldkorn.access.UserAccessEntry;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.users.User;
import net.nillus.waldkorn.util.KeyValueStringReader;
import net.nillus.waldkorn.util.SecurityUtil;

public class REGISTER extends SessionCommandHandler
{
	private UserAccessEntry m_accessEntry;
	
	public void handle(ClientMessage msg)
	{
		// 'Decode' user object sent by client
		KeyValueStringReader obj = new KeyValueStringReader(msg.getBody(), "=");
		User usr = new User();
		
		// 'Fill' the User object that will be stored in the database
		usr.name = obj.read("name");
		usr.password = obj.read("password");
			
		// Personal
		usr.email = obj.read("email");
		usr.dateOfBirth = obj.read("birthday");
		usr.phoneNumber = obj.read("phoneNumber");
		usr.registered = new Date();
			
		// Avatar
		usr.motto = SecurityUtil.filterInput(obj.read("customData"));
		usr.figure = obj.read("figure");
		usr.sex = (obj.read("sex").equals("Male")) ? 'M' : 'F';
			
		// Try to register user (sets default data and verifies given data)
		if (m_session.getServer().getUserRegister().registerUser(usr, null))
		{
			m_response.set("REGOK");
			sendResponse();
		}
		else
		{
			m_session.systemError("Invalid registration data, please try again.");
		}
	}
}
