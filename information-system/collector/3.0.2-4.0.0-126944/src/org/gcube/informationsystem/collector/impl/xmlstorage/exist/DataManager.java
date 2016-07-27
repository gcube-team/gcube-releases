package org.gcube.informationsystem.collector.impl.xmlstorage.exist;

import java.io.IOException;
import java.util.Properties;
import org.exist.backup.Restore;
import org.exist.storage.BrokerPool;
import org.exist.storage.ConsistencyCheckTask;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.contexts.ICServiceContext;
import org.gcube.informationsystem.collector.impl.resources.GCUBEInstanceStateResource;
import org.gcube.informationsystem.collector.impl.xmlstorage.backup.BackupsRootFolder;
import org.gcube.informationsystem.collector.impl.xmlstorage.backup.Scheduler;

/**
 * Data Manager
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class DataManager extends XMLStorage {        
    
    private final static GCUBELog logger = new GCUBELog(DataManager.class);
    
    private static ExistScheduler scheduler = null;
    
    /**
     * Backups the current content of the XMLStorage
     * @throws Exception
     */
    public synchronized void backup() throws IOException, Exception {
	
	//run the eXist's ConsistencyCheckTask
	//info: http://www.exist-db.org/backup.html
	//source: http://exist.svn.sourceforge.net/viewvc/exist/branches/eXist-stable-1.2/src/org/exist/backup/ExportMain.java?revision=8695&view=markup
	
	logger.info("Backup requested");
	ConsistencyCheckTask check = new ConsistencyCheckTask();		
	Properties props = new Properties();
	props.setProperty("output", BackupsRootFolder.createBackupFolder().getAbsolutePath()); //The directory to which the backup is written
	props.setProperty("backup", "yes"); //Create a database backup in addition to running the system checks
	props.setProperty("incremental", "no");  //Create a full backup	
	check.configure(null, props); //configuration is null, since we pass an absolute path as backup folder
        check.execute(BrokerPool.getInstance().get(org.exist.security.SecurityManager.SYSTEM_USER));
	
        logger.info("Backup completed");
        
        BackupsRootFolder.manageOldBackups(ExistBackupFolder.getBackupFolderFilter());
        
	//alternative backup way, but it DOES NOT produce a ZIP file and DOES NOT perform consistency check
	/*Backup backup = new Backup("admin", "admin", BackupsRootFolder.createBackupFolder().getAbsolutePath(), XmldbURI.create(URI + DBBroker.ROOT_COLLECTION));
	try {
	    backup.backup(false, null);
	} catch (XMLDBException e) {
	    throw new Exception("Unable to backup");
	} catch (IOException e) {
	    throw new Exception("Unable to backup");
	} catch (SAXException e) {
	    throw new Exception("Unable to backup");
	} */
    }

    /**
     * Restores from the latest backup 
     * @throws IOException 
     */
    public synchronized void restore() throws IOException {
	
	logger.info("Restore requested");
		
	try {
	    ExistBackupFolder lastBackup = this.getLastBackup();
	    logger.info("Restoring from " + lastBackup.getBackupFile());
	    Restore restore = new Restore(USER, PWD, PWD, lastBackup.getBackupFile(), URI);
	    restore.restore(false, null);
	    if (Boolean.valueOf((String) ICServiceContext.getContext().getProperty("deleteRPsOnStartup", true))) { 
		// cleanup the RPs collection
		logger.info("deleting all RPs...");
		this.deleteCollection(new GCUBEInstanceStateResource().getCollectionName());
	    }
	    logger.info("Restore completed");	
	} catch (Exception e1) {
	    logger.fatal("Failed to restore", e1);
	}

    }

    
    /**
     * Retrieves the last backup available
     * @return the last backup file or folder
     * @throws IOException if the backup cannot be found
     */
    private ExistBackupFolder getLastBackup() throws IOException {	
	return BackupsRootFolder.getLastBackup(ExistBackupFolder.class, ExistBackupFolder.getBackupFolderFilter());	
    }
    
    /**
     * Gets the scheduler for automatic and periodic backups of the XMLStorage.<br/>
     * Automatic backups are scheduled via the <em>scheduledBackupInHours</em> variable in the service's JNDI. 
     * If this variable is missing the automatic backups are not performed.
     * @return the scheduler or null if no automatic backup was configured
     */
    public static Scheduler getScheduler() {
	if (scheduler != null)
	    return scheduler;
	
	String interval = (String) ICServiceContext.getContext().getProperty("scheduledBackupInHours", false);
	if (interval == null)
	    return null; // scheduled backups are not requested
	
	//create a new scheduler if requested
	scheduler = new ExistScheduler(Integer.valueOf(interval));
	return scheduler;
    }
}
