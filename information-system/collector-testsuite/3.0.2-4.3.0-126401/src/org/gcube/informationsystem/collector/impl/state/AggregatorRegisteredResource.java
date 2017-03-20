package org.gcube.informationsystem.collector.impl.state;

import java.util.Calendar;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.AnyContentType;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.state.AggregatorRegisteredResource;
import org.gcube.informationsystem.collector.impl.utils.*;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.State;
import org.gcube.informationsystem.collector.impl.resources.BaseDAIXResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEInstanceStateResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEProfileResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource;
import org.globus.mds.aggregator.impl.AggregatorServiceGroupResource;
import org.globus.mds.aggregator.impl.AggregatorServiceGroupEntryResource;
import org.globus.mds.aggregator.impl.AggregatorSource;
import org.globus.mds.aggregator.impl.AggregatorSink;

import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.ResourcePropertySet;
import org.globus.wsrf.TopicList;
import org.globus.wsrf.impl.ReflectionResourceProperty;
import org.globus.wsrf.impl.SimpleTopicList;
import org.globus.wsrf.impl.SimpleResourcePropertyMetaData;
import org.globus.wsrf.impl.servicegroup.ServiceGroupConstants;
import org.globus.wsrf.impl.servicegroup.EntryResourcePropertyTopic;

/**
 * This class implements an aggregating in-memory service group resource. <br> 
 * For every registered AggregatorSource instance one connected AggregatorRegisteredResource
 * instance is created and it is delivered with its data, following the chosen registration mode
 * (Push/Pull).
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */

