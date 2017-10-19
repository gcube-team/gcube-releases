package org.gcube.portal.social.networking.liferay.ws;

import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.ws.LiferayWSRoleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that builds a (singleton) UserManagerWS object.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class RoleManagerWSBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(UserManagerWSBuilder.class);
	private static RoleManagerWSBuilder singleton = new RoleManagerWSBuilder();
	private RoleManager roleManagerWs;

	private RoleManagerWSBuilder(){

		logger.info("Building UserManager please wait");

		try{
			roleManagerWs = new LiferayWSRoleManager(
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
	 * Get the role manager instance
	 * @return
	 */
	public RoleManager getRoleManager(){
		return roleManagerWs;
	}
	
	public static RoleManagerWSBuilder getInstance(){
		return singleton;
	}

}
