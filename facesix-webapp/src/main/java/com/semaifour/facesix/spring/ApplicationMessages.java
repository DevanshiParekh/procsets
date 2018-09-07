package com.semaifour.facesix.spring;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;


/**
 * Application Messages Configuration
 * 
 * @author mjs
 *
 */
@Configuration
@PropertySource("classpath:messages.${fs.app:default}.properties")
public class ApplicationMessages {
	
	static Logger LOG = LoggerFactory.getLogger(ApplicationMessages.class.getName());

	@Resource
	protected Environment environment;
	
	public ApplicationMessages() {
		LOG.info("loaded :" + this.getClass().getCanonicalName());
	}
	
	public Environment environment() {
		return environment;
	}


	public String getMessage(String key) {
		return environment().getProperty(key);
	}
	
	public String getMessage(String key, String defaultValue) {
		return environment().getProperty(key, defaultValue);
	}
	
	
}