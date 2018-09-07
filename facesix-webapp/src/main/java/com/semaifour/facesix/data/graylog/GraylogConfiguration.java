package com.semaifour.facesix.data.graylog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.semaifour.facesix.spring.ApplicationProperties;
import com.semaifour.facesix.util.Cryptor;

/**
 * Graylog Configuration
 * 
 * @author mjs
 *
 */
@Component
public class GraylogConfiguration {
	
	@Autowired
	ApplicationProperties properties;
	
	@Autowired
	Cryptor cryptor;
	
	public ApplicationProperties properties() {
		return properties;
	}
	
	public String getWebUrl() {
		return properties().getProperty("graylog.weburl");
	}
	
	public String getRestUrl() {
		return properties().getProperty("graylog.resturl");
	}

	public String getPrincipal() {
		return cryptor.iencrypt(properties().getProperty("graylog.principal"));
	}
	
	public String getSecret() {
		return cryptor.iencrypt(properties().getProperty("graylog.secret"));
	}
	
}