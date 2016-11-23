package org.gcube.smartgears.utils.sweeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author andrea
 *
 */
public class ContainerSweeperClient {
	
	public static void main (String args[]) {
		
		
		Logger logger = LoggerFactory.getLogger(ContainerSweeperClient.class);
		
		Sweeper sw = null;
		try {
			sw = new Sweeper();
		} catch (Exception e) {
			logger.error("Error initializing the Sweeper ", e);
			System.exit(1);
		}
		
		
		
		try {
			sw.cleanGHNProfile();
		} catch (Exception e) {
			logger.error("Error cleaning the  GHN profile ", e);
			System.exit(1);
		}
		try {
			sw.cleanRIProfiles();
		} catch (Exception e) {
			logger.error("Error cleaning the  RunningInstance profiles ", e);
			System.exit(1);
		}
	}

}
