package com.semaifour.facesix.proxy.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpProxyBase {

	static Logger LOG = LoggerFactory.getLogger(HttpProxyBase.class.getName());

	
	public String stream2String(InputStreamReader ipsr) {
		BufferedReader rd = new BufferedReader(ipsr);
		StringBuilder output = new StringBuilder();
		try {
			String line = null;
			while ((line = rd.readLine()) != null) {
				output.append(line).append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}
	
	
	public String executeHTTPRequest(HttpUriRequest request) {
		String result = null;
		try {
			HttpClient client =  HttpClientBuilder.create().build();
			HttpResponse response = client.execute(request);
			if (response.getEntity() != null) {
				result = stream2String(new InputStreamReader(response.getEntity().getContent()));
			}
			if (response.getStatusLine().getStatusCode() != 200) {
				LOG.error(request.getMethod() + " " + request.getURI() + "    " + response.getStatusLine() );
			}
		} catch (Exception e) {
			LOG.warn("Proxy Failed  for HTTP Request :" + request.getURI(), e);
		}
		return result;
	}
	
	public HttpEntity extractRequestBody(HttpServletRequest httpRequest) {
		try {
			return new ByteArrayEntity(stream2String(new InputStreamReader(httpRequest.getInputStream())).getBytes("UTF-8"));
		} catch (Exception e) {
			return null;
		}
	}

}