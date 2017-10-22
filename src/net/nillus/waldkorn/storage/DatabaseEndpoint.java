package net.nillus.waldkorn.storage;

public class DatabaseEndpoint
{
	public final String username;
	public final String password;
	public final String url;
	
	public DatabaseEndpoint(String username, String password, String url)
	{
		this.username = username;
		this.password = password;
		this.url = url;
	}
}
