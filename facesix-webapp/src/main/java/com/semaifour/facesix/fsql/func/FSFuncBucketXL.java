package com.semaifour.facesix.fsql.func;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import com.semaifour.facesix.fsql.FSql;

public class FSFuncBucketXL extends FSFunc {
	
	private Map<String, Map<String, Object>>termsMap = new HashMap<String, Map<String, Object>>();
	private Map<String, String[]> statsColMap = new HashMap<String, String[]>();
	
	public FSFuncBucketXL(String func, String params, String ocolumn, FSql fsql) {
		super(func, params, ocolumn,fsql);
		fsql.flags.isAggregate = true;
		fsql.flags.isAggregateXL = true;
		String pval = null;
		String[] pvals = null;
		//parse each stats=inColName:outColName
		//build statColMap
		//setup output columns for stats cols
		super.fsql.getFieldNames().add(ocolumn + "__count");
		for(String param : this.getParams().keySet()) {
			if (param.equals("stats")) {
				pval = this.getParams().get(param);
				pvals = pval.split(":");
				statsColMap.put(pvals[0], pvals);
				super.fsql.getFieldNames().add(pvals[1] + "__sum");
				super.fsql.getFieldNames().add(pvals[1] + "__mean");
				super.fsql.getFieldNames().add(pvals[1] + "__max");
				super.fsql.getFieldNames().add(pvals[1] + "__min");
			}
		}
	}

	@Override
	public Map<String, Object> exe(Object value, Map<String, Object> target, Map<String, Object> source) {
		//bucket only when if if-condition is true
		if (!isiftrue(value, target, source)) return target;
		
		value = evalueOf(value, target, source);

		if (fsql.flags.hasFlushnote && !ifflushed) {
			flush(target);
		}
		ifflushed = false;
		String bucketid = resolveValueAsKey(value, target, source);
		Map<String, Object> odoc = termsMap.get(bucketid);
		if (odoc == null) {
			odoc = new HashMap<String, Object>();
			//odoc = target;
			termsMap.put(bucketid, odoc);
			//odoc.putAll(source);
			odoc.put(ocolumn, value);
		}
		Double nnum = new Double(0);
		Double sum = new Double(0);
		Double min = null;
		Double max = null;
		Double mean = null;
		Double count = null;
		for (String[] stat : statsColMap.values()) {
			try {
				nnum = Double.valueOf(String.valueOf(source.get(stat[0])));
			} catch (Exception e) {}
			try {
				sum = Double.valueOf(String.valueOf(odoc.get((stat[1] + "__sum"))));
			} catch (Exception e) {}
			try {
				min = Double.valueOf(String.valueOf(odoc.get((stat[1] + "__min"))));
			} catch (Exception e) {}
			try {
				max = Double.valueOf(String.valueOf(odoc.get((stat[1] + "__max"))));
			} catch (Exception e) {}
			try {
				count = Double.valueOf(String.valueOf(odoc.get((ocolumn + "__count"))));
				count++;
			} catch (Exception e) {}
			

			if (count == null) count = new Double(1);
			odoc.put((ocolumn + "__count"), count);
	
			if (nnum == null) nnum = new Double(0);
			if (sum == null) sum = new Double(0);
			if (min == null || min == 0.0) min = nnum;
			if (max == null) max = nnum;
					
			sum = nnum + sum;
			min = min < nnum ? min > 0.0 ? min : nnum : nnum > 0.0 ? nnum : min;
			max = max > nnum ? max : nnum;
			mean = sum/count;
			odoc.put(stat[1] +"__min", min);
			odoc.put(stat[1] +"__max", max);
			odoc.put(stat[1] +"__mean", mean);
			odoc.put(stat[1] +"__sum", sum);
		}
		return odoc;
	}
	
	/**
	 * resolve and get term value (key value) for the given value 
	 * 	
	 */
    private static SimpleDateFormat ES_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private String resolveValueAsKey(Object v, Map<String, Object> target, Map<String, Object> source) {
		if (this.params.get("evalaskey") != null) {
			scriptEngine.put("source", source);
			scriptEngine.put("target", target);
			scriptEngine.put("v", v);
			try {
				return String.valueOf(scriptEngine.eval(this.params.get("evalaskey")));
			} catch (ScriptException e) {
				if (this.params.get("debug") != null) {
					target.put(ocolumn + "_scriptfailure_issamebucket", e.toString() );
				}
				return String.valueOf(v);
			}
		} else {
			return String.valueOf(v).trim();
		}
	}

	@Override
	public Collection<Map<String, Object>> flushXL() {
		return termsMap.values();
	}

}


