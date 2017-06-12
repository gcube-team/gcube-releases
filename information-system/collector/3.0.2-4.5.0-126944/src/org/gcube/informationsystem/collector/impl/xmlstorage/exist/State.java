package org.gcube.informationsystem.collector.impl.xmlstorage.exist;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.contexts.ICServiceContext;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.XMLStorage.STATUS;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.XMLStorage.XMLStorageNotAvailableException;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.sweep.Sweeper;
import org.gcube.informationsystem.collector.impl.resources.GCUBEInstanceStateResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Collections;

/**
 * The global state of an IC instance
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class State {

    /**
     * Connection to XMLStorage for data management
     */
    private static DataManager dataManager;

    /**
     * Connection to XMLStorage for querying the data
     */
    private static QueryManager queryManager;

    /** Thread that periodically sweeps the XMLStorage from expired resources */
    public static Thread sweeperT = null;
    
    /** Thread that periodically backups the XMLStorage */
    public static Thread schedulerT = null;

    /**
     * List of recently deleted resources. It is used to avoid the storage of RPs of a deleted
     * resource
     */
    public static List<GCUBEXMLResource> deletedResources = Collections.synchronizedList(new ArrayList<GCUBEXMLResource>());

    private static GCUBELog logger = new GCUBELog(State.class);

    /**
     * Initializes the eXist DB connections using during the service life
     * 
     * @param configuration
     *            the RI configuration loaded from the JNDI resource
     * @throws Exception
     *             if the intialization fails
     */
    public static void initialize() throws Exception {
	logger.info("Starting IC service initialization...");
	long maxOperations = Long.valueOf((String) ICServiceContext.getContext().getProperty("maxOperationsPerConnection", true));
	State.initializeDataManager(maxOperations);
	State.initializeQueryManager(maxOperations);
	if (Boolean.valueOf((String) ICServiceContext.getContext().getProperty("deleteRPsOnStartup", true))) { 
	    // cleanup the RPs collection
	    logger.info("Deleting all RPs...");
	    try {
		State.dataManager.deleteCollection(new GCUBEInstanceStateResource().getCollectionName());
	    } catch (XMLStorageNotAvailableException e) {
		logger.warn("Unable to delete the properties's collection");
	    }
	} else {
	    logger.info("All RPs previously stored are kept in the storage");
	}

	logger.info("Initialising the sweeper...");
	// start the sweeper to periodically cleanup the storage and some data structures
	if (State.sweeperT == null) {
        	Sweeper sweeper = new Sweeper(Long.valueOf((String) ICServiceContext.getContext().getProperty("sweeperIntervalInMillis", true)), 
        		Long.valueOf((String) ICServiceContext.getContext().getProperty("resourceExpirationTimeInMillis", true)));
        	State.sweeperT = new Thread(sweeper);
        	State.sweeperT.setName("ICSweeper");
        	State.sweeperT.start();
	}
		
	
	//start the scheduler for automatic backups (if any)
	logger.info("Initialising the scheduled backups...");
	org.gcube.informationsystem.collector.impl.xmlstorage.backup.Scheduler scheduler = DataManager.getScheduler();
	if (scheduler != null) {
        	if (State.schedulerT == null)         	        	
                   State.schedulerT = new Thread(scheduler);        	            	        	
        	State.schedulerT.setName("BackupScheduler");
        	State.schedulerT.start();
	}
	logger.info("IC service initialization completed");
    }

    private static void initializeDataManager(long maxOperations) throws Exception {
	if (State.dataManager == null)	
	    State.dataManager = new DataManager();
	if (State.dataManager.getStatus() != STATUS.INITIALISED)
	    dataManager.initialize(maxOperations);
	else
	    logger.info("DataManager already initalized");
    }

    private static void initializeQueryManager(long maxOperations) throws Exception {
	if (State.queryManager == null)
	    State.queryManager = new QueryManager();
	if (State.queryManager.getStatus() != STATUS.INITIALISED)
	    queryManager.initialize(maxOperations);
	else
	    logger.info("QueryManager already initalized");
    }

    /**
     * Releases all the State resources
     * 
     * @throws Exception if the shutdown fails
     */
    public static void dispose() throws Exception {
	logger.info("Disposing IC service's resources...");
	if (State.sweeperT != null) {
        	State.sweeperT.interrupt();
        	State.sweeperT = null;
	}
	if (State.schedulerT == null) {
	    State.schedulerT.interrupt();
		State.schedulerT = null;
	}	
	State.dataManager.shutdown(true);
	State.queryManager.shutdown(true);
	
    }

    /**
     * @return the dataManager
     */
    public static DataManager getDataManager() {
        return dataManager;
    }

    /**
     * @return the queryManager
     */
    public static QueryManager getQueryManager() {
        return queryManager;
    }

    
    /**
     * @return the deletedResources
     */
    public static List<GCUBEXMLResource> getDeletedResources() {
        return deletedResources;
    }

    /**
     * Prints the enviromnet variables
     */
    public void printEnv() {
	java.util.Properties p = System.getProperties();
	Enumeration<Object> keys = p.keys();
	while (keys.hasMoreElements()) {
	    logger.debug(keys.nextElement());
	}
	logger.debug("Exist home: " + System.getProperty("exist.home"));
    }
}
