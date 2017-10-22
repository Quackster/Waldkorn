package net.nillus.waldkorn.moderation;

import java.util.Date;

/**
 * A ModerationBan is applied to a User or IP address to moderate HabboHotel. Bans are stored in the Database and automatically removed when they expire.
 * 
 * @author Nillus
 */
public class ModerationBan
{
	/**
	 * The ID of this ModerationBan. Each ModerationBan has a unique ID.
	 */
	public int ID;
	/**
	 * The database ID of the User this ModerationBan applies to. 0 if it does not apply to a single user.
	 */
	public int userID;
	/**
	 * The IP address string this ModerationBan applies to. Null if this is no IP address bar.
	 */
	public String ip;
	/**
	 * The java.util.Date object representing the date and time this ban was applied.
	 */
	public Date appliedAt;
	/**
	 * The database ID of the User (moderator) that applied this ban.
	 */
	public int appliedBy;
	/**
	 * The java.util.Date object representing the date and time this ban will expire.
	 */
	public Date expiresAt;
	/**
	 * The string holding the reason this user was banned.
	 */
	public String reason;
	
	public String generateReport()
	{
		// Ref ID: %banID%-%issuerID%-1337-%userID%
			return "";
			/*
			"Ban reference ID: " + this.ID + "-" + this.appliedBy + "-1337-" + this.userID + "\r" +
			"Includes IP ban: " + ((this.ip != null) ? "yes" : "no") + "\r" +
			"Applied at: " + Environment.getMomentFormatter().format(this.appliedAt) + "\r" +
			"Expires at: " + Environment.getMomentFormatter().format(this.expiresAt) + "\r" +
			"Reason: " + this.reason;*/
	}
}
