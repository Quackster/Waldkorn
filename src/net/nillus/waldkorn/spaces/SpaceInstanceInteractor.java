package net.nillus.waldkorn.spaces;

import java.util.Random;
import java.util.Vector;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.rp.Npc;
import net.nillus.waldkorn.spaces.pathfinding.Calculator2D;
import net.nillus.waldkorn.spaces.pathfinding.JoehPathfinder;
import net.nillus.waldkorn.spaces.pathfinding.PathfinderNode;

/**
 * Represents the 'core' of a running space.
 * 
 * @author Nillus
 */
public class SpaceInstanceInteractor
{
	private static final int CLIENT_FRAMETIME = 470;
	
	private Object m_lock = new Object();
	private SpaceInstance m_instance;
	
	private byte[][] m_map;
	private float[][] m_mapHeight;
	private boolean[][] m_mapUsers;
	private char[][] m_mapClient;
	private SpaceTile[][] m_mapRedirect;
	
	private Vector<Item> m_wallItems;
	private Vector<Item> m_activeObjects;
	private Vector<Item> m_passiveObjects;
	private boolean m_hasInteractivePassiveObjects;
	
	public SpaceInstanceInteractor(SpaceInstance instance)
	{
		m_instance = instance;
		
		m_map = new byte[0][0];
		m_mapHeight = new float[0][0];
		m_mapUsers = null;
		m_mapClient = new char[0][0];
		m_mapRedirect = new SpaceTile[0][0];
		
		m_wallItems = new Vector<Item>(0);
		m_activeObjects = new Vector<Item>(0);
		m_passiveObjects = new Vector<Item>(0);
	}
	
	/**
	 * Unloads all resources that are loaded in the interactor.
	 */
	public void clear()
	{
		synchronized (m_lock)
		{
			m_passiveObjects.clear();
			m_activeObjects.clear();
			m_wallItems.clear();
		}
	}
	
	public void pulse()
	{
		try
		{
			this.refreshUsers();
		}
		catch (Exception ex)
		{
			m_instance.getHost().getServer().getLogger().error("SpaceInstanceInteractor", "error in pulse() method", ex);
		}
	}
	
	/*
	@Override
	public void run()
	{
		while (m_alive)
		{
			try
			{
				// Measure task start time
				long tasksStart = System.nanoTime();
				
				// ALL TASKS GO HERE
				this.refreshUsers();
				// Bot AI, pool camera, etc etc
				
				// Measure sleep time for next heart beat
				double sleepTime = (System.nanoTime() - tasksStart);
				sleepTime /= 1000000;
				sleepTime = (SpaceInstanceInteractor.CLIENT_FRAMETIME - sleepTime);
				
				// Ensure there is CLIENT_FRAMETIME milliseconds between heart beat
				if (sleepTime > 0.0)
				{
					// Beat it - just beat it - w00t
					Thread.sleep((long)sleepTime);
				}
			}
			catch (InterruptedException ex)
			{
				// Thread aborted
				m_alive = false;
			}
			catch (Exception ex)
			{
				ServerMessage notify = new ServerMessage("SYSTEMBROADCAST");
				notify.appendArgument("ERROR!");
				notify.appendNewArgument("Engine for this space just crashed out of nowhere, please leave the space.");
				notify.appendNewArgument("Exception: " + ex.toString());
				notify.appendNewArgument("Stack trace: " + ex.getStackTrace());
				m_instance.broadcast(notify);
				m_alive = false;
			}
		}
	}*/

	public void loadItems()
	{
		synchronized (m_lock)
		{
			// Clear current collections
			m_wallItems.clear();
			m_activeObjects.clear();
			m_passiveObjects.clear();
			
			// Grab passive objects
			Vector<Item> objects = this.getInstance().getModel().getPassiveObjects();
			m_passiveObjects.addAll(objects);
			
			Vector<Item> items = m_instance.getHost().getServer().getItemAdmin().getSpaceItems(this.getInstance().getInfo().ID);
			for (Item item : items)
			{
				if (item.definition.behaviour.onFloor)
				{
					m_activeObjects.addElement(item);
				}
				else if (item.definition.behaviour.onWall)
				{
					m_wallItems.addElement(item);
				}
				else
				{
					// Item is in space but can not be here
					item.spaceID = 0;
					item.update(m_instance.getHost().getServer().getDatabase());
				}
			}
		}
	}
	
