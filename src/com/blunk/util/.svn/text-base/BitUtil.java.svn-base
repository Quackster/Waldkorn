package com.blunk.util;

public class BitUtil
{
	public static final int NUMBER_OF_BITS_IN_A_BYTE = 8;
	public static final short MASK_TO_BYTE = 0xFF;
	public static final int SIZE_OF_AN_INT_IN_BYTES = 4;
	
	public static byte[] intToBytes(int i)
	{
		byte[] bytes = new byte[4];
		/*
		bytes[0] = (byte)(i & MASK_TO_BYTE);
		i >>= NUMBER_OF_BITS_IN_A_BYTE;
		bytes[1] = (byte)(i & MASK_TO_BYTE);
		i >>= NUMBER_OF_BITS_IN_A_BYTE;
		bytes[2] = (byte)(i & MASK_TO_BYTE);
		i >>= NUMBER_OF_BITS_IN_A_BYTE;
		bytes[3] = (byte)(i & MASK_TO_BYTE);
		*/
		
		bytes[3] = (byte)(i & MASK_TO_BYTE);
		i >>= NUMBER_OF_BITS_IN_A_BYTE;
		bytes[2] = (byte)(i & MASK_TO_BYTE);
		i >>= NUMBER_OF_BITS_IN_A_BYTE;
		bytes[1] = (byte)(i & MASK_TO_BYTE);
		i >>= NUMBER_OF_BITS_IN_A_BYTE;
		bytes[0] = (byte)(i & MASK_TO_BYTE);
		
		return bytes;
	}
	
	public static int bytesToInt(byte A, byte B, byte C, byte D)
	{
		int i = (D & MASK_TO_BYTE);
		i |= ((C & MASK_TO_BYTE) << 8);
		i |= ((B & MASK_TO_BYTE) << 16);
		i |= ((A & MASK_TO_BYTE) << 24);
		
		return i;
	}
	

	public static int bytesToInt(byte[] bytes)
	{
		return bytesToInt(bytes[0], bytes[1], bytes[2], bytes[3]);
	}
	
	public static short bytesToShort(byte A, byte B)
	{
		short i = (short)(B & MASK_TO_BYTE);
		i |= ((A & MASK_TO_BYTE) << 8);
		
		return i;
	}
	
	public static byte[] getEvenStringBytes(String s)
	{
		return padByteArrayEven(s.getBytes());
	}
	
	public static byte[] padByteArrayEven(byte[] src)
	{
		// Is not even?
		if(src.length % 2 == 0)
			return src;
		else
		{
			byte[] out = new byte[src.length + 1];
			for(int i = 0; i < out.length - 1; i++)
			{
				out[i] = src[i];
			}
			
			// So there's an added NULL byte
			
			return out;
		}
	}
}
