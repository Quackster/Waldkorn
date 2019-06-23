package com.blunk;

public class GarbageCollector implements Runnable
{
	private Thread m_thread;
	private int m_intervalSeconds;
	
	public void start(int intervalInSeconds)
	{
		m_intervalSeconds = intervalInSeconds;
		m_thread = new Thread(this, "Timed GarbageCollector");
		m_thread.setPriority(Thread.MIN_PRIORITY);
		
		m_thread.start();
	}
	
	public void stop()
	{
		m_intervalSeconds = -1;
		m_thread.interrupt();
	}
	
	public void run()
	{
		while(m_intervalSeconds != -1)
		{
			try
			{
				Thread.sleep(m_intervalSeconds * 1000);
			}
			catch (Exception ex)
			{
				
			}
			
			// Force a GC
			System.gc();
		}
	}
}
