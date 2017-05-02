package org.gcube.informationsystem.collector.impl.utils;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.globus.mds.aggregator.impl.AggregatorServiceGroupEntryResource;


/**
 * Parser for {@link AggregatorServiceGroupEntryResource}
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class EntryParser {

    private AggregatorServiceGroupEntryResource entry = null;
    
    private static final String registryNS = "gcube/informationsystem/registry/Registry";
    
    public enum RESOURCETYPE {
	Profile, Properties
    }

    public EntryParser(AggregatorServiceGroupEntryResource entry) {
	this.entry = entry;
    }

    
    public EndpointReferenceType getSource() {
	return entry.getMemberEPR();
    }
    
    public EndpointReferenceType getSink() {
	return entry.getEntryEPR();
    }
    
    /**
     * @return the source key or an empty string if it does not exist
     */
    public String getSourceKey() {
	String key = "";
	EndpointReferenceType memberEpr = entry.getMemberEPR();
	try {
	    ReferencePropertiesType prop = memberEpr.getProperties();
	    if (prop != null) {
		MessageElement[] any = prop.get_any();
		if (any.length > 0) 
		    key = any[0].getValue();		
	    }
	} catch (java.lang.NullPointerException npe) {
	    // nothing to do, the source key does not exist (may be the publisher is a singleton
	    // or stateless service)
	}

	return key;
    }

    /**
     * @return the fully qualified source key or an empty string if it does not exist
     */
    public String getQualifiedSourceKey() {
	String key = "";
	EndpointReferenceType memberEpr = entry.getMemberEPR();
	try {
	    ReferencePropertiesType prop = memberEpr.getProperties();
	    if (prop != null) {
		MessageElement[] any = prop.get_any();
		if (any.length > 0) 
		    key = any[0].toString();
	    }
	} catch (java.lang.NullPointerException npe) {
	    // nothing to do, the source key does not exist (may be the publisher is a singleton
	    // or stateless service)
	}

	return key;
    }

    
    /**
     * 
     * @return the source URI, i.e. the URI from which the resource has been registered
     */
    public String getSourceURI() {
	EndpointReferenceType memberEpr = entry.getMemberEPR();
	return memberEpr.getAddress().toString();
    }

    /**
     * 
     * @return the type of the resource (Profile or Properties)
     */
    public String getType() {
	EndpointReferenceType memberEpr = entry.getMemberEPR();
	if (memberEpr.getAddress().toString().endsWith(registryNS)) {
	    return "Profile";
	} else {
	    return "Properties";
	}
    }

    public void getRPSet() {
	// get RP set from entry
	// ResourcePropertySet rpSet = entry.getResourcePropertySet();

	// get content RP from entry
	/*
	 * ResourceProperty contentRP = rpSet.get(ServiceGroupConstants.CONTENT);
	 * 
	 * AggregatorContent content = entry.getContent();
	 * 
	 * AggregatorConfig config = content.getAggregatorConfig();
	 * 
	 * MessageElement[] any = config.get_any();
	 */

    }
    
    /**
     * 
     * @return a parser for the Sink EPR
     * @throws Exception
     */
    public EntryEPRParser getEPRSinkParser() throws Exception {
	return new EntryEPRParser(entry.getEntryEPR());
    }
}
