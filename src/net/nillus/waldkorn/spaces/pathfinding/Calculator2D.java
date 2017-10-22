package net.nillus.waldkorn.spaces.pathfinding;

/**
 * Provides static methods for calculating rotations in space etc.
 * 
 * @author Nillus
 */
public abstract class Calculator2D
{
	public static byte calculateHumanDirection(int X, int Y, int x2, int y2)
	{
		byte result = 0;
		if (X > x2 && Y > y2)
		{
			result = 7;
		}
		else if (X < x2 && Y < y2)
		{
			result = 3;
		}
		else if (X > x2 && Y < y2)
		{
			result = 5;
		}
		else if (X < x2 && Y > y2)
		{
			result = 1;
		}
		else if (X > x2)
		{
			result = 6;
		}
		else if (X < x2)
		{
			result = 2;
		}
		else if (Y < y2)
		{
			result = 4;
		}
		else if (Y > y2)
		{
			result = 0;
		}
		
		return result;
	}
	
	public static byte calculateHumanMoveDirection(short X, short Y, short toX, short toY)
	{
		if (X == toX)
		{
			if (Y < toY)
				return 4;
			else
				return 0;
		}
		else if (X > toX)
		{
			if (Y == toY)
				return 6;
			else if (Y < toY)
				return 5;
			else
				return 7;
		}
		else if (X < toX)
		{
			if (Y == toY)
				return 2;
			else if (Y < toY)
				return 3;
			else
				return 1;
		}
		
		return 0;
	}
	
	public static double calculateDistance(short x1, short y1, short x2, short y2)
	{
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
}
