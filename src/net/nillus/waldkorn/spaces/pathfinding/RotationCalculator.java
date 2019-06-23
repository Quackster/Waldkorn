package com.suelake.habbo.spaces.pathfinding;

/**
 * Provides static methods for calculating rotations in space etc.
 * @author Nillus
 *
 */
public abstract class RotationCalculator
{
	public static byte calculateHumanDirection(short X, short Y, short toX, short toY)
	{
		byte result = 0;
        if (X > toX && Y > toY)
        {
            result = 7;
        }
        else if (X < toX && Y < toY)
        {
            result = 3;
        }
        else if (X > toX && Y < toY)
        {
            result = 5;
        }
        else if (X < toX && Y > toY)
        {
            result = 1;
        }
        else if (X > toX)
        {
            result = 6;
        }
        else if (X < toX)
        {
            result = 2;
        }
        else if (Y < toY)
        {
            result = 4;
        }
        else if (Y > toY)
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
