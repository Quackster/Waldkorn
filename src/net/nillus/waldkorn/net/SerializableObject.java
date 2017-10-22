package net.nillus.waldkorn.net;


/**
 * SerializeableObjects are objects that can have their data fully carried over to a ServerMessage.
 * @author Nillus
 *
 */
public interface SerializableObject
{
	/**
	 * Serializes the object to a ServerMessage.
	 * @param msg The ServerMessage object to serialize this SerializableObject to.
	 */
	public void serialize(ServerMessage msg);
}
