package com.blunk.mus;

/**
 * MusMessageHandler is hooked to a MusClient to handle incoming MusMessages.
 * 
 * @author Nillus
 */
public interface MusMessageHandler
{
	public void cleanup();
	public void handleIncomingMessage(MusClient client, MusMessage msg);
}
