package net.nillus.waldkorn.moderation.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import net.nillus.waldkorn.moderation.ModerationBan;

import com.blunk.storage.sql.SQLDataObject;

public class SQLModerationBan extends ModerationBan implements SQLDataObject
{
	@Override
	public boolean delete(Connection conn) throws SQLException
	{
		// Prepare query
		PreparedStatement query = conn.prepareStatement("DELETE FROM moderation_bans WHERE id = ?;");
		query.setInt(1, super.ID);
		
		// Execute it
		boolean deleted = (query.executeUpdate() > 0);
		
		// Dispose PreparedStatement
		query.close();
		
		// Return the result of deletion
		return deleted;
	}
	
	@Override
	public boolean insert(Connection conn) throws SQLException
	{
		// Prepare query
		PreparedStatement query = conn.prepareStatement("INSERT INTO moderation_bans(userid,ip,applied_at,applied_by,expires_at,reason) VALUES (?,?,?,?,?,?);", PreparedStatement.RETURN_GENERATED_KEYS);
		this.setParameters(query);
		
		// Execute it and fetch generated keys
		if(query.executeUpdate() > 0)
		{
			ResultSet keys = query.getGeneratedKeys();
			if(keys.next())
			{
				super.ID = keys.getInt(1);
			}
		}
		
		// Dispose PreparedStatement
		query.close();
		
		// Return the result of insertion
		return (super.ID > 0);
	}
	
	@Override
	public boolean load(Connection conn) throws SQLException
	{
		// Prepare query
		PreparedStatement query = null;
		if(super.ID > 0)
		{
			// Get ban with this ID
			query = conn.prepareStatement("SELECT * FROM moderation_bans WHERE id = ?;");
			query.setInt(1, super.ID);
		}
		else if(super.userID > 0)
		{
			// Get the longest during ban for this user id
			query = conn.prepareStatement("SELECT * FROM moderation_bans WHERE userid = ? ORDER BY expires_at DESC LIMIT 1;");
			query.setInt(1, super.userID);
		}
		else if(super.ip != null)
		{
			// Get the longest during ban for this ip
			query = conn.prepareStatement("SELECT * FROM moderation_bans WHERE ip = ? ORDER BY expires_at DESC LIMIT 1;");
			query.setString(1, super.ip);
		}
		
		// Reset ID
		super.ID = 0;
		
		// Query built?
		if(query != null)
		{
			// Execute it then!
			ResultSet result = query.executeQuery();
			if(result.next())
			{
				// Parse result data
				super.ID = result.getInt("id");
				super.userID = result.getInt("userid");
				super.ip = result.getString("ip");
				super.appliedAt = new Date(result.getTimestamp("applied_at").getTime());
				super.appliedBy = result.getInt("applied_by");
				super.expiresAt = new Date(result.getTimestamp("expires_at").getTime());
				super.reason = result.getString("reason");
			}
			
			// Dispose PreparedStatement + ResultSet
			query.close();
		}
		
		// If ID > 0, it was loaded successfully
		return (super.ID > 0);
	}
	
	@Override
	public boolean update(Connection conn) throws SQLException
	{
		// Prepare query
		PreparedStatement query = conn.prepareStatement("UPDATE moderation_bans SET userid = ?,ip = ?,applied_at = ?,applied_by = ?,expires_at = ?,reason = ? WHERE id = ?;");
		this.setParameters(query);
		query.setInt(7, super.ID);
		
		// Execute it
		boolean inserted = (query.executeUpdate() > 0);
		
		// Dispose PreparedStatement
		query.close();
		
		// Return the result of insertion
		return inserted;
	}
	
	private void setParameters(PreparedStatement query) throws SQLException
	{
		query.setInt(1, super.userID);
		query.setString(2, super.ip);
		query.setTimestamp(3, new Timestamp(super.appliedAt.getTime()));
		query.setInt(4, super.appliedBy);
		query.setTimestamp(5, new Timestamp(super.expiresAt.getTime()));
		query.setString(6, super.reason);
	}

	@Override
	public long getCacheKey() {
		return 0;
	}
}
