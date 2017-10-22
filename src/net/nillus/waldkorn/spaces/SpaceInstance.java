package net.nillus.waldkorn.spaces;

import java.util.Vector;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.rp.Npc;
import net.nillus.waldkorn.spaces.pathfinding.Calculator2D;
import net.nillus.waldkorn.util.ChatEmoteDetector;

/**
 * Represents an instance of a space running inside a SpaceInstanceServer. Broadcasts data of all action to inside SpaceUsers etc.
 * 
 * @author Nillus
 */
public class SpaceInstance
{
	private Space m_info;
	private SpaceModel m_model;
	private SpaceServer m_host;
	
	private Object m_lock = new Object();
	private boolean m_active;
	private SpaceInstanceInteractor m_interactor;
	private Vector<SpaceUser> m_users;
	private Vector<Npc> m_npcs;
	private Vector<Integer> m_flatControllers;
	
	public SpaceInstance(Space info, SpaceModel model, SpaceServer server)
	{
		// Set data
		m_info = info;
		m_model = model;
		m_host = server;
		
		// Create collections with initial capacities
		m_users = new Vector<SpaceUser>(m_info.usersMax / 5);
		m_npcs = new Vector<Npc>(0);
		m_flatControllers = new Vector<Integer>(2);
		
		// Install interactor
		m_interactor = new SpaceInstanceInteractor(this);
	}
	
	public void checkState()
	{
		
	}
	
	/**
	 * Stops and destroys the interactor, removes all the npcs and users and stops the SpaceInstance.
	 */
	public void destroy()
	{
		// Clear interactor
		m_interactor.clear();
		
		// Clear the user collections
		synchronized (m_lock)
		{
			// Disconnect all users
			for (SpaceUser usr : m_users)
			{
				usr.getSession().exit(null);
			}
			m_users.clear();
			
			// Remove npcs
			for (Npc npc : m_npcs)
			{
				npc.update(m_host.getServer().getDatabase());
			}
			m_npcs.clear();
			
			// Clear flatcontrollers
			m_flatControllers.clear();
		}
	}
	
	/**
	 * Broadcasts a string of characters to all users in the space. (network)
	 * 
	 * @param data The data string to broadcast.
	 */
	public void broadcast(String data)
	{
		m_host.getServer().getLogger().debug(this, "> " + data);
		synchronized (m_lock)
		{
			for (SpaceUser usr : m_users)
			{
				usr.getSession().getConnection().send(data);
			}
		}
	}
	
	/**
	 * Broadcasts a ServerMessage object to all users in the space.
	 * 
	 * @param msg The ServerMessage object to broadcast.
	 */
	public void broadcast(ServerMessage msg)
	{
		this.broadcast(msg.getResult());
	}
	
	public SpaceUser addSession(SpaceSession session)
	{
		// Create SpaceUser object
		SpaceUser usr = new SpaceUser(session);
		
		// Locate user at spawn position
		if (session.authenticatedTeleporter == 0)
		{
			usr.X = m_model.doorX;
			usr.Y = m_model.doorY;
			usr.Z = m_model.doorZ;
		}
		else
		{
			Item obj = this.getInteractor().getActiveObject(session.authenticatedTeleporter);
			if (obj == null)
			{
				return null;
			}
			else
			{
				// Spawn in teleporter
				usr.X = obj.X;
				usr.Y = obj.Y;
				usr.Z = obj.Z;
				session.authenticatedTeleporter = 0;
				
				// Broadcast activity
				this.getInteractor().broadcastTeleporterActivity(obj.ID, obj.definition.sprite, usr.getUserObject().name, false);
			}
		}
		
		// Determine privileges if in user flat
		if (this.getInfo().isUserFlat())
		{
			usr.isFlatAdmin = ((session.getUserObject().ID == this.getInfo().ownerID) || session.getUserObject().hasRight("is_any_flatadmin"));
			usr.isFlatController = (usr.isFlatAdmin || this.getInfo().superUsers || m_flatControllers.contains(session.getUserObject().ID));
			usr.refreshFlatPrivileges();
			
			if (session.getUserObject().hasRight("is_any_flatadmin"))
			{
				session.getConnection().send(new ServerMessage("YOUAREMOD"));
			}
		}
		
		// Set badge if available
		if (session.getUserObject().badge != null)
		{
			usr.addStatus("mod", usr.getUserObject().badge, 0, null, 0, 0);
		}
		
		// Add user to the collection
		m_active = true;
		synchronized (m_lock)
		{
			m_users.addElement(usr);
		}
		
		// Broadcast entry of user
		ServerMessage msg = new ServerMessage("USERS");
		msg.appendObject(usr);
		this.broadcast(msg);
		
		// Update user amount
		this.updateUserAmount();
		
		// Done!
		return usr;
	}
	
