package org.gcube.common.homelibary.model.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A MemoryCache (with LRU policy)
 * @param <K>
 * @param <T>
 */
public class MemoryCache<K, T extends Cleanable> {

	private long timeToLive;
	private Map<K, CrunchifyCacheObject> crunchifyCacheMap;
	private int MAX_NUM_ENTRY = 10;

	private static Logger logger = LoggerFactory.getLogger(MemoryCache.class);

	protected class CrunchifyCacheObject {
		public long lastAccessed = System.currentTimeMillis();
		public T value;

		protected CrunchifyCacheObject(T value) {
			this.value = value;
		}

		protected T getValue(){
			return value;
		}
	}

	public MemoryCache(long crunchifyTimeToLive, final long crunchifyTimerInterval, int maxItems) {
		logger.info("Start Homes Memory Cache...");
		this.timeToLive = crunchifyTimeToLive * 1000;
		logger.debug("TimeToLive set to " + this.timeToLive + " seconds.");
		crunchifyCacheMap = new HashMap<K, CrunchifyCacheObject>(maxItems);
		MAX_NUM_ENTRY = maxItems;

		if (timeToLive > 0 && crunchifyTimerInterval > 0) {
			Timer timer = new Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					cleanup();
				}
			}, crunchifyTimerInterval * 1000, crunchifyTimerInterval * 1000);
		}
	}

	private boolean isFull(){
		return crunchifyCacheMap.size() == MAX_NUM_ENTRY;
	}

	private void removeLRUEntry() {

		K keyToRemove = null;
		long lruTimestamp = System.currentTimeMillis();
		Iterator<Entry<K, MemoryCache<K, T>.CrunchifyCacheObject>> iterator = crunchifyCacheMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<K, MemoryCache<K, T>.CrunchifyCacheObject> entry = (Map.Entry<K, MemoryCache<K, T>.CrunchifyCacheObject>) iterator
					.next();

			long entryTimestamp = entry.getValue().lastAccessed;

			if(entryTimestamp < lruTimestamp)
				keyToRemove = entry.getKey();

		}

		// delete
		CrunchifyCacheObject c = (CrunchifyCacheObject) crunchifyCacheMap.remove(keyToRemove);
		if(c.getValue() != null)
			c.getValue().releaseResources();


	}

	public void put(K key, T value) {
		synchronized (crunchifyCacheMap) {

			// remove least recently used entry
			if(isFull())
				removeLRUEntry();


			crunchifyCacheMap.put(key, new CrunchifyCacheObject(value));
		}
	}

	public boolean containsKey(K key) {
		synchronized (crunchifyCacheMap) {
			return crunchifyCacheMap.containsKey(key);
		}
	}

	public Set<K> keySet() {
		synchronized (crunchifyCacheMap) {
			return crunchifyCacheMap.keySet();
		}
	}

	public T get(K key) {
		synchronized (crunchifyCacheMap) {
			CrunchifyCacheObject c = (CrunchifyCacheObject) crunchifyCacheMap.get(key);

			if (c == null)
				return null;
			else {
				c.lastAccessed = System.currentTimeMillis();
				return c.value;
			}
		}
	}

	public void remove(K key) {
		synchronized (crunchifyCacheMap) {
			CrunchifyCacheObject c = (CrunchifyCacheObject) crunchifyCacheMap.remove(key);
			if(c != null)
				c.getValue().releaseResources();
		}
	}

	public int size() {
		synchronized (crunchifyCacheMap) {
			return crunchifyCacheMap.size();
		}
	}

	public void cleanup() {

		logger.debug("Clean unused homes...");

		long now = System.currentTimeMillis();
		ArrayList<K> deleteKey = null;

		synchronized (crunchifyCacheMap) {
			Iterator<Entry<K, MemoryCache<K, T>.CrunchifyCacheObject>> itr = crunchifyCacheMap.entrySet().iterator();

			deleteKey = new ArrayList<K>((crunchifyCacheMap.size() / 2) + 1);

			while (itr.hasNext()) {
				Map.Entry<K, MemoryCache<K, T>.CrunchifyCacheObject> entry = (Map.Entry<K, MemoryCache<K, T>.CrunchifyCacheObject>) itr
						.next();
				K key = (K) entry.getKey();
				CrunchifyCacheObject c = (CrunchifyCacheObject) entry.getValue();

				if (c != null && (now > (timeToLive + c.lastAccessed))) {
					deleteKey.add(key);
				}
			}

		}

		for (K key : deleteKey) {
			synchronized (crunchifyCacheMap) {
				CrunchifyCacheObject c = (CrunchifyCacheObject) crunchifyCacheMap.remove(key);
				if(c!= null && c.getValue() != null)
					c.getValue().releaseResources();
				logger.debug(key + "'s home has been removed from memory home cache.");
			}

			Thread.yield();
		}
	}
}