package com.semaifour.facesix.fsql.func;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semaifour.facesix.fsql.FSql;
import com.semaifour.facesix.util.DateUtils;

public class FSFunc {
	
	public enum Type { STRING, NUMBER, DATE }
	
	static Logger LOG = LoggerFactory.getLogger(FSFunc.class.getName());

	protected String func;
	protected Map<String, String> params;
	protected FSql fsql;
	protected String ocolumn;
	protected Type type;
	
	//last value that has not been committed into current entity yet
	protected Object lastValue;
	protected int count;
	
	protected ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	protected ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("groovy");
	
	//variables to track if flush request & who flushed first
	protected boolean ifflushed = false;

	public FSFunc(String func, String params, String ocolumn, FSql fsql) {
		this.func = StringUtils.trim(func);
		this.ocolumn = ocolumn;
		this.fsql = fsql;
		this.params = new HashMap<String, String>();
		if (params != null) {
			params = StringUtils.trim(params);
			String[] tmpo = params.split("#");
			String[] so = null;
			
			for (String s : tmpo) {
				s = StringUtils.trim(s);
				so = s.split("=", 2);
				if (so.length == 2)  {
					this.params.put(StringUtils.trim(so[0]), StringUtils.trim(so[1]));
				} else {
					this.params.put(s, s);
				}
			}
		}
		
	}
	
	/**
	 * Runs if any 'eval' script is provided in function parameters.
	 * 
	 * <pre>
	 * 	eval=return (source.a>source.b)?target.a:target.b;
	 *  eval=function max_num(a,b){return (a>b)?a:b;} max_num(source.a,source.b);
	 * </pre>
	 * @param value
	 * @param target
	 * @param source
	 * @return returns the value returned by 'eval' script.
	 */
	public Object evalueOf(Object value, Map<String, Object> target, Map<String, Object> source) {

		if (this.params.get("debug") != null) LOG.info("value : [{}]", value);

		if (this.params.get("eval") == null) {
			return value != null ? value : params.get("default");
		} else {
			if (value != null) {
				scriptEngine.put("source", source);
				scriptEngine.put("target", target);
				scriptEngine.put("value", value);
				try {
					return scriptEngine.eval(this.params.get("eval"));
				} catch (Exception e) {
					if (this.params.get("debug") != null) {
						 return value + ":_scriptfailure_eval" + e.toString();
					}
					LOG.warn("Value script error :" + value, e);
					return null;
				}
			} else {
				return params.get("default");
			}
		}
	}
	
	public Object typecast(Object value) {
		if (!this.params.containsKey("typecast"))  return value;
		if ("JSON".equalsIgnoreCase(this.params.get("typecast"))) {
			ObjectMapper mapper = new ObjectMapper();
		    mapper.setSerializationInclusion(Include.NON_NULL);
			try {
				return mapper.readValue(String.valueOf(value), new TypeReference<Map<String,Object>>(){});
			} catch (Exception e) {
				LOG.debug("Failed to typecast:json:" + value, e);
			}
		} else if ("DATE".equalsIgnoreCase(this.params.get("typecast"))) {
			//LOG.info(value.getClass().getName());
			try {
				return DateUtils.parse2Timestamp(String.valueOf(value));
			} catch (Exception e) {
				LOG.info("parse error", e);
				return e.getMessage();
			}
		}
		return value;
	}
	
	public Map<String, Object> exe(Object value, Map<String, Object> target, Map<String, Object> source) {
		if (isiftrue(value, target, source)) {
			target.put(ocolumn, evalueOf(value, target, source));
		}
		return target;
		
	}
	
	/**
	 * 
	 * check if 'if-condition passed' if any 'if-condition' is provided in parameters.
	 * 
	 * if-condition is a groovy script. 
	 * 
	 * The groovy script return boolean. Optional 'debug=true' can be passed to know if there is any script-failure.
	 * 
	 * <code> 
	 * 			if=source.filed>10
	 *          if=target.MyField>10
	 *          if=source.f1+target.f2<=25
	 * </code>
	 * @param target
	 * @param source
	 * @return true if if-script is null or resolved to true, else false.
	 */
	public boolean isiftrue(Object value, Map<String, Object> target, Map<String, Object> source) {
		if (this.params.get("if") == null) return true;
		scriptEngine.put("source", source);
		scriptEngine.put("target", target);
		scriptEngine.put("value", value);

		try {
			return Boolean.valueOf(scriptEngine.eval(this.params.get("if")).toString());
		} catch (ScriptException e) {
			if (this.params.get("debug") != null) {
				target.put(ocolumn + "_scriptfailure_if", e.toString() );
			}
			return false;
		}
	}

	/**
	 * Wrap up and flush current document
	 * 
	 */
	public void flush(Map<String, Object> entity) {
		reset();
	}
	
	/**
	 * 
	 * If it is a XL function (meaning, if it self manages value aggregations), this function returns result docs as list
	 * 
	 * @return
	 */
	public Collection<Map<String, Object>> flushXL() {
		return null;
	}
	
	public void reset() {
		lastValue = null;
		count = 0;
		ifflushed = false;
	}
	
	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	

	@Override
	public String toString() {
		return "FSFunc [func=" + func + ", params=" + params + "]";
	}

}
