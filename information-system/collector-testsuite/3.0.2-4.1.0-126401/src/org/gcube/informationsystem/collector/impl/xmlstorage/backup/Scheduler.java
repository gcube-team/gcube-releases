package org.gcube.informationsystem.collector.impl.xmlstorage.backup;

import org.gcube.common.core.utils.logging.GCUBELog;


/**
 * Scheduler for periodic XMLStorage backups
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class Scheduler implements Runnable {

    /** scheduler logger */
    protected static GCUBELog logger = new GCUBELog(Scheduler.class);
    
    private int intervalInMS = 24 * 3600 * 1000; //default, 1 day
    
    /**
     * @param intervalInHours the interval expressed in hours between two backups
     */    
    public Scheduler(int intervalInHours) {
	this.intervalInMS = intervalInHours * 3600 * 1000;
	logger.info("Scheduler will work every " + intervalInHours + " hour(s)");
    }

    public Scheduler() {}
    
    
    public void run() {
	
	do {
	    try {
		Thread.sleep(this.intervalInMS);
		this.doBackup();
	    } catch (InterruptedException e) { 
		logger.error("Unable to sleep (yawn)", e); 
	    } catch (Exception e) {
		logger.error("Unable to backup", e);
	    }
	    
	} while (! Thread.interrupted());
	
	//logger.info("Backup Scheduler was interrupted");
    }

    /**
     * Performs the backup
     * 
     * @throws Exception if the backup fails
     */
    protected abstract void doBackup() throws Exception;
           
}