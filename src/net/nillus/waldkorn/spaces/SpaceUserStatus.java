package net.nillus.waldkorn.spaces;

/**
 * Represents a unique status of a space user.
 * @author Nillus
 *
 */
public class SpaceUserStatus
{
	/**
	 * The key/name of this status. Will be replaced by 'action' when this is a flipping status.
	 * @see action
	 */
	public String name;
	/**
	 * The value of this status. This can be omitted in some cases.
	 */
	public String data;
	
	/**
	 * A string that will hold the action to be switched with the key when this is a flipping status.
	 */
	private String m_action;
	/**
	 * The amount of seconds that this status last before it is switched with the action.
	 */
	private int m_actionSwitchSeconds;
	/**
	 * The amount of seconds that the action of this status lasts before it turns into the normal status again.
	 */
	private int m_actionLengthSeconds;
	/**
	 * The time in SECONDS the status switches with the action or vice versa.
	 */
	private int m_actionEndTime;
	/**
	 * The time in SECONDS this status ends. 0 = infinite status.
	 */
	private int m_statusEndTime;
	/**
	 * True if this status is currently using 'action' instead of 'name'.
	 */
	private boolean m_actionActive;
	private boolean m_lastCheckResult;
	
	/**
	 * Constructs a new SpaceUserStatus with given data. Omit data by supplying null, not an empty string.
	 * @param name The name of the status.
	 * @param data The data of the status.
	 * @param lifeTimeSeconds The total amount of seconds this status lasts.
	 * @param action The action of the status, will be flipped with name etc.
	 * @param actionSwitchSeconds The total amount of seconds this action flips with the name.
	 * @param actionLengthSeconds The total amount of seconds that the action lasts before it flips back.
	 */
	public SpaceUserStatus(String name, String data, int lifeTimeSeconds, String action, int actionSwitchSeconds, int actionLengthSeconds)
    {
		int nowSeconds = (int)(System.currentTimeMillis() / 1000);
		this.name = name;
		this.data = data;
		if(lifeTimeSeconds != 0)
		{
			m_statusEndTime = nowSeconds + lifeTimeSeconds;
		}
		
		if(action != null)
		{
			m_action = action;
			m_actionSwitchSeconds = actionSwitchSeconds;
			m_actionLengthSeconds = actionLengthSeconds;
			m_actionEndTime = nowSeconds + actionSwitchSeconds;
		}
		
		m_lastCheckResult = true;
    }
	
	/**
	 * Checks if the status is still valid, and returns if the check result is different from the last time.
	 * @return True if status was updated, false if it remained the same.
	 */
	public boolean isUpdated()
	{
		 boolean hasUpdated = false;
		 
         boolean validCheckResult = this.checkStatus();
         if (validCheckResult != m_lastCheckResult)
         {
             hasUpdated = true; // Different result than last check!
         }
         m_lastCheckResult = validCheckResult;

         return hasUpdated;
	}
	
	/**
	 * Processes the status by flipping statuses and other things when it's time, and returns whether the status is still valid.
	 * @return True if status is valid, false if not. (it should be removed then!)
	 */
	public boolean checkStatus()
	{
		int nowSeconds = (int)(System.currentTimeMillis() / 1000);
		if (m_statusEndTime == 0)
		{
            return true; // Static action, always valid
		}
        else
        {
            if (m_statusEndTime < nowSeconds) // Non-persistent status expired
            {
                return false;
            }
        }
		
        if (m_action != null) // Status changes (eg, carry item)
        {
            if (m_actionEndTime < nowSeconds) // Status requires update
            {
            	// Swap name and action
            	String swap = this.name;
            	this.name = m_action;
            	m_action = swap;

                // Calculate new action length
                int switchSeconds = 0;
                if (m_actionActive)
                {
                    switchSeconds = m_actionSwitchSeconds;
                }
                else
                {
                    switchSeconds = m_actionLengthSeconds;
                }

                // Set new action length and force update
                m_actionActive = !m_actionActive;
                m_actionEndTime = nowSeconds + switchSeconds;
                m_lastCheckResult = !m_lastCheckResult;
            }
        }

        return true; // Still valid!
	}
}
