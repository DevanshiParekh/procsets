package com.semaifour.facesix.fsql;

/*import static org.junit.Assert.*;
import org.junit.Test;*/

public class FSqlTest {

	/*@Test
	public void testSerialQueryParsing() {
		String fsql = "index=packetbeat*, type=http, query=+@timestamp:>now-15d, sort=method, size=10000"
				+ "|#serial;bucket(method,Method,default=NA); count(method,Count,params); mean(responsetime,Response Time,params); "
				+ "sum(responsetime,Sum Response Time,params);sum(bytes_in, Bytes In, default=0)|table";
		FSql fsqlo = FSql.parse(fsql);
		assertEquals(fsqlo.exemode, FSql.EXEMODE.SERIAL);
		
	}
	
	@Test
	public void testParallelQueryParsing() {
		String fsql = "index=packetbeat*, type=http, query=+@timestamp:>now-15d, sort=method, size=10000"
				+ "|#parallel;bucket(method,Method,default=NA); count(method,Count,params); mean(responsetime,Response Time,params); "
				+ "sum(responsetime,Sum Response Time,params);sum(bytes_in, Bytes In, default=0)|table";
		FSql fsqlo = FSql.parse(fsql);
		assertEquals(fsqlo.exemode, FSql.EXEMODE.PARALLEL);
	}
	
	@Test
	public void testDefaultQueryParsing() {
		String fsql = "index=packetbeat*, type=http, query=+@timestamp:>now-15d, sort=method, size=10000"
				+ "|bucket(method,Method,default=NA); count(method,Count,params); mean(responsetime,Response Time,params); "
				+ "sum(responsetime,Sum Response Time,params);sum(bytes_in, Bytes In, default=0)|table";
		FSql fsqlo = FSql.parse(fsql);
		assertEquals(fsqlo.exemode, FSql.EXEMODE.PARALLEL);
		
	}*/
	
}
