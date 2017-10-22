package net.nillus.waldkorn.rp;

import java.util.Random;
import java.util.Vector;

import net.nillus.waldkorn.spaces.SpaceInstance;
import net.nillus.waldkorn.spaces.SpaceTile;
import net.nillus.waldkorn.spaces.SpaceUser;
import net.nillus.waldkorn.spaces.pathfinding.Calculator2D;
import net.nillus.waldkorn.storage.Database;
import net.nillus.waldkorn.users.User;

public class Npc extends SpaceUser
{
	private Random m_random;
	private long m_lastActionTime;
	private SpaceInstance m_space;
	public boolean freeRoam;
	private Vector<SpaceTile> m_tiles;
	private Vector<String> m_gossips;
	private Vector<NpcTrigger> m_triggers;
	private String m_lastGossip;
	
	// Ordering of items such as drinks
	private int m_customerID;
	private NpcTrigger m_order;
	
	public Npc(User info)
	{
		super(info);
		
		m_random = new Random();
		m_tiles = new Vector<SpaceTile>();
		m_gossips = new Vector<String>();
		m_triggers = new Vector<NpcTrigger>();
		m_lastActionTime = System.currentTimeMillis();
	}
	
	public void clear()
	{
		m_tiles.clear();
		m_gossips.clear();
		m_triggers.clear();
	}
	
	public void addWalkTile(short X, short Y)
	{
		this.addWalkTile(new SpaceTile(X, Y));
	}
	
	public void addWalkTile(SpaceTile tile)
	{
		m_tiles.add(tile);
	}
	
	public void addGossip(String text)
	{
		m_gossips.add(text);
	}
	
	public void addTrigger(NpcTrigger trigger)
	{
		m_triggers.add(trigger);
	}
	
	public boolean isServing()
	{
		return (m_customerID > 0);
	}
	
	public Random getRandom()
	{
		return m_random;
	}
	
	public SpaceInstance getSpace()
	{
		return m_space;
	}
	
	public void setSpace(SpaceInstance instance)
	{
		m_space = instance;
	}
	
	public void update(Database database)
	{
		try
		{
			database.executeUpdate("UPDATE rp_npcs SET health = " + this.getUserObject().health + ",spawn_x = " + this.X + ",spawn_y = " + this.Y + " WHERE id = " + this.getUserObject().ID + ";");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public boolean listenTo(SpaceUser usr, String[] words)
	{
		for (NpcTrigger t : m_triggers)
		{
			for (int i = words.length - 1; i >= 0; i--)
			{
				if (t.trigger(words[i]))
				{
					m_space.chat(this, t.getReply());
					if (t.isServeTrigger())
					{
						// Accept the order
						m_customerID = usr.getUserObject().ID;
						m_order = t;
						this.addStatus("carryd", t.getItem(), 0, null, 0, 0);
						
						// Find the closest tile
						SpaceTile dest = null;
						if (this.freeRoam)
						{
							int x = usr.X + this.generateRandomInt(-1, 1);
							int y = usr.Y + this.generateRandomInt(-1, 1);
							dest = new SpaceTile((short)x, (short)y);
						}
						else
						{
							double closest = 999;
							for (SpaceTile tile : m_tiles)
							{
								double distance = Calculator2D.calculateDistance(tile.X, tile.Y, usr.X, usr.Y);
								if (distance < closest)
								{
									dest = tile;
									closest = distance;
								}
							}
						}
						
						// Bring it to the customer?
						this.goalX = -1;
						if (dest != null)
						{
							this.goalX = dest.X;
							this.goalY = dest.Y;
						}
					}
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean pulse()
	{
		// Busy already?
		if (this.goalX != -1)
		{
			return false;
		}
		
		// Customer is king - any orders to be delivered?
		if (m_customerID > 0)
		{
			// Process the customer if he/she is still here
			SpaceUser customer = m_space.getUserByUserID(m_customerID);
			if (customer != null && Calculator2D.calculateDistance(this.X, this.Y, customer.X, customer.Y) <= 2)
			{
				// Look to each other
				this.lookTo(customer.X, customer.Y);
				customer.lookTo(this.X, this.Y);
				
				// Give the order to the customer
				customer.addStatus("carryd", m_order.getItem(), 120, "drink", 12, 1);
				
				// 'Here you are!'
				m_space.chat(this, m_order.getServedReply());
			}
			
			// Remove the order from the NPC
			m_customerID = 0;
			m_order = null;
			this.removeStatus("carryd");
			return true;
		}
		
		// Time for a random action?
		long secondsSinceLastAction = (System.currentTimeMillis() - m_lastActionTime) / 1000;
		if (m_random.nextInt(7) == 0 && secondsSinceLastAction > 10) 
		{
			int actionID = m_random.nextInt(5);
			switch (actionID)
			{
				case 1:
				{
					// Random gossip
					if (m_random.nextBoolean() && m_gossips.size() > 0)
					{
						String gossip = m_lastGossip;
						while (gossip == null || gossip.equals(m_lastGossip))
						{
							gossip = m_gossips.get(m_random.nextInt(m_gossips.size()));
						}
						m_lastGossip = gossip;
						m_space.chat(this, gossip);
					}
					break;
				}
					
				case 2:
				{
					// Move
					if (this.freeRoam)
					{
						this.goalX = (short)(this.X + this.generateRandomInt(-2, 2));
						this.goalY = (short)(this.Y + this.generateRandomInt(-2, 2));
					}
					else
					{
						SpaceTile next = m_tiles.get(m_random.nextInt(m_tiles.size()));
						this.goalX = next.X;
						this.goalY = next.Y;
					}
					break;
				}
					
				case 3:
				{
					// Look to somewhere
					int X = this.generateRandomInt(0, 50);
					int Y = this.generateRandomInt(0, 50);
					this.lookTo(X, Y);
					break;
				}
					
				case 4:
				{
					// Wave!
					this.wave();
					break;
				}
					
			}
			m_lastActionTime = System.currentTimeMillis();
			return true;
		}
		
		return false;
	}
	
	private final int generateRandomInt(int min, int max)
	{
		return m_random.nextInt(max - min + 1) + min;
	}
}