	/**
	 * Resets all tiles to their default states and re-calculates the map.
	 */
	public void generateFloorMap(boolean mapPassiveObjects)
	{
		// Get default map string and split axes
		String defaultMapString = getInstance().getModel().defaultHeightmap;
		String[] axes = defaultMapString.split(" ");
		
		// Determine map boundaries
		int maxX = axes[0].length();
		int maxY = axes.length;
		
		// Create blank maps
		byte[][] tmpMap = new byte[maxX][maxY];
		float[][] tmpHeightmap = new float[maxX][maxY];
		char[][] tmpClientmap = new char[maxX][maxY];
		if (m_mapUsers == null)
		{
			m_mapUsers = new boolean[maxX][maxY];
		}
		SpaceTile[][] tmpRedirectMap = new SpaceTile[maxX][maxY];
		
		// Create default floor map
		for (int Y = 0; Y < maxY; Y++)
		{
			for (int X = 0; X < maxX; X++)
			{
				// Grab tile
				char tile = axes[Y].charAt(X);
				tmpClientmap[X][Y] = tile;
				
				// Determine tile
				if (tile != 'x' && Character.isDigit(tile))
				{
					tmpMap[X][Y] = 0; // Free
					tmpHeightmap[X][Y] = Float.parseFloat(Character.toString(tile));
				}
				else
				{
					tmpMap[X][Y] = 1; // Blocked
				}
			}
		}
		
		// Apply passive objects to map?
		synchronized (m_lock)
		{
			if (mapPassiveObjects)
			{
				for (Item obj : m_passiveObjects)
				{
					this.applyObjectToMaps(obj, tmpMap, tmpHeightmap, tmpRedirectMap);
					if (obj.definition.behaviour.canSitOnTop || obj.definition.behaviour.canLayOnTop || obj.definition.behaviour.isTrigger)
					{
						m_hasInteractivePassiveObjects = true;
					}
				}
			}
			
			// Apply active objects to map
			for (Item obj : m_activeObjects)
			{
				this.applyObjectToMaps(obj, tmpMap, tmpHeightmap, tmpRedirectMap);
			}
		}
		
		// Restore door tile
		SpaceModel model = m_instance.getModel();
		tmpMap[model.doorX][model.doorY] = 0; // Door always free
		tmpHeightmap[model.doorX][model.doorY] = model.doorZ;
		//tmpClientmap[model.doorX][model.doorY + 1] = '0';
		
		// Set instance maps
		m_map = tmpMap;
		m_mapHeight = tmpHeightmap;
		m_mapClient = tmpClientmap;
		m_mapRedirect = tmpRedirectMap;
	}
	
	private void applyObjectToMaps(Item obj, byte[][] tmpMap, float[][] tmpHeightmap, SpaceTile[][] tmpRedirectMap)
	{
		// TRIGGERS ARE SIMPLE. K.
		if (obj.definition.behaviour.isTrigger)
		{
			tmpMap[obj.X][obj.Y] = 2; // Interactive
			return;
		}
		
		for (SpaceTile tile : this.getAffectedTiles(obj, true))
		{
			// Out of range?
			if (!(tile.X >= 0 && tile.X < tmpMap.length && tile.Y >= 0 && tile.Y < tmpMap[0].length))
			{
				m_instance.getHost().getServer().getLogger().info(this, "object " + obj.ID + " at tile [" + obj.X + "," + obj.Y + "] was out of range. Tile [" + tile.X + "," + tile.Y + "] was outside the boundaries of map. Object not applied to map.");
				return;
			}
			
			// Increase heightmap
			if (obj.Z >= tmpHeightmap[tile.X][tile.Y])
			{
				tmpHeightmap[tile.X][tile.Y] = obj.Z;
			}
			
			// Is this object a door?
			if (obj.definition.behaviour.isDoor)
			{
				// Is the door open?
				if (obj.customData != null && obj.customData.equals("O"))
				{
					tmpMap[tile.X][tile.Y] = 0; // Free
				}
				else
				{
					tmpMap[tile.X][tile.Y] = 1; // Blocked
				}
				
				// Doors haven't got further handling
				return;
			}
			
			// Is this object the top item on the tile?
			if ((obj.Z >= tmpHeightmap[tile.X][tile.Y]))
			{
				if (obj.definition.behaviour.canStandOnTop)
				{
					if (obj.Z > tmpHeightmap[tile.X][tile.Y])
					{
						tmpMap[tile.X][tile.Y] = 0; // Free
						tmpHeightmap[tile.X][tile.Y] = (obj.Z + obj.definition.heightOffset);
					}
				}
				else if (obj.definition.behaviour.canSitOnTop)
				{
					tmpMap[tile.X][tile.Y] = 2; // Interactive
				}
				else if (obj.definition.behaviour.canLayOnTop)
				{
					tmpMap[tile.X][tile.Y] = 2; // Interactive
					
					// Redirect to top
					if (obj.rotation == 0 || obj.rotation == 4)
					{
						tmpRedirectMap[tile.X][tile.Y] = new SpaceTile(tile.X, obj.Y);
					}
					else
					{
						tmpRedirectMap[tile.X][tile.Y] = new SpaceTile(obj.X, tile.Y);
					}
				}
				else
				{
					// Block the tile if there is no interactive object on top
					if (tmpMap[tile.X][tile.Y] != 2)
						tmpMap[tile.X][tile.Y] = 1;
				}
			}
		}
	}
	
