package net.nillus.waldkorn.spaces;

import java.util.Vector;

import net.nillus.waldkorn.net.SerializableObject;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.spaces.pathfinding.Calculator2D;
import net.nillus.waldkorn.spaces.pathfinding.PathfinderNode;
import net.nillus.waldkorn.users.User;

/**
 * Represents a user or npc in a space instance.
 * 
 * @author Nillus
 */
public class SpaceUser implements SerializableObject
{
	private static final int MAX_SIMULTANEOUS_STATUSES = 5;
	
	/**
	 * The User holding information about this user.
	 */
	private User m_userObject;
	
	/**
	 * The SpaceSession of this user.
	 */
	private SpaceSession m_session;
	
	/**
	 * The current X position of this space user in the space.
	 */
	public short X;
	/**
	 * The current Y position of this space user in the space.
	 */
	public short Y;
	/**
	 * The current height position of this space user in the space.
	 */
	public float Z;
	
	/**
	 * The current rotation (direction) of this space user's head.
	 */
	public byte headRotation;
	/**
	 * The current rotation (direction) of this space user's body.
	 */
	public byte bodyRotation;
	
	/**
	 * True if this user currently can not select a new goal tile for moving.
	 */
	public boolean moveLock = false;
	/**
	 * The X position of the goal tile this user walks to. -1 if not walking.
	 */
	public short goalX;
	/**
	 * The Y position of the goal tile this user walks to.
	 */
	public short goalY;
	public Vector<PathfinderNode> path;
	/**
	 * True if this space user can override current tiles (walkable or not) to get to the goal.
	 */
	public boolean overrideNextTile = false;
	public byte movementRetries = 0;
	
	private SpaceUserStatus[] m_statuses;
	public boolean m_requiresUpdate;
	
	/**
	 * User flat only. True if this SpaceUser is a controller in the flat it currently is in. Flat controllers can move furniture around (no picking up!) and can kick other users from the flat.
	 */
	public boolean isFlatController;
	/**
	 * User flat only. True if this SpaceUser is the owner of the flat it currently is in. Flat owners can do everything flat controllers can, but including picking up furniture etc.
	 */
	public boolean isFlatAdmin;
	
	/**
	 * True if this SpaceUser is invisible for other clients.
	 */
	public boolean isInvisible;
	/**
	 * True if this SpaceUser walks reversed.
	 */
	public boolean isReverseWalk;
	
	/**
	 * True if this SpaceUser is a npc.
	 */
	private boolean m_isBot;
	
	public SpaceUser(User info)
	{
		m_isBot = true;
		m_session = null;
		m_userObject = info;
		
		this.goalX = -1;
		this.path = new Vector<PathfinderNode>();
		m_statuses = new SpaceUserStatus[SpaceUser.MAX_SIMULTANEOUS_STATUSES];
	}
	
	public SpaceUser(SpaceSession session)
	{
		m_isBot = false;
		m_session = session;
		m_userObject = session.getUserObject();
		
		this.goalX = -1;
		this.path = new Vector<PathfinderNode>();
		m_statuses = new SpaceUserStatus[SpaceUser.MAX_SIMULTANEOUS_STATUSES];
	}
	
