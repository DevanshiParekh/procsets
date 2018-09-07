package com.semaifour.facesix.http.proxy;

import org.springframework.web.client.RestOperations;

public class ElasticRestProxy extends HttpProxy {

	public ElasticRestProxy(RestOperations restTemplate, String host, int port) {
		super(restTemplate, host, port);
	}

}
