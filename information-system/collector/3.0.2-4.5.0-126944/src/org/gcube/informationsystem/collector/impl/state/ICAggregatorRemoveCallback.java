package org.gcube.informationsystem.collector.impl.state;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.utils.EntryEPRParser;
import org.gcube.informationsystem.collector.impl.utils.EntryParser;
import org.gcube.informationsystem.collector.impl.utils.Identifier;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.State;
import org.gcube.informationsystem.collector.impl.resources.GCUBEInstanceStateResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource;
import org.globus.mds.aggregator.impl.AggregatorServiceGroupEntryRemovedCallback;
import org.globus.mds.aggregator.impl.AggregatorServiceGroupEntryResource;

/**
 * Whenever a AggregatorServiceGroupEntryResource is removed from an
 * AggregatorServiceGroupEntryHome, the corresponding remove method of this class will be invoked
 * passing as a parameter the instance of the resource that is about to be removed.
 * 
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class ICAggregatorRemoveCallback implements AggregatorServiceGroupEntryRemovedCallback {

    private static GCUBELog logger = new GCUBELog(ICAggregatorRemoveCallback.class);

    /**
     * Creates a new Callback object
     * 
     */
    public ICAggregatorRemoveCallback() {
	super();
    }

    /**
     * Removes from the storage the supplied resource
     * 
     * @param entry the AggregatorServiceGroupEntryResource that is about to be removed
     * @throws Exception if the delete operation fails
     */
    public void remove(AggregatorServiceGroupEntryResource entry) throws Exception {

	logger.debug("ICAggregatorRemoveCallback invoked " + entry.getEntryEPR().toString());
	
	EntryParser eparser = new EntryParser(entry);
	logger.debug("Aggregator Source " + eparser.getSource());	   
	logger.debug("Aggregator Sink " +  eparser.getSink());	    
	
	EntryEPRParser parser = eparser.getEPRSinkParser();
	GCUBEInstanceStateResource instancestate = new GCUBEInstanceStateResource();
	instancestate.setResourceName(Identifier.buildInstanceStateID(eparser));
	GCUBEXMLResource res = new GCUBEXMLResource(instancestate);	
	res.setEntryKey(parser.getEntryKey());
	res.setGroupKey(parser.getGroupKey());
	res.setSourceKey(eparser.getSourceKey());
	res.setSource(eparser.getSourceURI());

	// mark the resource as no longer available
	synchronized (State.deletedResources) {
	    State.getDeletedResources().add(res);
	}

	// delete the resource from the database
	try {
	    //State.getDataManager().retrieveAndDeleteResourceFromID(res.getID());
	} catch (Exception e) {
	    logger.error("Unable to remove resource: " + res.getResourceName(), e);
	}

    }

}
