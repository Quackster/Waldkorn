package net.nillus.waldkorn.spaces;

import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.items.ItemInventory;
import net.nillus.waldkorn.items.ItemTrader;
import net.nillus.waldkorn.net.NetworkConnection;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.rp.Npc;
import net.nillus.waldkorn.sessions.Session;
import net.nillus.waldkorn.sessions.commands.spaces.ADDITEM;
import net.nillus.waldkorn.sessions.commands.spaces.ADDSTRIPITEM;
import net.nillus.waldkorn.sessions.commands.spaces.ASSIGNRIGHTS;
import net.nillus.waldkorn.sessions.commands.spaces.CHAT;
import net.nillus.waldkorn.sessions.commands.spaces.CLOSE_UIMAKOPPI;
import net.nillus.waldkorn.sessions.commands.spaces.CarryDrink;
import net.nillus.waldkorn.sessions.commands.spaces.Dance;
import net.nillus.waldkorn.sessions.commands.spaces.FLATPROPERTYBYITEM;
import net.nillus.waldkorn.sessions.commands.spaces.GETSTRIP;
import net.nillus.waldkorn.sessions.commands.spaces.GOAWAY;
import net.nillus.waldkorn.sessions.commands.spaces.GOVIADOOR;
import net.nillus.waldkorn.sessions.commands.spaces.IntoDoor;
import net.nillus.waldkorn.sessions.commands.spaces.JUMPPERF;
import net.nillus.waldkorn.sessions.commands.spaces.JUMPSTART;
import net.nillus.waldkorn.sessions.commands.spaces.KILLUSER;
import net.nillus.waldkorn.sessions.commands.spaces.LETUSERIN;
import net.nillus.waldkorn.sessions.commands.spaces.LOOKTO;
import net.nillus.waldkorn.sessions.commands.spaces.MOVESTUFF;
import net.nillus.waldkorn.sessions.commands.spaces.Move;
import net.nillus.waldkorn.sessions.commands.spaces.PLACEITEMFROMSTRIP;
import net.nillus.waldkorn.sessions.commands.spaces.PLACESTUFFFROMSTRIP;
import net.nillus.waldkorn.sessions.commands.spaces.REMOVEITEM;
import net.nillus.waldkorn.sessions.commands.spaces.REMOVERIGHTS;
import net.nillus.waldkorn.sessions.commands.spaces.REMOVESTUFF;
import net.nillus.waldkorn.sessions.commands.spaces.SETITEMDATA;
import net.nillus.waldkorn.sessions.commands.spaces.SETSTUFFDATA;
import net.nillus.waldkorn.sessions.commands.spaces.SHOUT;
import net.nillus.waldkorn.sessions.commands.spaces.SPLASH_POSITION;
import net.nillus.waldkorn.sessions.commands.spaces.STOP;
import net.nillus.waldkorn.sessions.commands.spaces.Sign;
import net.nillus.waldkorn.sessions.commands.spaces.TRADE_ACCEPT;
import net.nillus.waldkorn.sessions.commands.spaces.TRADE_ADDITEM;
import net.nillus.waldkorn.sessions.commands.spaces.TRADE_CLOSE;
import net.nillus.waldkorn.sessions.commands.spaces.TRADE_OPEN;
import net.nillus.waldkorn.sessions.commands.spaces.TRADE_UNACCEPT;
import net.nillus.waldkorn.sessions.commands.spaces.UPDATE;
import net.nillus.waldkorn.sessions.commands.spaces.WHISPER;
import net.nillus.waldkorn.users.User;

public class SpaceSession extends Session
{
	// Space variables (when entered space)
	private SpaceInstance m_spaceInstance;
	private SpaceUser m_spaceUser;
	private ItemInventory m_inventory;
	private ItemTrader m_trader;
	
	// Variables prior to entering the space
	public int authenticatedFlat;
	public int authenticatedTeleporter;
	public boolean waitingForFlatDoorbell;
	
	public SpaceSession(long sessionID, NetworkConnection connection)
	{
		super(sessionID, connection);
		
		// Listen for entering flat with teleporter ('door')
		this.getCmdHandlerMgr().add(new GOVIADOOR());
	}
	