	public void removeSession(SpaceSession session)
	{
		// Get SpaceUser of this client
		SpaceUser usr = session.getUser();
		if (usr == null)
			return;
		
		// Remove user
		synchronized (m_lock)
		{
			m_users.removeElement(usr);
		}
		
		// Log activity
		this.updateUserAmount();
		
		// Are there remaining users in the space?
		if (this.userAmount() > 0)
		{
			// Broadcast 'remove from room'
			ServerMessage msg = new ServerMessage("LOGOUT");
			msg.appendNewArgument(usr.getUserObject().name);
			this.broadcast(msg);
			
			// Release map spot
			this.getInteractor().setUserMapTile(usr.X, usr.Y, false);
			
			// Update users amount
		}
		else
		{
			// Destroy instance if not a unit
			if (m_info.isUserFlat())
			{
				m_host.destroyFlatInstance(m_info.ID);
			}
			else
			{
				m_active = false;
			}
		}
	}
	
	public void reloadFlatControllers()
	{
		synchronized (m_lock)
		{
			m_flatControllers = m_host.getServer().getSpaceAdmin().getFlatControllers(m_info.ID);
		}
	}
	
	public boolean addFlatController(int userID)
	{
		// Not a flat controller already?
		synchronized (m_lock)
		{
			if (m_flatControllers.contains(userID))
			{
				return false;
			}
			else
			{
				// Add to collection
				m_flatControllers.add(userID);
				
				// Update in database
				m_host.getServer().getSpaceAdmin().addFlatController(m_info.ID, userID);
				
				// Added!
				return true;
			}
		}
	}
	
	public boolean removeFlatController(int userID)
	{
		// Really a flat controller?
		synchronized (m_lock)
		{
			if (!m_flatControllers.contains(userID))
			{
				return false;
			}
			else
			{
				// Remove from collection
				m_flatControllers.removeElement(userID);
				
				// Update in database
				m_host.getServer().getSpaceAdmin().removeFlatController(m_info.ID, userID);
				
				// Removed!
				return true;
			}
		}
	}
	
	public boolean isFlatController(int userID)
	{
		synchronized (m_lock)
		{
			return m_flatControllers.contains(userID);
		}
	}
	
	public void addNpc(Npc npc)
	{
		// Work out position
		if (!this.getInteractor().mapTileExists(npc.X, npc.Y))
		{
			npc.X = m_model.doorX;
			npc.Y = m_model.doorY;
		}
		npc.Z = m_interactor.getMapTileHeight(npc.X, npc.Y);
		
		// Add npc to collection
		npc.setSpace(this);
		synchronized (m_lock)
		{
			m_npcs.add(npc);
		}
		
		// Broadcast entry to room
		ServerMessage msg = new ServerMessage("USERS");
		msg.appendObject(npc);
		this.broadcast(msg);
		
		// Refresh
		npc.ensureUpdate(true);
		
		// Log
		m_host.getServer().getLogger().info(this, "loaded npc #" + npc.getUserObject().ID + " [" + npc.getUserObject().name + "]");
	}
	
	public void removeNpc(Npc npc)
	{
		// Remove from collection
		m_npcs.remove(npc);
		
		// Update to database
		npc.update(m_host.getServer().getDatabase());
		
		// Broadcast 'remove from room'
		ServerMessage msg = new ServerMessage("LOGOUT");
		msg.appendNewArgument(npc.getUserObject().name);
		this.broadcast(msg);
		
		// Release map spot
		this.getInteractor().setUserMapTile(npc.X, npc.Y, false);
	}
	
