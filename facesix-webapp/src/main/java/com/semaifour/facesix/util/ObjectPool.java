package com.semaifour.facesix.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Object Pool
 * 
 * @author mjs
 *
 * @param <T>
 */
public abstract class ObjectPool<T extends AutoCloseable> {
	
	Logger LOG = LoggerFactory.getLogger(this.getClass());

	private String name;
	
    private ConcurrentLinkedQueue<T> pool;

    private ScheduledExecutorService executorService;

    /**
     * Creates the pool.
     *
     * @param minIdle minimum number of objects residing in the pool
     */
    public ObjectPool(final String name) {
    	this.name = name;
    }
    
    /**
     * Creates the pool.
     *
     * @param minIdle minimum number of objects residing in the pool
     */
    public ObjectPool(final String name, final int minIdle) {
    	this(name);
        // initialize pool
        initialize(minIdle);
    }

    /**
     * Creates the pool.
     *
     * @param minIdle            minimum number of objects residing in the pool
     * @param maxIdle            maximum number of objects residing in the pool
     * @param validationInterval time in seconds for periodical checking of minIdle / maxIdle conditions in a separate thread.
     *                           When the number of objects is less than minIdle, missing instances will be created.
     *                           When the number of objects is greater than maxIdle, too many instances will be removed.
     */
    public ObjectPool(final String name, final int minIdle, final int maxIdle, final long validationInterval) {
    	this(name);
    	
        // initialize pool
        initialize(minIdle);

        // check pool conditions in a separate thread
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new Runnable()
        {
            @Override
            public void run() {
                LOG.info("Cleaning pool : " + name + " for minIdle :" + minIdle + ", maxIdle :" + maxIdle + ", refereshInterval :" + validationInterval);
                int size = pool.size();
                if (size < minIdle) {
                    int sizeToBeAdded = minIdle - size;
                    for (int i = 0; i < sizeToBeAdded; i++) {
                        pool.add(createObject());
                    }
                    LOG.info("pool: " + name + ", objected added :" + sizeToBeAdded);
                } else if (size > maxIdle) {
                    int sizeToBeRemoved = size - maxIdle;
                    for (int i = 0; i < sizeToBeRemoved; i++) {
                        try {
							pool.poll().close();
						} catch (Exception e) {
							LOG.warn("Error closing object " , e);
						}
                    }
                    LOG.info("pool: " + name + ", objected removed :" + sizeToBeRemoved);
                }
            }
        }, validationInterval, validationInterval, TimeUnit.SECONDS);
        LOG.info("Initialized pool : " + name + " with minIdle :" + minIdle + ", maxIdle :" + maxIdle + ", refereshInterval :" + validationInterval);
    }

    /**
     * Gets the next free object from the pool. If the pool doesn't contain any objects,
     * a new object will be created and given to the caller of this method back.
     *
     * @return T borrowed object
     */
    public T borrowObject() {
        T object;
        if ((object = pool.poll()) == null) {
            object = createObject();
        }

        return object;
    }

    /**
     * Returns object back to the pool.
     *
     * @param object object to be returned
     */
    public void returnObject(T object) {
        if (object == null) {
            return;
        }

        this.pool.offer(object);
    }

    /**
     * Shutdown this pool.
     */
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * Creates a new object.
     *
     * @return T new object
     */
    protected abstract T createObject();
    

    /**
     * Initializes the service.
     * 
     * @param minIdle
     */
    protected void initialize(final int minIdle) {
        pool = new ConcurrentLinkedQueue<T>();

        for (int i = 0; i < minIdle; i++) {
            pool.add(createObject());
        }
    }

	public String getName() {
		return name;
	}

}