package org.gcube.informationsystem.collector.impl.resources;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.gcube.common.core.state.GCUBEWSResourcePropertySet;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.resources.DAIXResource.MalformedResourceException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * 
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class GCUBEXMLResource {

    /**
     * the resource type states if the resource contains a profile or generic WS-ResourceProperties
     */
    public enum RESOURCETYPE {
	Profile, Properties
    }

    protected RESOURCETYPE type = null;

    protected static GCUBELog logger = new GCUBELog(GCUBEXMLResource.class);

    protected Calendar terminationTime = null, lastUpdateTime = null;

    protected String entryKey, groupKey, source, sourceKey = "", namespace = "";

    // xpath factory to evaluate Xpath expressions
    static protected XPath path = XPathFactory.newInstance().newXPath();
    
    static protected Transformer transformer;

    private DAIXResource resource;

    private String publicationMode = "";

    public GCUBEXMLResource(DAIXResource resource) throws MalformedXMLResourceException {
	this.resource = resource;
	this.terminationTime = new GregorianCalendar();
	this.terminationTime.setTimeZone(TimeZone.getTimeZone("GMT"));
	this.lastUpdateTime = new GregorianCalendar();
	this.lastUpdateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
	try {
	    if (resource.getResourceName() == null)
	        throw new MalformedXMLResourceException("Invalid resource name");
	} catch (MalformedResourceException e) {
	    throw new MalformedXMLResourceException("Invalid resource name");
	}
    }

   
    /**
     * @return the name of the collection including the resource
     * @throws MalformedResourceException
     */
    public String getCollectionName() throws MalformedXMLResourceException {
	try {
	    return this.resource.getCollectionName();
	} catch (MalformedResourceException e) {
	    throw new MalformedXMLResourceException(e);
	}
    }

    /**
     * @return the resource name
     * @throws MalformedResourceException
     */
    public String getResourceName() throws MalformedXMLResourceException {
	try {
	    return this.resource.getResourceName();
	} catch (MalformedResourceException e) {
	    throw new MalformedXMLResourceException(e);
	}
    }

    /**
     * @return the terminationTime of this resource
     */
    public Calendar getTerminationTime() {
	return this.terminationTime;
    }

    /**
     * @return the lastUpdateTime of this resource
     */
    public Calendar getLastUpdateTime() {
	return this.lastUpdateTime;
    }

    /**
     * @param terminationTime
     *            the terminationTime to set
     */
    public void setTerminationTime(final Calendar terminationTime) {
	this.terminationTime = (Calendar) terminationTime.clone();
	this.terminationTime.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * @return the lastUpdateTime in milliseconds
     * @throws Exception
     *             if an error occurs when accessing the LastUpdateMs field
     */
    public long getLastUpdateTimeinMills() throws MalformedResourceException {

	if (lastUpdateTime != null)
	    return lastUpdateTime.getTimeInMillis();
	else
	    throw new MalformedResourceException("unable to retrieve last update time for resource " + this.resource.getResourceName());	
    }

    /**
     * Accesses the source GroupKey
     * 
     * @return the ID
     */
    public String getGroupKey() {
	return this.groupKey;
    }

    /**
     * Sets the source GroupKey
     * 
     * @param groupKey
     *            the new group key
     * 
     */
    public void setGroupKey(String groupKey) {
	this.groupKey = groupKey;
    }

    /**
     * Accesses the source EntryKey
     * 
     * @return the ID
     */
    public String getEntryKey() {
	return this.entryKey;
    }

    /**
     * Sets the source EntryKey
     * 
     * @param entryKey
     *            the new entry key
     */
    public void setEntryKey(String entryKey) {
	this.entryKey = entryKey;
    }

    /**
     * Sets the source address of the RI that publishes resource as reported in the servicegroup
     * entry
     * 
     * @param source
     *            the new source address
     */
    public void setSource(String source) {
	this.source = source;
    }

    /**
     * Accesses the source address of the service that published the data
     * 
     * @return the source
     */
    public String getSource() {
	return this.source;
    }
    
    /**
     * The mode in which the resource was published
     * 
     * @return the mode
     */
    public String getPublicationMode() {
	return this.publicationMode;
    }

    /**
     * Sets the key of the WS-Resource that published the data
     * 
     * @param key
     *            the new source key
     */
    public void setSourceKey(String key) {
	this.sourceKey = key;
    }

    /**
     * Gets the key of the WS-Resource that published the data
     * 
     * @return the key
     */
    public String getSourceKey() {
	return this.sourceKey;
    }

    /**
     * Sets the namespace
     * 
     * @param namamespace
     *            the namespace
     */
    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }

    /**
     * Gets the namespace 
     * 
     * @return the namespace
     */
    public String getNamespace() {
	return this.namespace;
    }

    public void setPublicationMode(String publicationMode) {
	this.publicationMode  = publicationMode;
	
    }
    /**
     * Sets the content of the resource
     * @param content the content
     * @param enveloped true if the content is wrapped in the metadata envelop
     */
    public void deserializeFromIndexing(String content, boolean... enveloped) {
	try {
	    if (enveloped != null && enveloped.length > 0 && enveloped[0]) {		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringReader reader = new StringReader(content);
		InputSource source = new InputSource(reader);
		Document doc = builder.parse(source);		
		this.parseEnvelop(doc);
		resource.deserializeFromIndexing(this.removeEnvelop(doc));
	    }
	    else
		resource.deserializeFromIndexing(content);
	} catch (Exception e) {
	    
	} 

    }
    
    /**
     * Extract the resource informantion from the envelop
     * @param doc the enveloped content
     * @throws MalformedXMLResourceException
     */
    private void parseEnvelop(final Document doc) throws MalformedXMLResourceException {
	String value = "";
	    try {					
		value = path.evaluate("Document/LastUpdateMs", doc);
		this.lastUpdateTime.setTimeInMillis(Long.parseLong(value));
		value = path.evaluate("Document/TerminationTime", doc);
		this.terminationTime.setTimeInMillis(Long.parseLong(value));
		value = path.evaluate("Document/Source", doc);
		this.setSource(value);
		value = path.evaluate("Document/SourceKey", doc);
		this.setSourceKey(value);
		value = path.evaluate("Document/EntryKey", doc);
		this.setEntryKey(value);
		value = path.evaluate("Document/GroupKey", doc);
		this.setGroupKey(value);
		value = path.evaluate("Document/Namespace", doc);
		this.setNamespace(value);
		value = path.evaluate("Document/PublicationMode", doc);
		this.setPublicationMode(value);
	    } catch (Exception xpee) {
		logger.error("" + xpee.getMessage());
		logger.error("" + xpee.getStackTrace());
		throw new MalformedXMLResourceException("Unable to retrieve last update time for resource");
	    }
	
    }

    public String toString() {
	// we do not use an XML parser for performance reasons
	StringBuilder resource = new StringBuilder("<Document>\n");
	try {
	    
	    resource.append("<ID>" + this.resource.getResourceName() + "</ID>\n");
	    resource.append("<Source>" + this.getSource() + "</Source>\n");
	    resource.append("<SourceKey>" + this.getSourceKey() + "</SourceKey>\n");
	    
	    if (this.getNamespace()!=null && this.getNamespace().length() > 0) { 
		String completeKey = "<ns1:ResourceKey xmlns:ns1=\"NS\">KEY</ns1:ResourceKey>";
		completeKey = completeKey.replace("NS", this.getNamespace());
		completeKey = completeKey.replace("KEY", this.getSourceKey());
		resource.append("<CompleteSourceKey>" + completeKey + "</CompleteSourceKey>\n");
	    } else {
		resource.append("<CompleteSourceKey></CompleteSourceKey>\n");
	    }
	    resource.append("<EntryKey>" + this.getEntryKey() + "</EntryKey>\n");
	    resource.append("<GroupKey>" + this.getGroupKey() + "</GroupKey>\n");
	    resource.append("<TerminationTime>" + this.getTerminationTime().getTimeInMillis() + "</TerminationTime>\n");
	    resource.append("<TerminationTimeHuman>" + this.getTerminationTime().getTime().toString() + "</TerminationTimeHuman>\n");
	    resource.append("<LastUpdateMs>" + this.lastUpdateTime.getTimeInMillis() + "</LastUpdateMs>\n");
	    resource.append("<LastUpdateHuman>" + this.lastUpdateTime.getTime().toString() + "</LastUpdateHuman>\n");
	    resource.append("<PublicationMode>" + this.publicationMode + "</PublicationMode>\n");
	    resource.append("<Data>\n");
	    if (this.resource instanceof GCUBEInstanceStateResource) {
		//this check is to avoid a stupid message ("[Fatal Error] :1:83: The markup in the document following the root element must be well-formed.") in the nohup.out from the sax parser
		if (this.resource.toString().startsWith("<"+GCUBEInstanceStateResource.INSTANCESTATE_ROOT_ELEMENT))
		    resource.append(this.resource.toStringFromElement(GCUBEInstanceStateResource.INSTANCESTATE_ROOT_ELEMENT) + "\n");
		else
		    resource.append(this.resource.serializeForIndexing() + "\n");
	    }
	    else
		resource.append(this.resource.serializeForIndexing() + "\n");
	    resource.append("</Data>\n");
	    resource.append("</Document>");
	} catch (MalformedResourceException e) {
	    logger.error("invalid content", e);
	    throw new RuntimeException("invalid content");

	}
	return resource.toString();
    }

    /**
     * Removes the document envelop from the document
     * 
     * @param doc  the content to clean up
     * @return the content string without the document envelop
     * @throws MalformedResourceException
     */
    private String removeEnvelop(final Document doc) throws MalformedXMLResourceException {
	try {
	    return this.toStringFromElement(doc, "Data"); // Data/*
	} catch (Exception e) {
	    logger.error("unable to retrieve parse the resource's content ", e);
	}
	throw new MalformedXMLResourceException("unable to retrieve parse the resource's content");
    }

    
    /**
     * Returns a sub-serialization of the given XML, starting from the element name
     * 
     * @param xml
     *            the source XML serialization
     * @param elementName
     *            the name of the element
     * @return the node content serialized as string
     * @throws Exception
     *             if the serialization fails
     */
    public String toStringFromElement(final Document xml, String elementName) throws MalformedXMLResourceException {

	try {	    
	    Node targetNode = xml.getElementsByTagName(elementName).item(0);
	    TransformerFactory transFactory = TransformerFactory.newInstance();
	    Transformer transformer = transFactory.newTransformer();	    
	    StringBuilder ret = new StringBuilder();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    int index = 0;
	    Node node = targetNode.getChildNodes().item(index);
	    while (node != null) {
		StringWriter buffer = new StringWriter();
		transformer.transform(new DOMSource(node), new StreamResult(buffer));
		ret.append(buffer.toString().trim());
		node = targetNode.getChildNodes().item(index++);		
	    }	    	  
	    return ret.toString();

	} catch (Exception e) {
	    logger.error("Unable to deserialise content data", e);
	    throw new MalformedXMLResourceException("Unable to deserialise the resource");
	}

    }

       /**
     * Gets the wrapped {@link BaseDAIXResource}'s content
     * @return
     * @throws MalformedXMLResourceException
     */
    public Document getContent() throws MalformedXMLResourceException {
	try {
	   try {
	       return resource.getContent();	       
	   } catch (Exception e) {
	       //try to wrap with a root element
	       StringBuilder resource = new StringBuilder("<Data>\n");
	       resource.append(this.resource.toString() + "\n");
	       resource.append("</Data>\n");
	       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	       factory.setNamespaceAware(false);
	       DocumentBuilder builder = factory.newDocumentBuilder();
	       StringReader reader = new StringReader(resource.toString());
	       InputSource source = new InputSource(reader);
	       return builder.parse(source);
	   }
	   	        	    
	} catch (Exception e) {
	    logger.error("Invalid data", e);
	    throw new MalformedXMLResourceException("Invalid data");
	}
    }

    /**
     * Removes the {@link GCUBEInstanceStateResource}'s root element, if it exists 
     * (for backward compatibility, the RPs has to be rooted with the Data element)
     * @param content the content to trim
     * @return the trimmed content
     */
//    private String trimProperties(final String content) {
//	String trimmedContent = content;
//	String elem = "<"+GCUBEInstanceStateResource.INSTANCESTATE_ROOT_ELEMENT +">";
//	if (content.startsWith(elem)) {
//	    trimmedContent=trimmedContent.substring(elem.length(), trimmedContent.length());
//	}
//	
//	elem = "</"+GCUBEInstanceStateResource.INSTANCESTATE_ROOT_ELEMENT +">";
//	if (content.endsWith(elem)) {	    
//	    //trimmedContent=trimmedContent.replace(, "");
//	    trimmedContent=trimmedContent.substring(0, trimmedContent.length() - elem.length());
//	}
//	return trimmedContent;
//    }
    
    /**
     * 
     * Malformed XML resource exception
     * 
     * @author Manuele Simi (ISTI-CNR)
     * 
     */
    public static class MalformedXMLResourceException extends Exception {
	private static final long serialVersionUID = 1L;

	public MalformedXMLResourceException(Exception e) {
	    super(e);
	}

	public MalformedXMLResourceException(String message) {
	    super(message);
	}
    }

    public String getSourceRunningInstance() throws Exception {
	try {

	    if (transformer==null) {
		transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
	    }
	    List<String> results = new ArrayList<String>();
	    NodeList set = (NodeList) path.evaluate("/"+GCUBEWSResourcePropertySet.RP_RIID_NAME+"/text()",
	    this.resource.getContent(), XPathConstants.NODESET);
		for (int i=0;i<set.getLength();i++) {
			StreamResult sr = new StreamResult(new StringWriter());
			try {transformer.transform(new DOMSource(set.item(i)),sr);}catch(Exception ignore) {continue;}
			results.add(sr.getWriter().toString());
		}
	    return results.get(0);
	    
	}  catch(Exception e) {
	    logger.error("Unable to get RIID",e); throw e;
	}
	
    }

}
