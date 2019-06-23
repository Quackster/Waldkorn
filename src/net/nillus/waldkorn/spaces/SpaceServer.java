package net.nillus.waldkorn.spaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

import net.nillus.waldkorn.MasterServer;
import net.nillus.waldkorn.ServerComponent;
import net.nillus.waldkorn.net.NetworkConnection;

public class SpaceServer extends ServerComponent implements Runnable
{
	private LinkedHashMap<String, SpaceInstance> m_units;
	private LinkedHashMap<Integer, SpaceInstance> m_flats;
	private ArrayList<Space> m_popularFlats;
	
	private boolean m_alive;
	private Thread m_thread;
	
	public SpaceServer(MasterServer server)
	{
		super(server);
		
		m_units = new LinkedHashMap<String, SpaceInstance>();
		m_flats = new LinkedHashMap<Integer, SpaceInstance>();
		m_popularFlats = new ArrayList<Space>(60);
		
		// Load server host & port
		Space.host = server.getProperties().get("spaceserver.host", "localhost");
		System.out.println(Space.host);
		Space.flatPort = server.getProperties().getInt("spaceserver.flatport", 38120);
	}
	
	public void initFlatListener()
	{
		m_server.addConnectionListener(Space.flatPort, NetworkConnection.SPACE_CONNECTION);
	}
	
	public void loadUnits()
	{
		m_server.getLogger().info(this, "preparing units...");
		
		int portNumber = 37125;
		ArrayList<Integer> unitIDs = m_server.getSpaceAdmin().getUnits();
		for (Integer unitID : unitIDs)
		{
			// Try to create instance
			SpaceInstance instance = this.createSpaceInstance(unitID);
			if (instance == null)
			{
				continue;
			}
			
			// Assign the next port number
			instance.getInfo().port = portNumber++;
			
			// Add a connection listener dedicated to this unit
			m_server.addConnectionListener(instance.getInfo().port, NetworkConnection.SPACE_CONNECTION);
			
			// Yay, it's ready!
			m_units.put(instance.getInfo().name, instance);
			m_server.getLogger().info(this, "created unit " + instance + " on TCP port " + portNumber);
		}
		
		m_server.getLogger().info(this, "prepared " + m_units.size() + " units.");
	}
	
	public void startThreads()
	{
		m_alive = true;
		m_thread = new Thread(this, "SpaceServer thread");
		m_thread.setPriority(Thread.MAX_PRIORITY - 1);
		m_thread.start();
	}
	
	private SpaceInstance createSpaceInstance(int spaceID)
	{
		// Get info on the Space
		Space info = m_server.getSpaceAdmin().getSpaceInfo(spaceID);
		if (info == null)
		{
			m_server.getLogger().error(this, "cannot create SpaceInstance of Space #" + spaceID + ", space doesn't exist!", null);
			return null;
		}
		
		// Get model of the Space
		SpaceModel model = m_server.getSpaceAdmin().getSpaceModel(info.model);
		if (model == null)
		{
			m_server.getLogger().error(this, "cannot create SpaceInstance of Space #" + spaceID + ", there is no SpaceModel '" + info.model + "'", null);
			return null;
		}
		
		// Create instance and prepare the interactor
		SpaceInstance instance = new SpaceInstance(info, model, this);
		instance.getInteractor().loadItems();
		instance.getInteractor().generateFloorMap(true);
		instance.reloadFlatControllers();
		instance.loadNpcs();
		return instance;
	}
	
	public SpaceInstance getUnitInstance(String name)
	{
		return m_units.get(name);
	}
	
	public SpaceInstance getUnitInstance(int portNumber)
	{
		for (SpaceInstance unit : m_units.values())
		{
			if (unit.getInfo().port == portNumber)
			{
				return unit;
			}
		}
		
		return null;
	}
	
	public Collection<SpaceInstance> getUnitInstances()
	{
		return m_units.values();
	}
	
	public SpaceInstance getFlatInstance(int spaceID, boolean allowCreation)
	{
		SpaceInstance instance;
		synchronized (m_flats)
		{
			instance = m_flats.get(spaceID);
			if (instance == null && allowCreation)
			{
				instance = this.createSpaceInstance(spaceID);
				if (instance != null)
				{
					m_flats.put(spaceID, instance);
					
					// Remove old one
					Space toremove = null;
					for (Space popular : m_popularFlats)
					{
						if (popular.ID == instance.getInfo().ID)
						{
							toremove = popular;
							break;
						}
					}
					if (toremove != null)
						m_popularFlats.remove(toremove);
					m_popularFlats.add(instance.getInfo());
				}
			}
		}
		
		return instance;
	}
	
	public void destroyFlatInstance(int spaceID)
	{
		synchronized (m_flats)
		{
			SpaceInstance instance = m_flats.remove(spaceID);
			if (instance != null)
			{
				instance.destroy();
				m_server.getLogger().info(this, "destroyed flat instance " + instance);
			}
		}
	}
	
	public void updatePopularFlats()
	{
		Collections.sort(m_popularFlats);
	}
	
	public ArrayList<Space> getPopularFlats()
	{
		this.updatePopularFlats();
		return m_popularFlats;
	}
	
	public void run()
	{
		while (m_alive)
		{
			// Measure the start time
			long start = System.nanoTime();
			
			// Pulse the units
			synchronized (m_units)
			{
				for (SpaceInstance unit : m_units.values())
				{
					if(unit.isActive())
					{
						unit.getInteractor().pulse();
					}
				}
			}
			
			// Pulse the flats
			synchronized (m_flats)
			{
				for (SpaceInstance flat : m_flats.values())
				{
					if(flat.isActive())
					{
						flat.getInteractor().pulse();
					}
				}
			}
			
			// Ensure there is x ms between each pulse
			double sleepTime = (System.nanoTime() - start);
			sleepTime /= 1000000;
			sleepTime = (470 - sleepTime);
			if (sleepTime > 0.0)
			{
				try
				{
					Thread.sleep((long)sleepTime);
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName();
	}
}