	private Vector<SpaceTile> getAffectedTiles(Item obj, boolean includeRoot)
	{
		return this.getAffectedTiles(obj.definition.length, obj.definition.width, obj.X, obj.Y, obj.rotation, includeRoot);
	}
	
	private Vector<SpaceTile> getAffectedTiles(byte length, byte width, short X, short Y, byte rotation, boolean includeRoot)
	{
		Vector<SpaceTile> tiles = new Vector<SpaceTile>(0);
		
		// Is this a non-square item?
		if (length != width)
		{
			// Flip rotation
			if (rotation == 0 || rotation == 4)
			{
				byte tmpL = length;
				length = width;
				width = tmpL;
			}
		}
		
		for (short iX = X; iX < X + width; iX++)
		{
			for (short iY = Y; iY < Y + length; iY++)
			{
				tiles.add(new SpaceTile(iX, iY));
			}
		}
		
		return tiles;
	}
	
	public boolean moveActiveObject(int itemID, Item item, short newX, short newY, byte newRotation)
	{
		// Validate new rotation, this can really trouble client and server!
		if (newRotation != 0 && newRotation != 2 && newRotation != 4 && newRotation != 6)
		{
			return false;
		}
		
		// Determine if object is new to space
		boolean isNew = (item != null);
		
		// Determine the Item that is being used while moving
		Item obj;
		if (isNew)
		{
			obj = item;
		}
		else
		{
			obj = this.getActiveObject(itemID);
			if (obj == null)
			{
				return false;
			}
		}
		
		// Calculate new height
		float newZ = m_mapHeight[newX][newY];
		
		// Trying to stack on itself? (by rotating an object that is already in the space)
		if (!isNew)
		{
			if (obj.rotation == newRotation && obj.X == newX && obj.Y == newY && obj.Z != newZ)
			{
				return false;
			}
		}
		
		// Get the tiles this object would reside on if this item was placed here
		Vector<SpaceTile> newTiles = this.getAffectedTiles(obj.definition.length, obj.definition.width, newX, newY, newRotation, true);
		
		// Check those tiles for 'free-ness'
		for (SpaceTile tile : newTiles)
		{
			if (!this.mapTileExists(tile.X, tile.Y))// || m_mapClient[tile.X][tile.Y] == 'x')
			{
				return false; // Out of map range / bad tile
			}
			
			// Is there a SpaceUser on this tile?
			if (m_mapUsers[tile.X][tile.Y])
			{
				// Can rotate on different tile than root?
				if (newRotation == obj.rotation && (tile.X != obj.X || tile.Y != obj.Y))
				{
					return false;
				}
			}
		}
		
		// Now check all the new tiles to determine new height
		boolean mod_stack = false;
		for (SpaceTile tile : newTiles)
		{
			// Get the objects that are 'leaning' on this tile somehow
			Vector<Item> objsOnTile = this.getActiveObjectsOnTile(tile.X, tile.Y);
			
			// First check if mod_stack needs enabling
			byte found = 0;
			if (!mod_stack)
			{
				for (Item tileObj : objsOnTile)
				{
					if (tileObj.Z > 0 && tileObj.definition.behaviour.canStackOnTop && tileObj.definition.heightOffset == 0 && tileObj.definition.width == 1 && tileObj.definition.length == 1)
					{
						if (++found == 2)
						{
							mod_stack = true;
							break;
						}
					}
				}
			}
			
			// Check those objects and determine top height
			for (Item tileObj : objsOnTile)
			{
				// Is this not the object that is being moved now?
				if (tileObj.ID != obj.ID)
				{
					// Is this the top item?
					if ((tileObj.Z + tileObj.definition.heightOffset) > newZ)
					{
						// Break when mod_stack is disabled and we can't stack on the top item
						if (!mod_stack && !tileObj.definition.behaviour.canStackOnTop)
						{
							return false;
						}
						else if (mod_stack && !obj.definition.behaviour.canStackOnTop)
						{
							newZ = tileObj.Z;
						}
						else
						{
							newZ = tileObj.Z + tileObj.definition.heightOffset;
						}
					}
				}
			}
		}
		
		// Max stacking height
		final int maxItemZ = 15;
		newZ = (newZ > maxItemZ) ? maxItemZ : newZ;
		
		// Backup the old position
		short oldX = obj.X;
		short oldY = obj.Y;
		byte oldRotation = obj.rotation;
		
		// If is object is new to space...
		if (isNew)
		{
			obj.spaceID = this.getInstance().getInfo().ID;
			obj.ownerID = this.getInstance().getInfo().ownerID;
			m_activeObjects.addElement(obj);
		}
		
		// Set object on new position
		obj.X = newX;
		obj.Y = newY;
		obj.Z = newZ;
		obj.rotation = newRotation;
		
		// Update Item
		obj.update(m_instance.getHost().getServer().getDatabase());
		
		// Broadcast event
		this.broadcastActiveObjectEvent(obj, isNew);
		
		// Generate the new map
		this.generateFloorMap(false);
		
		// Restore SpaceUsers on old SpaceTiles?
		if (!isNew)
		{
			for (SpaceTile tile : this.getAffectedTiles(obj.definition.length, obj.definition.width, oldX, oldY, oldRotation, true))
			{
				this.refreshUserOnTile(tile.X, tile.Y);
			}
		}
		
		// Refresh SpaceUsers on new SpaceTiles
		for (SpaceTile tile : this.getAffectedTiles(obj, true))
		{
			this.refreshUserOnTile(tile.X, tile.Y);
		}
		
		// Move OK!
		return true;
	}
	
