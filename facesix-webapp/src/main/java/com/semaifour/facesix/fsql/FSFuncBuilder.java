package com.semaifour.facesix.fsql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.semaifour.facesix.fsql.func.FSFunc;
import com.semaifour.facesix.fsql.func.FSFuncBucket;
import com.semaifour.facesix.fsql.func.FSFuncBucketXL;
import com.semaifour.facesix.fsql.func.FSFuncColumn;
import com.semaifour.facesix.fsql.func.FSFuncCount;
import com.semaifour.facesix.fsql.func.FSFuncMax;
import com.semaifour.facesix.fsql.func.FSFuncMean;
import com.semaifour.facesix.fsql.func.FSFuncMin;
import com.semaifour.facesix.fsql.func.FSFuncRow;
import com.semaifour.facesix.fsql.func.FSFuncSum;
import com.semaifour.facesix.fsql.func.FSFuncValue;
import com.semaifour.facesix.fsql.func.FSFuncValues;

@Component
public class FSFuncBuilder {
	
	static Logger LOG = LoggerFactory.getLogger(FSFuncBuilder.class.getName());

	public static FSFunc createFunc(String func, String params,String ocolumn,FSql fsql) {
		if (func.equalsIgnoreCase("bucket")) return new FSFuncBucket(func, params, ocolumn,fsql);
		if (func.equalsIgnoreCase("bucketxl")) return new FSFuncBucketXL(func, params, ocolumn,fsql);
		if (func.equalsIgnoreCase("sum")) return new FSFuncSum(func, params, ocolumn,fsql);
		if (func.equalsIgnoreCase("min")) return new FSFuncMin(func, params, ocolumn,fsql);
		if (func.equalsIgnoreCase("max")) return new FSFuncMax(func, params, ocolumn,fsql);
		if (func.equalsIgnoreCase("mean")) return new FSFuncMean(func, params, ocolumn,fsql);
		if (func.equalsIgnoreCase("count")) return new FSFuncCount(func, params, ocolumn,fsql);
		if (func.equalsIgnoreCase("value")) return new FSFuncValue(func, params, ocolumn,fsql);
		if (func.equalsIgnoreCase("values")) return new FSFuncValues(func, params, ocolumn,fsql);
		if (func.equalsIgnoreCase("row")) return new FSFuncRow(func, params, ocolumn,fsql);
		if (func.equalsIgnoreCase("column")) return new FSFuncColumn(func, params, ocolumn,fsql);


		return new FSFunc(func, params, ocolumn,fsql);
	}

}
