package com.semaifour.facesix.fsql;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EvalGroovy {

	
	
	private static class Position
	{
	    private int x;
	    private int y;
	    public Position(int x, int y)
	    {
	        super();
	        this.x = x;
	        this.y = y;
	    }
	 
	}
	
	
	public static void main(String[] args) {
		
		ScriptEngineManager factory = new ScriptEngineManager();
	    ScriptEngine engine = factory.getEngineByName("groovy");
	    
	    if(null==engine)
	    {
	        System.err.println("Could not find groovy script engine,make sure to include groovy engine in your classpath");
	    }
	    try
	    {
	        // basic groovy expression example
	        System.out.println(engine.eval("(1..10).sum()"));
	 
	        // example showing scripting with variables (object method invoking)
	        engine.put("first", "HELLO");
	        engine.put("second", "world");
	        System.out.println(engine.eval("first.toLowerCase() + second.toUpperCase()"));
	 
	        //example with boolean expression
	        engine.put("m", 3);
	        engine.put("n", 9);
	        System.out.println(engine.eval("m<n"));
	 
	        //example with variable object and object member access:
	        Position pos = new Position(100, 200);
	        engine.put("p", pos);
	        System.out.println(engine.eval("p.y-p.x>0"));
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
	
	}
	    
