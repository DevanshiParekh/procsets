package com.semaifour.facesix.fsql.func;

import java.util.Map;

import com.semaifour.facesix.fsql.FSql;

public class FSFuncValues extends FSFunc {

	StringBuilder sb = null;
	
	public FSFuncValues(String func, String params, String ocolumn, FSql fsql) {
		super(func, params, ocolumn, fsql);
	}
	
	@Override
	public Map<String, Object> exe(Object value, Map<String, Object> target, Map<String, Object> source) {
		if (!isiftrue(value, target, source)) return target;

		count++;
		if (fsql.flags.hasFlushnote == true) {
			flush(target);
		}
		if (lastValue == null) {
			lastValue = evalueOf(value, target, source);
		} else {
			append(lastValue);
			lastValue = evalueOf(value, target, source);
		}
		return target;
	}
	
	
	private void append(Object value) {
		if (sb == null) {
			sb = new StringBuilder("[").append(value);
		} else {
			sb.append(",").append(value);
		}
	}
	
	@Override
	public void flush(Map<String, Object> target) {
		//if flush request is received from someone else.
		//so we need to finalize current target
		//just wrap up
		append(lastValue);
		append("]");
		target.put(ocolumn, sb.toString());
		sb = null;
		reset();
	}


}
