package net.nillus.waldkorn.spaces;

public class SpaceTile
{
	public final short X;
	public final short Y;
	
	public SpaceTile(short X, short Y)
	{
		this.X = X;
		this.Y = Y;
	}
	
	public SpaceTile(String position)
	{
		String[] coords = position.split(",", 2);
		this.X = Short.parseShort(coords[0]);
		this.Y = Short.parseShort(coords[1]);
	}
	
	public String toString()
	{
		return this.X + "," + this.Y;
	}
	
	public static SpaceTile parse(String position)
	{
		return new SpaceTile(position);
	}
}
