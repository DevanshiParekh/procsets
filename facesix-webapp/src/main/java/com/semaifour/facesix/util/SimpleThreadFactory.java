package com.semaifour.facesix.util;

import java.util.concurrent.ThreadFactory;

/**
 * 
 * Simple Thread Factory
 * 
 * @author mjs
 *
 */
public class SimpleThreadFactory implements ThreadFactory {
	
	private static int _tcount = 0;
	
	private String name;
	private int priority;
	private boolean isDaemon; 
	
	
	public SimpleThreadFactory(String name) {
		this(name, Thread.NORM_PRIORITY, false);
	}
	
	public SimpleThreadFactory(String name, int priority, boolean isDaemon) {
		this.name = name;
		this.priority = priority;
		this.isDaemon = isDaemon;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		_tcount++;
		t.setName(name + "-" + _tcount);
		t.setPriority(priority);
		t.setDaemon(isDaemon);
		return t;
	}

}