package com.semaifour.facesix.fsql.func;

import java.text.SimpleDateFormat;
import java.util.Map;

import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semaifour.facesix.fsql.FSql;

public class FSFuncBucket extends FSFunc {
	
	static Logger LOG = LoggerFactory.getLogger(FSFuncBucket.class.getName());

	public FSFuncBucket(String func, String params, String ocolumn, FSql fsql) {
		super(func, params, ocolumn,fsql);
		fsql.flags.isAggregate = true;
	}

	@Override
	public Map<String, Object> exe(Object value, Map<String, Object> target, Map<String, Object> source) {
		LOG.info("value :" + value);
		//bucket only when if if-condition is true
		if (!isiftrue(value, target, source)) return target;
		
		value = evalueOf(value, target, source);

		if (fsql.flags.hasFlushnote && !ifflushed) {
			flush(target);
		}
		ifflushed = false;
		if (lastValue == null) {
			//this is a new value for sure; 0-th iteration 
			//or iteration after someone triggered flushe
			//so just hold the value in lastValue
			//until we hear from every func/field to flush
			lastValue = value;
			count = 1;
		} else {
			//this, could be i-th iteration
			//if (lastValue.equals(value)) {
			if (isSameBucket(lastValue, value, target, source)) {
				//just set current value as lastknown value; dont have to ;-)
				//target.put(ocolumn, value(lastValue) + "(" + count + ")");
				//lastValue = value(value);
				count++;
			} else {
				//found a new value for bucketing, 
				//we have to wrap up current target and flush it
				//so i finalize current doc and notify others.
				flush(target);
				//mark it is me flushed first
				fsql.flags.hasFlushnote = true;
				ifflushed = true;
				//notify others to flush currenttarget with lastValue
				//fsql.triggerFlush(new FuncFlushNote(ocolumn, lastValue));
				//hold current new value in lastValue and continue
				lastValue = value;
				//reset count for current value is 1
				count = 1;
			}
		}
		
		return target;
	}
	
	/**
	 * Determines if the gives values belong to same bucket by invoking the script "issamebucket" in function parameters
	 * 
	 * @param v1
	 * @param v2
	 */
	
    private static SimpleDateFormat ES_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private boolean isSameBucket(Object v1, Object v2, Map<String, Object> target, Map<String, Object> source) {
		if (this.params.get("issamebucket") != null) {
			scriptEngine.put("source", source);
			scriptEngine.put("target", target);
			scriptEngine.put("v1", v1);
			scriptEngine.put("v2", v2);	
			try {
				return Boolean.valueOf(scriptEngine.eval(this.params.get("issamebucket")).toString());
			} catch (ScriptException e) {
				if (this.params.get("debug") != null) {
					target.put(ocolumn + "_scriptfailure_issamebucket", e.toString() );
				}
				return false;
			}
		} else if (this.params.get("interval") != null) {
			try {
				long interval = Long.parseLong(this.params.get("interval"));
				long t1 = 0;
				long t2 = 0;
				
				if (v1 != null) t1 = ES_DATEFORMAT.parse(String.valueOf(v1)).getTime();
				if (v2 != null) t2 = ES_DATEFORMAT.parse(String.valueOf(v2)).getTime();
				if (t1 > 0 && t2 > 0) {
					t1 = t1-t2;
					if (t1 < 0) t1 = t1 * -1;
					if (t1 <= interval) return true;
				}
			} catch(Exception e) {
				if (this.params.get("debug") != null) {
					target.put(ocolumn + "_scriptfailure_interval", e.toString() );
				}
				return false;
			}
		} else if (v1 instanceof String) {
				LOG.info("["+ v1 + "]==["+v2+"]");
				return String.valueOf(v1).trim().equals(String.valueOf(v2));
		} else {
			return v1.equals(v2);
		}
		return false;
	}
	
	@Override
	public void flush(Map<String, Object> target) {
		target.put(ocolumn, lastValue);
		if (params.containsKey("count")) {
			target.put(ocolumn + "_" + params.get("count"), count);
		}
		reset();
	}
}
