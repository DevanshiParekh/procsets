package com.semaifour.facesix.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simple Object Cache Component
 * 
 * @author mjs
 *
 */

@Component
public class SimpleCache<T> {

	private static final long DEFAULT_CACHE_TTL_MS = 8 * 60 * 60 * 1000;

	static Logger LOG = LoggerFactory.getLogger(ObjectPool.class.getName());

    private Map<String, T> cache = Collections.synchronizedMap(new HashMap<String, T>());

    private SimpleCache() {
    }

    public void putForGood(String cacheKey, T value) {
        cache.put(cacheKey, value);
        if (LOG.isDebugEnabled()) LOG.debug("Cached immortal object for key :" + cacheKey);
    }
    
    /**
     * 
     * Put a cache object with default timer
     * 
     * @param cacheKey
     * @param value
     */
    public void put(String cacheKey, T value) {
    	this.put(cacheKey, value, DEFAULT_CACHE_TTL_MS);
    }
    
    public void put(String cacheKey, T value, long ttlms) {
        cache.put(cacheKey, value);
        setTimer(cacheKey, ttlms);
    }

    public T get(String cacheKey) {
        return cache.get(cacheKey);
    }
    
    public Set<String> keys() {
    	return cache.keySet();
    }

    public Collection<T> values() {
    	return cache.values();
    }

    public T clear(String cacheKey) {
        return cache.put(cacheKey, null);
    }

    public void clear() {
        cache.clear();
    }
	private void setTimer(String cacheKey, long ttlms) {
		Timer t = new Timer("Cache Invalidator :"  + cacheKey, true);
		t.schedule(new CacheInvalidationTask<T>(cacheKey, this), ttlms);
	}

}

class CacheInvalidationTask<T> extends TimerTask {
	
	static Logger LOG = LoggerFactory.getLogger(ClearCacheTimerTask.class.getName());

	SimpleCache<T> simpleCache;
	String cacheKey;
	
	CacheInvalidationTask(String cacheKey, SimpleCache<T> simpleCache) {
		this.cacheKey = cacheKey;
		this.simpleCache = simpleCache;
	}
	
	@Override
	public void run() {
		this.simpleCache.clear(this.cacheKey);
	}
	
}