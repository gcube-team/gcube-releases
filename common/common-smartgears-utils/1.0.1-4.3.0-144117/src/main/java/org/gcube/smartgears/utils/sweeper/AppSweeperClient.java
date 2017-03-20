package org.gcube.smartgears.utils.sweeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author andrea
 *
 */
public class AppSweeperClient {
	
	public static void main (String args[]) {
		
		
		Logger logger = LoggerFactory.getLogger(AppSweeperClient.class);
		
		if (args.length <1) {
			logger.error("Missing app name parameter");
			System.exit(1);
		}
		
		String name = args[0];

		Sweeper sw = null;
		try {
			sw = new Sweeper();
		} catch (Exception e) {
			logger.error("Error initializing the Sweeper ", e);
			System.exit(1);
		}
		
		
		try {
			sw.cleanRIProfile(name);
		} catch (Exception e) {
			logger.error("Error cleaning the  RunningInstance profile ", e);
			System.exit(1);
		}
	}

}