	/**
	 * Determines if this SpaceUser requires it's status broadcasted to space instance because it's updated.
	 * 
	 * @return True, False
	 */
	public boolean requiresUpdate()
	{
		if (m_requiresUpdate)
		{
			// Requires update no matter what the statuses say
			return true;
		}
		else
		{
			for (int i = 0; i < SpaceUser.MAX_SIMULTANEOUS_STATUSES; i++)
			{
				if (m_statuses[i] != null && m_statuses[i].isUpdated())
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void ensureUpdate(boolean state)
	{
		m_requiresUpdate = state;
	}
	
	/**
	 * Adds a new SpaceUserStatus with given data. Old status with this name is removed. Omit data by supplying null, not an empty string.
	 * 
	 * @param name The name of the status.
	 * @param data The data of the status.
	 * @param lifeTimeSeconds The total amount of seconds this status lasts.
	 * @param action The action of the status, will be flipped with name etc.
	 * @param actionSwitchSeconds The total amount of seconds this action flips with the name.
	 * @param actionLengthSeconds The total amount of seconds that the action lasts before it flips back.
	 */
	public boolean addStatus(String name, String data, int lifeTimeSeconds, String action, int actionSwitchSeconds, int actionLengthSeconds)
	{
		// Remove old status
		removeStatus(name);
		
		// Allocate status
		for (int i = 0; i < SpaceUser.MAX_SIMULTANEOUS_STATUSES; i++)
		{
			if (m_statuses[i] == null)
			{
				m_statuses[i] = new SpaceUserStatus(name, data, lifeTimeSeconds, action, actionSwitchSeconds, actionLengthSeconds);
				m_requiresUpdate = true;
				return true;
			}
		}
		
		// Could not allocate
		return false;
	}
	
	/**
	 * Attempts to remove a status with a given name.
	 * 
	 * @param name The name of the status to remove. Case sensitive.
	 * @return True if status was removed, false if it was not found.
	 */
	public boolean removeStatus(String name)
	{
		for (int i = 0; i < SpaceUser.MAX_SIMULTANEOUS_STATUSES; i++)
		{
			if (m_statuses[i] != null && m_statuses[i].name.equals(name))
			{
				m_statuses[i] = null;
				m_requiresUpdate = true;
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if this user has a status with a given name.
	 * 
	 * @param name The name of the status to check. Case sensitive.
	 * @return True if this user has this status, False otherwise.
	 */
	public boolean hasStatus(String name)
	{
		for (int i = 0; i < SpaceUser.MAX_SIMULTANEOUS_STATUSES; i++)
		{
			if (m_statuses[i] != null && m_statuses[i].name.equals(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void removeInteractiveStatuses()
	{
		this.removeStatus("sit");
		this.removeStatus("lay");
	}
	
	/**
	 * Clears and resets this SpaceUsers path. (for walking in the space)
	 */
	public void clearPath()
	{
		this.goalX = -1;
		this.goalY = 0;
		this.path.clear();
	}
	
	public void wave()
	{
		if(!this.hasStatus("lay"))
		{
			this.addStatus("wave", null, 2, null, 0, 0);
		}
	}
	
	public void lookTo(int X, int Y)
	{
		// Cannot rotate while sitting or laying
		if(!(this.hasStatus("sit") || this.hasStatus("lay")))
		{
			byte rotation = Calculator2D.calculateHumanDirection(this.X, this.Y, X, Y);
			if(rotation != this.headRotation) this.ensureUpdate(true);
			this.headRotation = rotation;
			this.bodyRotation = rotation;
		}
	}
	
	public void angleHead(short toX, short toY)
	{
		if(this.bodyRotation == 2)
		{
			if(this.X <= toX && this.Y < toY)
			{
				this.headRotation = 3;
			}
			else if(this.X <= toX && this.Y > toY)
			{
				this.headRotation = 5;
			}
			else if(this.X < toX && this.Y == toY)
			{
				this.headRotation = 2;
			}
		}
		else if(this.bodyRotation == 4)
		{
			if(this.X > toX && this.Y <= toY)
			{
				this.headRotation = 5;
			}
			else if(this.X < toX && this.Y <= toY)
			{
				this.headRotation = 3;
			}
			else if(this.X == toX && this.Y < toY)
			{
				this.headRotation = 4;
			}
		}
		else if(this.bodyRotation == 6)
		{
			if(this.X >= toX && this.Y > toY)
			{
				this.headRotation = 5;
			}
			else if(this.X >= toX && this.Y < toY)
			{
				this.headRotation = 7;
			}
			else if(this.X > toX && this.Y == toY)
			{
				this.headRotation = 6;
			}
		}
		else if(this.bodyRotation == 0)
		{
			if(this.X > toX && this.Y >= toY)
			{
				this.headRotation = 9;
			}
			else if(this.X < toX && this.Y >= toY)
			{
				this.headRotation = 1;
			}
			else if(this.X == toX && this.Y > toY)
			{
				this.headRotation = 0;
			}
		}
	}
		
		/*
		 *   X = Client(a).PosX
        this.Y = Client(a).PosY
        tmpX = Client(Index).PosX
        toY = Client(Index).PosY
        If bodyRotation = "2" Then
            If X <= tmpX And this.Y < toY Then
                Client(a).Look = "3,2"
            ElseIf X <= tmpX And this.Y > toY Then
                Client(a).Look = "5,2"
            ElseIf X < tmpX And this.Y = toY Then
                Client(a).Look = "2,2"
            End If
        ElseIf bodyRotation = "4" Then
            If X > tmpX And this.Y <= toY Then
                Client(a).Look = "5,4"
            ElseIf X < tmpX And this.Y <= toY Then
                Client(a).Look = "3,4"
            ElseIf X = tmpX And this.Y < toY Then
                Client(a).Look = "4,4"
            End If
        ElseIf bodyRotation = "6" Then
            If X >= tmpX And this.Y > toY Then
                Client(a).Look = "5,6"
            ElseIf X >= tmpX And this.Y < toY Then
                Client(a).Look = "7,6"
            ElseIf X > tmpX And this.Y = toY Then
                Client(a).Look = "6,6"
            End If
        ElseIf bodyRotation = "0" Then
            If X > tmpX And this.Y >= toY Then
                Client(a).Look = "9,0"
            ElseIf X < tmpX And this.Y >= toY Then
                Client(a).Look = "1,0"
            ElseIf X = tmpX And Y > toY Then
                Client(a).Look = "0,0"
            End If
        Else
            GoTo nxtb
        End If
	}
	
	/**
	 * Refreshes this SpaceUser's (real client) privileges in the flat, by setting the appropriate 'flatctrl' status and sending the appropriate network messages.
	 */
	public void refreshFlatPrivileges()
	{
		// Remove old status
		this.removeStatus("flatctrl");
		String flatControlValue = null;
		
		// Handle messaging
		if (this.isFlatController)
		{
			m_session.send(new ServerMessage("YOUARECONTROLLER"));
		}
		if (this.isFlatAdmin)
		{
			m_session.send(new ServerMessage("YOUAREOWNER"));
			flatControlValue = "useradmin";
		}
		
		// Add 'flatctrl' status
		if (this.isFlatController || this.isFlatAdmin)
		{
			this.addStatus("flatctrl", flatControlValue, 0, null, 0, 0);
		}
	}
	
	@Override
	public void serialize(ServerMessage msg)
	{
		msg.appendNewArgument(this.getUserObject().name);
		msg.appendArgument(this.getUserObject().figure);
		msg.appendArgument(Short.toString(this.X));
		msg.appendArgument(Short.toString(this.Y));
		msg.appendArgument(Integer.toString((int)this.Z));
		msg.appendArgument("Health: " + this.getUserObject().health + "%\n" + this.getUserObject().motto);
		
		// Swimming clothes on?
		if (m_session != null && m_session.getSpace().getModel().hasSwimmingPool)
		{
			msg.appendArgument(this.getUserObject().poolFigure);
		}
	}
	
	/**
	 * Updates this users temporarily statuses and appends the current status to a given ServerMessage.
	 * 
	 * @param msg The ServerMessage to write (append) the data to.
	 */
	public String getStatusString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append((char)13);
		
		sb.append(this.getUserObject().name);
		sb.append(' ');
		
		sb.append(this.X);
		sb.append(',');
		
		sb.append(this.Y);
		sb.append(',');
		
		sb.append((int)this.Z);
		sb.append(',');
		
		sb.append(this.headRotation);
		sb.append(',');
		
		sb.append(this.bodyRotation);
		sb.append('/');
		
		for (int i = 0; i < SpaceUser.MAX_SIMULTANEOUS_STATUSES; i++)
		{
			SpaceUserStatus status = m_statuses[i];
			if (status != null)
			{
				if (status.checkStatus())
				{
					sb.append(status.name);
					if (status.data != null)
					{
						sb.append(' ');
						sb.append(status.data);
					}
					sb.append('/');
				}
				else
				{
					m_statuses[i] = null;
				}
			}
		}
		
		return sb.toString();
	}
	
	public User getUserObject()
	{
		return m_userObject;
	}
	
	public SpaceSession getSession()
	{
		return m_session;
	}
	
	public boolean isBot()
	{
		return m_isBot;
	}
}