	public void loadNpcs()
	{
		// Load the npcs from the database
		Vector<Npc> npcs = m_host.getServer().getRoleplayMgr().getNpcs(m_info.ID);
		
		// Add & position them all
		synchronized (m_lock)
		{
			for (Npc npc : npcs)
			{
				this.addNpc(npc);
			}
		}
	}
	
	public void clearNpcs()
	{
		synchronized (m_lock)
		{
			for (Npc npc : m_npcs)
			{
				
			}
			
			// Be gone!
			m_npcs.clear();
		}
	}
	
	public void triggerNpcs(SpaceUser usr, String text, final int actionRadius)
	{
		// Parse the words
		String[] words = text.toLowerCase().trim().split(" ");
		if (words.length == 1)
		{
			return;
		}
		
		// Find the closest NPC
		Npc npc = null;
		double closest = 999;
		synchronized (m_lock)
		{
			for (Npc test : m_npcs)
			{
				if (!test.isServing())
				{
					double distance = Calculator2D.calculateDistance(usr.X, usr.Y, test.X, test.Y);
					if (distance <= actionRadius && distance < closest)
					{
						npc = test;
						closest = distance;
					}
				}
			}
		}
		
		// Any NPC near?
		if (npc != null)
		{
			npc.listenTo(usr, words);
		}
	}
	
	public void updateUserAmount()
	{
		this.getInfo().usersNow = (short)this.userAmount();
		this.getInfo().update(m_host.getServer().getDatabase());
	}
	
