package com.semaifour.facesix.fsql.func;

import java.util.Map;

import com.semaifour.facesix.fsql.FSql;

/**
 * 
 * Func value(..)
 * 
 * <code>
 *  Usage:
 *  
 *  value(in_field, out_field, {if=, eval=, vindex=-1=last value,0=first value,i=nth value, convert=json|integer|float|date|timestamp|time|string})
 * 
 * params:
 * 
 *  vindex - value index is used when aggregations used. 
 *  
 *  vindex set to 0 by default to return the first value. vindex=-1 means, last value
 *  
 * </code>
 * 
 * @author mjs
 *
 */
public class FSFuncValue extends FSFunc {

	private int vindex = 0;
	
	public FSFuncValue(String func, String params, String ocolumn, FSql fsql) {
		super(func, params, ocolumn, fsql);
		try {
			String vin = this.params.get("vindex");
			if (vin != null) {
				vindex = Integer.parseInt(vin);
			}
		} catch (Exception e) {}
	}
	
	@Override
	public Map<String, Object> exe(Object value, Map<String, Object> target, Map<String, Object> source) {
		if (!isiftrue(value, target, source)) return target;

		if (fsql.flags.hasFlushnote == true) {
			flush(target);
		}
		
		if (vindex == 0) {
			//if vindex==0, first value
			lastValue = evalueOf(value, target, source);

		} else if (vindex > 0 ) {
			//if vindex > 0, return value at vindex-th position if found
			if (count == vindex) {
				lastValue = evalueOf(value, target, source);
			}
		} else {
			// if vindex < 0, return last value
			lastValue = evalueOf(value, target, source);
		}
		count++;
		return target;
	}
	
	@Override
	public void flush(Map<String, Object> target) {
		if (lastValue != null) target.put(ocolumn, typecast(lastValue));
		reset();
	}

}
