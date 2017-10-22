package net.nillus.waldkorn.rp;

import java.util.Random;

public class NpcText
{
	private final String[] m_strings;
	private final String[] m_triggers;
	
	public NpcText(String text)
	{
		this(new String[] { text });
	}
	
	public NpcText(String[] texts)
	{
		m_strings = texts;
		m_triggers = null;
	}
	
	public NpcText(String trigger, String reply)
	{
		this(new String[] { trigger }, new String[] { reply });
	}
	
	public NpcText(String[] triggers, String reply)
	{
		this(triggers, new String[] { reply });
	}
	
	public NpcText(String trigger, String[] replies)
	{
		this(new String[] { trigger }, replies);
	}
	
	public NpcText(String[] triggers, String[] replies)
	{
		m_triggers = triggers;
		m_strings = replies;
	}
	
	public boolean trigger(String word)
	{
		for (int x = 0; x < m_strings.length; x++)
		{
			if (m_strings[x].equalsIgnoreCase(word))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isGossip()
	{
		return (m_triggers == null);
	}
	
	public boolean isTrigger()
	{
		return (m_triggers != null);
	}
	
	public String getString()
	{
		Random rnd = new Random();
		return m_strings[rnd.nextInt(m_strings.length)];
	}
}
