package com.semaifour.facesix.fsql;

public class FuncFlushNote {
	
	String  field;
	Object	value;
	
	public FuncFlushNote(String field, Object value) {
		this.field = field;
		this.value = value;
	}

	@Override
	public String toString() {
		return "FuncFlushNote [field=" + field + ", value=" + value + "]";
	}
}