public class AggregatorRegisteredResource extends AggregatorServiceGroupResource 
	implements AggregatorSink, ICRegisteredResource {

    private static GCUBELog logger = new GCUBELog(AggregatorRegisteredResource.class);

    private ResourceKey resourceKey = null;

    // private static I18n i18n = I18n.getI18n(Resources.class.getName());    

    protected Calendar terminationTime, currentTime;

    private ResourcePropertySet propSet;

    private TopicList topicList;

    public static final QName RP_SET = new QName(
	    "http://gcube-system.org/namespaces/informationsystem/collector/XMLCollectionAccess",
	    "ICRP");

    /**
     * Builds a new resource
     */
    public AggregatorRegisteredResource() {
	super.init(RP_SET);

	// this.baseDir = getBaseDirectory();
	this.propSet = this.getResourcePropertySet();
	this.topicList = new SimpleTopicList(this);
	ResourceProperty prop = null;

	EntryResourcePropertyTopic rpTopic = new EntryResourcePropertyTopic(this.propSet.get(ServiceGroupConstants.ENTRY));
	this.propSet.add(rpTopic);
	this.topicList.addTopic(rpTopic);

	try {
	    // ResourceLifeTime properties
	    prop = new ReflectionResourceProperty(SimpleResourcePropertyMetaData.TERMINATION_TIME, this);
	    this.propSet.add(prop);
	    prop = new ReflectionResourceProperty(SimpleResourcePropertyMetaData.CURRENT_TIME, this);
	    this.propSet.add(prop);
	    this.propSet.add(prop);

	    // initialize aggregator
	    this.loadCompatibleSources(this);

	} catch (Exception e) {
	    logger.error("Error during DISICResource creation: ", e);
	}
    }

    /**
     * Used to inform the resource of its key.
     * 
     * @param k the resource key
     * 
     * @throws Exception if the resource key is already set
     */
    public void setResourceKey(ResourceKey k) throws Exception {
	if (resourceKey == null) 
	    resourceKey = k;
	else 
	    throw new Exception("Resource key can only be set once.");
	
    }

    /**
     * 
     * @return the resource key
     */
    public ResourceKey getResourceKey() {
	return resourceKey;
    }

    // ResourceLifetime methods

    /**
     * Sets the termination time
     * 
     * @param time the new termination time
     */
    public void setTerminationTime(Calendar time) {
	logger.debug("Set Termination time called: " + time.getTime());
	this.terminationTime = time;
    }

    /**
     * 
     * 
     * {@inheritDoc}
     */
    public Calendar getTerminationTime() {
	return this.terminationTime;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Calendar getCurrentTime() {
	return Calendar.getInstance();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public TopicList getTopicList() {
	return this.topicList;
    }

    // AggregatorSink methods

    /**
     * Takes delivery of a message from an AggregatorSource. <br>
     * This is called by an AggregatorSource to
     * deliver a message with the assumption that the message data will be aggregated as its native
     * type and into whatever data storage format is used by the underlying AggregatorSink class.
     * 
     * @param messageObj the message content
     * @param entry the related service group entry
     * 
     */
    public void deliver(AnyContentType messageObj, AggregatorServiceGroupEntryResource entry) {

	logger.info("New delivered resource");
	try {
	    // get the message content
	    MsgParser aentry = new MsgParser(messageObj);
	    String entryType = aentry.getEntryType();
	    logger.debug("Entry type " + entryType);
	    logger.debug("Entry RunningInstance ID " + aentry.getRunningInstanceID());
	    logger.debug("Entry Service Name " + aentry.getServiceName());
	    logger.debug("Entry Service Class " + aentry.getServiceClass());
	    	    
	    // extract the entry EPR
	    EntryParser entryparser = new EntryParser(entry);
	    EntryEPRParser sinkparser = entryparser.getEPRSinkParser();

	    logger.debug("Aggregator Source " + entryparser.getSource());	   
	    logger.debug("Aggregator Sink " +  entryparser.getSink());
	    
	    // Build the new resource to store
	    logger.debug("Storing the new delivered resource");	    
	    BaseDAIXResource resource;            
            if (entryparser.getType().compareToIgnoreCase(GCUBEProfileResource.TYPE) == 0) {
    	    	//TODO: this will be removed after all the gHNs have the ISPublisher 2.0 installed
        	// and all the profiles arrive via the XMLCollectionAccess PT
        	resource = new GCUBEProfileResource();        	
        	String profile = aentry.getProfile();
        	//logger.debug("Received profile: " + profile);
        	resource.setResourceName(Identifier.buildProfileID(profile));
        	resource.setContent(profile);
            } else {
        	resource = new GCUBEInstanceStateResource();
        	resource.setResourceName(Identifier.buildInstanceStateID(entryparser));
        	resource.setContent(aentry.getEntryAsString());
            } 
	   
            
	    GCUBEXMLResource res = new GCUBEXMLResource(resource);
	    res.setEntryKey(sinkparser.getEntryKey());
	    res.setGroupKey(sinkparser.getGroupKey());
	    res.setTerminationTime(entry.getTerminationTime());
	    res.setSource(entryparser.getSourceURI());
	    res.setSourceKey(entryparser.getSourceKey());
	    logger.debug("Qualified Source Key: " + entryparser.getQualifiedSourceKey());
	    //res.setCompleteSourceKey(entryparser.getQualifiedSourceKey());	    
	    res.setNamespace("");
	    //if the resource is in the to-be-removed list, raise an exception
	    synchronized (State.deletedResources) {
		if (State.deletedResources.contains(res)) {
		    State.deletedResources.remove(res);
		    throw new Exception("the resource " + res.getResourceName() + " is no longer available");
		}
	    }
	    logger.trace("Resource: " + res.toString());

	    // store/update the new resource
	    State.getDataManager().storeResource(res);
	    aentry.dispose();
	    logger.info("Delivered resource stored with success");
	} catch (Exception e) {
	    logger.error("An error occurred when managing aggregator content, the resource has NOT be stored successfully", e);
	}
    }

    /**
     * Called to initialize any necessary state.
     * 
     * @param parameters any initialization parameters (not used)
     */
    public void initialize(Object parameters) {
	// NO OP
    }

    /**
     * Sets the AggregatorSource connected to this sink
     * 
     * @param source the source
     * 
     */
    public void setSource(AggregatorSource source) {
	// NO OP
    }

    /**
     * Gets the AggregatorSource connected to this sink
     * 
     * @return always null
     */
    public AggregatorSource getSource() {
	return null;
    }

    /**
     * Called to free resources used by the sink.
     * 
     */
    public void terminate() {
	logger.debug("Resource terminated");
    }

    // RemoveCallback methods
    // Notifies that the resource was removed
    /**
     * Callback method invoked when the resource is removed by the Aggregator Framework
     */
    public void remove() {
	logger.debug("Resource removed");

    }

}
