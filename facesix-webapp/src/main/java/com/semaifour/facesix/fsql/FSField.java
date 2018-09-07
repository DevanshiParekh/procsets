package com.semaifour.facesix.fsql;

import com.semaifour.facesix.fsql.func.FSFunc;

public class FSField {
	
	protected int rindex;
	protected String icolumn;
	protected String ocolumn;
	protected FSFunc func;
	
	

	public FSField(int rindex, String icolumn, String ocolumn, FSFunc func) {
		this.rindex = rindex;
		this.icolumn = icolumn.trim();
		this.ocolumn = ocolumn.trim();
		this.func = func;
	}
	
	public int getRindex() {
		return rindex;
	}
	public void setRindex(int rindex) {
		this.rindex = rindex;
	}
	public String getIcolumn() {
		return icolumn;
	}
	public void setIcolumn(String icolumn) {
		this.icolumn = icolumn;
	}
	public String getOcolumn() {
		return ocolumn;
	}
	public void setOcolumn(String ocolumn) {
		this.ocolumn = ocolumn;
	}
	public FSFunc getFunc() {
		return func;
	}

	public void setFunc(FSFunc func) {
		this.func = func;
	}

	@Override
	public String toString() {
		return "FSField [rindex=" + rindex + ", icolumn=" + icolumn
				+ ", ocolumn=" + ocolumn + ", func=" + func + "]";
	}

}