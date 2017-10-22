package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.moderation.CallForHelp;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.util.KeyValueStringReader;
import net.nillus.waldkorn.util.SecurityUtil;

public class CRYFORHELP extends SessionCommandHandler
{
	public void handle(ClientMessage msg) throws Exception
	{
			// Get data from the 'cry for help', such as space name, message etc
			KeyValueStringReader callData = new KeyValueStringReader(msg.getBody(), ":");
			
			// Construct CallForHelp
			CallForHelp myCall = net.nillus.waldkorn.moderation.ModerationCenter.createCallForHelp();
			myCall.setSender(m_session.getUserObject());
			myCall.setSpace((m_session.getSpaceSession().getSpace() != null) ? m_session.getSpaceSession().getSpace().getInfo() : null);
			myCall.text = SecurityUtil.filterInput(callData.read("text"));
			
			// Submit call
		    net.nillus.waldkorn.moderation.ModerationCenter.submitCallForHelp(myCall);
	}
}
