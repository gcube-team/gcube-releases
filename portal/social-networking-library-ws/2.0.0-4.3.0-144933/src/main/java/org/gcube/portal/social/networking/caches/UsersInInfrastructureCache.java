package org.gcube.portal.social.networking.caches;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
public class UsersInInfrastructureCache{

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UsersInInfrastructureCache.class);
	private static UsersInInfrastructureCache singleton = new UsersInInfrastructureCache();
	private static ConcurrentHashMap<Long, GCubeUser> userIdAndUser;

	/**
	 * Private constructor: build the cache
	 * @return
	 */
	private UsersInInfrastructureCache(){

		logger.info("Building cache");
		userIdAndUser = new ConcurrentHashMap<Long, GCubeUser>();

		// create a thread to build the cache
		new Thread(){
			public void run() {
				try{
					GroupManager groupManager = GroupManagerWSBuilder.getInstance().getGroupManager();
					UserManager userManager = UserManagerWSBuilder.getInstance().getUserManager();
					ApplicationContext ctx = ContextProvider.get(); // get this info from SmartGears
					List<GCubeUser> users = userManager.listUsersByGroup(groupManager.getGroupIdFromInfrastructureScope("/"+ctx.container().configuration().infrastructure()));
					for (GCubeUser gCubeUser : users) {
						userIdAndUser.put(gCubeUser.getUserId(), gCubeUser);
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
	public static UsersInInfrastructureCache getSingleton() {
		return singleton;
	}

	/**
	 * Retrieve an entry
	 * @param id
	 * @return user associated to the user
	 */
	public GCubeUser getUser(long userId){
		return userIdAndUser.get(userId);
	}

	/**
	 * Save an entry into the cache
	 * @param id
	 * @param user
	 */
	public void pushEntry(long id, GCubeUser user){
		userIdAndUser.put(id, user);
	}

}
