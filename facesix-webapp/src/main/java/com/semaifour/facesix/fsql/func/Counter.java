package com.semaifour.facesix.fsql.func;

import java.io.Serializable;

public class Counter implements Serializable {

	private static final long serialVersionUID = -8405721551751322225L;

	private long count;
	
	public Counter() {
		this.count = 0;
	}
	
	public Counter(int i) {
		this.count = i;
	}

	public long increment() {
		this.count++;
		return count;
	}
	
	public long decrement() {
		this.count--;
		return count;
	}
	
	public int intValue() {
		return (int) this.count;
	}
	
	public long longValue() {
		return this.count;
	}
	
	public long getCount() {
		return count;
	}
	
	public void setCount(long count) {
		this.count = count;
	}
	
	@Override
	public String toString() {
		return String.valueOf(count);
	}
}
