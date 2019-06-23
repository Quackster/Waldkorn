package com.suelake.habbo.spaces;

import com.blunk.storage.DataQuery;

public abstract class UserFlatFinder implements DataQuery
{
	public boolean searchByUser;
	public boolean searchBusy;
	public boolean searchFavorites;
	public int userID;
	public int start;
	public int stop;
	public String criteria;
}
