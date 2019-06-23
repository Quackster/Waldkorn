package net.nillus.waldkorn;

public abstract class ServerComponent
{
	protected MasterServer m_server;
	
	public ServerComponent(MasterServer server)
	{
		m_server = server;
	}
	
	public MasterServer getServer()
	{
		return m_server;
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName();
	}
}
