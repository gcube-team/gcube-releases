package org.gcube.portal.social.networking.liferay.ws;

import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.ws.LiferayWSGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that builds a (singleton) GroupManagerWS object.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GroupManagerWSBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(GroupManagerWSBuilder.class);
	private static GroupManagerWSBuilder singleton = new GroupManagerWSBuilder();
	private static GroupManager groupManagerWs;

	private GroupManagerWSBuilder(){

		logger.info("Building GroupManager please wait");

		try{
			groupManagerWs = new LiferayWSGroupManager(
					LiferayJSONWsCredentials.getSingleton().getUser(), 
					LiferayJSONWsCredentials.getSingleton().getPassword(), 
					LiferayJSONWsCredentials.getSingleton().getHost(), 
					LiferayJSONWsCredentials.getSingleton().getSchema(), 
					LiferayJSONWsCredentials.getSingleton().getPort());
		}catch(Exception e){
			logger.error("Failed to build the GroupManager. ", e);
			return;
		}

		logger.info("GroupManager instance built");

	}
	
	/**
	 * Get the user manager instance
	 * @return
	 */
	public GroupManager getGroupManager(){
		return groupManagerWs;
	}
	
	public static GroupManagerWSBuilder getInstance(){
		return singleton;
	}

}
