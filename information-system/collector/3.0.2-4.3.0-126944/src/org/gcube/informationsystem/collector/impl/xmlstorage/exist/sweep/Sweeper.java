/**
 * 
 */
package org.gcube.informationsystem.collector.impl.xmlstorage.exist.sweep;

import java.lang.InterruptedException;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.State;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.XMLStorage.XMLStorageNotAvailableException;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.sweep.Sweeper;
import org.gcube.informationsystem.collector.impl.resources.GCUBEInstanceStateResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource;

/**
 * This class provides some cleanup procedures to use to maintain a consistent
 * content in the XML storage. One of them is a thread, activated at RI startup
 * time, that periodically drops the out to date resources from the storage
 * 
 * @author Manuele Simi (ISTI-CNR) 
 * 
 */
public class Sweeper implements Runnable {

    private static long DELAY = 180000; // default value

    private static long resourceExpirationTime = 1800000; // default value

    private static GCUBELog logger = new GCUBELog(Sweeper.class);

    //private static XMLStorage storage = null;

    /**
     * Initializes a new Sweeper
     * 
     * @param delay the sweeper delay
     * @param resourceExpirationTime the time after that a resource is classified as exipired
     * @throws Exception if the eXist connection fails
     */
    public Sweeper(long delay, long resourceExpirationTime) throws Exception {
	Sweeper.DELAY = delay;
	Sweeper.resourceExpirationTime = resourceExpirationTime;
	logger.info("Starting sweeper thread with an interval of " + Sweeper.DELAY + " ms");
	
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
	if (Sweeper.DELAY == -1) return;
	try {
	    while (!Thread.interrupted()) {
		Thread.sleep(Sweeper.DELAY);
		logger.info("Starting IC sweeper...");
		this.cleanDeletedResourcesList();
		this.cleanExpiredResources();
	    }
	} catch (InterruptedException ie) {
	    logger.error("Unable to sleep (yawn)", ie); 
	    // thread was interrupted
	}
    }

    /**
     * Deletes from the backend storage the expired resources
     * 
     */
    public void cleanExpiredResources() {

	Calendar now = new GregorianCalendar();
	now.setTimeZone(TimeZone.getTimeZone("GMT"));
	try {
	    GCUBEInstanceStateResource fakeresource = new GCUBEInstanceStateResource();
	    //get all IDs for Instance States' collection
	    String[] ids = State.getDataManager().listAllCollectionResourceIDs(fakeresource.getCollectionName());
	    for (String id : ids) {
		try {
		    GCUBEInstanceStateResource tempresource = new GCUBEInstanceStateResource();
		    tempresource.setResourceName(id);
		    GCUBEXMLResource xmlresource = new GCUBEXMLResource(tempresource); 
		    State.getDataManager().retrieveResourceContent(xmlresource);	
		    logger.trace("Checking resource " + id);
		    if (ResourceFilter.isExpired(xmlresource)) {
			// removes the resources from the database
			State.getDataManager().deleteResource(xmlresource);
			logger.info("Resource " + xmlresource.getResourceName() + " deleted");
		    }
		    // break;
		} catch (Exception e) {
		    logger.debug("IC sweeper - the resource " + id  + " is no longer available in the storage");
		}
	    }

	} catch (Exception e2) {
	    logger.warn("IC sweeper - an exception was rised when trying to cleanup the storage ",   e2);
	}
    }

    /**
     * Removes all the properties documents related to the given Running
     * Instance ID
     * 
     * @param id
     *            the ID of the Running Instance whose properties documents have
     *            to be removed
     */
    public static void cleanResourceForRI(String id) {
	// TO DO
    }

    /**
     * Deletes the Properties collection from the storage
     * @throws XMLStorageNotAvailableException 
     * 
     */
    public static void cleanRPs() throws XMLStorageNotAvailableException {
	// cleanup the RPs collection	
	State.getDataManager().deleteCollection(new GCUBEInstanceStateResource().getCollectionName());
    }

    /**
     * Deletes the expired resources from the list of deleted resources as
     * notified by the AF
     * 
     */

    public void cleanDeletedResourcesList() {

	Calendar now = new GregorianCalendar();
	now.setTimeZone(TimeZone.getTimeZone("GMT"));
	List<GCUBEXMLResource> toRemove = new ArrayList<GCUBEXMLResource>();		
	for (GCUBEXMLResource res : State.deletedResources) {
	    try {
		if (now.getTimeInMillis() - res.getLastUpdateTimeinMills() > Sweeper.resourceExpirationTime) {
		    toRemove.add(res);
		}
	    } catch (Exception e) {
		logger.error("Failed to clean up the resources", e);
	    }
	}// end loop on deletedResources
	synchronized (State.deletedResources) {
	    for (GCUBEXMLResource res : toRemove) {
		State.deletedResources.remove(res);
	    }
	}// end synch block

    }

}
