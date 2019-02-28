/**
 * 
 */
package org.gcube.common.homelibrary.consistency.processor;

import java.util.List;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class HomeManagerProcessor extends AbstractProcessor<HomeManager, Home>{
	
	protected Logger logger = LoggerFactory.getLogger(HomeManagerProcessor.class);

	

	@Override
	public void process(HomeManager manager) throws Exception {
		List<User> users = manager.getUsers();
		for(User user:users){
			try{
				logger.trace("checking user "+user.getPortalLogin());
				Home home = manager.getHome(user.getPortalLogin());
				subProcess(home);
			}catch(HomeNotFoundException hne)
			{
				System.err.println(hne);
			}

		}
	}
}