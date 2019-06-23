package com.blunk.util;

import java.util.Vector;

public class ArrayUtil
{
	public static int indexOfChar(char[] array, int start, int stop, char seek)
	{
		for (int i = start; i < stop; i++)
		{
			if (array[i] == seek)
				return i;
		}
		
		return -1;
	}
	
	public static char[] chompArray(char[] array, int start, int end)
	{
		char[] newArray = new char[end - start];
		for (int i = 0, j = start; j < end; i++, j++)
		{
			newArray[i] = array[j];
		}
		
		return newArray;
	}
	
	public static String copyBytesToString(byte[] src, int arg1, int arg2)
	{
		char[] chrs = new char[arg2 - arg1];
		for(int i = 0, j = arg1; j < arg2; i++, j++)
		{
			chrs[i] = (char)src[j];
		}
		
		String s = String.valueOf(chrs);
		return s;
	}
	
	public static String[] convertStringVectorToArray(Vector<String> vector)
	{
		if(vector != null)
		{
			String[] array = new String[vector.size()];
			for(int index = 0; index < array.length; index++)
			{
				array[index] = vector.get(index);
			}
			
			return array;
		}
		
		return new String[0];
	}
}
