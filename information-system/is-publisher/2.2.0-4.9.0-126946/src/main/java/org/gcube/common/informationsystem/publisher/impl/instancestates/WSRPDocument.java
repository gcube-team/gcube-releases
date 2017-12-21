package org.gcube.common.informationsystem.publisher.impl.instancestates;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPElement;

import org.apache.axis.message.MessageElement;
import org.gcube.common.core.state.GCUBEPublicationProfile;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.state.GCUBEWSResourcePropertySet;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.utils.AnyHelper;
import org.kxml2.io.KXmlParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;

/**
 * Harvester for WS-Resource Property Documents
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
final class WSRPDocument {

    private static final GCUBELog logger = new GCUBELog(WSRPDocument.class);
    private GCUBEWSResource resource;
    private PublicationProfileParser parser;
    private int pollingInterval = 600; //default polling time is 10 minutes
    private List<QName> resourcePropertyNames;

    WSRPDocument(final GCUBEWSResource resource) throws Exception {
	this.resource = resource;
	this.parser = new PublicationProfileParser(resource.getPorttypeContext().getPublicationProfile());
	parser.parse();
	this.initializetResourcePropertyNames();
	this.pollingInterval = parser.getPollingInterval();
    }

    void initializetResourcePropertyNames() throws Exception {
	resourcePropertyNames = new ArrayList<QName>();
	GCUBEWSResourcePropertySet rps = resource.getResourcePropertySet();
	if (rps == null)
	    throw new Exception("Null property set from " + this.getName());
	try {        	
        	List<QName> serviceRPNames = parser.getRPNames();        
        	// add service RPs
        	for (QName name : serviceRPNames){
        	    logger.trace("found RP " + name);
        	    resourcePropertyNames.add(name);
        	}        	            	
	} catch (Exception e) {
	    logger.error("Unable to retrieve the RP names from " + this.getName(), e);
	    throw e;
	}				
    }

    /**
     * Gets the values of the RPs published by the resource
     * @return a map RP name -> its actual values as {@link SOAPElement}
     * @throws Exception
     */
    Map<QName, SOAPElement[]> getResourcePropertyValues() throws Exception {
	Map<QName, SOAPElement[]> resourcePropertyValues = new LinkedHashMap<QName, SOAPElement[]>();
	GCUBEWSResourcePropertySet rps = resource.getResourcePropertySet();
	for (QName name : this.resourcePropertyNames) {
        	try {        	                     
        	    ResourceProperty rp = rps.get(name);
        	    if (rp == null)
        		logger.warn("Null value for RP " + name);         		
        	    else 
        		resourcePropertyValues.put(name, rp.toSOAPElements());        	        	
        	} catch (Exception e) {
        	    logger.error("Failed to retrieve the value of RP " + name, e);
        	}	
	}
	for (String name : GCUBEWSResourcePropertySet.getSystemRPNames()) {
        	try {        	                     
        	    ResourceProperty rp = rps.getSystemRP(name);
        	    if (rp == null)
        		logger.warn("Null value for RP " + name);         		
        	    else 
        		resourcePropertyValues.put(new QName(GCUBEWSResourcePropertySet.PROVIDER_NS, name), rp.toSOAPElements());        	        	
        	} catch (Exception e) {
        	    e.printStackTrace();
        	    logger.error("Failed to retrieve the value of RP " + name, e);
        	}	
	}
		
	logger.trace("Resource Property values successfully read");
	return resourcePropertyValues;
    }
    
    /**
     * Gets the polling interval
     * @return the polling interval of this document
     */
    int getPollingInterval() {
	return this.pollingInterval;
    }
    
    /**
     * The address of the resource that publishes this document
     * @return the source
     * @throws Exception
     */
    String getSource() throws Exception {
	return this.resource.getEPR().getAddress().toString();
    }

    /**
     * Gets the name of the resource to query
     * @return the resource to query
     */
    String getName() {
	return resource.getClass().getSimpleName() + "(" + resource.getID() + ")";
    }
    
    /**
     * Queries the resource for its resource properties
     * @return the actual Resource Property document
     */
    Document query() throws Exception {
	//get the RPs from the PT
	logger.trace("Querying RPs for " + this.getName());
	
	int attempts = 0;
	Exception ret = null;
	while (attempts++ < 3) {
	    	try {
                	Map<QName, SOAPElement[]> values = this.getResourcePropertyValues();
                	if (values == null) {
                	    logger.warn("No RPs' values available at registration time");
                	    continue;
                	}
                	StringBuilder rpd = new StringBuilder();
                	rpd.append("<ResourceProperties>");
                	//int counter = 0;
                	List<MessageElement> elements = new ArrayList<MessageElement>();
                	for (QName name : values.keySet()) {
                	    if (name == null)
                		continue;
                	    if ( values.get(name) == null) {                		
                		logger.trace("Skipping null values for RP " + name);
                		continue;
                	    }
                	    for (SOAPElement elem : values.get(name)) {
                		if (elem == null) {
                		    logger.warn("Skipping null value for " + name);
                		    continue;
                		}
                		elements.add((MessageElement)elem);
                		//rpd.append(replaceNS(AnyHelper.toSingleString(new MessageElement[]{element}), counter++));
                	    }
                	}
            	    	rpd.append(AnyHelper.toSingleString(elements.toArray(new MessageElement[elements.size()])));
                	rpd.append("</ResourceProperties>");
                	logger.trace("Resource property document is " +rpd);
                        return this.getAsDocument(rpd.toString());
	    	} catch (Exception e) {
		    logger.warn("Failed to read the RPs", e);
		    ret =e;
		}
	    	Thread.sleep(1000);//give a try to initialise the RPs
	  }	
	throw ret;
    }
    
    private Document getAsDocument(String rpd) throws Exception {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware(true);
	DocumentBuilder builder = factory.newDocumentBuilder();	
	StringReader reader = new StringReader(rpd);
	InputSource source = new InputSource(reader);
	return builder.parse(source);
    }
    

    /**
     * Gets the resource property document ID
     * 
     * @return the identifier
     * @throws Exception 
     */
    String getID() throws Exception {
	String source = resource.getEPR().getAddress().toString(); 
	String id = resource.getID().getValue();
	return source.replace("http://", "").replace(":", "").replace("/", "-") + "-" + id.replace("http://", "").replace(":", "").replace("/", "-");
    }
    
    /**
     * Gets the source resource ID
     * 
     * @return the identifier
     * @throws Exception 
     */
    String getSourceID() throws Exception {
	return resource.getID().getValue();	
    }
    
    /**
     * Gets the resource property document ID
     * 
     * @return the identifier
     * @throws Exception 
     */
    static String getID(final GCUBEWSResource resource) throws Exception {
	String source = resource.getEPR().getAddress().toString(); 
	String id = resource.getID().getValue();
	return source.replace("http://", "").replace(":", "").replace("/", "-") + "-" + id.replace("http://", "").replace(":", "").replace("/", "-");
    }


    /**
     * 
     * Parser for the publication profile of a WSResource
     * 
     * @author Manuele Simi (ISTI-CNR)
     * 
     */
    private static class PublicationProfileParser {

	private GCUBEPublicationProfile profile;

	private List<QName> names;

	private Integer pollInterval;

	PublicationProfileParser(GCUBEPublicationProfile profile) {
	    this.profile = profile;
	    this.names = new ArrayList<QName>();
	}

	void parse() throws Exception {
	    logger.trace("Parsing pubblication profile " + profile.getAbsoluteFileName());
	    BufferedReader reader = new BufferedReader(new FileReader(profile.getAbsoluteFileName()));
	    KXmlParser parser = new KXmlParser();
	    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
	    Map<String, String> namespaces = new HashMap<String, String>();
	    try {
		parser.setInput(reader);
		boolean foundRPName = false;
		boolean foundInterval = false;
		loop: while (true) {
		    switch (parser.next()) {
		    case KXmlParser.START_TAG:
			for (int i = parser.getNamespaceCount(parser.getDepth()-1); i < parser.getNamespaceCount(parser.getDepth()); i++) { 			
			    logger.trace("Found namespace " + parser.getNamespacePrefix(i) + ", uri "+ parser.getNamespaceUri(i));				namespaces.put(parser.getNamespacePrefix(i), parser.getNamespaceUri(i));
			    namespaces.put( parser.getNamespacePrefix(i), parser.getNamespaceUri(i));
			}
			if (parser.getName().equalsIgnoreCase("ResourcePropertyNames"))
			    foundRPName = true;
			if (parser.getName().equalsIgnoreCase("PollIntervalMillis"))
			    foundInterval = true;
			break;
		    case KXmlParser.TEXT:
			if (foundRPName) {
			    //clean up from the ns alias (if any)
			    String[] tokens = parser.getText().split(":");
			    logger.trace("Parsing RP: " + Arrays.toString(tokens));
			    this.names.add(new QName (parser.getNamespace(tokens[0]), tokens[tokens.length-1]));
			    foundRPName = false;
			}
			if (foundInterval) {
			    this.pollInterval = Integer.valueOf(parser.getText());
			    foundInterval = false;
			}
			break;
		    case KXmlParser.END_TAG:
			break;
		    case KXmlParser.END_DOCUMENT:
			break loop;
		    }

		}
		reader.close();
	    } catch (Exception e) {
		logger.warn("Could not configure resource publication because " + e.getMessage());
		throw e;
	    }

	}

	List<QName> getRPNames() {
	    return this.names;
	}

	int getPollingInterval() {
	    return this.pollInterval;
	}
    }


    String getNamespace() throws Exception {

//	String ret = "<ns1:ResourceKey xmlns:ns1=\"NS\">KEY</ns1:ResourceKey>";
//	ret = ret.replace("NS", this.resource.getID().getName().getNamespaceURI());
//	ret = ret.replace("KEY", this.resource.getID().getValue());
//	logger.info("Returned key" + ret);
//	return ret;
	return this.resource.getID().getName().getNamespaceURI();
    }

    
}
