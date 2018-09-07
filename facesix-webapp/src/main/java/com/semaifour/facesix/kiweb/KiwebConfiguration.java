package com.semaifour.facesix.kiweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.semaifour.facesix.spring.ApplicationProperties;

/**
 * Kiweb Configuration
 * 
 * @author mjs
 *
 */
@Component
public class KiwebConfiguration {
	
	@Autowired
	ApplicationProperties properties;
	
	public ApplicationProperties properties() {
		return properties;
	}
	
	
	public String getWebUrl() {
		return properties().getProperty("kiweb.weburl");
	}
	
}