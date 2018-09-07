package com.semaifour.facesix.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Object Cache Manager
 * 
 * @author mjs
 *
 */
public class SimpleCacheManager {

	static Logger LOG = LoggerFactory.getLogger(ObjectPool.class.getName());

	private static final long DEFAULT_CACHE_TTL_MS = 8 * 60 * 60 * 1000;

    private static SimpleCacheManager instance;
    
    private static Object monitor = new Object();
    
    private static Map<String, Object> cache = Collections.synchronizedMap(new HashMap<String, Object>());

    private SimpleCacheManager() {
    }

    public void putForGood(String cacheKey, Object value) {
        cache.put(cacheKey, value);
        LOG.info("Cached immortal object for key :" + cacheKey);
    }
    
    public void put(String cacheKey, Object value) {
    	this.put(cacheKey, value, DEFAULT_CACHE_TTL_MS);
    }
    
    public void put(String cacheKey, Object value, long ttlms) {
        cache.put(cacheKey, value);
        setTimer(cacheKey, ttlms);
        LOG.info("Cached mortal object for key :" + cacheKey + " | ttlms :" + ttlms);

    }

    public Object get(String cacheKey) {
        return cache.get(cacheKey);
    }
    
    public Set<String> keys() {
    	return cache.keySet();
    }

    public void clear(String cacheKey) {
        cache.put(cacheKey, null);
    }

    public void clear() {
        cache.clear();
    }

    public static SimpleCacheManager getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new SimpleCacheManager();
                    LOG.info("Simple Cache Manager initialized ...");
                }
            }
        }
        return instance;
    }

	private void setTimer(String cacheKey, long ttlms) {
		Timer t = new Timer("Cache Invalidator :"  + cacheKey, true);
		t.schedule(new ClearCacheTimerTask(cacheKey), ttlms);
	}

}

class ClearCacheTimerTask extends TimerTask {
	static Logger LOG = LoggerFactory.getLogger(ClearCacheTimerTask.class.getName());

	String cacheKey;
	
	ClearCacheTimerTask(String cacheKey) {
		this.cacheKey = cacheKey;
	}
	
	@Override
	public void run() {
		SimpleCacheManager.getInstance().clear(this.cacheKey);
		LOG.info("Cleared expired object for key :" + cacheKey);
	}
	
}