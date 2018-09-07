package com.semaifour.facesix.fsql.func;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semaifour.facesix.fsql.FSql;

/**
 * 
 * Count function
 * 
 * <code>
 * usage:
 * 		count(in_field_name, out_field_name, if=xxx&global=true/false)
 * </code>
 * 
 * @author mjs
 *
 */
public class FSFuncCount extends FSFunc {

	static Logger LOG = LoggerFactory.getLogger(FSFuncCount.class.getName());

	public FSFuncCount(String func, String params, String ocolumn, FSql fsql) {
		super(func, params, ocolumn,fsql);
		fsql.flags.isAggregate = true;
	}

	@Override
	public Map<String, Object> exe(Object value, Map<String, Object> target, Map<String, Object> source) {
		//count only when iffify is true
		if (!isiftrue(value, target, source)) return target;
		
		if (fsql.flags.hasFlushnote == true) {
			flush(target);
		}
		count++;
		return target;
	}
	
	@Override
	public void flush(Map<String, Object> target) {
		target.put(ocolumn, count);
		if (!this.params.containsKey("global")) {
			reset();
		}
	}
	
}
