package com.blunk.security;

import java.security.SecureRandom;
import java.util.Random;

/**
 * This class contains functions such as SecretDecode (For decoding a public key to init with
 * Encryption) and GenerateSecret (For generating a secret key to init with Encryption) All secretz!
 * 
 * 07/06/2009 - * Tidyed up, made more efficient for Connection Handshake!
 * 
 * @author Mike
 */
public class FuseSecret
{
	private static SecureRandom randomGen = new SecureRandom();	/* Used for SecretKeyDecode within Encryption */
	
	/**
	 * Decodes a public key sent by the Server for use with the Encryption.
	 * 
	 * @param tKey The public Key sent to the client.
	 * @return tKeyChecksum The decoded key.
	 * @author Sulake
	 */
	public static int SecretDecode(String origKey)
	{
		String table = origKey.substring(0, origKey.length() / 2);
		String key = origKey.substring(origKey.length() / 2);
		int checkSum = 0;
		for (int i = 0; i < table.length(); i++)
		{
			int offset = table.indexOf(key.charAt(i));
			if (offset % 2 == 0)
				offset *= 2;
			if (i % 3 == 0)
				offset *= 3;
			if (offset < 0)
				offset = table.length() % 2;
			checkSum += offset;
			checkSum ^= offset << (i % 3) * 8;
		}
		
		return checkSum;
	}
	
	/**
	 * Generates a random key. (Public Key)
	 * 
	 * @return tTable + tKey The final Key
	 * @author Sulake
	 */
	public static String GenerateSecret()
	{
		
		int length = 30 + Math.abs(randomGen.nextInt() % 40);
		
		StringBuffer table = new StringBuffer(length);
		StringBuffer key = new StringBuffer(length);
		int charModLen = "abcdefghijklmnopqrstuvwxyz1234567890".length();
		
		for (int i = 0; i < length; i++)
		{
			Character c = new Character("abcdefghijklmnopqrstuvwxyz1234567890".charAt(Math.abs(randomGen.nextInt() % charModLen)));
			table.append(c);
			
			c = new Character("abcdefghijklmnopqrstuvwxyz1234567890".charAt(Math.abs(randomGen.nextInt() % charModLen)));
			
			table.append(c);
			key.append(c);
		}
		
		return table.toString() + key.toString();
	}
	
	/**
	 * Once again, Math.Pow() sucks in Java.
	 * 
	 * @param i
	 * @param power
	 * @return int The result
	 * @author Nillus
	 */
	public static int IntegralPow(int i, int power)
	{
		// Cba with using two double() with Math.pow
		for (int x = 0; x < power; x++)
			i *= i;
		
		return i;
	}
	
	/**
	 * Because Java's random function sucks ass - you can't generate a random number BETWEEN a given
	 * range. Hence the reason why this is here.
	 * 
	 * @param tStart The start of the range.
	 * @param tEnd The end of the range.
	 * @param tRand The instance of the "Random()" class.
	 * @return int A random int between given range.
	 * @author javapractices.com
	 */
	public static int GenerateRandomInRange(int tStart, int tEnd, Random tRand)
	{
		if (tStart > tEnd)
			throw new IllegalArgumentException("The starting number cannot be larger than the ending number!");
		
		// Stop any possible overflow
		long tRange = (long)tEnd - (long)tStart + 1;
		long tFraction = (long)(tRange * tRand.nextDouble());
		
		return (int)(tFraction + tStart);
	}
}
