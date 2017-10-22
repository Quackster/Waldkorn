package net.nillus.waldkorn.spaces;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import net.nillus.waldkorn.Server;
import net.nillus.waldkorn.ServerComponent;
import net.nillus.waldkorn.items.Item;
import net.nillus.waldkorn.rp.Npc;

/**
 * SpaceAdministration provides methods for inserting, creating and editing Space objects in the DatabaseEndpoint, aswell as providing other misc tasks on Spaces such as rights in user flats etc.
 * 
 * @author Nillus
 */
public class SpaceAdministration extends ServerComponent
{
	private HashMap<String, SpaceModel> m_models;
	
	public SpaceAdministration(Server server)
	{
		super(server);
		m_models = new HashMap<String, SpaceModel>();
	}
	
	public void loadSpaceModels()
	{
		// Clear current values
		m_models.clear();
		
		// Tell shawty what we're doing!
		m_server.getLogger().info(this, "loading space models...");
		
		// Parse new values from database
		ResultSet result = null;
		PreparedStatement objQuery = null;
		try
		{
			result = m_server.getDatabase().executeQuery("SELECT * FROM spaces_models;");
			objQuery = m_server.getDatabase().prepareStatement("SELECT * FROM spaces_models_objects WHERE model = ?;");
			while (result.next())
			{
				SpaceModel model = SpaceModel.parse(result);
				
				// Read space model objects for this spacemodel
				objQuery.setString(1, model.type);
				Vector<Item> objects = new Vector<Item>();
				ResultSet objResult = objQuery.executeQuery();
				while(objResult.next())
				{
					Item obj = Item.parsePassiveObject(objResult, m_server.getItemAdmin().getDefinitions());
					objects.add(obj);
				}
				model.setPassiveObjects(objects);
				
				// Add the model
				m_models.put(model.type, model);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(result);
		}
		
		// Log how many loaded
		m_server.getLogger().info(this, "loaded " + m_models.size() + " space models");
	}
	
	public SpaceModel getSpaceModel(String type)
	{
		return m_models.get(type);
	}
	
	public Space getSpaceInfo(int spaceID)
	{
		// Prefer data from instance (kinda like caching)
		SpaceInstance instance = m_server.getSpaceServer().getFlatInstance(spaceID, false);
		if(instance != null)
		{
			return instance.getInfo();
		}
		
		ResultSet result = null;
		try
		{
			result = m_server.getDatabase().executeQuery("SELECT *,users.name AS owner FROM spaces LEFT JOIN users ON users.id = spaces.ownerid WHERE spaces.id = " + spaceID + ";");
			if(result.next())
			{
				return Space.parse(result);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(result);
		}
		return null;
	}
	
	public ArrayList<Integer> getUnits()
	{
		ArrayList<Integer> spaceIDs = new ArrayList<Integer>();
		ResultSet result = null;
		try
		{
			result = m_server.getDatabase().executeQuery("SELECT id FROM spaces WHERE ownerid = 0 ORDER BY id ASC;");
			while (result.next())
			{
				spaceIDs.add(result.getInt("id"));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(result);
		}
		
		return spaceIDs;
	}
	
	public Vector<Space> searchFlats(String criteria)
	{
		PreparedStatement query = null;
		Vector<Space> flats = new Vector<Space>();
		try
		{
			query = m_server.getDatabase().prepareStatement("SELECT spaces.*,users.name AS owner FROM spaces JOIN users ON spaces.ownerid = users.id WHERE users.name = ? OR spaces.name LIKE ? LIMIT 35;");
			query.setString(1, criteria);
			query.setString(2, criteria + "%");
			
			ResultSet result = query.executeQuery();
			while(result.next())
			{
				flats.add(Space.parse(result));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(query);
		}
		
		return flats;
	}
	
	public Vector<Space> searchFlatsForUser(String userName)
	{
		PreparedStatement query = null;
		Vector<Space> flats = new Vector<Space>();
		try
		{
			query = m_server.getDatabase().prepareStatement("SELECT spaces.*,users.name AS owner FROM spaces JOIN users ON spaces.ownerid = users.id WHERE users.name = ?;");
			query.setString(1, userName);
			
			ResultSet result = query.executeQuery();
			while(result.next())
			{
				flats.add(Space.parse(result));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(query);
		}
		
		return flats;
	}
	
	public boolean createFlat(Space flat)
	{
		// Fill up fields that would turn out to NULL for now
		flat.description = "";
		flat.usersMax = 25;
		flat.wallpaper = 101;
		flat.floor = 101;
		flat.password = "";
		
		PreparedStatement query = null;
		try
		{
			// Insert the item into the database
			query = m_server.getDatabase().prepareStatement("INSERT INTO spaces(ownerid,name,description,showowner,superusers,model,accesstype,password,wallpaper,floor,users_max) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
			query.setInt(1, flat.ownerID);
			query.setString(2, flat.name);
			query.setString(3, flat.description);
			query.setBoolean(4, flat.showOwner);
			query.setBoolean(5, flat.superUsers);
			query.setString(6, flat.model);
			query.setString(7, flat.accessType);
			query.setString(8, flat.password);
			query.setShort(9, flat.wallpaper);
			query.setShort(10, flat.floor);
			query.setShort(11, flat.usersMax);
			query.executeUpdate();
			
			// Fetch the generated keys
			ResultSet keys = query.getGeneratedKeys();
			if(keys.next())
			{
				flat.ID = keys.getInt(1);
				return true;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(query);
		}
		
		// Insertion failed!
		return false;
	}
	
	public void resetCurrentUserCounts()
	{
		try
		{
			m_server.getDatabase().executeUpdate("UPDATE spaces SET users_now = 0;");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		m_server.getLogger().info(this, "current user counts reset");
	}
	
	public Vector<Integer> getFlatControllers(int spaceID)
	{
		Vector<Integer> userIDs = new Vector<Integer>();
		ResultSet result = null;
		try
		{
			result = m_server.getDatabase().executeQuery("SELECT userid FROM spaces_flatcontrollers WHERE spaceid = " + spaceID + ";");
			while(result.next())
			{
				userIDs.add(result.getInt(1));
			}
			return userIDs;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(result);
		}
		
		return userIDs;
	}
	
	public void addFlatController(int spaceID, int userID)
	{
		try
		{
			m_server.getDatabase().executeUpdate("INSERT INTO spaces_flatcontrollers(spaceid,userid) VALUES (" + spaceID + "," + userID + ");");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void removeFlatController(int spaceID, int userID)
	{
		try
		{
			m_server.getDatabase().executeUpdate("DELETE FROM spaces_flatcontrollers WHERE spaceid = " + spaceID + " AND userid = " + userID + " LIMIT 1;");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public Vector<Space> getFavoriteFlatListForUser(int userID)
	{
		Vector<Space> favs = new Vector<Space>();
		ResultSet result = null;
		try
		{
			result = m_server.getDatabase().executeQuery("SELECT spaces.*,users.name AS owner FROM spaces JOIN users ON spaces.ownerid = users.id WHERE spaces.id IN(SELECT spaceid FROM users_favoriteflats WHERE userid = " + userID + ");");
			while(result.next())
			{
				favs.add(Space.parse(result));
			}
			return favs;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			m_server.getDatabase().releaseStatement(result);
		}
		
		return favs;
	}
	
	public void addToFavoriteFlatListOfUser(int userID, int spaceID)
	{
		try
		{
			m_server.getDatabase().executeUpdate("INSERT INTO users_favoriteflats(userid,spaceid) VALUES (" + userID + "," + spaceID + ");");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void removeFromFavoriteFlatListOfUser(int userID, int spaceID)
	{
		try
		{
			m_server.getDatabase().executeUpdate("DELETE FROM users_favoriteflats WHERE userid = " + userID + " AND spaceid = " + spaceID + " LIMIT 1;");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public Vector<Npc> getNpcsForSpace(int spaceID)
	{
		return new Vector<Npc>();
	}
}