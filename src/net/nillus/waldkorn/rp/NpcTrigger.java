package net.nillus.waldkorn.rp;

import java.util.Random;

public class NpcTrigger
{
	private Random m_random;
	private String[] m_words;
	private String[] m_replies;
	private String m_item;
	private String[] m_servedReplies;
	
	public NpcTrigger(String[] words, String[] replies, String item, String[] servedReplies, Random random)
	{
		m_random = random;
		m_words = words;
		m_replies = replies;
		m_item = item;
		m_servedReplies = servedReplies;
	}
	
	public boolean trigger(String word)
	{
		word = word.replace("?", ""); word = word.replace("!", "");
		for (int x = 0; x < m_words.length; x++)
		{
			if (m_words[x].equalsIgnoreCase(word))
			{
				return true;
			}
		}
		return false;
	}
	
	public String getReply()
	{
		return m_replies[m_random.nextInt(m_replies.length)];
	}
	
	public String getItem()
	{
		return m_item;
	}
	
	public String getServedReply()
	{
		return m_servedReplies[m_random.nextInt(m_servedReplies.length)];
	}
	
	public boolean isServeTrigger()
	{
		return (m_item != null);
	}
}
