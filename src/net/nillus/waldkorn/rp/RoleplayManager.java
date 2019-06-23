package net.nillus.waldkorn.rp;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import net.nillus.waldkorn.MasterServer;
import net.nillus.waldkorn.ServerComponent;
import net.nillus.waldkorn.users.User;

public class RoleplayManager extends ServerComponent
{
	public RoleplayManager(MasterServer server)
	{
		super(server);
	}
	
	public Vector<Npc> getNpcs(int spaceID)
	{
		Vector<Npc> npcs = new Vector<Npc>();
		Statement query = null;
		Statement query2 = null;
		try
		{
			query = m_server.getDatabase().createStatement();
			query2 = query.getConnection().createStatement();
			
			ResultSet result = query.executeQuery("SELECT * FROM rp_npcs WHERE spaceid = " + spaceID + ";");
			while(result.next())
			{
				// First, parse the user info
				User info = new User();
				info.ID = result.getInt("id");
				info.name = "@" + result.getString("name");
				info.motto = result.getString("motto");
				info.figure = result.getString("figure");
				info.sex = result.getString("sex").charAt(0);
				info.health = result.getByte("health");
				Npc npc = new Npc(info);
				
				// Load last spawn position
				npc.X = result.getShort("spawn_x");
				npc.Y = result.getShort("spawn_y");
				
				// Load in goal tiles (tiles that the NPC will try to reach)
				String walkTiles = result.getString("walk");
				if(walkTiles.length() == 0 || walkTiles.startsWith("*"))
				{
					npc.freeRoam = true;
				}
				else
				{
					for(String tile : walkTiles.split(" "))
					{	
						String[] coords = tile.split(",", 2);
						npc.addWalkTile(Short.parseShort(coords[0]), Short.parseShort(coords[1]));
					}
				}
				
				// Load in gossip texts
				ResultSet result2 = query2.executeQuery("SELECT text FROM rp_npcs_gossip WHERE npc_id = " + info.ID + ";");
				while(result2.next())
				{
					npc.addGossip(result2.getString(1));
				}
				
				// Load in triggers
				result2 = query2.executeQuery("SELECT words,replies,serve_item,serve_replies FROM rp_npcs_triggers WHERE npc_id = " + info.ID + ";");
				while(result2.next())
				{
					// Parse the trigger
					NpcTrigger trigger;
					String[] words = result2.getString("words").split("#");
					String[] replies = result2.getString("replies").split("#");
					String serveItem = result2.getString("serve_item");
					String serveReplies = result2.getString("serve_replies");
					if(serveItem == null || serveReplies == null)
					{
						trigger = new NpcTrigger(words, replies, null, null, npc.getRandom());
					}
					else
					{
						String[] servedReplies = serveReplies.split("#");
						trigger = new NpcTrigger(words, replies, serveItem, servedReplies, npc.getRandom());
					}
					
					// Add the trigger to the npc
					npc.addTrigger(trigger);
				}
				
				// NPC parsed
				npcs.add(npc);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(query);
		}
		
		return npcs;
	}
}
