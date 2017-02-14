package org.gcube.datatransformation;

import org.gcube.datatransformation.datatransformationlibrary.PropertiesManager;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dimitris Katris, NKUA
 *
 * This thread is used to periodically publish statistics of the {@link DataTransformationService} in the IS
 */
public class StatisticsUpdater extends Thread {
	
	/**
	 * The interval in which the statistics are published
	 */
	private static final long UPDATE_INTERVAL = PropertiesManager.getInMillisPropertyValue("statistics.updateinterval", "600");//30 mins

	/**
	 * Logs operations performed by {@link StatisticsUpdater} class
	 */

	private static final Logger logger = LoggerFactory.getLogger(StatisticsUpdater.class);

	
	/**
	 * Simple constructor which also starts executing this {@link Thread}.
	 */
	public StatisticsUpdater(){
		this.setDaemon(true);
		this.start();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		while (true) {
			try {
				try {sleep(UPDATE_INTERVAL);} catch (Exception e) { }
				String stats = StatisticsManager.toXML();
				logger.trace("Updating DTS Running Instance Statistics with: \n"+stats);
//				DTSContext.getContext().getInstance().setSpecificData(stats);
				logger.trace("Statistics were updated successfully");
			} catch (Exception e) {
				logger.error("Did not manage to update the statistics of the dts running instance", e);
			}
		}
	}
}