	/**
	 * Finds a SpaceUser with a given user ID and returns it.
	 * 
	 * @param userID The ID of the User.
	 * @return The SpaceUser if the user is found, null otherwise.
	 */
	public SpaceUser getUserByUserID(int userID)
	{
		synchronized (m_lock)
		{
			for (SpaceUser usr : m_users)
			{
				if (usr.getUserObject().ID == userID)
				{
					return usr;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Finds a SpaceUser with a given name and returns it.
	 * 
	 * @param name The name string of the user to find. Case sensitive.
	 * @return The SpaceUser if the user is found, null otherwise.
	 */
	public SpaceUser getUserByName(String name)
	{
		synchronized (m_lock)
		{
			for (SpaceUser usr : m_users)
			{
				if (usr.getUserObject().name.equals(name))
				{
					return usr;
				}
			}
		}
		
		return null;
	}
	
	public SpaceUser getUserOnTile(short X, short Y)
	{
		synchronized (m_lock)
		{
			for (Npc npc : m_npcs)
			{
				if (npc.X == X && npc.Y == Y)
				{
					return npc;
				}
			}
			
			for (SpaceUser usr : m_users)
			{
				if (usr.X == X && usr.Y == Y)
				{
					return usr;
				}
			}
		}
		
		return null;
	}
	
	public void chat(SpaceUser usr, String text)
	{
		if (usr != null)
		{
			// Animate user
			this.animateUserTalking(usr, text, true);
			
			// Angle heads
			//this.angleHeadsTo(usr.X, usr.Y, 3);
			
			// Broadcast chat to room
			ServerMessage msg = new ServerMessage("CHAT");
			msg.appendNewArgument(usr.getUserObject().name);
			msg.appendArgument(text);
			this.broadcast(msg);
		}
	}
	
	public void shout(SpaceUser usr, String text)
	{
		if (usr != null)
		{
			// Animate user
			this.animateUserTalking(usr, text, true);
			
			// Angle heads
			//this.angleHeadsTo(usr.X, usr.Y, 6);
			
			// Broadcast shout to room
			ServerMessage msg = new ServerMessage("SHOUT");
			msg.appendNewArgument(usr.getUserObject().name);
			msg.appendArgument(text);
			this.broadcast(msg);
		}
	}
	
	private void angleHeadsTo(short X, short Y, double range)
	{
		for (SpaceUser usr : m_users)
		{
			double distance = Calculator2D.calculateDistance(X, Y, usr.X, usr.Y);
			if (distance != 0 && distance <= range)
			{
				usr.angleHead(X, Y);
				usr.ensureUpdate(true);
			}
		}
	}
	
	public void animateUserTalking(SpaceUser usr, String text, boolean applyEmotes)
	{
		String[] words = text.split(" ");
		if (applyEmotes)
		{
			String emote = null;
			for (String word : words)
			{
				// Determine emote
				emote = ChatEmoteDetector.detectEmote(word);
				if (emote != null)
				{
					// Add emote status for 5 seconds
					usr.addStatus("gest", emote, 5, null, 0, 0);
					
					// If there was only one word, then this emote is that word and we are done now
					if (words.length == 1)
					{
						return;
					}
					
					// Stop checking for more emotes
					break;
				}
			}
		}
		
		// Determine length of talk animation duration
		int talkDuration = 1;
		if (words.length > 1)
		{
			talkDuration = words.length / 2;
		}
		if (talkDuration > 5)
		{
			// Truncate duration
			talkDuration = 5;
		}
		
		// Set talk animation status
		usr.addStatus("talk", null, talkDuration, null, 0, 0);
	}
	
	/**
	 * Notifies all flat controllers in the room that a user is ringing the doorbell to gain access to this user flat.
	 * 
	 * @param name The username of the user that rings the doorbell.
	 * @return True if atleast one flat controller is in the flat and has 'heard' the doorbell, False otherwise.
	 */
	public boolean ringDoorbell(String name)
	{
		boolean isReceived = false;
		synchronized (m_lock)
		{
			for (SpaceUser usr : m_users)
			{
				// Can this user answer doorbelling users?
				if (usr.isFlatController)
				{
					// Broadcast 'User %x rings doorbell. Let in?'
					ServerMessage msg = new ServerMessage("DOORBELL_RINGING");
					msg.appendNewArgument(name);
					usr.getSession().send(msg);
					
					// Atleast one user has heard the doorbell
					isReceived = true;
				}
			}
		}
		
		return isReceived;
	}
	
	public void moderationKick(byte issuerRole, String info)
	{
		// Maintain collection with targets
		Vector<SpaceUser> toKick = new Vector<SpaceUser>();
		
		// Check all the users and grab the targets
		synchronized (m_lock)
		{
			for (SpaceUser usr : m_users)
			{
				// Safe to kick?
				if (usr.getUserObject().role < issuerRole)
				{
					toKick.add(usr);
				}
			}
		}
		
		// Kick the targets
		for (SpaceUser usr : toKick)
		{
			usr.getSession().exit(info);
		}
	}
	
	public void showProgram(String program, String data)
	{
		ServerMessage msg = new ServerMessage("SHOWPROGRAM");
		msg.appendArgument(program);
		msg.appendArgument(data);
		
		this.broadcast(msg);
	}
	
	/**
	 * Returns the Space object of this space instance, holding all data about the space etc.
	 * 
	 * @return
	 */
	public Space getInfo()
	{
		return m_info;
	}
	
	/**
	 * Returns the SpaceModel of this space.
	 */
	public SpaceModel getModel()
	{
		return m_model;
	}
	
	/**
	 * Returns the SpaceServer that controls this space.
	 * 
	 * @return
	 */
	public SpaceServer getHost()
	{
		return m_host;
	}
	
	/**
	 * Returns the SpaceInstanceInteractor instance of this space instance.
	 */
	public SpaceInstanceInteractor getInteractor()
	{
		return m_interactor;
	}
	
	/**
	 * Returns the SpaceUsers.
	 */
	public Vector<SpaceUser> getUsers()
	{
		return m_users;
	}
	
	/**
	 * Returns the SpaceUser npcs.
	 */
	public Vector<Npc> getNpcs()
	{
		return m_npcs;
	}
	
	/**
	 * Returns the amount of users in this space instance.
	 */
	public int userAmount()
	{
		synchronized (m_lock)
		{
			return (m_users != null) ? m_users.size() : 0;
		}
	}
	
	public boolean isActive()
	{
		return m_active;
	}
	
	/**
	 * Returns True if this space instance cannot hold more users, False otherwise.
	 */
	public boolean isFull()
	{
		return this.userAmount() >= m_info.usersMax;
	}
	
	public Vector<Integer> getFlatControllers()
	{
		return m_flatControllers;
	}
	
	public String toString()
	{
		return "SpaceInstance #" + m_info.ID;
	}
}
