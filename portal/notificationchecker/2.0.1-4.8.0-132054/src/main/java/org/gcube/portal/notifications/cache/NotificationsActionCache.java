package org.gcube.portal.notifications.cache;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * The cache keep track of the actions to take for a user, that is if notifications
 * must be pushed or not, according to the timestamp of the last action as well a timeout value.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class NotificationsActionCache{

	private static final Log logger = LogFactoryUtil.getLog(NotificationsActionCache.class);
	
	// date formatter
	private SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss");

	/**
	 * The instance
	 */
	private static NotificationsActionCache instance = new NotificationsActionCache();

	/**
	 * The hashmap
	 */
	private Map<String, Long> cache;

	/**
	 * Cache entry expires after EXPIRED_AFTER ms
	 */
	private static final long EXPIRED_AFTER = 1000 * 60 * 3; 

	/**
	 * Private constructor
	 */
	private NotificationsActionCache(){

		cache = new ConcurrentHashMap<String, Long>();
		logger.info("NotificationsActionCache created");

	}

	/**
	 * Retrieve the current cache instance object
	 * @return
	 */
	public static NotificationsActionCache getCacheInstance(){

		return instance;

	}

	/**
	 * Retrieve the value associated to the key
	 * @param key
	 * @return null if the key doesn't exist or expired
	 */
	public Long get(String key) {

		logger.debug("GET REQUEST: <" + key + ">");

		if(cache.containsKey(key)){

			Long timestamp = cache.get(key);

			if(expired(timestamp)){
				cache.remove(key);
				logger.debug("Information expired for user <" + key + ">");
			}
			else {
				logger.debug("Information not expired for user " + key);
				return timestamp;
			}
		}
		return null;
	}

	/**
	 * Insert a couple <username, timestamp> in the cache
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean insert(String key, Long value) {

		Date d = new Date(value);
		logger.debug("INSERT REQUEST: <" + key + "," + ft.format(d) + ">");
		cache.put(key, value);

		return true;
	}

	/**
	 * Check if expired
	 * @param timestamp
	 * @return
	 */
	private static boolean expired(long timestamp){

		long currentTime = System.currentTimeMillis();

		if((timestamp + EXPIRED_AFTER) <= currentTime)
			return true;
		else
			return false;

	}

	/**
	 * Forcedly remove an entry
	 * @param username
	 */
	public void removeKey(String key) {
		logger.debug("REMOVE REQUEST: <" + key + ">");
		if(cache.containsKey(key))
			cache.remove(key);
	}
}