	public Item pickupActiveObject(int itemID)
	{
		Item obj = this.getActiveObject(itemID);
		if (obj != null)
		{
			// Remove from collection
			synchronized (m_lock)
			{
				m_activeObjects.remove(obj);
			}
			
			// Notify clients
			ServerMessage msg = new ServerMessage("ACTIVEOBJECT_REMOVE");
			msg.appendNewArgument(String.format("%026d", obj.ID));
			this.getInstance().broadcast(msg);
			
			// Re-generate floormap
			this.generateFloorMap(false);
			
			// Refresh users on tile
			for (SpaceTile tile : this.getAffectedTiles(obj, true))
			{
				this.refreshUserOnTile(tile.X, tile.Y);
			}
			
			// Picked up OK!
			obj.spaceID = 0;
			obj.X = 0;
			obj.Y = 0;
			obj.Z = 0;
			obj.rotation = 0;
			return obj;
		}
		
		// An hero!
		return null;
	}
	
	public boolean placeWallItem(Item item, String position)
	{
		if (item != null)
		{
			// Update the item to this Space
			item.spaceID = this.getInstance().getInfo().ID;
			item.ownerID = this.getInstance().getInfo().ownerID;
			item.wallPosition = position;
			
			// Update Item
			item.update(m_instance.getHost().getServer().getDatabase());
			
			// Add to collection
			synchronized (m_lock)
			{
				m_wallItems.addElement(item);
			}
			
			// Notify clients
			ServerMessage msg = new ServerMessage("ITEMS");
			msg.appendObject(item);
			this.getInstance().broadcast(msg);
			
			// Placed OK!
			return true;
		}
		
		// Item not found or whatever
		return false;
	}
	
	public Item pickupWallItem(int itemID)
	{
		Item item = this.getWallItem(itemID);
		if (item != null)
		{
			// Remove from collection
			synchronized (m_lock)
			{
				m_wallItems.removeElement(item);
			}
			
			// Notify clients
			ServerMessage msg = new ServerMessage("REMOVEITEM");
			msg.appendNewArgument(Integer.toString(item.ID));
			this.getInstance().broadcast(msg);
			
			// Picked up OK!
			item.spaceID = 0;
			item.wallPosition = null;
			return item;
		}
		
		// Could not pickup, this is an true hero scripter
		return null;
	}
	
	public void updateActiveObjectData(int userID, int itemID, String classType, String data)
	{
		// Object exists?
		Item obj = this.getActiveObject(itemID);
		if (obj == null)
		{
			return;
		}
		
		// Does this object require flat controller for interaction?
		if (obj.definition.behaviour.requiresRightsForInteraction && !this.getInstance().getUserByUserID(userID).isFlatController)
		{
			return;
		}
		
		// Does this object requires user to stand one tile removed from item?
		if (obj.definition.behaviour.requiresTouchingForInteraction)
		{
			SpaceUser usr = this.getInstance().getUserByUserID(userID);
			if (!SpaceInstanceInteractor.mapTilesTouch(obj.X, obj.Y, usr.X, usr.Y))
			{
				return;
			}
		}
		
		// Is this object a door?
		if (obj.definition.behaviour.isDoor)
		{
			// Close door?
			if (data.equals("C"))
			{
				// Check if there is no SpaceUser on one of the door tiles
				for (SpaceTile tile : this.getAffectedTiles(obj, true))
				{
					// User here?
					if (m_mapUsers[tile.X][tile.Y])
					{
						return;
					}
				}
			}
			else
			{
				data = "O";
			}
			
			// Required for map generation
			obj.customData = data;
			
			// Update map!
			this.generateFloorMap(false);
		}
		
		// Notify clients
		ServerMessage msg = new ServerMessage("STUFFDATAUPDATE");
		msg.appendNewArgument(String.format("%026d", obj.ID));
		msg.appendPartArgument("");
		msg.appendPartArgument(classType);
		msg.appendPartArgument(data);
		this.getInstance().broadcast(msg);
		
		// Save customdata to database if it's not temporarily
		boolean isTemporarily = data.equals("TRUE") || data.equals("FALSE");
		if (!isTemporarily)
		{
			obj.customData = data;
			obj.update(m_instance.getHost().getServer().getDatabase());
		}
	}
	
