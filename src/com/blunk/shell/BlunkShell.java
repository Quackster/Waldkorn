package com.blunk.shell;

import java.net.URL;

import sun.net.www.content.text.PlainTextInputStream;
import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * BlunkShell is a Blunk component providing dynamic Java code evaluation, by using BeanShell.
 * 
 * @author Nillus
 * @see http://www.beanshell.org/
 */
public class BlunkShell
{
	private Interpreter m_interpreter;
	private final static String REMOTESCRIPTS_DIRECTORY = "http://www.suelake.com/server/shell/";
	
	public BlunkShell()
	{
		m_interpreter = new Interpreter();
	}
	
	public boolean setVariable(String name, Object value)
	{
		try
		{
			m_interpreter.set(name, value);
			return true;
		}
		catch (EvalError ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public Object getVariable(String name)
	{
		try
		{
			return m_interpreter.get(name);
		}
		catch (EvalError ex)
		{
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public Object evaluate(String java) throws Exception
	{
		return m_interpreter.eval(java);
	}
	
	public Object evaluateFile(String scriptPath) throws Exception
	{
		return m_interpreter.source(scriptPath);
	}
	
	public Object evaluateScript(String scriptName) throws Exception
	{
		String scriptPath = "scripts/" + scriptName + ".blunk";
		return m_interpreter.source(scriptPath);
	}
	
	public Object evaluateRemoteScript(String scriptName) throws Exception
	{
		String java;
		String url = BlunkShell.REMOTESCRIPTS_DIRECTORY + scriptName + ".blunk";
		
		try
		{
			// Read remote bytes over HTTP
			PlainTextInputStream stream = (PlainTextInputStream)new URL(url).getContent();
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			stream.close();
			
			// Create string
			java = new String(bytes);
		}
		catch(Exception ex)
		{
			throw new Exception("Failed to get valid response from remote scripting library server.");
		}
		
		// Script found?
		if(java.equals("EOF"))
		{
			throw new Exception("Script \"" + scriptName + "\" was not present in the remote scripting library!");
		}
		else
		{
			return m_interpreter.eval(java);
		}
	}
	
	public Interpreter getInterpreter()
	{
		return m_interpreter;
	}
	
	public BshClassManager getClassManager()
	{
		return m_interpreter.getClassManager();
	}
}
