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
			System.out.println("Error initializing the Sweeper");
			e.printStackTrace();
			logger.error("Error initializing the Sweeper ", e);
			System.exit(1);
		}
							
		try {
			if (args.length>0){
				String savedTokenFileName = args[0];
				sw.saveTokens(savedTokenFileName);
				logger.info("file saved on Smartgears directory with name {} ",savedTokenFileName);
			}
			sw.forceDeleteHostingNode();
		} catch (Exception e) {
			logger.error("Error cleaning the  GHN profile ", e);
			System.exit(1);
		}
		
		
	}

}
