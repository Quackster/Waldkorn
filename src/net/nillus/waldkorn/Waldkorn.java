package net.nillus.waldkorn;

public class Waldkorn
{
	public static void main(String[] args)
	{
		// Initialize configuration, test database etc
		Server server = new Server(args[0]);
		
		// Start loading everything etcc
		server.start();
	}
}
