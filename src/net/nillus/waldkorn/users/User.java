package net.nillus.waldkorn.users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import net.nillus.waldkorn.net.SerializableObject;
import net.nillus.waldkorn.net.ServerMessage;
import net.nillus.waldkorn.storage.Database;

public class User implements SerializableObject
{
	public int ID;
	public String name;
	public String password;
	public String email;
	public String dateOfBirth;
	public String phoneNumber;
	public Date registered;
	public byte role;
	public String motto;
	public String figure;
	public char sex;
	public int credits;
	public int gameTickets;
	public String poolFigure;
	public String badge;
	public String messengerMotto;
	public Date lastActivity;
	
	// RP
	public byte health;
	
	/*
	 * FIGURE
	 hd=head
	 bd=body
	 fc=face
	 ey=eye
	 ch=chest
	 ls=left sleeve
	 rs=right sleeve
	 rh=right arm
	 lh=left arm
	 lg=leg
	 ft=feet
	 sd=shadow
	*/
	
	public void updateLastActivity()
	{
		this.lastActivity = new Date();
	}
	
	public void determineBadge()
	{
		/*
		 * Roles
		 * 1 = Normal
		 * 2 = Silver Hobba
		 * 3 = Gold Hobba
		 * 4 = Moderator
		 * 5 = Administrator
		 * 6 = etc
		 */

		if (this.role == UserRole.SILVER)
		{
			this.badge = "1"; // Silver Hobba badge
		}
		else if (this.role == UserRole.GOLD)
		{
			this.badge = "2"; // Gold Hobba badge
		}
		else if (this.role > UserRole.GOLD)
		{
			this.badge = "A"; // Staff badge
		}
		else
		{
			this.badge = null; // No badge
		}
	}
	
	public boolean hasRight(String right)
	{
		return UserRole.hasUserRight(this.role, right);
	}
	
	@Override
	public void serialize(ServerMessage msg)
	{
		msg.appendKVArgument("name", this.name);
		msg.appendKVArgument("email", this.email);
		msg.appendKVArgument("birthday", this.dateOfBirth);
		msg.appendKVArgument("phoneNumber", this.phoneNumber);
		msg.appendKVArgument("directMail", "0"); // No spam please
		msg.appendKVArgument("had_read_agreement", "1"); // Ofcourse
		msg.appendKVArgument("customData", this.motto);
		msg.appendKVArgument("sex", this.sex == 'M' ? "Male" : "Female");
		msg.appendKVArgument("figure", this.figure);
		msg.appendKVArgument("has_special_rights", (this.role != -1) ? "1" : "0");
		msg.appendKVArgument("badge_type", this.badge);
	}
	
	public static User parse(ResultSet row) throws Exception
	{
		User user = new User();
		user.ID = row.getInt("id");
		user.name = row.getString("name");
		user.password = row.getString("password");
		user.email = row.getString("email");
		user.dateOfBirth = row.getString("dob");
		user.phoneNumber = row.getString("phonenumber");
		user.registered = new Date(row.getTimestamp("registered").getTime());
		user.role = row.getByte("role");
		user.motto = row.getString("motto");
		user.figure = row.getString("figure");
		user.sex = row.getString("sex").charAt(0);
		user.poolFigure = row.getString("poolfigure");
		user.credits = row.getInt("credits");
		user.gameTickets = row.getInt("gametickets");
		user.messengerMotto = row.getString("motto_messenger");
		user.lastActivity = new Date(row.getTimestamp("lastactivity").getTime());
		return user;
	}

	/**
	 * Updates password, email, region, etc.
	 */
	public void updateInfo(Database db)
	{
		PreparedStatement query = null;
		try
		{
			query = db.prepareStatement("UPDATE users SET password = ?,email = ?,phonenumber = ?,figure = ?,sex = ?,motto = ?,motto_messenger = ?,lastactivity = NOW() WHERE id = ?;");
			query.setString(1, this.password);
			query.setString(2, this.email);
			query.setString(3, this.phoneNumber);
			query.setString(4, this.figure);
			query.setString(5, Character.toString(this.sex));
			query.setString(6, this.motto);
			query.setString(7, this.messengerMotto);
			query.setInt(8, this.ID);
			query.executeUpdate();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			db.releaseStatement(query);
		}
	}
	
	/**
	 * Updates credits and game tickets.
	 */
	public void updateValuables(Database db)
	{
		PreparedStatement query = null;
		try
		{
			query = db.prepareStatement("UPDATE users SET credits = ?,gametickets = ? WHERE id = ?;");
			query.setInt(1, this.credits);
			query.setInt(2, this.gameTickets);
			query.setInt(3, this.ID);
			query.executeUpdate();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			db.releaseStatement(query);
		}
	}
}
