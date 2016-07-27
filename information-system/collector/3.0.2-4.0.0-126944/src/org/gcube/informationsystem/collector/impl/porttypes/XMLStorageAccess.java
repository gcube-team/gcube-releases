package org.gcube.informationsystem.collector.impl.porttypes;

import java.io.IOException;

import org.xmldb.api.base.XMLDBException;

import org.gcube.informationsystem.collector.stubs.AlreadyConnectedFaultType;
import org.gcube.informationsystem.collector.stubs.BackupFailedFaultType;
import org.gcube.informationsystem.collector.stubs.BackupNotAvailableFaultType;
import org.gcube.informationsystem.collector.stubs.ShutdownFailedFaultType;
import org.gcube.informationsystem.collector.stubs.XMLStorageNotAvailableFaultType;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.VOID;
import org.gcube.informationsystem.collector.impl.contexts.ICServiceContext;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.State;

/**
 * <em>XMLStorageAccess</em> portType implementation. <br/>
 * It gives remote access to administration features of the XML Storage
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class XMLStorageAccess extends GCUBEPortType {

    /** {@inheritDoc} */
    @Override
    protected GCUBEServiceContext getServiceContext() {	
	return ICServiceContext.getContext();
    }

    /**
     * Backups the current content of the XMLStorage 
     * @throws BackupFailedFaultType if the backup fails
     * @throws XMLStorageNotAvailableFaultType if the XMLStorage is not available (may be corrupted)
     */
    public VOID backup(VOID params) throws BackupFailedFaultType, XMLStorageNotAvailableFaultType {
	
	try {
	    State.getDataManager().backup();
	} catch (XMLDBException e) {
	    logger.error("Failed to backup", e);
	    throw new XMLStorageNotAvailableFaultType();	    
	} catch (Exception e) {
	    logger.error("Failed to backup", e);
	    BackupFailedFaultType fault = new BackupFailedFaultType();
	    fault.addFaultDetailString("");
	    throw fault;
	}
	return new VOID();
    }

    /**
     * 
     * @param params
     * @return
     * @throws BackupNotAvailableFaultType
     * @throws XMLStorageNotAvailableFaultType
     */
    public VOID restore(VOID params) throws BackupNotAvailableFaultType, XMLStorageNotAvailableFaultType {
	
	try {
	    State.getDataManager().restore();
	} catch (IOException e) {
	    logger.error("Failed to restore the last backup", e);
	    BackupNotAvailableFaultType fault = new BackupNotAvailableFaultType();
	    fault.addFaultDetailString("No valid backup has been found");
	    throw fault;	    
	}
	
	return new VOID();	
    }
    
   /**
    * Shutdowns the XMLStorage
    * 
    * @throws BackupFailedFaultType if the backup before the shutdown fails
    * @throws XMLStorageNotAvailableFaultType if the XMLStorage is not available
    * @throws ShutdownFailedFaultType if the shutdown fails
    */
    public VOID shutdown(VOID params) throws BackupFailedFaultType, XMLStorageNotAvailableFaultType, ShutdownFailedFaultType {
	
	logger.info("Shutdown operation invoked");
	    
	//request the backup before to shutdown
	try {
	    State.getDataManager().backup();
	} catch (XMLDBException e) {
	    logger.error("Unable to backup before shutting down" ,e);
	    XMLStorageNotAvailableFaultType fault = new XMLStorageNotAvailableFaultType();
	    fault.addFaultDetailString("No valid backup has been found");
	    throw fault;	    	    	
	} catch (Exception e) {
	    logger.error("Unable to backup before shutting down" ,e);
	    //should we throw here and leave?
	    BackupFailedFaultType fault = new BackupFailedFaultType();
	    fault.addFaultDetailString("No valid backup has been found");
	    throw fault;
	}
	
	try {
	    State.dispose();
	    //ICServiceContext.getContext().setStatus(Status.DOWN);
	} catch (Exception e) {
	    logger.error("Shutdown failed", e);
	    ShutdownFailedFaultType fault = new ShutdownFailedFaultType();
	    fault.addFaultDetailString("Shutdown failed," + e.getMessage());
	    throw fault;
	}
	return new VOID();
    }
    
    /**
     * 
     * @param params
     * @return
     * @throws XMLStorageNotAvailableFaultType
     * @throws AlreadyConnectedFaultType
     */
    public VOID connect(VOID params) throws XMLStorageNotAvailableFaultType, AlreadyConnectedFaultType {
	
	logger.info("Connect operation invoked");	
	try {
	    State.initialize();
	    //ICServiceContext.getContext().setStatus(Status.READIED);
	} catch (Exception e) {
	    logger.error("Initialisation failed", e);
	    XMLStorageNotAvailableFaultType fault = new XMLStorageNotAvailableFaultType();
	    fault.addFaultDetailString("Initialisation failed," + e.getMessage());
	    throw fault;
	}
	return new VOID();
    }
}