	public void enter(SpaceInstance instance)
	{
		m_spaceInstance = instance;
		ServerMessage msg = new ServerMessage();
		
		// Send the room model
		msg.set("ROOM_READY");
		msg.appendArgument(instance.getModel().type);
		msg.appendArgument(Integer.toString(instance.getInfo().ID));
		send(msg);
		
		// Send the heightmap
		msg.set("HEIGHTMAP");
		msg.appendArgument(instance.getInteractor().generateHeightMapString());
		send(msg);
		
		// Send the passive objects ('world objects')
		msg.set("OBJECTS");
		msg.appendArgument("WORLD");
		msg.appendArgument("0");
		msg.appendArgument(instance.getModel().type);
		if (!instance.getInfo().isUserFlat())
		{
			for (Item obj : instance.getInteractor().getPassiveObjects())
			{
				if (!obj.definition.behaviour.isInvisible)
				{
					msg.appendObject(obj);
				}
			}
		}
		send(msg);
		
		// Send the active objects (furniture that is movable etc)
		msg.set("ACTIVE_OBJECTS");
		for (Item obj : instance.getInteractor().getActiveObjects())
		{
			msg.appendObject(obj);
		}
		send(msg);
		
		// Send flat properties and wallitems if it's a flat
		if (instance.getInfo().isUserFlat())
		{
			msg.set("FLATPROPERTY");
			msg.appendNewArgument("wallpaper");
			msg.appendPartArgument(Integer.toString(instance.getInfo().wallpaper));
			send(msg);
			
			msg.set("FLATPROPERTY");
			msg.appendNewArgument("floor");
			msg.appendPartArgument(Integer.toString(instance.getInfo().floor));
			send(msg);
			
			msg.set("ITEMS");
			////msg.set("ITEMS " + (char)13 + "0000001;poster;null;leftwall 2.3,3.1,10531/1" + (char)13 + "1\\__0000002;poster;null;leftwall 1.3,3.1,10500/1" + (char)13 + "1");
			//msg.set("ITEMS " + (char)13 + "1\\__0000002;poster;null;leftwall 1.3,3.1,10500/1");
			
			for (Item item : instance.getInteractor().getWallItems())
			{
				msg.appendObject(item);
			}
			send(msg);
		}
		
		// Send the users (npcs and players)
		msg.set("USERS");
		for (Npc npc : instance.getNpcs())
		{
			msg.appendObject(npc);
		}
		for (SpaceUser usr : instance.getUsers())
		{
			if (!usr.isInvisible)
			{
				msg.appendObject(usr);
			}
		}
		send(msg);
		
		// Locate user in door of space and broadcast entry
		m_spaceUser = instance.addSession(this);
		
		// Add the handlers
		this.getCmdHandlerMgr().add(new GOAWAY());
		this.getCmdHandlerMgr().add(new CHAT());
		this.getCmdHandlerMgr().add(new SHOUT());
		this.getCmdHandlerMgr().add(new WHISPER());
		this.getCmdHandlerMgr().add(new Move());
		this.getCmdHandlerMgr().add(new Dance());
		this.getCmdHandlerMgr().add(new STOP());
		this.getCmdHandlerMgr().add(new LOOKTO());
		this.getCmdHandlerMgr().add(new CarryDrink());
		
		// Swimming Pool handlers
		if (instance.getModel().hasSwimmingPool)
		{
			this.getCmdHandlerMgr().add(new Sign());
			if (instance.getModel().type.equals("pool_a"))
			{
				this.getCmdHandlerMgr().add(new CLOSE_UIMAKOPPI());
				this.getCmdHandlerMgr().add(new UPDATE());
			}
			else
			{
				this.getCmdHandlerMgr().add(new JUMPSTART());
				this.getCmdHandlerMgr().add(new JUMPPERF());
				this.getCmdHandlerMgr().add(new SPLASH_POSITION());
			}
		}
		
		// Flat handlers
		if (instance.getInfo().isUserFlat())
		{
			// Items
			this.getCmdHandlerMgr().add(new GETSTRIP());
			this.getCmdHandlerMgr().add(new PLACESTUFFFROMSTRIP());
			this.getCmdHandlerMgr().add(new PLACEITEMFROMSTRIP());
			this.getCmdHandlerMgr().add(new ADDITEM());
			this.getCmdHandlerMgr().add(new ADDSTRIPITEM());
			this.getCmdHandlerMgr().add(new MOVESTUFF());
			this.getCmdHandlerMgr().add(new SETSTUFFDATA());
			this.getCmdHandlerMgr().add(new SETITEMDATA());
			this.getCmdHandlerMgr().add(new REMOVEITEM());
			this.getCmdHandlerMgr().add(new REMOVESTUFF());
			this.getCmdHandlerMgr().add(new FLATPROPERTYBYITEM());
			
			// Flat controller actions
			this.getCmdHandlerMgr().add(new KILLUSER());
			this.getCmdHandlerMgr().add(new ASSIGNRIGHTS());
			this.getCmdHandlerMgr().add(new REMOVERIGHTS());
			this.getCmdHandlerMgr().add(new LETUSERIN());
			
			// Trading
			this.getCmdHandlerMgr().add(new TRADE_OPEN());
			this.getCmdHandlerMgr().add(new TRADE_CLOSE());
			this.getCmdHandlerMgr().add(new TRADE_ACCEPT());
			this.getCmdHandlerMgr().add(new TRADE_UNACCEPT());
			this.getCmdHandlerMgr().add(new TRADE_ADDITEM());
			
			// Teleporting
			this.getCmdHandlerMgr().add(new IntoDoor());
		}
		
		// Send statuses of all users (npcs and players) to this client
		msg.set("STATUS");
		for (Npc npc : instance.getNpcs())
		{
			msg.append(npc.getStatusString());
		}
		for (SpaceUser usr : instance.getUsers())
		{
			if (!usr.isInvisible)
			{
				msg.append(usr.getStatusString());
			}
		}
		send(msg);
		
		// Install other components when entering a flat
		if (instance.getInfo().isUserFlat())
		{
			m_inventory = new ItemInventory(this);
			m_trader = new ItemTrader(this);
		}
	}
	
	public void exit(String msg)
	{
		// Entered the space?
		if (m_spaceInstance != null)
		{
			// Send moderation warning etc (over the space connection)
			if (msg != null)
			{
				this.systemMsg(msg);
			}
			
			// Remove user
			m_spaceInstance.removeSession(this);
			
			// Disconnect
			this.getConnection().disconnect();
		}
	}
	
	public void killTrade()
	{
		m_trader.abort();
	}
	
	public void systemMsg(String text)
	{
		ServerMessage msg = new ServerMessage("WHISPER");
		msg.appendNewArgument(this.getUserObject().name);
		msg.appendArgument(text);
		this.send(msg);
	}
	
	public boolean inSpace()
	{
		return m_spaceInstance != null;
	}
	
	public User getUserObject()
	{
		return this.getMasterSession().getUserObject();
	}
	
	public SpaceInstance getSpace()
	{
		return m_spaceInstance;
	}
	
	public SpaceUser getUser()
	{
		return m_spaceUser;
	}
	
	public ItemInventory getInventory()
	{
		m_inventory.loadStripItems();
		return m_inventory;
	}
	
	public ItemTrader getTrader()
	{
		return m_trader;
	}
}
