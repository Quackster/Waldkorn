package com.suelake.habbo.spaces.pathfinding;

import java.util.Vector;

public class NodeQueue
{
	protected Vector<PathfinderNode> innerList;
	
	public NodeQueue()
	{
		innerList = new Vector<PathfinderNode>();
	}
	
	public void switchElements(int i, int j)
	{
		PathfinderNode swap = innerList.get(i);
		innerList.set(i, innerList.get(j));
		innerList.set(j, swap);
	}
	
	public int compareElements(int indexA, int indexB)
	{
		PathfinderNode A = innerList.get(indexA);
		PathfinderNode B = innerList.get(indexB);
		
		if (A.F > B.F)
			return 1;
		else if (A.F < B.F)
			return -1;
		return 0;
	}
	
	public int push(PathfinderNode item)
	{
		int p = innerList.size(), p2;
		innerList.addElement(item); // E[p] = O
		do
		{
			if (p == 0)
				break;
			p2 = (p - 1) / 2;
			if (compareElements(p, p2) < 0)
			{
				switchElements(p, p2);
				p = p2;
			}
			else
				break;
		}
		while (true);
		return p;
	}
	
	public PathfinderNode pop()
	{
		PathfinderNode result = innerList.get(0);
		innerList.set(0, innerList.lastElement());
		innerList.remove(innerList.size() - 1);

		int p = 0, p1, p2, pn;
		do
		{
			pn = p;
			p1 = 2 * p + 1;
			p2 = 2 * p + 2;
			if (innerList.size() > p1 && compareElements(p, p1) > 0) // links kleiner
				p = p1;
			if (innerList.size() > p2 && compareElements(p, p2) > 0) // rechts noch kleiner
				p = p2;
			
			if (p == pn)
				break;
			switchElements(p, pn);
		}
		while (true);
		
		return result;
	}
	
	public void update(int i)
	{
		int p = i, pn;
		int p1, p2;
		do // aufsteigen
		{
			if (p == 0)
				break;
			p2 = (p - 1) / 2;
			if (compareElements(p, p2) < 0)
			{
				switchElements(p, p2);
				p = p2;
			}
			else
				break;
		}
		while (true);
		if (p < i)
			return;
		do // absteigen
		{
			pn = p;
			p1 = 2 * p + 1;
			p2 = 2 * p + 2;
			if (innerList.size() > p1 && compareElements(p, p1) > 0) // links kleiner
				p = p1;
			if (innerList.size() > p2 && compareElements(p, p2) > 0) // rechts noch kleiner
				p = p2;
			
			if (p == pn)
				break;
			switchElements(p, pn);
		}
		while (true);
	}
	
	public PathfinderNode peek()
	{
		if (innerList.size() != 0)
			return innerList.get(0);
		
		return null;
	}
	
	public void clear()
	{
		innerList.clear();
	}
	
	public int size()
	{
		return innerList.size();
	}
	
	public PathfinderNode get(int index)
	{
		return innerList.get(index);
	}
	
	public void set(int index, PathfinderNode value)
	{
		innerList.set(0, value);
		update(index);
	}
}