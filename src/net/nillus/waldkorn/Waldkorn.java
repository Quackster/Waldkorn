package net.nillus.waldkorn;

public class Waldkorn
{
	public static MasterServer server;

	public static void main(String[] args)
	{
		// Initialize configuration, test database etc
		server = new MasterServer(args[0]);
		
		// Start loading everything etcc
		server.start();
	}

	public static MasterServer getServer() {
		return server;
	}
}
