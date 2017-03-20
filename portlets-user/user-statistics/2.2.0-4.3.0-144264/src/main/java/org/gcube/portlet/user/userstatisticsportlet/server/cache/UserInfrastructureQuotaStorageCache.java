package org.gcube.portlet.user.userstatisticsportlet.server.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gcube.portlet.user.userstatisticsportlet.shared.QuotaInfo;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


/**
 * Cache for the user's storage quota max/in-use within the infrastructure
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class UserInfrastructureQuotaStorageCache implements CacheInterface<String, QuotaInfo> {

	//private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserInfrastructureSpaceCache.class);
	private static final Log logger = LogFactoryUtil.getLog(UserInfrastructureSpaceCache.class);
	
	/**
	 * The instance
	 */
	private static UserInfrastructureQuotaStorageCache instance = new UserInfrastructureQuotaStorageCache();

	/**
	 * The hashmap
	 */
	private Map<String, CacheValueBean<QuotaInfo>> userQuotaMap;
	
	/**
	 * Cache entry expires after EXPIRED_AFTER ms
	 */
	private static final long EXPIRED_AFTER = 1000 * 60 * 10;
	
	/**
	 * Private constructor
	 */
	private UserInfrastructureQuotaStorageCache(){
		userQuotaMap = new ConcurrentHashMap<>();
	}

	/**
	 * Retrieve the current cache instance object
	 * @return
	 */
	public static UserInfrastructureQuotaStorageCache getCacheInstance(){
		return instance;
	}
	
	@Override
	public QuotaInfo get(String key) {

		if(userQuotaMap.containsKey(key)){
			CacheValueBean<QuotaInfo> bean = userQuotaMap.get(key);
			if(CacheUtilities.expired(bean.getTTL(), EXPIRED_AFTER)){
				userQuotaMap.remove(key);
				logger.debug("Amount of space in the infrastructure used expired for key " + key + ", returning null");
			}
			else 
				return bean.getValue();
		}
		return null;
	}

	@Override
	public boolean insert(String key, QuotaInfo value) {
		CacheValueBean<QuotaInfo> newBean = new CacheValueBean<QuotaInfo>(value, System.currentTimeMillis());
		userQuotaMap.put(key, newBean);
		return true;
	}

}
