package org.gcube.informationsystem.collector.impl.resources;


import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * 
 * A gCube profile resource
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GCUBEProfileResource extends BaseDAIXResource {

    public static final String TYPE = "Profile";
    
    public static String ROOT_COLLECTION_NAME = "Profiles";
    
    private String cachedSubtype;
    
    //start and end envelop have to be added for backward compatibility, without them, we have to rewrite all the ISClient queries
    private static String startEnvelop = "<ns1:Profile xmlns:ns1=\"http://gcube-system.org/namespaces/informationsystem/registry\">";
    
    private static  String endEnvelop = "</ns1:Profile>";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCollectionName() throws MalformedResourceException {
	String subType = this.getSubtype();
	return (subType != null)?ROOT_COLLECTION_NAME + "/" + subType : ROOT_COLLECTION_NAME;	
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setCollectionName(String collectionName) {
     //   cannot change the collection name
    }

    /**
     * @param type the resource type (if not set, it's extracted from the content, if any)
     */
    public void setResourceType(String type) {
	cachedSubtype = type;
    }

    /**
     * Accesses the subtype to which the resource is related to (if any)
     * 
     * @return the subtype or an empty string if the resource does not contain a subtype
     * @throws MalformedResourceException
     */
    private String getSubtype() throws MalformedResourceException {
	String subtype = "";
	try {
	    // gets the type
	    XPath path = XPathFactory.newInstance().newXPath();
	    subtype = path.evaluate("/Resource/Type", this.getContent());	   
	} catch (Exception e) {
	    logger.warn("Unable to extract the profile type from the resource");
	    if (cachedSubtype != null)
		return cachedSubtype;
	    throw new MalformedResourceException("Unable to extract the profile type from the resource " + e.getMessage());
	    
	}

	return subtype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deserializeFromIndexing(String content) throws MalformedResourceException {	
	super.deserializeFromIndexing(content.replaceFirst(startEnvelop, "").replace(endEnvelop, ""));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serializeForIndexing() {
	return startEnvelop+ super.serializeForIndexing() + "\n" +endEnvelop;
    }
}
