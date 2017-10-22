package net.nillus.waldkorn.util;

import net.nillus.waldkorn.spaces.SpaceSession;

/**
 * ChatCommandParser is able to parse 'chat commands' (like for moderation etc) from user composed chat messages.
 * 
 * @author Nillus
 */
public class ChatCommandParser
{
	private final static char CHATCOMMAND_START = ':';
	
	public static boolean parseCommand(SpaceSession session, String text)
	{
		try
		{
			if (text.charAt(0) == ChatCommandParser.CHATCOMMAND_START)
			{
				// Strip off leading character
				text = text.substring(1);
				
				// Determine command
				int indexOfWhiteSpace = text.indexOf(' ', 1);
				String command = (indexOfWhiteSpace == -1) ? text : text.substring(0, indexOfWhiteSpace);
				String body = (indexOfWhiteSpace == -1) ? null : text.substring(indexOfWhiteSpace + 1);
				
				// General commands
				if (command.equals("commands"))
				{
					return ChatCommandHandler.showCommandList(session);
				}
				if (command.equals("about"))
				{
					return ChatCommandHandler.showAbout(session);
				}
				if (command.equals("servcast"))
				{
					return ChatCommandHandler.sendServCast(session, body);
				}
				if (command.equals("modcredits"))
				{
					return ChatCommandHandler.modCredits(session, body);
				}
				else if (command.equals("status"))
				{
					return ChatCommandHandler.showStatus(session);
				}
				else if(command.equals("moonwalk"))
				{
					return ChatCommandHandler.walkReverse(session);
				}
				else if (command.equals("who"))
				{
					return ChatCommandHandler.showUserList(session);
				}
				else if(command.equals("position"))
				{
					return ChatCommandHandler.handlePositionRequest(session);
				}
				else if (command.equals("alert"))
				{
					return ChatCommandHandler.alertUser(session, body);
				}
				else if (command.equals("kick"))
				{
					return ChatCommandHandler.kickUser(session, body);
				}
				else if (command.equals("roomalert"))
				{
					return ChatCommandHandler.alertSpace(session, body);
				}
				else if (command.equals("roomkick"))
				{
					return ChatCommandHandler.kickSpace(session, body);
				}
				else if (command.equals("ban"))
				{
					return ChatCommandHandler.banUser(session, body);
				}
				else if (command.equals("superban"))
				{
					return ChatCommandHandler.banUserIp(session, body);
				}
				else if (command.equals("unban"))
				{
					return ChatCommandHandler.unbanUser(session, body);
				}
				else if(command.equals("unbanip"))
				{
					return ChatCommandHandler.unbanIp(session, body);
				}
				else if(command.equals("server"))
				{
					return ChatCommandHandler.handleServerFunctions(session, body);
				}
			}
		}
		catch (Exception ex)
		{
			//session.getServer().getLogger().error("ChatCommandParser", "error while parsing/handling chat input \"" + text + "\"", ex);
			//session.systemMsg("ERROR!\rYour chat input \"" + text + "\" was parsed in the ChatCommandParser but caused an error serverside.\rThis often indicates bad parameters/syntax.\rThe error message was: " + ex.getMessage());
			return true;
		}
		
		// Non-recognized command!
		return false;
	}
}
