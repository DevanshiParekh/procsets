package com.semaifour.facesix.domain;

import java.net.URL;

import oi.thekraken.grok.api.Grok;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FSGrok {
	
	static Logger LOG = LoggerFactory.getLogger(FSGrok.class.getName());

	public static Grok newGrok(String pattern) {
		try {
			URL path = FSGrok.class.getClassLoader().getResource("patterns/patterns");
			LOG.info("Loading Grok Patterns from :" + path.getFile());
			Grok grok = Grok.create(path.getFile());
			grok.compile(pattern);
			return grok;
		} catch (Exception e) {
			LOG.info("Grok initialization failed:", e);
		}
		return null;
	}
}