	public boolean mapTileExists(short X, short Y)
	{
		return (X >= 0 && X < m_map.length && Y >= 0 && Y < m_map[0].length);
	}
	
	public static boolean mapTilesTouch(short X, short Y, short X2, short Y2)
	{
		return (Math.abs(X - X2) <= 1 && Math.abs(Y - Y2) <= 1);
	}
	
	public boolean mapTileWalkable(int X, int Y)
	{
		return (m_map[X][Y] == 0 && !m_mapUsers[X][Y]);
	}
	
	/**
	 * Sets a tile on the user map to a given boolean value.
	 * 
	 * @param X The X position of the tile.
	 * @param Y The Y position of the tile.
	 * @param blocked True if the tile is now blocked, False otherwise.
	 */
	public void setUserMapTile(short X, short Y, boolean blocked)
	{
		m_mapUsers[X][Y] = blocked;
	}
	
	public String generateHeightMapString()
	{
		int maxX = m_map.length;
		int maxY = m_map[0].length;
		StringBuilder sb = new StringBuilder((maxX * maxY) + maxY);
		
		for (int Y = 0; Y < maxY; Y++)
		{
			for (int X = 0; X < maxX; X++)
			{
				sb.append(m_mapClient[X][Y]);
			}
			sb.append((char)13);
		}
		
		return sb.toString();
	}
	
	public void broadcastActiveObjectEvent(Item obj, boolean placement)
	{
		ServerMessage msg = new ServerMessage();
		if (placement)
		{
			msg.set("ACTIVEOBJECT_ADD");
		}
		else
		{
			msg.set("ACTIVEOBJECT_UPDATE");
		}
		msg.appendObject(obj);
		
		this.getInstance().broadcast(msg);
	}
	
	public void broadcastTeleporterActivity(int itemID, String itemSprite, String user, boolean enter)
	{
		// Notify clients
		ServerMessage msg = new ServerMessage();
		if (enter)
		{
			msg.set("DOOR_OUT");
		}
		else
		{
			msg.set("DOOR_IN");
		}
		msg.appendNewArgument(String.format("%026d", itemID));
		msg.appendNewArgument(user);
		msg.appendNewArgument(itemSprite);
		
		// Broadcast
		this.getInstance().broadcast(msg);
	}
	
	private void refreshUsers()
	{
		boolean sendUpdates = false;
		ServerMessage msg = new ServerMessage("STATUS");
		
		// Pulse & refresh npcs
		synchronized (m_lock)
		{
			for (Npc npc : m_instance.getNpcs())
			{
				npc.pulse();
				if ((npc.goalX != -1 || npc.requiresUpdate()))
				{
					this.refreshUser(npc, msg);
					sendUpdates = true;
				}
			}
			
			// Refresh real users
			for (SpaceUser usr : m_instance.getUsers())
			{
				if (usr.goalX != -1 || usr.requiresUpdate())
				{
					this.refreshUser(usr, msg);
					sendUpdates = true;
				}
			}
		}
		
		// Updates available?
		if (sendUpdates)
		{
			m_instance.broadcast(msg);
		}
	}
	
