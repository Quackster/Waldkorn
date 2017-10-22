package net.nillus.waldkorn.sessions.commands.master;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ClientMessage;
import net.nillus.waldkorn.sessions.SessionCommandHandler;
import net.nillus.waldkorn.spaces.Space;
import net.nillus.waldkorn.spaces.SpaceUser;

public class GETDOORFLAT extends SessionCommandHandler
{
	public void handle(ClientMessage msg)
	{
		// Get item
		int itemID = Integer.parseInt(msg.nextArgument('/'));
		Item obj = m_session.getSpaceSession().getSpace().getInteractor().getActiveObject(itemID);
		
		// Item valid?
		if(obj != null && obj.definition.behaviour.isTeleporter)
		{
			// Sleep...
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException ex)
			{
				return;
			}
			
			// Get user
			SpaceUser usr =  m_session.getSpaceSession().getUser();
			if(usr != null)
			{
				// User actually in teleporter?
				if(usr.X == obj.X && usr.Y == obj.Y)
				{
					// Get teleporter 2
					Item obj2 = m_session.getServer().getItemAdmin().getItem(obj.teleporterID);
					
					// Teleporter 2 is found and in a space?
					if(obj2 != null && obj2.spaceID > 0)
					{
						// Broadcast user going in
						 m_session.getSpaceSession().getSpace().getInteractor().broadcastTeleporterActivity(obj.ID, obj.definition.sprite, m_session.getUserObject().name, true);
						
						// Unblock current tile (user goes into the void)
						usr.moveLock = true;
						 m_session.getSpaceSession().getSpace().getInteractor().setUserMapTile(usr.X, usr.Y, false);

						// Serverside teleport
						if(obj2.spaceID ==  m_session.getSpaceSession().getSpace().getInfo().ID)
						{
							// Wait for client...
							try
							{
								Thread.sleep(500);
							}
							catch (InterruptedException ex)
							{
								return;
							}
							
							
							// Broadcast user coming out of teleporter 2
							 m_session.getSpaceSession().getSpace().getInteractor().broadcastTeleporterActivity(obj2.ID, obj2.definition.sprite, m_session.getUserObject().name, false);
							
							// And warp to teleporter 2 tile
							usr.X = obj2.X;
							usr.Y = obj2.Y;
							usr.Z = obj2.Z;
							usr.headRotation = obj2.rotation;
							usr.bodyRotation = obj2.rotation;
							 m_session.getSpaceSession().getSpace().getInteractor().setUserMapTile(usr.X, usr.Y, true);
							
							// Update state
							usr.moveLock = false;
							usr.ensureUpdate(false);
						}
						else
						{
							// Set IDs
							m_session.getSpaceSession().authenticatedFlat = obj2.spaceID;
							m_session.getSpaceSession().authenticatedTeleporter = obj2.ID;
							
							Space flat = m_session.getServer().getSpaceAdmin().getSpaceInfo(obj2.spaceID);
							
							// Trigger client
							m_response.set("DOORFLAT");
							m_response.appendNewArgument(Integer.toString(obj2.ID));
							m_response.appendObject(flat);
							sendResponse();
						}
					}
				}
			}
		}
	}
}
