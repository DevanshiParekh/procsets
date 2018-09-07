package com.semaifour.facesix.data.graylog;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class GraylogRestClient {
	
	static Logger LOG = LoggerFactory.getLogger(GraylogRestClient.class.getName());

	private String restUrl;
	private MultiValueMap<String, String> headers;
	
	public GraylogRestClient(GraylogConfiguration graylogConfiguration) {
		this.restUrl = graylogConfiguration.getRestUrl();
		this.headers = getHttpHeaders(graylogConfiguration.getPrincipal(), graylogConfiguration.getSecret());
	}
	
	public GraylogRestClient(String restUrl, String principal, String secret) {
		this.restUrl = restUrl;
		this.headers = getHttpHeaders(principal, secret);
	}
	
	public GraylogRestClient(String restUrl) {
		this.restUrl = restUrl;
	}
	
	public <T> ResponseEntity<T> invoke(String path, Object requestBody, Class<T> responseType) {
		return restTemplate().exchange(restUrl + path, HttpMethod.GET, new HttpEntity<Object>(requestBody, headers), responseType);
	}
	
	public <T> ResponseEntity<T> invoke(HttpMethod method, String path, Object requestBody, Class<T> responseType) {
		return restTemplate().exchange(restUrl + path, method, new HttpEntity<Object>(requestBody, headers), responseType);
	}
	
	public void invokePut(String path, Object requestBody) {
		restTemplate().put(restUrl + path, new HttpEntity<Object>(requestBody, headers));
	}
	
	public static RestTemplate restTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		return restTemplate;
	}
	
	public MultiValueMap<String, String> getHttpHeaders(String principal, String secret) {
		MultiValueMap<String, String> headers  = new LinkedMultiValueMap<String, String>();
		try {
			if (principal != null) {
				byte[] basicauth = (principal + ":" + secret) .getBytes("UTF-8");
				headers.add("Authorization", "Basic " + DatatypeConverter.printBase64Binary(basicauth));
			}
			headers.add("Content-Type", "application/json");
			headers.add("Accept", "application/json");
			
		} catch (Exception e) {
			LOG.warn("Failed to setup basic-auth header :" + principal);
		}
		return headers;
	}
	
}