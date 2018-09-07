package com.semaifour.facesix.spring;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;


/**
 * Application Pages Configuration
 * 
 * @author mjs
 *
 */
@Configuration
@PropertySource("classpath:pages.${fs.app:default}.properties")
public class ApplicationPages {
	
	static Logger LOG = LoggerFactory.getLogger(ApplicationPages.class.getName());

	@Resource
	protected Environment environment;
	private String app_key;
	private String app_value;
	
	public ApplicationPages() {
		LOG.info("loaded :" + this.getClass().getCanonicalName());
	}
	
	public Environment environment() {
		return environment;
	}

	public String getPage(String key, String defaultValue) {
		app_key =  key;
		app_value = defaultValue;
		LOG.info("loaded :" + app_key + app_value);
		return environment().getProperty(key, defaultValue);
	}
	
	public String getkey() {
		return app_key;
	}	
	
	public String getvalue() {
		return app_value;
	}
}