package com.semaifour.facesix.fsql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.unit.TimeValue;

import com.semaifour.facesix.domain.FSGrok;
import com.semaifour.facesix.fsql.func.FSFunc;
import com.semaifour.facesix.fsql.func.FSFuncFlag;

import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;

/**
 * 
 * FSql - Facesix Query Language
 * 
 * 
 *  String fsql = "index=indexname, type=type, q=query, sort=field ASC|DESC, filter=query, from=0, size=100
 *  					|value(f1,F1,default=xyz&this=that); min(f2,F2,params); bucket(f3,F3,none); 
 *  					 eval($f1+$f2/100,F2,percentile)**
 *  					|table,sort=F1:desc";
 * 
 * 
 * @author mjs
 *
 */
public class FSql extends Observable {
	
	public enum EXEMODE {
		
		PARALLEL("PARALLEL"),
		SERIAL("SERIAL");

	    private final String exemode;

	    private EXEMODE(final String exemode) {
	        this.exemode = exemode.toUpperCase();
	    }
	    
	    @Override
	    public String toString() {
	        return exemode;
	    }

		public static EXEMODE valueof(String val) {
			if (StringUtils.equalsIgnoreCase(val, "SERIAL")) {
				return EXEMODE.SERIAL;
			} else {
				return EXEMODE.PARALLEL;
			}
		}
	}	
	
	private Map<String, String> query;
	protected List<FSField> fields;
	protected List<String> fieldNames;
	protected Map<String, String> output;
	public FSFuncFlag flags;
	public Map<String, FSField> fieldMap;
	public EXEMODE exemode = EXEMODE.PARALLEL; 
	
	private FSql() { 
		query = new HashMap<String, String>();
		fields = new ArrayList<FSField>();
		output = new HashMap<String, String>();
		flags = new FSFuncFlag();
		fieldNames = new ArrayList<String>();
		fieldMap = new HashMap<String, FSField>();
	}
	
	public static FSql parse(String fsql) {
		FSql instance = new FSql();
		String[] fsqlo = fsql.split("\\|");
		
		//query information
		String tmp = fsqlo[0];
		String[] tmpo = tmp.split(",");
		
		for(String s : tmpo) {
			s = StringUtils.trim(s);
			
			String[] so = s.split("=", 2);
			if (so.length == 2) {
				instance.query.put(StringUtils.trim(so[0]), StringUtils.trim(so[1]));
			} else {
				instance.query.put(s, s);
			}
		}
		
		//fields & transformation
		tmp = fsqlo[1];
		tmp = tmp.trim();
		if (StringUtils.startsWith(tmp, "#")) {
			tmp = tmp.substring(1, tmp.indexOf(";"));
			instance.exemode = EXEMODE.valueof(tmp);
			instance.exemode = instance.exemode == null ? EXEMODE.PARALLEL : instance.exemode;
			tmp = fsqlo[1].substring(fsqlo[1].indexOf(";") + 1);
		}
		
		//Grok grok = FSGrok.newGrok("%{DATA:func}\\(%{DATA:icolumn},%{DATA:ocolumn},%{DATA:params}\\)");
		//Match gm = grok.match(s);
		//gm.captures();
		//map = gm.toMap();
		//FSFunc func = new FSFunc(String.valueOf(map.get("func")),
		//		 String.valueOf(map.get("params")), String.valueOf(map.get("ocolumn")));
		
		tmpo = tmp.split(";");
		FSField field  = null;
		int rindex = 0;
		for(String s : tmpo) {
			s = s.trim();
			Map<String, Object> map = new HashMap<String, Object>(); 
			String f = s.substring(0, s.indexOf("("));
			map.put("func", StringUtils.trim(f));
			s = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
			String[] sig = s.split(",", 3);
			
			map.put("icolumn", StringUtils.trim(sig[0]));
			map.put("ocolumn", StringUtils.trim(sig[1]));
			map.put("params", StringUtils.trim(sig[2]));

			FSFunc func = FSFuncBuilder.createFunc(String.valueOf(map.get("func")), String.valueOf(map.get("params")), String.valueOf(map.get("ocolumn")), instance);
			
			
			field = new FSField(rindex++,
										String.valueOf(map.get("icolumn")),
										String.valueOf(map.get("ocolumn")), func);
			
			instance.fields.add(field);
			instance.fieldNames.add(field.getOcolumn());
			instance.fieldMap.put(field.getIcolumn(), field);
			instance.fieldMap.put(field.getOcolumn(), field);
		}
		
		//output format & delivery details
		tmp = fsqlo[2];
		tmpo = tmp.split(",");
		for(String s : tmpo) {
			s = StringUtils.trim(s);
			String[] so = s.split("=", 2);
			if (so.length == 2) {
				instance.output.put(StringUtils.trim(so[0]), StringUtils.trim(so[1]));
			} else {
				instance.output.put(s, s);
			}
		}
		return instance;
	}

	public String getIndex() {
		return query.get("index");
	}

	public String getType() {
		return query.get("type");
	}

	public int getSize() {
		try {
			return Integer.parseInt(query.get("size"));
		} catch (Exception e) {
			return 500;
		}
	}
	
	public String getQuery() {
		return query.get("query");
	}

	public String getSort() {
		return query.get("sort");
	}
	
	public List<Sort> getSorts() {
		return parseSorts(getSort());
	}
	
	public static List<Sort> parseSorts(String sort) {
		List<Sort> sorts = new ArrayList<Sort>();

		if (sort == null) return sorts;
		
		String[] tmpo = sort.split(",");
		String[] so = null;
		for (String s : tmpo) {
			so = s.split("\\s+");
			if (so.length == 2) {
				sorts.add(new Sort(so[0], so[1].toUpperCase()));
			} else {
				sorts.add(new Sort(s, "ASC"));
			}
		}
		return sorts;
	}

	public List<FSField> getFileds() {
		return fields;
	}

	public String getOformat() {
		return output.get("oformat");
	}
	
	
	public void triggerFlush(FuncFlushNote note) {
		super.setChanged();
		super.notifyObservers(note);
	}

	public FSFuncFlag getFlags() {
		return flags;
	}

	public void setFlags(FSFuncFlag flags) {
		this.flags = flags;
	}
	
	public Map<String, String> output() {
		return this.output;
	}
	
	public Map<String, String> query() {
		return this.query;
	}
	
	@Override
	public String toString() {
		return "FSql [query=" + query + ", fields=" + fields + ", output="
				+ output + "]";
	}

	public static void main(String[] args) {
		String fsql = "index=indexname, type=type, q=query, sort=field|value(f1,F1,default=xyz&this=that); min(f2,F2,params); bucket(f3,F3,none); eval($f1+$f2/100,F2,percentile)|table";
		FSql  o = FSql.parse(fsql);
		System.out.println(o);
	}

	public int getFrom() {
		try {
			return query.get("from") != null ? Integer.parseInt(query.get("from")) : 0;
		} catch(Exception e) {
			return 0;
		}
	}

	public TimeValue getTimeout() {
		try {
			return TimeValue.timeValueMillis(query.get("timeout") != null ? Integer.parseInt(query.get("timeout")) : 60000);
		} catch(Exception e) {
			return TimeValue.timeValueMillis(60000);
		}	}

	public List<String> getFieldNames() {
		return this.fieldNames;
	}

	public FSField getFiled(String field) {
		return fieldMap.get(field);
	}
	
}
