package net.nillus.waldkorn.users;

public final class UserRole
{
	public final static byte NORMAL = 1;
	public final static byte SILVER = 2;
	public final static byte GOLD = 3;
	public final static byte MODERATOR = 4;
	public final static byte ADMINISTRATOR = 5;
	public final static byte SYSTEM_ADMINISTRATOR = 6;
	public final static byte MAX_USER_ROLE = SYSTEM_ADMINISTRATOR;
	
	
	private static String[][] m_userRights = new String[MAX_USER_ROLE + 1][0];
	
	public static void setUserRights(byte roleID, String[] rights)
	{
		m_userRights[roleID] = rights;
	}
	
	public static String[] getUserRights(byte roleID)
	{
		// Role in range?
		if(roleID <= UserRole.MAX_USER_ROLE)
		{
			return m_userRights[roleID];
		}
		
		// No rights should be appropriate here
		return new String[0];
	}
	
	public static boolean hasUserRight(byte roleID, String right)
	{
		// Role in range?
		if(roleID <= UserRole.SYSTEM_ADMINISTRATOR)
		{
			// Cycle all rights for this role
			for(String userRight : m_userRights[roleID])
			{
				// Match?
				if(userRight.equals(right))
				{
					// Found!
					return true;
				}
			}
		}
		
		// Nope!
		return false;
	}
	

	public static byte getMinimumRole(String right)
	{
		// Cycle all roles
		for (byte roleID = 0; roleID <= UserRole.MAX_USER_ROLE; roleID++)
		{
			if(UserRole.hasUserRight(roleID, right))
			{
				return roleID;
			}
		}
		
		// Ensure this right is never valid for a User
		return UserRole.MAX_USER_ROLE + 1;
	}
}
