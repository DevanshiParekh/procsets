package com.semaifour.facesix.fsql.func;

import java.util.Map;

import com.semaifour.facesix.fsql.FSql;

/**
 * 
 * Func row(..) to make it like "SELECT *"
 * 
 * <code>
 *  Usage:
 *  
 *  row(*, *, [include=col1:col2:col3:exclude=col1:col2:col3])
 * 
 * params:
 * 
 *  include - a list of columns separated by colon (:) to be included
 *  
 *  exclude - a list of columns separated by colon (:) to be excluded. if param:include is given, 'param:exclude' is ignored.
 *  
 * </code>
 * 
 * @author mjs
 *
 */
public class FSFuncRow extends FSFunc {

	public FSFuncRow(String func, String params, String ocolumn, FSql fsql) {
		super(func, params, ocolumn, fsql);
	}
	
	@Override
	public Map<String, Object> exe(Object value, Map<String, Object> target, Map<String, Object> source) {
		if (!isiftrue(value, target, source)) return target;

		if (params.containsKey("include")) {
			String[] cols = params.get("include").split(":");
			for (String c : cols) {
				String[] cc = c.split(">");
				target.put(cc.length > 1 ? cc[1] : cc[0], evalueOf(source.get(cc[0]), source, target));
			}
		} else if (params.containsKey("exclude")) {
			target.putAll(source);
			String[] cols = params.get("exclude").split(":");
			for (String c : cols) {
				target.remove(c);
			}
		} else {
			target.putAll(source);
		}
		return target;
	}
	
	@Override
	public void flush(Map<String, Object> target) {
		reset();
	}

}
