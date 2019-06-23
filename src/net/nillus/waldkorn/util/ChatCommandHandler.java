package net.nillus.waldkorn.util;

import net.nillus.waldkorn.moderation.ModerationBan;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.sessions.MasterSession;
import net.nillus.waldkorn.spaces.SpaceSession;
import net.nillus.waldkorn.spaces.SpaceUser;
import net.nillus.waldkorn.users.User;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatCommandHandler
{
	private final static String CHATCOMMAND_ABOUT_TXT = "�&�	Project Blunk" + "\r" + "�&�	Habbo Hotel V5 server emulator" + "\r" + "�&�	Platform: Java" + "\r" + "�&�	Authors: Nils [nillus] and Mike [office.boy]" + "\r" + "�&�	Special thanks go to:"
			+ "\r" + "�&�		- Matthew Parlane" + "\r" + "�&�		- Joe 'Joeh' Hegarty" + "\r" + "�&�		- Aapo 'kyrpov' Kyrola";
	private final static int CHATCOMMAND_WHO_USERSPERPAGE = 20;
	
	public static boolean showAbout(SpaceSession session)
	{
		session.getMasterSession().systemMsg(CHATCOMMAND_ABOUT_TXT);
		return true;
	}
	
	public static boolean showStatus(SpaceSession session)
	{
		// Retrieve the statistics
		int maxConns = 1000;
		int liveConns = session.getServer().getSessionMgr().count();
		long uptime = System.currentTimeMillis() - session.getServer().getStartTime();
		long days = (uptime / (1000 * 60 * 60 * 24));
		long hours = (uptime - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (uptime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (uptime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);
		Runtime runtime = Runtime.getRuntime();
		
		// Build the window message
		ServerMessage wnd = new ServerMessage("SYSTEMBROADCAST");
		wnd.appendNewArgument("SERVER");
		wnd.appendTextLine("MasterServer uptime is " + days + " day(s), " + hours + " hour(s), " + minutes + " minute(s) and " + seconds + " second(s)");
		wnd.appendTextLine("Currently there are " + liveConns + "/" + maxConns + " connections in use.");
		wnd.appendTextLine("Your master session ID is " + session.getMasterSession().sessionID + ".");
		wnd.appendTextLine("");
		wnd.appendTextLine("SYSTEM");
		wnd.appendTextLine("CPU cores: " + runtime.availableProcessors());
		wnd.appendTextLine("JVM memory usage: " + (runtime.totalMemory() / 1024) + " KB");
		session.send(wnd);
		
		return true;
	}
	
	public static boolean showCommandList(SpaceSession session)
	{
		ServerMessage list = new ServerMessage("SYSTEMBROADCAST");
		list.appendNewArgument("You can use the following chat commands:");
		
		session.getServer().getItemAdmin().loadItemDefinitions();
		
		if (session.getUserObject().hasRight("can_see_server_info"))
		{
			list.appendTextLine(":about\tDisplays server information");
		}
		if (session.getUserObject().hasRight("can_see_server_status"))
		{
			list.appendTextLine(":status\tDisplays server status");
		}
		if (session.getUserObject().hasRight("can_access_stocks"))
		{
			list.appendTextLine(":stocks help\tDisplays information on the Stocks Market");
		}
		if (session.getUserObject().hasRight("can_alert_user"))
		{
			list.appendTextLine(":alert %user% %msg%\tAlerts %user% with message %msg%");
		}
		if (session.getUserObject().hasRight("can_kick_user"))
		{
			list.appendTextLine(":kick %user% [%msg%]\tKicks %user% from space displaying optional message %msg%");
		}
		if (session.getUserObject().hasRight("can_alert_space"))
		{
			list.appendTextLine(":roomalert %msg%\tAlerts all users in your current space with %msg%");
		}
		if (session.getUserObject().hasRight("can_kick_space"))
		{
			list.appendTextLine(":roomkick %msg%\tKicks all users below your role in your current space, alerting them with %msg%");
		}
		if (session.getUserObject().hasRight("can_ban_user"))
		{
			list.appendTextLine(":ban %user% %hours% %reason%\tApplies a moderation ban to %user% for %hours% with %reason%");
		}
		if (session.getUserObject().hasRight("can_ban_ip"))
		{
			list.appendTextLine(":superban %user% %hours% %reason%\tApplies a moderation ban to %user% and %user%'s IP address for %hours% with %reason%");
			list.appendTextLine(":banip %ip% %hours% %reason%\tApplies a moderation ban to %ip% for %hours% with %reason%");
		}
		if (session.getUserObject().hasRight("can_unban"))
		{
			list.appendTextLine(":unban %user%\tRemoves all moderation bans (+ IP bans) for %user%");
			list.appendTextLine(":unbanip %ip%\tRemoves all moderation bans for IP address %ip%");
		}
		if (session.getUserObject().hasRight("can_be_invisible"))
		{
			list.appendTextLine(":invisible\tMakes your avatar invisible in the space");
		}
		if (session.getUserObject().hasRight("can_moonwalk"))
		{
			list.appendTextLine(":moonwalk\tEnables/disables your avatar's moonwalk status (reverse walking)");
		}
		if (session.getUserObject().hasRight("can_kick_to_door"))
		{
			list.appendTextLine(":ktd x\tKicks x to door. Requires rights!");
		}
		if (session.getUserObject().hasRight("can_kill_user"))
		{
			list.appendTextLine(":kill %user%\tDisconnects %user% from the server");
		}
		if (session.getUserObject().hasRight("can_send_servcast"))
		{
			list.appendTextLine(":servcast %msg%\tSends %msg% to all online users on the server");
		}
		if (session.getUserObject().hasRight("can_see_userlist"))
		{
			list.appendTextLine(":who\tDisplays a list with online users and their IP address");
		}
		if (session.getUserObject().hasRight("can_modify_credits"))
		{
			list.appendTextLine(":modcredits %user% %x%\tModifies the credit amount of %user% with %x%, %x% can be a positive or negative integer");
		}
		if (session.getUserObject().hasRight("can_control_server"))
		{
			list.appendTextLine(":server %function\tProvides various server functions, such as reloading configuration etc");
		}
		
		session.send(list);
		return true;
	}
	
	public static boolean sendServCast(SpaceSession session, String body)
	{
		if (session.getUserObject().hasRight("can_send_servcast"))
		{
			if (body != null)
			{
				ServerMessage msg = new ServerMessage("SYSTEMBROADCAST");
				msg.appendArgument("Message from Hotel staff:");
				msg.appendNewArgument(body);
				
				for(int i = 0; i < session.getServer().getSessionMgr().userCount(); i++)
				{
					MasterSession receiver = session.getServer().getSessionMgr().getMasterSessionOfUser(i);
					
					if(receiver != null)
					{
						receiver.send(msg);
					}
				}
				
				session.systemMsg("Servcast sent.");
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean showUserList(SpaceSession session)
	{
		if (session.getUserObject().hasRight("can_see_userlist"))
		{
			// Get all logged in sessions
			Collection<MasterSession> sessions = session.getServer().getSessionMgr().getLoggedInUsers();

			// Prepare message
			ServerMessage wnd = new ServerMessage("SYSTEMBROADCAST");
			wnd.appendNewArgument("There are " + sessions.size() + " user(s) logged in.\r");
			
			int counter = 0;
			for (MasterSession tsession : sessions)
			{
				// Past page?
				if ((++counter % ChatCommandHandler.CHATCOMMAND_WHO_USERSPERPAGE) == 0)
				{
					// New page
					session.send(wnd);
					wnd.set("SYSTEMBROADCAST");
				}
				
				// "1.	Administrator	127.0.0.1"
				// "2.	Administrator2	127.0.0.1"
				wnd.appendTextLine(counter + ".");
				wnd.appendTabArgument(tsession.getUserObject().name);
				wnd.appendTabArgument(tsession.getConnection().getIpAddress());
			}
			
			// Send response
			session.send(wnd);
			
			return true;
		}
		
		return false;
	}
	
	public static boolean modCredits(SpaceSession session, String body) throws Exception
	{
		if (session.getUserObject().hasRight("can_modify_credits"))
		{
			String[] args = body.split(" ", 2);
			if (args.length == 2)
			{
				String name = args[0];
				short amount = Short.parseShort(args[1]);
				
				User usr = session.getServer().getUserRegister().getUserInfo(name);
				if (usr == null)
				{
					session.systemMsg("User \"" + name + "\" was not found.");
				}
				else
				{
					usr.credits += amount;
					usr.updateValuables(session.getServer().getDatabase());
					
					// Update online?
					MasterSession receiver = session.getServer().getSessionMgr().getMasterSessionOfUser(usr.ID);
					if (receiver != null)
					{
						ServerMessage msg = new ServerMessage("WALLETBALANCE");
						msg.appendNewArgument(Integer.toString(receiver.getUserObject().credits + amount));
						receiver.send(msg);
					}
					session.systemMsg("Gave user " + usr.name + " " + amount + " credits, user now has " + usr.credits + ".");
				}
				
				return true;
			}
		}	
		return false;
	}
	
	public static boolean alertUser(SpaceSession session, String body)
	{
		if (session.getUserObject().hasRight("can_alert_user"))
		{
			String[] args = body.split(" ", 2);
			if (args.length == 2)
			{
				MasterSession target = session.getServer().getSessionMgr().getMasterSessionOfUser(args[0]);
				if (target == null)
				{
					session.systemMsg("Could not alert " + args[0] + ", user was not online.");
				}
				else
				{
					target.systemMsg(args[1]);
					session.systemMsg("Alerted " + target.getUserObject().name + ".");
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean kickUser(SpaceSession session, String body)
	{
		if (session.getUserObject().hasRight("can_kick_user"))
		{
			String[] args = body.split(" ", 2);
			if (args.length >= 1)
			{
				MasterSession target = session.getServer().getSessionMgr().getMasterSessionOfUser(args[0]);
				if (target == null || target.getUserObject().role > session.getUserObject().role)
				{
					session.systemMsg("Could not kick " + args[0] + ", user was not online, or you have bad permissions.");
				}
				else
				{
					// Reason given
					if(target.getSpaceSession() == null)
					{
						session.systemMsg(target.getUserObject().name + " is currently not in a space.");
					}
					else
					{
						target.getSpaceSession().exit(args.length == 2 ? args[1] : null);
						session.systemMsg("Kicked " + target.getUserObject().name + " from space.");
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean alertSpace(SpaceSession session, String body)
	{
		if (session.getUserObject().hasRight("can_alert_space"))
		{
			if (body == null)
			{
				session.systemMsg("Could not alert space, please supply a message.");
			}
			else
			{
				for(SpaceUser user : session.getSpace().getUsers())
				{
					user.getSession().getMasterSession().systemMsg(body);
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public static boolean kickSpace(SpaceSession session, String body)
	{
		if (session.getUserObject().hasRight("can_kick_space"))
		{
			if (body == null)
			{
				session.systemMsg("Could not kick space, please supply a message.");
			}
			else
			{
				session.getSpace().moderationKick(session.getUserObject().role, body);
				session.systemMsg("All users below your role have been kicked from the space.");
			}
			
			return true;
		}
		
		return false;
	}
	
	public static boolean handlePositionRequest(SpaceSession session)
	{
		SpaceUser usr = session.getUser();
		if (usr != null)
		{
			session.systemMsg("Your current position in the room is:\rX=" + usr.X + "\rY=" + usr.Y);
		}
		return true;
	}
	
	public static boolean banUser(SpaceSession session, String body)
	{
		if (session.getUserObject().hasRight("can_ban_user"))
		{
			ChatCommandHandler.internalBanUser(session, body, false);
			return true;
		}
		
		return false;
	}
	
	public static boolean banUserIp(SpaceSession session, String body)
	{
		if (session.getUserObject().hasRight("can_ban_user") && session.getUserObject().hasRight("can_ban_ip"))
		{
			ChatCommandHandler.internalBanUser(session, body, true);
			return true;
		}
		
		return false;
	}
	
	private static void internalBanUser(SpaceSession session, String body, boolean banIP)
	{
		String[] args = body.split(" ", 3);
		if (args.length == 3)
		{
			// Parse the arguments
			String username = args[0];
			int hours = Integer.parseInt(args[1]);
			String reason = args[2];
			
			// Get user
			User usr = session.getServer().getUserRegister().getUserInfo(username);
			if (usr == null)
			{
				session.systemMsg("Ban failed. User '" + username + "' does not exist.");
			}
			else if (usr.role > session.getUserObject().role)
			{
				session.systemMsg("Ban failed. You do not have enough permissions to ban " + usr.name);
			}
			else
			{
				// Apply the ban
				ModerationBan ban = null;//session.getServer().getm(usr.ID, banIP, hours, reason, session.getUserObject().ID);
				if (ban == null)
				{
					session.systemMsg("Ban failed. Please verify if you have given valid details and you have the required privileges.");
				}
				else
				{
					session.systemMsg(ban.generateReport());
				}
			}
		}
		else
		{
			session.systemMsg("Ban failed. Please supply username, hours and ban reason.");
		}
	}
	
	public static boolean unbanUser(SpaceSession session, String body)
	{
		/*
		if (session.getUserObject().hasRight("can_unban"))
		{
			if (body != "")
			{
				// Get user
				User usr = HabboHotel.getUserRegister().getUserInfo(body, false);
				if (usr == null)
				{
					session.systemMsg("Unban failed. User '" + body + "' does not exist.");
				}
				else
				{
					ModerationBan ban = HabboHotel.getModerationCenter().getUserBan(usr.ID);
					if (ban == null)
					{
						session.systemMsg("Unban failed. No bans for " + usr.name + ".");
					}
					else
					{
						if (HabboHotel.getModerationCenter().deleteBan(ban))
						{
							session.systemMsg("Unban OK. The following ban was deleted:\r" + ban.generateReport());
						}
						else
						{
							session.systemMsg("Unban failed. Ban was found, but system was unable to delete it for whatever reason.\r" + ban.generateReport());
						}
					}
				}
			}
			else
			{
				session.systemMsg("Unban failed. Please supply username.");
			}
			
			return true;
		}*/
		
		return false;
	}
	
	public static boolean unbanIp(SpaceSession session, String body)
	{
		/*
		if (session.getUserObject().hasRight("can_unban"))
		{
			if (body.length() > 0)
			{
				ModerationBan ban = HabboHotel.getModerationCenter().getIpBan(body);
				if (ban == null)
				{
					session.systemMsg("Unban failed. No IP bans for " + body);
				}
				else
				{
					if (HabboHotel.getModerationCenter().deleteBan(ban))
					{
						session.systemMsg("Unban OK. The following ban was deleted:\r" + ban.generateReport());
					}
					else
					{
						session.systemMsg("Unban failed. Ban was found, but system was unable to delete it for whatever reason.\r" + ban.generateReport());
					}
				}
			}
			else
			{
				session.systemMsg("Unban failed. Please supply IP address.");
			}
			
			return true;
		}*/
		
		return false;
	}
	
	public static boolean handleServerFunctions(SpaceSession session, String body)
	{
		if (session.getUserObject().hasRight("can_control_server"))
		{
			if (body != null)
			{
				String arg[] = body.split(" ");
				if (arg[0].equals("setmotd"))
				{
					// Determine the new MOTD
					arg = body.split(" ", 2);
					if (arg.length == 2)
					{
						// Blank?
						if (arg[1].equals("null"))
						{
							arg[1] = null;
						}
						
						// Set it
						session.getServer().getAccessControl().setMessageOfTheDay(arg[1]);
						
						// Broadcast it to existing sessions
						if (arg[1] != null)
						{
							ChatCommandHandler.sendServCast(session, arg[1]);
						}
						
						// Done
						session.systemMsg("New MOTD successfully set.");
					}
					else
					{
						session.systemMsg("Please specify a new Message Of The Day, or 'null' to disable the message.");
					}
				}
				else if (arg[0].equals("killemptyconnections"))
				{
					int amount = 0;
					session.systemMsg("MasterServer killed " + amount + " empty connections.");
				}
				else if (arg[0].equals("reloaditemdefinitions"))
				{
					int amount = session.getServer().getItemAdmin().loadItemDefinitions();
					session.systemMsg("ItemDefinitions reloaded, server knows " + amount + " definitions.");
				}
				else if (arg[0].equals("reloadcatalogue"))
				{
					// Re-load from DatabaseEndpoint
					int amount = session.getServer().getCatalogue().loadArticles();
					
					// Notification
					session.systemMsg("Catalogue reloaded: " + amount + " articles.");
				}
				else if (arg[0].equals("reloadspacemodels"))
				{
					// Re-load from DatabaseEndpoint
					session.getServer().getSpaceAdmin().loadSpaceModels();
					session.systemMsg("Reloaded SpaceModels.");
				}
				else if (arg[0].equals("reloaduserrights"))
				{
					session.getServer().getAccessControl().loadUserRoleRights();
					session.systemMsg("Reloaded user rights.");
				}
				else if (arg[0].equals("clearcfhs"))
				{
					int amount = 0;//HabboHotel.getModerationCenter().clearPendingCalls();
					session.systemMsg("Cleared " + amount + " pending CFHs.");
				}
				else
				{
					// Send a window with all of the available :server parameters
					ServerMessage wnd = new ServerMessage("SYSTEMBROADCAST");
					wnd.appendNewArgument("Available server functions:");
					wnd.appendTextLine("setmotd\tSets the Message Of The Day, shown to Users at login");
					wnd.appendTextLine("killemptyconnections\tKills all connections with no UserObject");
					wnd.appendTextLine("reloaditemdefinitions\tReloads the ItemDefinitions");
					wnd.appendTextLine("reloadcatalogue\tReloads the Catalogue pages and articles");
					wnd.appendTextLine("reloadspacemodels\tReloads the SpaceModels from the DatabaseEndpoint");
					wnd.appendTextLine("reloaduserrights\tReloads the UserRightContainers");
					wnd.appendTextLine("clearcfhs\tDrops the pending Call for Helps");
					session.send(wnd);
				}
			}
			else
			{
				session.systemMsg("Please specify a server function, or type :server help for help.");
			}
			
			return true;
		}
		
		return false;
	}
	
	public static boolean walkReverse(SpaceSession session)
	{
		if (session.getUserObject().hasRight("can_moonwalk"))
		{
			// Swap flag
			SpaceUser usr = session.getUser();
			usr.isReverseWalk = !usr.isReverseWalk;
			
			// Yay!
			session.systemMsg("Billie Jean status: " + Boolean.toString(usr.isReverseWalk));
			return true;
		}
		
		return false;
	}
	
	/*
	public static boolean handleTransfer(SpaceSession session, String body)
	{
		if (session.getUserObject().hasRight("can_transfer_user"))
		{
			if (body != null)
			{
				SpaceSession session = HabboHotel.getSpaceSessions().getsessionOfUser(body);
				if (session == null)
				{
					session.systemMsg("Target user \"" + body + "\" not logged in.");
				}
				else
				{
					session.systemMsg("You are being transfered to \"" + session.getSpace().getInfo().name + "\" by " + session.getUserObject().name + ".");
					
					session.send(new ServerMessage("ADVERTISEMENT 0"));
					session.send(new ServerMessage("OPC_OK"));
					session.goToSpace(session.getSpace().getInfo().ID);
				}
			}
			else
			{
				session.systemMsg("Please specify a user name.");
			}
			
			return true;
		}
		
		return false;
	}*/

	private static String[] wordWrap(String str)
	{
		final Pattern wrapRE = Pattern.compile(".{0,79}(?:\\S(?:-| |$)|$)");
		
		List<String> list = new LinkedList<String>();
		
		Matcher m = wrapRE.matcher(str);
		
		while (m.find())
		{
			list.add(m.group());
		}
		
		return (String[])list.toArray(new String[list.size()]);
		
	}
}
