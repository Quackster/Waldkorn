package net.nillus.waldkorn;

import java.util.Hashtable;
import java.util.Random;

import net.nillus.waldkorn.access.AccessControl;
import net.nillus.waldkorn.catalogue.Catalogue;
import net.nillus.waldkorn.items.ItemAdministration;
import net.nillus.waldkorn.net.NetworkConnection;
import net.nillus.waldkorn.net.NetworkConnectionListener;
import net.nillus.waldkorn.rp.RoleplayManager;
import net.nillus.waldkorn.sessions.SessionManager;
import net.nillus.waldkorn.spaces.SpaceAdministration;
import net.nillus.waldkorn.spaces.SpaceServer;
import net.nillus.waldkorn.storage.Database;
import net.nillus.waldkorn.users.UserRegister;
import net.nillus.waldkorn.util.PropertiesBox;

public class Server implements Runnable
{
	private long m_startTime;
	private Logger m_logger;
	
	private Random m_random;
	private PropertiesBox m_properties;
	private Thread m_thread;
	private Database m_database;
	
	private SessionManager m_sessionMgr;
	private Hashtable<Integer, NetworkConnectionListener> m_connectionListeners;
	private SpaceServer m_spaceServer;
	
	private AccessControl m_accessControl;
	private UserRegister m_userRegister;
	private SpaceAdministration m_spaceAdmin;
	private ItemAdministration m_itemAdmin;
	private Catalogue m_catalogue;
	private RoleplayManager m_roleplayMgr;
	
	public Server(String propertiesFile)
	{
		// Create instances of all the components
		m_startTime = System.currentTimeMillis();
		m_random = new Random();
		m_logger = new Logger("Server");
		
		m_logger.info(this, "preparing with configuration values from \"" + propertiesFile + "\"...");
		m_properties = new PropertiesBox();
		m_properties.load(propertiesFile);
		m_logger.info(this, "loaded " + m_properties.size() + " properties.");
		
		m_database = new Database(m_properties.get("server.dbprops", "database.properties"), m_logger);
		m_connectionListeners = new Hashtable<Integer, NetworkConnectionListener>();
		m_sessionMgr = new SessionManager(this);
		m_spaceServer = new SpaceServer(this);
		m_accessControl = new AccessControl(this);
		m_userRegister = new UserRegister(this);
		m_spaceAdmin = new SpaceAdministration(this);
		m_itemAdmin = new ItemAdministration(this);
		m_catalogue = new Catalogue(this);
		m_roleplayMgr = new RoleplayManager(this);
		
		// Prepare the thread
		m_thread = new Thread(this, this.toString() + " main thread");
		m_thread.setPriority(Thread.NORM_PRIORITY);
	}
	
	public boolean start()
	{
		m_logger.info(this, "starting...");
		
		// Test database
		m_logger.info(m_database, "testing database connectivity...");
		if(!m_database.getConnectionManager().test())
		{
			m_logger.info(m_database, "could not connect to database. Please consult error log");
			return false;
		}
		m_database.getConnectionManager().startMonitor();
		m_logger.info(m_database, "database OK");
		
		// Load user roles and MOTD
		m_accessControl.loadUserRoleRights();
		
		// Load item definitions
		m_itemAdmin.loadItemDefinitions();
		
		// Load space models and reset users inside counters
		m_spaceAdmin.loadSpaceModels();
		m_spaceAdmin.resetCurrentUserCounts();
		
		// Prepare master server
		this.addConnectionListener(m_properties.getInt("masterserver.port", 37120), NetworkConnection.MASTER_CONNECTION);
		
		// Prepare space server
		m_spaceServer.initFlatListener();
		m_spaceServer.loadUnits();
		m_spaceServer.startThreads();
		
		// Load catalogue articles
		m_catalogue.loadArticles();
		
		// Start the connection listeners
		for(NetworkConnectionListener listener : m_connectionListeners.values())
		{
			m_logger.info(this, "starting " + listener);
			listener.start();
		}
		m_logger.info(this, "started connection listeners");
		
		// Yay, done
		m_logger.info(this, "startup complete");
		return true;
	}
	
	public void addConnectionListener(int port, byte type)
	{
		NetworkConnectionListener listener = new NetworkConnectionListener(port, type, m_sessionMgr);
		m_connectionListeners.put(port, listener);
		
		m_logger.info(this, "installed " + listener.toString());
	}
	

	@Override
	public void run()
	{
		// Perform logic every 10 seconds etc, for things like disconnectin clients, perform backups/cronjobs etc?
	}

	public long getStartTime()
	{
		return m_startTime;
	}
	
	public Logger getLogger()
	{
		return m_logger;
	}
	
	public Random getRandom()
	{
		return m_random;
	}
	
	public PropertiesBox getProperties()
	{
		return m_properties;
	}
	
	public Database getDatabase()
	{
		return m_database;
	}

	public NetworkConnectionListener getConnectionListener(int port)
	{
		return m_connectionListeners.get(port);
	}
	
	public SessionManager getSessionMgr()
	{
		return m_sessionMgr;
	}
	
	public SpaceServer getSpaceServer()
	{
		return m_spaceServer;
	}
	
	public AccessControl getAccessControl()
	{
		return m_accessControl;
	}
	
	public UserRegister getUserRegister()
	{
		return m_userRegister;
	}
	
	public SpaceAdministration getSpaceAdmin()
	{
		return m_spaceAdmin;
	}
	
	public ItemAdministration getItemAdmin()
	{
		return m_itemAdmin;
	}
	
	public Catalogue getCatalogue()
	{
		return m_catalogue;
	}
	
	public RoleplayManager getRoleplayMgr()
	{
		return m_roleplayMgr;
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName();
	}

}
