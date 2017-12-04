package org.gcube.portal.social.networking.caches;

import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.gcube.portal.social.networking.liferay.ws.GroupManagerWSBuilder;
import org.gcube.portal.social.networking.liferay.ws.UserManagerWSBuilder;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.LoggerFactory;

/**
 * This cache will store GCubeUser of the users of the infrastructure as couples {user-id, user screename}
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class UsersCache{

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UsersCache.class);
	private static UsersCache singleton = new UsersCache();

	/**
	 * Private constructor: build the cache
	 * @return
	 */
	private UsersCache(){

		// create a thread to build the cache
		new Thread(){
			public void run() {
				try{
					logger.info("Fetching users and putting them into cache");
					Ehcache usersCache = CachesManager.getCache(CachesManager.USERS_CACHE);
					GroupManager groupManager = GroupManagerWSBuilder.getInstance().getGroupManager();
					UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
					ApplicationContext ctx = ContextProvider.get(); // get this info from SmartGears
					List<GCubeUser> users = userManager.listUsersByGroup(groupManager.getGroupIdFromInfrastructureScope("/"+ctx.container().configuration().infrastructure()));
					for (GCubeUser gCubeUser : users) {
						usersCache.put(new Element(gCubeUser.getUserId(), gCubeUser));
					}
				}catch(Exception e){
					logger.error("Unable to retrieve user's usernames. Other users will be discovered later on", e);
				}
			}
		}.start();

	}

	/**
	 * Get the singleton object
	 */
	public static UsersCache getSingleton() {
		return singleton;
	}

	/**
	 * Retrieve an entry
	 * @param id 
	 * @return user associated to the user
	 */
	public GCubeUser getUser(long userId){
		Ehcache usersCache = CachesManager.getCache(CachesManager.USERS_CACHE);
		if(usersCache.get(userId) != null)
			return (GCubeUser) usersCache.get(userId).getObjectValue();
		else
			return null;
	}

	/**
	 * Save an entry into the cache
	 * @param id
	 * @param user
	 */
	public void pushEntry(long id, GCubeUser user){
		Ehcache usersCache = CachesManager.getCache(CachesManager.USERS_CACHE);
		usersCache.put(new Element(id, user));
	}

}
