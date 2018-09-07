package com.semaifour.facesix.fsql;

import java.text.MessageFormat;

public class ESqlBuilder {
	
	
	
	StringBuilder esql = null;
	
	public ESqlBuilder() {
		esql = new StringBuilder("{");
	}
	
	
	
	public void agg(String variable, String func, String param) {
		esql.append(new Agg(variable, func, param).toString());
	}
	
	public void pipe(String variable, String func, String param) {
		esql.append(new Agg(variable, func, param).toString());
	}
	

	@Override
	public String toString() {
		return esql.append(" }").toString();
	}
}

class Agg {

	static String AGG = "\"agg\" : { \"{0}\" : { \"{1}\" : { \"field\" : \"{2}\"} } }";

	String func;
	String param;
	String variable;
	
	public Agg(String variable, String func, String param) {
		this.variable = variable;
		this.func = func;
		this.param = param;
	}

	@Override
	public String toString() {
		return MessageFormat.format(AGG, new Object[]{variable, func, param});
	}
	
}

class Pipe {

	static String AGG = "\"agg\" : { \"{0}\" : { \"{1}\" : { \"field\" : \"{2}\"} } }";

	String func;
	String param;
	String variable;
	
	public Pipe(String variable, String func, String param) {
		this.variable = variable;
		this.func = func;
		this.param = param;
	}

	@Override
	public String toString() {
		return MessageFormat.format(AGG, new Object[]{variable, func, param});
	}
	
}