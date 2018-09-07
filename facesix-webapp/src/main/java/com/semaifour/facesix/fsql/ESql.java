package com.semaifour.facesix.fsql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.semaifour.facesix.domain.FSGrok;
import com.semaifour.facesix.fsql.func.FSFunc;
import com.semaifour.facesix.fsql.func.FSFuncFlag;

public class ESql {
	
	private Map<String, String> query;
	protected List<FSField> fields;
	protected Map<String, String> output;
	protected FSFuncFlag flag;
	
	private ESql() { 
		query = new HashMap<String, String>();
		fields = new ArrayList<FSField>();
		output = new HashMap<String, String>();
		flag = new FSFuncFlag();
	}
	
	public static ESql parse(String fsql) {
		ESql instance = new ESql();
		String[] fsqlo = fsql.split("\\|");
		
		//query information
		String tmp = fsqlo[0];
		String[] tmpo = tmp.split(",");
		
		for(String s : tmpo) {
			s = StringUtils.trim(s);
			String[] so = s.split("=");
			if (so.length == 2) {
				instance.query.put(StringUtils.trim(so[0]), StringUtils.trim(so[1]));
			} else {
				instance.query.put(s, s);
			}
		}
		
		//fields & transformation
		tmp = fsqlo[1];
		tmpo = tmp.split(";");
		Grok grok = FSGrok.newGrok("%{DATA:func}\\(%{DATA:icolumn},%{DATA:ocolumn},%{DATA:params}\\)");
		FSField field  = null;
		int rindex = 0;
		for(String s : tmpo) {
			s = s.trim();
			Match gm = grok.match(s);
			gm.captures();
			
			Map<String, Object> map = gm.toMap();
			
			FSFunc func = null;//FSFuncBuilder.createFunc(String.valueOf(map.get("func")), String.valueOf(map.get("params")), String.valueOf(map.get("ocolumn")), instance);
			
			//FSFunc func = new FSFunc(String.valueOf(map.get("func")),
			//		 String.valueOf(map.get("params")), String.valueOf(map.get("ocolumn")));
			
			field = new FSField(rindex++,
										String.valueOf(map.get("icolumn")),
										String.valueOf(map.get("ocolumn")), func);
			
			instance.fields.add(field);
			
			StringBuilder agg = new StringBuilder();
			
			
		}
		
		//output format & delivery details
		tmp = fsqlo[2];
		tmpo = tmp.split(",");
		for(String s : tmpo) {
			s = StringUtils.trim(s);
			String[] so = s.split("=");
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
		String tmp = getSort();
		String[] tmpo = tmp.split(",");
		List<Sort> sorts = new ArrayList<Sort>();
		String[] so = null;
		for (String s : tmpo) {
			so = s.split("");
			if (so.length == 2) {
				sorts.add(new Sort(Direction.valueOf(so[1]), so[0]));
			} else {
				sorts.add(new Sort(Direction.ASC, s));
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

	
	@Override
	public String toString() {
		return "FSql [query=" + query + ", fields=" + fields + ", output="
				+ output + "]";
	}

	public static void main(String[] args) {
		String fsql = "index=indexname, type=type, q=query, sort=field|value(f1,F1,default=xyz&this=that); min(f2,F2,params); bucket(f3,F3,none); eval($f1+$f2/100,F2,percentile)|table";
		ESql  o = ESql.parse(fsql);
		System.out.println(o);
	}

	
}

