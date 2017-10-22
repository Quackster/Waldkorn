package net.nillus.waldkorn.items;

/**
 * A set of booleans representing the behaviour of a item definition.
 * 
 * @author Nillus
 */
public class ItemBehaviour
{
	/**
	 * True if this item is 'stuff'. (a 'touchable' substantial non-flat object like a table or a chair)
	 */
	public boolean STUFF;
	/**
	 * True if this item is 'item'. (a flat non-STUFF item, such as a poster, post.it, photo etc)
	 */
	public boolean ITEM;
	
	/**
	 * True if this item can be placed on the floor of a space.
	 */
	public boolean onFloor;
	/**
	 * True if this item can be placed on the wall of a space.
	 */
	public boolean onWall;
	/**
	 * True if this item is a passive object (OBJECT), such as wall parts etc.
	 */
	public boolean isPassiveObject;
	/**
	 * True if this item is invisible to clients.
	 */
	public boolean isInvisible;
	/**
	 * True if this item triggers some action when a SpaceUser 'steps' on it.
	 */
	public boolean isTrigger;
	
	/**
	 * True if this item can be placed on the floor and allows space users to sit on it.
	 */
	public boolean canSitOnTop;
	/**
	 * True if this item can be placed on the floor and allows room users to lay on it. (beds etc)
	 */
	public boolean canLayOnTop;
	/**
	 * True if this item can be placed on the floor and allows room users to stand on top of it.
	 */
	public boolean canStandOnTop;
	/**
	 * True if other items can be stacked on top of this item.
	 */
	public boolean canStackOnTop;
	
	/**
	 * True if this item requires a room user to have room rights to interact with the item.
	 */
	public boolean requiresRightsForInteraction;
	/**
	 * True if this item can be placed on the floor and requires a room user to stand one tile removed from the item to interact with the item.
	 */
	public boolean requiresTouchingForInteraction;
	
	/**
	 * True if this item can be used to decorate the walls/floor of a user flat.
	 */
	public boolean isDecoration;
	/**
	 * True if this item is a wall item and behaves as a post.it item. ('sticky')
	 */
	public boolean isPostIt;
	/**
	 * True if this item is a photo taken by camera item and who's image is saved in database.
	 */
	public boolean isPhoto;
	/**
	 * True if this item can be placed on the floor and can be opened/closed. Open items allow room users to walk through them, closed items do not.
	 */
	public boolean isDoor;
	/**
	 * True if this item can be placed on the floor and can teleport room users to other teleporter items. BEAM ME UP SCOTTY FFS!!11oneone
	 */
	public boolean isTeleporter;
	/**
	 * True if this item can be placed on the floor and can be opened and closed, and 'rolled' to a random number.
	 */
	public boolean isDice;
	/**
	 * True if this item can be placed on the floor and can hold a user-given message as 'inscription'.
	 */
	public boolean isPrizeTrophy;
	
	/**
	 * Returns a String representing the flags that are true in this ItemBehaviour.
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder(3);
		
		if (this.STUFF)
		{
			sb.append('S');
		}
		if (this.ITEM)
		{
			sb.append('I');
		}
		if (this.onFloor)
		{
			sb.append('F');
		}
		if (this.onWall)
		{
			sb.append('W');
		}
		if (this.canSitOnTop)
		{
			sb.append('C');
		}
		if (this.canLayOnTop)
		{
			sb.append('B');
		}
		if (this.canStandOnTop)
		{
			sb.append('K');
		}
		if (this.isPassiveObject)
		{
			sb.append('P');
		}
		if (this.isInvisible)
		{
			sb.append('E');
		}
		if(this.isTrigger)
		{
			sb.append('M');
		}
		
		if (this.requiresRightsForInteraction)
		{
			sb.append('G');
		}
		if (this.requiresTouchingForInteraction)
		{
			sb.append('T');
		}
		
		if (this.canStackOnTop)
		{
			sb.append('H');
		}
		if (this.isDecoration)
		{
			sb.append('V');
		}
		if (this.isPostIt)
		{
			sb.append('J');
		}
		if (this.isPhoto)
		{
			sb.append('N');
		}
		if (this.isDoor)
		{
			sb.append('D');
		}
		if (this.isTeleporter)
		{
			sb.append('X');
		}
		if (this.isDice)
		{
			sb.append('L');
		}
		if (this.isPrizeTrophy)
		{
			sb.append('Y');
		}
		
		return sb.toString();
	}
	
	/**
	 * Parses a String of flags to a ItemBehaviour object.
	 * 
	 * @param s The String of flags to parse.
	 * @return The ItemBehaviour with the flags flipped to their real states etc.
	 */
	public static ItemBehaviour parse(String s)
	{
		// Create empty behaviour
		ItemBehaviour behaviour = new ItemBehaviour();
		
		// Process all flags
		for (char flag : s.toCharArray())
		{
			// Determine flag
			switch (flag)
			{
				case 'S':
					behaviour.STUFF = true;
					break;
				
				case 'I':
					behaviour.ITEM = true;
					break;
				
				case 'F':
					behaviour.onFloor = true;
					break;
				
				case 'W':
					behaviour.onWall = true;
					break;
				
				case 'P':
					behaviour.isPassiveObject = true;
					break;
				
				case 'E':
					behaviour.isInvisible = true;
					break;
					
				case 'M':
					behaviour.isTrigger = true;
					break;
					
				case 'C':
					behaviour.canSitOnTop = true;
					break;
				
				case 'B':
					behaviour.canLayOnTop = true;
					break;
				
				case 'K':
					behaviour.canStandOnTop = true;
					break;
				
				case 'G':
					behaviour.requiresRightsForInteraction = true;
					break;
				
				case 'T':
					behaviour.requiresTouchingForInteraction = true;
					break;
				
				case 'H':
					behaviour.canStackOnTop = true;
					break;
				
				case 'V':
					behaviour.isDecoration = true;
					break;
				
				case 'J':
					behaviour.isPostIt = true;
					break;
				
				case 'N':
					behaviour.isPhoto = true;
					break;
				
				case 'D':
					behaviour.isDoor = true;
					break;
				
				case 'X':
					behaviour.isTeleporter = true;
					break;
				
				case 'L':
					behaviour.isDice = true;
					break;
				
				case 'Y':
					behaviour.isPrizeTrophy = true;
					break;
			}
		}
		
		// Return the parsed behaviour
		return behaviour;
	}
}
