package com.dbs.mycache;

import java.util.logging.Logger;

/**
 * @author Sudhan Balaji
 *
 */

public class App 
{
	private final static Logger logger = Logger.getLogger(App.class.getName());
	
    public static void main( String[] args ) throws InterruptedException
    {
    	App myCacheTest = new App();
    	 
    	
        logger.info("\n\n\n :::::::::::::::: Testing Addition & Removal of Cache Objects :: testAddRemoveCacheObjects() ::::::::::::::::\n\n");
        myCacheTest.testAddRemoveCacheObjects();
        logger.info("\n\n\n :::::::::::::::: Testing Expiration of Cache Objects :: testExpiredCacheObjects() ::::::::::::::::\n\n");
        myCacheTest.testExpiredCacheObjects();
        logger.info("\n\n\n :::::::::::::::: Testing Cache Cleanup :: testCleanup() ::::::::::::::::\n\n");
        myCacheTest.testCleanup();
    }
    
    
    /*
     * Testing addition & removal of Cache Objects 
     *
     */
    private void testAddRemoveCacheObjects() {
   	 
        // cacheExpiry: 300 seconds
        // timerInterval: 700 seconds
        // maxElements: 5
        MyCache<String, String> myCache = new MyCache<String, String>(300, 700, 5);
 
        myCache.put("DBS", "DBS");
        myCache.put("UOB", "UOB");
        myCache.put("OCBC", "OCBC");
        myCache.put("CITI", "CITI");
        myCache.put("BOA", "BOA");
 
        logger.info("Cache Objects Added :: cache.size() :: " + myCache.size());
        myCache.remove("CITI");
        logger.info("After removal :: cache.size() :: " + myCache.size());
 
        myCache.put("Barclays", "Barclays");
        // Exceeded maxElements size
        myCache.put("RBS", "RBS");
        logger.info("Cache input exceeded maxElements size :: cache.size() :: " + myCache.size());
 
    }
    
    
    /*
     * Testing expired Cache Objects 
     *
     */
    private void testExpiredCacheObjects() throws InterruptedException {
    	 
    	// cacheExpiry: 2 seconds
        // timerInterval: 1 seconds
        // maxElements: 6
        MyCache<String, String> myCache = new MyCache<String, String>(2, 1, 6);
 
        myCache.put("DBS", "DBS");
        myCache.put("UOB", "UOB");
        myCache.put("OCBC", "OCBC");

        // 4 seconds sleep > cacheExpiry
        Thread.sleep(4000);
        logger.info("Cache Objects expired as the cacheExpiry time outlived :: cache.size() ::" + myCache.size());
    }
    
    
    /*
     * Testing Cache cleanup
     *
     */
    private void testCleanup() throws InterruptedException {
        int size = 100000;
 
        // cacheExpiry: 60 seconds
        // timerInterval: 60 seconds
        // maxElements: 100000
        MyCache<String, String> cache = new MyCache<String, String>(100, 100, 500000);
 
        for (int i = 0; i < size; i++) {
            String value = Integer.toString(i);
            cache.put(value, value);
        }
        
        long start = System.currentTimeMillis();
        cache.cleanup();
        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
 
        logger.info("Cache cleanup time for " + size + " objects is :: " + finish + " seconds");
 
    }
}
