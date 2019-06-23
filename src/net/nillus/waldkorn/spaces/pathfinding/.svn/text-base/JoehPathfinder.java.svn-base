package com.suelake.habbo.spaces.pathfinding;

/*
 * Code: Project Thor Pathfinding
Author: Joe Hegarty

Copyright (c) 2009 Joe Hegarty

Permission is hereby granted, free of charge, to the Blunk Server Team to obtain a copy
of the above software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute copies of the Software,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

import java.util.Vector;

/**
 * A quick messy Java port of a pathfinder written by Joe 'Joeh' Hegarty for Thor server.
 * 
 * @author Joe Hegarty (ported by Nillus)
 */
public class JoehPathfinder
{
	private static final int HEURISTIC_ESTIMATE = 2;
	private static final int MAX_CYCLE_LIMIT = 2500;
	
	private final byte[][] map;
	private final boolean[][] userMap;
	private float[][] heightMap;
	private final int maxX;
	private final int maxY;
	private NodeQueue listOpen;
	private Vector<PathfinderNode> listClosed;
	private boolean pathFound = false;
	
	private final float maxAscend;
	private final float maxDescend;
	
	public JoehPathfinder(byte[][] map, boolean[][] userMap, float[][] heightMap, float maxAscend, float maxDescend)
	{
		this.map = map;
		this.userMap = userMap;
		this.heightMap = heightMap;
		this.maxAscend = maxAscend;
		this.maxDescend = maxDescend;
		
		// -1 because of zero based arrays
		this.maxX = userMap.length - 1;
		this.maxY = userMap[0].length - 1;
		
		this.listOpen = new NodeQueue();
		this.listClosed = new Vector<PathfinderNode>();
	}
	
	public Vector<PathfinderNode> findPath(short X, short Y, short goalX, short goalY)
	{
		byte[][] Direction = new byte[][] { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };
		
		int openListCount = 0;
		int closedListCount = 0;
		
		PathfinderNode parent = new PathfinderNode();
		parent.X = X;
		parent.Y = Y;
		parent.parentX = X;
		parent.parentY = Y;
		parent.H = HEURISTIC_ESTIMATE;
		parent.F = HEURISTIC_ESTIMATE;
		parent.G = 0;
		
		listOpen.push(parent);
		openListCount++;
		
		while (openListCount > 0)
		{
			openListCount--;
			parent = listOpen.pop();
			if (parent.X == goalX && parent.Y == goalY)
			{
				pathFound = true;
				listClosed.add(parent);
				closedListCount++;
				break;
			}
			
			if (closedListCount > MAX_CYCLE_LIMIT)
				break;
			
			for (int i = 0; i < 4; i++)
			{
				PathfinderNode newNode = new PathfinderNode();
				newNode.X = (short)(parent.X + Direction[i][0]);
				newNode.Y = (short)(parent.Y + Direction[i][1]);
				
				if (newNode.X < 0 || newNode.Y < 0 || newNode.X > this.maxX || newNode.Y > this.maxY || map[newNode.X][newNode.Y] != 0 || this.userMap[newNode.X][newNode.Y])
				{
					continue;
				}
				
				int newG = parent.G + (int)(this.map[newNode.X][newNode.Y]) + 1; // Cost
				if (newG == parent.G) // Same cost as the parent, why taking this route?
					continue;
				
				float hParent = this.heightMap[parent.X][parent.Y];
				float hNew = this.heightMap[newNode.X][newNode.Y];
				if (hParent - maxDescend > hNew || hParent + maxAscend < hNew)
					continue;
				
				int listIndex = -1;
				for (int j = 0; j < openListCount; j++)
				{
					PathfinderNode curNode = listOpen.get(j);
					if (curNode.X == newNode.X && curNode.Y == newNode.Y)
					{
						listIndex = j;
						break;
					}
				}
				if (listIndex != -1 && listOpen.get(listIndex).G <= newG) // Better node already in open list
					continue;
				
				listIndex = -1;
				for (int j = 0; j < closedListCount; j++)
				{
					PathfinderNode curNode = listClosed.get(j);
					if (curNode.X == newNode.X && curNode.Y == newNode.Y)
					{
						listIndex = j;
						break;
					}
				}
				if (listIndex != -1 && listClosed.get(listIndex).G <= newG) // Better node already in closed list
					continue;
				
				newNode.parentX = parent.X;
				newNode.parentY = parent.Y;
				newNode.G = newG;
				
				// Calculate heuristic
				int xD1 = parent.X - goalX;
				int xD2 = X - goalX;
				int yD1 = parent.Y - goalY;
				int yD2 = Y - goalY;
				
				newNode.H = HEURISTIC_ESTIMATE * (Math.max(Math.abs(newNode.X - goalX), Math.abs(newNode.Y - goalY)));
				newNode.H = (int)(newNode.H + Math.abs(xD1 * yD2 - xD2 * yD1) * 0.001);
				newNode.F = newNode.G + newNode.H;
				listOpen.push(newNode);
				openListCount++;
			}
			listClosed.add(parent);
			closedListCount++;
		}
		
		if (this.pathFound)
		{
			PathfinderNode topNode = listClosed.lastElement();
			for (int j = closedListCount - 1; j >= 0; j--) // Inverse scroll
			{
				PathfinderNode pNode = listClosed.get(j);
				if (topNode.parentX == pNode.X && topNode.parentY == pNode.Y || j == closedListCount - 1)
				{
					topNode = pNode;
				}
				else
				{
					listClosed.remove(j);
				}
			}
			
			return listClosed;
		}
		
		return new Vector<PathfinderNode>(0);
	}
}
