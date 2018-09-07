package com.semaifour.facesix.fsql.func;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semaifour.facesix.fsql.FSql;

public class FSFuncMax extends FSFunc {

	static Logger LOG = LoggerFactory.getLogger(FSFuncMax.class.getName());

	public FSFuncMax(String func, String params, String ocolumn, FSql fsql) {
		super(func, params, ocolumn,fsql);
		fsql.flags.isAggregate = true;
	}
	
	@Override
	public Object evalueOf(Object value,  Map<String, Object> target, Map<String, Object> source) {
		value = value != null ? value : params.get("default");
		try {
			return Double.parseDouble(String.valueOf(value));
		} catch (Exception e) {
			return new Double(0);
		}
	}

	@Override
	public Map<String, Object> exe(Object value, Map<String, Object> target, Map<String, Object> source) {
		//sum only when iffify is true
		if (!isiftrue(value, target, source)) return target;

		count++;
		if (fsql.flags.hasFlushnote == true) {
			flush(target);
		}
		if (lastValue == null) {
			lastValue = evalueOf(value, target, source);
		} else {
			Double v = (Double) evalueOf(value, target, source);
			lastValue = (Double)lastValue > v ? lastValue : v;
		}
		return target;
	}
	
	@Override
	public void flush(Map<String, Object> target) {
		target.put(ocolumn, lastValue);
		reset();
	}
	
}
