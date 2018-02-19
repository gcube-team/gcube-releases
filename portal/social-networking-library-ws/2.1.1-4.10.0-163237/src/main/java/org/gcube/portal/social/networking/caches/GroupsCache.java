package org.gcube.portal.social.networking.caches;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.slf4j.LoggerFactory;

public class GroupsCache {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GroupsCache.class);
	private static GroupsCache singleton = new GroupsCache();

	/**
	 * Private constructor: build the cache
	 * @return
	 */
	private GroupsCache(){

		logger.info("Building cache");
		CachesManager.getCache(CachesManager.GROUPS_CACHE);

	}

	/**
	 * Get the singleton object
	 */
	public static GroupsCache getSingleton() {
		return singleton;
	}

	/**
	 * Retrieve an entry
	 * @param id
	 * @return user associated to the user
	 */
	public GCubeGroup getGroup(long groupId){
		Ehcache groupsCache = CachesManager.getCache(CachesManager.GROUPS_CACHE);
		if(groupsCache.get(groupId) != null)
			return (GCubeGroup) groupsCache.get(groupId).getObjectValue();
		else
			return null;
	}

	/**
	 * Save an entry into the cache
	 * @param id
	 * @param user
	 */
	public void pushEntry(long id, GCubeGroup group){
		Ehcache groupsCache = CachesManager.getCache(CachesManager.GROUPS_CACHE);
		groupsCache.put(new Element(id, group));
	}

}