	private void refreshUser(SpaceUser usr, ServerMessage statusMsg)
	{
		// Done for now!
		boolean appendStatus = true;
		
		// No ensured updates needed until further notice!
		usr.ensureUpdate(false);
		
		// Moving?
		if (usr.goalX != -1)
		{
			// Destination reached!
			if (usr.X == usr.goalX && usr.Y == usr.goalY)
			{
				// Trip complete!
				usr.clearPath();
				
				// Is SpaceUser on an interactive tile?
				if (m_map[usr.X][usr.Y] == 2)
				{
					this.applyTileToUser(usr);
				}
			}
			else
			{
				// Determine next tile in path
				PathfinderNode next;
				if (usr.overrideNextTile)
				{
					// Override used!
					usr.overrideNextTile = false;
					
					// Substitute the next step
					next = new PathfinderNode();
					next.X = usr.goalX;
					next.Y = usr.goalY;
				}
				else
				{
					// Pop the next tile from the path
					next = this.getMovingUserNextTile(usr);
				}
				
				// Next tile available?
				if (next == null)
				{
					// Add a try to the counter
					usr.movementRetries++;
					
					// Re-find path or give up?
					if (usr.movementRetries < 3)
					{
						this.startUserMovement(usr, usr.goalX, usr.goalY, false);
					}
					else
					{
						// Give up on this path
						usr.movementRetries = 0;
						usr.clearPath();
					}
					
					// Is SpaceUser on an interactive tile?
					if (m_map[usr.X][usr.Y] == 2)
					{
						this.applyTileToUser(usr);
					}
				}
				else
				{
					// Moved a tile
					usr.movementRetries = 0;
					
					// Block & unblock tiles
					m_mapUsers[usr.X][usr.Y] = false;
					m_mapUsers[next.X][next.Y] = (!usr.isInvisible);
					
					// Rotate users body
					byte rotation = Calculator2D.calculateHumanMoveDirection(usr.X, usr.Y, next.X, next.Y);
					if (usr.isReverseWalk)
					{
						rotation = (rotation < 4) ? (rotation += 4) : (rotation -= 4);
					}
					
					// Apply rotation
					usr.headRotation = rotation;
					usr.bodyRotation = rotation;
					
					// Remove interactive statuses
					usr.removeInteractiveStatuses();
					
					// Append status
					statusMsg.append(usr.getStatusString());
					statusMsg.append("mv " + next.X + "," + next.Y + "," + (int)m_mapHeight[next.X][next.Y] + "/");
					appendStatus = false;
					
					// Serverside movement of user
					usr.X = next.X;
					usr.Y = next.Y;
					usr.Z = m_mapHeight[next.X][next.Y];
				}
			}
		}
		
		// Append status string?
		if (appendStatus)
		{
			statusMsg.append(usr.getStatusString());
		}
	}
	
	private PathfinderNode getMovingUserNextTile(SpaceUser usr)
	{
		int tileAmount = usr.path.size();
		if (tileAmount > 0)
		{
			// Pop next node and check if the tile is walkable
			PathfinderNode next = usr.path.remove(0);
			if (next == null || m_map[next.X][next.Y] != 0 && m_map[next.X][next.Y] != 2)
			{
				return null; // Next tile blocked
			}
			else if (m_mapUsers[next.X][next.Y])
			{
				return null;
			}
			
			// Can do a shortcut?
			final boolean CROSS_DIAGONAL = true;
			if (tileAmount > 1 && CROSS_DIAGONAL)
			{
				// Peek + test next node (already picked one, so this is like path[1])
				PathfinderNode node = usr.path.get(0);
				if (this.mapTileWalkable(node.X, node.Y))
				{
					// Can't skip more than one tile!
					if (!(Math.abs(usr.X - node.X) > 1 || Math.abs(usr.Y - node.Y) > 1))
					{
						byte X1 = 0;
						byte X2 = 0;
						if (node.X > usr.X)
						{
							X1 = -1;
							X2 = 1;
						}
						else
						{
							X1 = 1;
							X2 = -1;
						}
						
						if (this.mapTileWalkable(node.X + X1, node.Y) && this.mapTileWalkable(usr.X + X2, usr.Y)) // Valid shortcut
						{
							next = usr.path.remove(0); // Skip this node! Yay!
						}
					}
				}
			}
			
			// The next tile!
			return next;
		}
		else
		{
			return null;
		}
	}
	
	private void applyTileToUser(SpaceUser usr)
	{
		// This check saves us checking wallparts!
		if (m_hasInteractivePassiveObjects)
		{
			for (Item obj : m_passiveObjects)
			{
				if (obj.X == usr.X && obj.Y == usr.Y)
				{
					if (applyInteractiveObjectToUser(usr, obj))
					{
						// Applied!
						return;
					}
				}
			}
		}
		
		// Check all active objects
		for (Item obj : this.getActiveObjectsOnTile(usr.X, usr.Y))
		{
			synchronized (m_lock)
			{
				if (applyInteractiveObjectToUser(usr, obj))
				{
					return;
				}
			}
		}
	}
	
