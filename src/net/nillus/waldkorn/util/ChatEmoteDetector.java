package net.nillus.waldkorn.util;

public class ChatEmoteDetector
{
	private static final String[] smlEmotes = { ":)", ":-)", ":d", ":p", ";)", ";-)", ":]", "xD", "=P", "=D", "=)"};
	private static final String[] sadEmotes = { ":s", ":(", ":-(", ":'(", ":'-(", ":[", "=[" };
	private static final String[] agrEmotes = { ":@", ">:(", ">:-(", "Myrax", "myrax", ">=)", ">:]" };
	
	public static final String detectEmote(String word)
	{
		if (word.length() > 0)
		{
			char start = word.charAt(0);
			if (start != ':' && start != '>' &&  start != 'M' && start != 'm' && start != '=' && start != 'x')
			{
				return null; // None of them
			}
			word = word.toLowerCase();
			
			for (String emote : smlEmotes)
			{
				if (word.equals(emote))
				{
					return "sml";
				}
			}
			
			if (word.equals(":o"))
			{
				return "srp";
			}
			
			for (String emote : sadEmotes)
			{
				if (word.equals(emote))
				{
					return "sad";
				}
			}
			
			for (String emote : agrEmotes)
			{
				if (word.equals(emote))
				{
					return "agr";
				}
			}
		}
		
		return null;
	}
}
