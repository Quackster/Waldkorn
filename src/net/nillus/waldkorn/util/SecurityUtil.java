package net.nillus.waldkorn.util;

import net.nillus.waldkorn.spaces.Space;
import net.nillus.waldkorn.users.User;

public class SecurityUtil
{
	private final static String[] POSTIT_ALLOWED_COLORS = { "FFFFFF", "FFFF33", "FF9CFF", "9CFF9C", "9CCEFF" };
	
	public static boolean canSeeFlatOwner(Space flat, User usr)
	{
		// Hidden name?
		if (!flat.showOwner)
		{
			// Not owner?
			if (flat.ownerID != usr.ID)
			{
				// Atleast moderation privilege?
				/*if ())//!usr.hasRight("can_see_hidden_flatowner"))
				{
					// Aww no!
					return false;
				}*/
			}
		}
		
		// Can see!
		return true;
	}
	
	public static boolean postItColorValid(String color)
	{
		// Look for matches
		for (String allowedColor : SecurityUtil.POSTIT_ALLOWED_COLORS)
		{
			if (allowedColor.equals(color))
			{
				return true;
			}
		}
		
		// NUH UH...
		return false;
	}
	
	public static String filterInput(String input)
	{
		input = input.replace((char)10, ' ');
		input = input.replace((char)11, ' ');
		input = input.replace((char)12, ' ');
		input = input.replace((char)13, ' ');
		input = input.replace((char)14, ' ');
		return input;
	}
}
