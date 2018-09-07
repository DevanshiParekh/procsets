package com.semaifour.facesix.fsql.func;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semaifour.facesix.fsql.FSql;

public class FSFuncSum extends FSFunc {

	static Logger LOG = LoggerFactory.getLogger(FSFuncSum.class.getName());

	public FSFuncSum(String func, String params, String ocolumn, FSql fsql) {
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
			Double sum = (Double) target.get(ocolumn);
			if (sum == null) sum = new Double(0);
			if (lastValue == null) lastValue = new Double(0);
			sum += (Double) lastValue;
			target.put(ocolumn, sum);
			lastValue = evalueOf(value, target, source);
		}
		return target;
	}
	
	@Override
	public void flush(Map<String, Object> target) {
		//if flush request is received from someone else.
		//so we need to finalize current target
		//just wrap up
		Double sum = (Double) target.get(ocolumn);
		if (sum == null) sum = new Double(0);
		if (lastValue == null) lastValue = new Double(0);
		sum += (Double) lastValue;
		target.put(ocolumn, sum);
		reset();
	}
	
}