	private boolean applyInteractiveObjectToUser(SpaceUser usr, Item obj)
	{
		if (obj.definition.behaviour.canSitOnTop)
		{
			usr.Z = obj.Z;
			usr.headRotation = obj.rotation;
			usr.bodyRotation = obj.rotation;
			usr.removeStatus("dance");
			usr.addStatus("sit", Float.toString(obj.Z + obj.definition.heightOffset), 0, null, 0, 0);
			
			return true;
		}
		else if (obj.definition.behaviour.canLayOnTop)
		{
			// Position user at bed
			SpaceTile redirect = m_mapRedirect[usr.X][usr.Y];
			if (redirect != null)
			{
				usr.X = redirect.X;
				usr.Y = redirect.Y;
			}
			usr.Z = obj.Z;
			usr.headRotation = obj.rotation;
			usr.bodyRotation = obj.rotation;
			
			usr.removeStatus("dance");
			usr.removeStatus("carryd");
			usr.addStatus("lay", Float.toString(obj.definition.heightOffset) + " null", 0, null, 0, 0);
			
			return true;
		}
		else if (obj.definition.behaviour.isTrigger && !usr.isBot())
		{
			/*
			 * Triggers
			 * Item definition sprite: object class ('emitter')
			 * Item data: object ID ('emitter')
			 * Custom data: can hold positions and other data
			 * Incase of positions: "x,y x,y x,y ..."
			 */
			if (obj.definition.sprite.equals("poolBooth"))
			{
				// Lock user at this position
				usr.moveLock = true;
				
				// Notify clients that this pool booth is in use
				this.getInstance().showProgram(obj.itemData, "close");
				
				// Open 'clothe urself' window
				usr.getSession().send(new ServerMessage("OPEN_UIMAKOPPI"));
			}
			if (obj.definition.sprite.equals("poolEnter"))
			{
				// Has user got swimming clothes on?
				if (!usr.getUserObject().poolFigure.equals(""))
				{
					// Parse positions
					String[] positions = obj.customData.split(" ", 2);
					SpaceTile warp = SpaceTile.parse(positions[0]);
					SpaceTile goal = SpaceTile.parse(positions[1]);
					
					// Drop user in pool
					usr.addStatus("swim", null, 0, null, 0, 0);
					this.warpUser(usr, warp.X, warp.Y, true);
					this.refreshUserStatus(usr);
					
					// Display splash for clients
					this.getInstance().showProgram(obj.itemData, "enter");
					
					// Move to goal
					this.startUserMovement(usr, goal.X, goal.Y, true);
				}
			}
			else if (obj.definition.sprite.equals("poolExit"))
			{
				// Parse positions
				String[] positions = obj.customData.split(" ", 2);
				SpaceTile warp = SpaceTile.parse(positions[0]);
				SpaceTile goal = SpaceTile.parse(positions[1]);
				
				// Display splash for clients
				this.getInstance().showProgram(obj.itemData, "exit");
				
				// Kick user out of pool
				usr.moveLock = false;
				usr.removeStatus("swim");
				this.warpUser(usr, warp.X, warp.Y, true);
				this.refreshUserStatus(usr);
				
				// Move to goal
				this.startUserMovement(usr, goal.X, goal.Y, true);
			}
			else if (obj.definition.sprite.equals("poolLift"))
			{
				// Swimming clothes on?
				if (!usr.getUserObject().poolFigure.equals(""))
				{
					final short ticketCost = 1;
					if (usr.getUserObject().gameTickets < ticketCost)
					{
						usr.getSession().send(new ServerMessage("PH_NOTICKETS"));
					}
					else
					{
						// Deduct one ticket
						usr.getUserObject().gameTickets -= ticketCost;
						
						// Lock user
						usr.moveLock = true;
						
						// Notify clients lift closed
						this.getInstance().showProgram(obj.itemData, "close");
						
						// Client can start diving!
						usr.getSession().send(new ServerMessage("JUMPINGPLACE_OK"));
					}
				}
			}
			
			// Triggered
			return true;
		}
		
		// No trigger
		return false;
	}
	
	public void startUserMovement(SpaceUser usr, short goalX, short goalY, boolean overrideNext)
	{
		// Clear old path
		usr.clearPath();
		usr.ensureUpdate(true);
		
		// Tile exists?
		if (!this.mapTileExists(goalX, goalY))
		{
			// WTF TILE AINT ON MAP?!
			return;
		}
		
		// Redirecting tile?
		SpaceTile redirect = m_mapRedirect[goalX][goalY];
		if (redirect != null)
		{
			goalX = redirect.X;
			goalY = redirect.Y;
		}
		
		// Override?
		if (overrideNext)
		{
			usr.goalX = goalX;
			usr.goalY = goalY;
			usr.overrideNextTile = true;
		}
		else
		{
			// Use pathfinder
			// One move to seat etc?
			boolean interactiveTile = false;
			if (m_map[goalX][goalY] == 2)
			{
				if (!m_mapUsers[goalX][goalY])
				{
					interactiveTile = true;
					m_map[goalX][goalY] = 0; // Free for one tick
				}
			}
			
			// Create pathfinder
			boolean noDescLimit = m_instance.getInfo().isUserFlat();
			JoehPathfinder finder = new JoehPathfinder(m_map, m_mapUsers, m_mapHeight, 1.5f, noDescLimit ? 100 : 2.0f);
			
			// Find path
			Vector<PathfinderNode> path = finder.findPath(usr.X, usr.Y, goalX, goalY);
			
			// Restore map
			if (interactiveTile)
			{
				m_map[goalX][goalY] = 2;
			}
			
			// Is there a path found?
			if (path.size() > 0)
			{
				// Remove current tile node
				path.remove(0);
				
				// Set this path and destination to space user
				usr.path = path;
				usr.goalX = goalX;
				usr.goalY = goalY;
			}
		}
	}
	
