package org.gcube.informationsystem.collector.impl.xmlstorage.exist;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;

import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Query Manager
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class QueryManager extends XMLStorage {

    private final static GCUBELog logger = new GCUBELog(QueryManager.class);
    
    private final int MAXQUERYATTEMPTS = 3;
    
    /**
     * Executes the given XQuery on the current collection or on the root collection if any was
     * previously loaded
     * 
     * @param query the XQuery to run
     * @return a formatted resultset
     * @throws XMLStorageNotAvailableException 
     */
    public ResourceSet executeXQuery(XQuery query) throws XMLStorageNotAvailableException {		
	
	if (this.getStatus() != STATUS.INITIALISED)
	    throw new XMLStorageNotAvailableException("XMLStorage is not available");
	
	boolean retry = true;
	int attempts = 0;
	ResourceSet result = null;
	Collection currentCollection = null;
	
	State.getDataManager().lock();
	try {
	    	currentCollection = this.loadAllCollections();
        	while ((retry) && (attempts < MAXQUERYATTEMPTS)) {
        	    try {		
        		// execute query and get results in ResourceSet
        		if (currentCollection == null)
        		    result = query.execute(this.rootCollection);
        		else
        		    result = query.execute(currentCollection);
        		retry = false;
        	    } catch (XMLDBException edb) {
        		logger.error("Failed to execute XQuery " + query.toString());
        		logger.error("Error details: " + edb.errorCode + " " + edb.getMessage(), edb);
        		// if the cause is a NullPointer, this can be due to a temporary
        		// lock on the database instance
        		if (edb.getCause() instanceof java.lang.NullPointerException) {
        		    retry = true;
        		    attempts++;
        		    logger.warn("Trying a new attempt for query execution");
        		} else
        		    retry = false;
        
        	    } catch (Exception e) {
        		logger.error("", e);
        		// if the cause is a NullPointer, this can be due to a temporary
        		// lock on the database instance
        		if (e instanceof java.lang.NullPointerException) {
        		    retry = true;
        		    attempts++;
        		    logger.warn("Trying a new attempt for query execution");
        		} else
        		    retry = false;
        
        	    }
        	}
        	this.resetCollection(currentCollection);
	} catch (Exception e) {
		logger.error("Failed to execute the XQuery", e);
	} finally {State.getDataManager().unlock(); }
	//operationsCounter++;
	//this.checkConnection();
	return result;
    }

}
