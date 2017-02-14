package org.gcube.portlet.user.userstatisticsportlet.server.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Cache for the user's space in use within the infrastructure
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class UserInfrastructureSpaceCache implements CacheInterface<String, Long> {

	//private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserInfrastructureSpaceCache.class);
	private static final Log logger = LogFactoryUtil.getLog(UserInfrastructureSpaceCache.class);

	/**
	 * The instance
	 */
	private static UserInfrastructureSpaceCache instance = new UserInfrastructureSpaceCache();

	/**
	 * The hashmap
	 */
	private Map<String, CacheValueBean<Long>> userSpaceMap;

	/**
	 * Cache entry expires after EXPIRED_AFTER ms
	 */
	private static final long EXPIRED_AFTER = 1000 * 60 * 10;

	/**
	 * Private constructor
	 */
	private UserInfrastructureSpaceCache(){
		userSpaceMap = new ConcurrentHashMap<>();
	}

	/**
	 * Retrieve the current cache instance object
	 * @return
	 */
	public static UserInfrastructureSpaceCache getCacheInstance(){
		return instance;
	}

	@Override
	public Long get(String key) {

		if(userSpaceMap.containsKey(key)){
			CacheValueBean<Long> bean = userSpaceMap.get(key);
			if(CacheUtilities.expired(bean.getTTL(), EXPIRED_AFTER)){
				userSpaceMap.remove(key);
				logger.debug("Amount of space in the infrastructure used expired for key " + key + ", returning null");
			}
			else 
				return bean.getValue();
		}
		return null;
	}

	@Override
	public boolean insert(String key, Long value) {
		CacheValueBean<Long> newBean = new CacheValueBean<Long>(value, System.currentTimeMillis());
		userSpaceMap.put(key, newBean);
		return true;
	}
}
