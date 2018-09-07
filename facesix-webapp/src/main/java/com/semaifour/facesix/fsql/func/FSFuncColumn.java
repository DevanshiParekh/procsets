package com.semaifour.facesix.fsql.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.semaifour.facesix.fsql.FSql;

/**
 * 
 * Func column(..) to get an array of values for each column; i.e the vertical values
 * 
 * <code>
 *  Usage:
 *  
 *  column(*, *, NA)
 * 
 * params:
 * 
 *  
 *  
 * </code>
 * 
 * @author mjs
 *
 */
public class FSFuncColumn extends FSFunc {

	public FSFuncColumn(String func, String params, String ocolumn, FSql fsql) {
		super(func, params, ocolumn, fsql);
		fsql.flags.isAggregate = true;
	}
	
	@Override
	public Map<String, Object> exe(Object value, Map<String, Object> target, Map<String, Object> source) {
		if (!isiftrue(value, target, source)) return target;

		List<Object> valist = (List<Object>)target.get(ocolumn);
		if (valist == null) {
			valist = new ArrayList<Object>();
			valist.add(ocolumn);
			target.put(ocolumn, valist);
		}
		valist.add(evalueOf(value, target, source));
		return target;
	}
	
	@Override
	public void flush(Map<String, Object> target) {
		reset();
	}

}
