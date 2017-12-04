package org.gcube.portal.social.networking.liferay.ws;

import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.ws.LiferayWSUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that builds a (singleton) UserManagerWS object.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class UserManagerWSBuilder {

	private static final Logger logger = LoggerFactory.getLogger(UserManagerWSBuilder.class);
	private static UserManagerWSBuilder singleton = new UserManagerWSBuilder();
	private UserManager userManagerWs;

	private UserManagerWSBuilder(){

		logger.info("Building UserManager please wait");

		try{
			userManagerWs = new LiferayWSUserManager(
					LiferayJSONWsCredentials.getSingleton().getUser(), 
					LiferayJSONWsCredentials.getSingleton().getPassword(), 
					LiferayJSONWsCredentials.getSingleton().getHost(), 
					LiferayJSONWsCredentials.getSingleton().getSchema(), 
					LiferayJSONWsCredentials.getSingleton().getPort());
		}catch(Exception e){
			logger.error("Failed to build the UserManager. ", e);
			return;
		}

		logger.info("UserManager instance built");

	}
	
	/**
	 * Get the user manager instance
	 * @return
	 */
	public UserManager getUserManager(){
		return userManagerWs;
	}
	
	public static UserManagerWSBuilder getInstance(){
		return singleton;
	}

}
