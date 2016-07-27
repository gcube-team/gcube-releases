package org.gcube.common.homelibrary.jcr;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.common.homelibary.model.util.MemoryCache;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.jcr.home.JCRHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRHomeManager implements HomeManager{

	private Map<String, JCRUser> users = new LinkedHashMap<String, JCRUser>();
	//1 hour
	private static long timeToLiveInSeconds = 3600;
	private static long timerIntervalInSeconds = 7200;
	private static int maxItems = 20;
	
//	private Map<String, JCRHome> userHomesLogged = new LinkedHashMap<String, JCRHome>();

	private static MemoryCache<String, Home> cache = new MemoryCache<String, Home>(timeToLiveInSeconds, timerIntervalInSeconds, maxItems);
    
	private HomeManagerFactory factory;
	private static Logger logger = LoggerFactory.getLogger(JCRHomeManager.class);


	public JCRHomeManager(HomeManagerFactory factory) {
		this.factory = factory;
	} 


	@Override
	public HomeManagerFactory getHomeManagerFactory() {
		return factory;
	}

	@Override
	public List<User> getUsers() {
		return new LinkedList<User>(users.values());
	}

	@Override
	public User getUser(String portalLogin) throws InternalErrorException {

		logger.info("getUser portalLogin: "+portalLogin);
		return createUser(portalLogin);
	}

	@Override
	public synchronized boolean existUser(String portalLogin) throws InternalErrorException {

		logger.trace("existUser portalLogin: "+portalLogin);

		if (portalLogin == null){
			logger.error("portalLogin null");
			throw new IllegalArgumentException("The portalLogin value is null");
		}

		return users.containsKey(portalLogin);
	}

	@Override
	public synchronized User createUser(String portalLogin) throws InternalErrorException {

		// TODO check if is a really user of the infrastructure
		JCRUser user = users.get(portalLogin);
		if (user == null){
			logger.info("User "+portalLogin+" not found, creating a new one.");
			// Create a new user with scope = null
			user = new JCRUser(UUID.randomUUID().toString(),portalLogin);

			try {
				this.getHome(user);
			} catch (Exception e) {
				logger.error("error creating home",e);
			}

			logger.info("User created: "+user.getPortalLogin());
			users.put(portalLogin,user);	
		}

		return user;
	}

	@Override
	public Home getHome(User user) throws InternalErrorException,
	HomeNotFoundException {

		logger.info("getHome user: "+user.getPortalLogin());
		
		logger.debug("List homes in cache...");
		for (String key: cache.keySet())
			logger.info(key + "'s home");
		
		

		if (cache.containsKey(user.getPortalLogin())) {

			logger.debug("User is already logged");

			Home home = cache.get(user.getPortalLogin());

			return home;

		}

		JCRHome home;
		try {
//			System.out.println("*** GET HOME ***");
			home = new JCRHome(this, (JCRUser)user);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}

		logger.debug("Add " + user.getPortalLogin() + "'s home to memory cache");
		cache.put(user.getPortalLogin(), home);
		
		logger.trace("User loaded.");

		return home;

	}

	@Override
	public Home getHome(String portalLogin) throws InternalErrorException,
	HomeNotFoundException, UserNotFoundException {

		logger.info("getHome portalLogin: "+portalLogin);

		User user = getUser(portalLogin);
		return getHome(user);
	}

	@Override
	public synchronized void removeUser(User user) throws InternalErrorException {
		cache.remove(user.getPortalLogin());
	}


	@Override
	public MemoryCache<String, Home> getCache() throws InternalErrorException {
		return cache;
	}



}
