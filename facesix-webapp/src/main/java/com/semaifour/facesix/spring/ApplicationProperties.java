package com.semaifour.facesix.spring;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;


/**
 * Application Properties Configuration
 * 
 * @author mjs
 *
 */
@Configuration
@PropertySource("classpath:application.${fs.app:default}.${fs.env:default}.properties")
public class ApplicationProperties {
	
	static Logger LOG = LoggerFactory.getLogger(ApplicationProperties.class.getName());

	protected String app;
	protected String env;
	protected String node;
	protected String cluster;
	protected String id;
	
	@Autowired
	protected Environment environment;
	
	protected String[] restrictedUris;
	
	public ApplicationProperties() {
		app = System.getProperty("fs.app", "app01");
		env = System.getProperty("fs.env", "env01");
		node = System.getProperty("fs.node", "node01");
		cluster = System.getProperty("fs.cluster", "cluster01");
		LOG.info("Loading config for app [{}], env [{}], node [{}], cluster[{}]", app, env, node, cluster);
		id = app + "-" + env + "-" + node + "-" + cluster;
	}
	
	public String getInstanceId() {
		return id;
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
	
	public String[] getUnrestrictedUris() {
		if (restrictedUris == null) {
			synchronized (this) {
				if (restrictedUris == null) {
					String tmp = getProperty("facesix.restricted.uris", "/facesix/web,/facesix/rest");
					restrictedUris = tmp.split(",");
				}
			}
		}
		return restrictedUris;
	}

	public String getServerContextPath() {
		return getProperty("server.contextPath", "/facesix");
	}
	
	public String getServerPort() {
		return getProperty("server.port", "8175");
	}
	
	public String getDefaultAdminUser() {
		return getProperty("facesix.admin.user", null);
	}
	
	public String getDefaultAdminSecret() {
		return getProperty("facesix.admin.secret", null);
	}
	
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(getProperty(key));
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		String v = getProperty(key);
		if (v != null) {
			return Boolean.getBoolean(v);
		} else {
			return defaultValue;
		}
	}

	public long getLong(String key, long defaultValue) {
		try {
			return Long.parseLong(getProperty(key));
		} catch (Exception e) {
			return defaultValue;
		}

	}
	
	public int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt(getProperty(key));
		} catch (Exception e) {
			return defaultValue;
		}

	}
	
	public double getFloat(String key, double defaultValue) {
		try {
			return Double.parseDouble(getProperty(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}
	
	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	/**
	 * 
	 * Checks if the config value for the key matches the given value.
	 * 
	 * 
	 * @param key
	 * @param value
	 * @return true if key=value matched the given value, else false.
	 * 
	 */
	public boolean isconfig(String key, String value) {
		String v = getProperty(key);
		if (StringUtils.equals(v, value)) {
			return true;
		} else {
			return false;
		}
	}
	
}