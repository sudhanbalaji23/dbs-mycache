/**
 * 
 */
package com.dbs.mycache;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LRUMap;

/**
 * @author Sudhan Balaji
 *
 */

public class MyCache<K, T> {

	// Expiry period for the cached objects
	private long cacheExpiry;
	// LRUMap removes the least used entries from a fixed sized map
	private final LRUMap dbsCacheMap;

	protected class MyCacheObject<T> {
		// Last accessed timestamp used for eliminating unused objects
		public long lastAccessedTS = System.currentTimeMillis();
		public T value;

		protected MyCacheObject(T value) {
			this.value = value;
		}
	}

	public MyCache(long cacheExpiry, final long timerInterval, int maxElements) {

		this.cacheExpiry = cacheExpiry * 1000;
		dbsCacheMap = new LRUMap(maxElements);

		if (cacheExpiry > 0 && timerInterval > 0) {
			Timer cleanUpTimer = new Timer(true);
			TimerTask cleanupTask = new TimerTask() {
				@Override
				public void run() {
					cleanup();
				}
			};

			cleanUpTimer.scheduleAtFixedRate(cleanupTask, 0, timerInterval * 1000);
		}

	}

	public void put(K key, T value) {
		synchronized (dbsCacheMap) {
			dbsCacheMap.put(key, new MyCacheObject(value));
		}
	}

	@SuppressWarnings("unchecked")
	public T get(K key) {
		synchronized (dbsCacheMap) {
			MyCacheObject c = (MyCacheObject) dbsCacheMap.get(key);

			if (c == null)
				return null;
			else {
				c.lastAccessedTS = System.currentTimeMillis();
				return (T) c.value;
			}
		}
	}

	public void remove(K key) {
		synchronized (dbsCacheMap) {
			dbsCacheMap.remove(key);
		}
	}

	public int size() {
		synchronized (dbsCacheMap) {
			return dbsCacheMap.size();
		}
	}

	@SuppressWarnings("unchecked")
	public void cleanup() {

		long now = System.currentTimeMillis();
		ArrayList<K> deleteKey = null;

		synchronized (dbsCacheMap) {
			MapIterator itr = dbsCacheMap.mapIterator();

			deleteKey = new ArrayList<K>((dbsCacheMap.size() / 2) + 1);
			K key = null;
			MyCacheObject c = null;

			while (itr.hasNext()) {
				key = (K) itr.next();
				c = (MyCacheObject) itr.getValue();

				if (c != null && (now > (cacheExpiry + c.lastAccessedTS))) {
					deleteKey.add(key);
				}
			}
		}
		
		for (K key : deleteKey) {
            synchronized (dbsCacheMap) {
            	dbsCacheMap.remove(key);
            }
            Thread.yield();
        }
	}
}
