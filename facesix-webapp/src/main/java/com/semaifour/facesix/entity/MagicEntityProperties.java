package com.semaifour.facesix.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;


/**
 * Magic Entity Properties Configuration
 * 
 * @author mjs
 *
 */
@Configuration
@PropertySource("classpath:${fs.entities:entities}.properties")
public class MagicEntityProperties {
	
	static Logger LOG = LoggerFactory.getLogger(MagicEntityProperties.class.getName());

	@Autowired
	protected Environment environment;
	
	public MagicEntityProperties() {
		LOG.info("loaded :" + this.getClass().getCanonicalName());
	}
	
	public Environment environment() {
		return environment;
	}


	public String getProperty(String key) {
		return environment().getProperty(key);
	}
	
	public String getProperty(String key, String defaultValue) {
		return environment().getProperty(key, defaultValue);
	}
}