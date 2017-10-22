package net.nillus.waldkorn;

public abstract class ServerComponent
{
	protected Server m_server;
	
	public ServerComponent(Server server)
	{
		m_server = server;
	}
	
	public Server getServer()
	{
		return m_server;
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName();
	}
}
