package com.semaifour.facesix.fsql.func;

import java.text.DecimalFormat;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semaifour.facesix.fsql.FSql;

/**
 * 
 * 
 * mean(f1,F1,format=#.###)
 * 
 * @author mjs
 *
 */
public class FSFuncMean extends FSFuncSum {
	static Logger LOG = LoggerFactory.getLogger(FSFuncMean.class.getName());


	private DecimalFormat dformat = null;
	public FSFuncMean(String func, String params, String ocolumn, FSql fsql) {
		super(func, params, ocolumn,fsql);
		fsql.flags.isAggregate = true;
		dformat = new DecimalFormat(super.params.getOrDefault("format", "#.##"));
	}
	
	
	@Override
	public void flush(Map<String, Object> entity) {
		//if flush request is received from someone else.
		//so we need to finalize current entity
		//just wrap up
		Double sum = (Double) entity.get(ocolumn);
		if (sum == null) sum = new Double(0);
		if (lastValue == null) lastValue = new Double(0);
		sum += (Double) lastValue;
		entity.put(ocolumn, dformat.format(sum / count));
		reset();
	}
	
}