	public void refreshUserStatus(SpaceUser usr)
	{
		// Broadcast updates
		ServerMessage msg = new ServerMessage("STATUS");
		msg.appendArgument(usr.getStatusString());
		this.getInstance().broadcast(msg);
	}
	
	private void refreshUserOnTile(short X, short Y)
	{
		SpaceUser usr = this.getInstance().getUserOnTile(X, Y);
		if (usr != null)
		{
			// Remove old statuses and calculate new
			usr.removeInteractiveStatuses();
			this.applyTileToUser(usr);
			
			// Immediate refresh (no waiting on worker)
			ServerMessage msg = new ServerMessage("STATUS");
			msg.append(usr.getStatusString());
			
			this.getInstance().broadcast(msg);
		}
	}
	
	public void warpUser(SpaceUser usr, short X, short Y, boolean setHeight)
	{
		// Unblock current position
		m_mapUsers[usr.X][usr.Y] = false;
		
		// Set user on new position
		usr.X = X;
		usr.Y = Y;
		
		// Optionally adjust height
		if (setHeight)
		{
			usr.Z = m_mapHeight[usr.X][usr.Y];
		}
		
		// Block new position
		m_mapUsers[usr.X][usr.Y] = true;
		
		// Requires update!
		usr.ensureUpdate(true);
	}
	
	public float getMapTileHeight(short X, short Y)
	{
		if (this.mapTileExists(X, Y))
		{
			return m_mapHeight[X][Y];
		}
		else
		{
			return 0.0f;
		}
	}
	
	public Vector<Item> getActiveObjectsOnTile(short X, short Y)
	{
		Vector<Item> objs = new Vector<Item>();
		synchronized (m_lock)
		{
			for (Item obj : m_activeObjects)
			{
				for (SpaceTile tile : this.getAffectedTiles(obj, true))
				{
					if (tile.X == X && tile.Y == Y)
						objs.add(obj);
					// Break inner loop?
				}
			}
		}
		
		return objs;
	}
	
	public Item getPassiveObjectOnTile(short X, short Y)
	{
		synchronized (m_lock)
		{
			for (Item obj : m_passiveObjects)
			{
				if (obj.X == X && obj.Y == Y)
				{
					return obj;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Searches through the passive object collection for a Item with a given item ID.
	 * 
	 * @param itemID The database ID of the Item.
	 * @return The Item if the object is found, NULL otherwise.
	 */
	public Item getPassiveObject(int itemID)
	{
		synchronized (m_lock)
		{
			for (Item obj : m_passiveObjects)
			{
				if (obj.ID == itemID)
				{
					return obj;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Searches through the active object collection for a Item with a given item ID.
	 * 
	 * @param itemID The database ID of the Item.
	 * @return The Item if the object is found, NULL otherwise.
	 */
	public Item getActiveObject(int itemID)
	{
		synchronized (m_lock)
		{
			for (Item obj : m_activeObjects)
			{
				if (obj.ID == itemID)
				{
					return obj;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Searches through the wall item collection for a Item with a given item ID.
	 * 
	 * @param itemID The database ID of the Item.
	 * @return The Item if the object is found, NULL otherwise.
	 */
	public Item getWallItem(int itemID)
	{
		synchronized (m_lock)
		{
			for (Item item : m_wallItems)
			{
				if (item.ID == itemID)
				{
					return item;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the SpaceInstance this interactor belongs to. (Bad naming tho, it's no singleton!)
	 */
	public SpaceInstance getInstance()
	{
		return m_instance;
	}
	
	/**
	 * Returns the Vector collection with the PassiveObjects installed in this space.
	 */
	public Vector<Item> getPassiveObjects()
	{
		return m_passiveObjects;
	}
	
	/**
	 * Returns the Vector collection with the ActiveObjects installed in this space.
	 */
	public Vector<Item> getActiveObjects()
	{
		return m_activeObjects;
	}
	
	/**
	 * Returns the Vector collection with the Item items located on the walls in this space.
	 */
	public Vector<Item> getWallItems()
	{
		return m_wallItems;
	}
}
