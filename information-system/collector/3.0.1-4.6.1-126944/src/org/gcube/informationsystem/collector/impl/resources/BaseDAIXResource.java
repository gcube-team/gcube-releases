package org.gcube.informationsystem.collector.impl.resources;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.common.core.utils.logging.GCUBELog;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

/**
 * 
 * Encapsulates a WS-DAIX data resource which represents a
 * collection or document in an XML database.
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class BaseDAIXResource implements DAIXResource {

    protected String resourceName;
    
    protected URI resourceURI;
    
    protected String collectionName;   
    
    protected Document data = null;
    
    protected String dataAsString = null;
    
    protected final String rootElement = "Data";
    
    protected static GCUBELog logger = new GCUBELog(BaseDAIXResource.class);


    public BaseDAIXResource() {}
    
    public BaseDAIXResource(String resourceName) {
	this.resourceName = resourceName;
    }
   
    /**
     * @return the resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @param resourceName the resourceName to set
     */
    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * @return the resourceURI
     */
    public URI getResourceURI() {
        return resourceURI;
    }

    /**
     * @param resourceURI the resourceURI to set
     */
    public void setResourceURI(final URI resourceURI) {
        this.resourceURI = resourceURI;
    }

    /**
     * @return the name of the collection including the resource
     * @throws MalformedResourceException 
     */
    public String getCollectionName() throws MalformedResourceException {
        return collectionName;
    }

    /**
     * @param collectionName the name of the collection including the resource
     */
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
                             
    public Document getContent() throws MalformedResourceException {
	if (this.data != null) {
	    return this.data;
	} else if (this.dataAsString.compareToIgnoreCase("") != 0) {
	    try {
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    factory.setNamespaceAware(false);
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    StringReader reader = new StringReader(this.dataAsString);
		    InputSource source = new InputSource(reader);
		    return builder.parse(source);	  
		} catch (Exception e) {
		    throw new MalformedResourceException(e);
		}
	}
	throw new MalformedResourceException("content is null");
	
    }

    public void setContent(Document content) throws MalformedResourceException {
	this.data = content;	
    }
    
    public void setContent(String content) throws MalformedResourceException {	
	this.dataAsString = content;		
    }
    
    public String toString() {
	if (this.dataAsString != null)
	    return this.dataAsString;
	try {
	    TransformerFactory transFactory = TransformerFactory.newInstance();
	    Transformer transformer = transFactory.newTransformer();
	    StringWriter buffer = new StringWriter();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    transformer.transform(new DOMSource(this.data), new StreamResult(buffer));
	    return buffer.toString();
	} catch (TransformerConfigurationException e) {
	    logger.error("Unable to deserialise content data", e);	    
	} catch (TransformerException e) {
	    logger.error("Unable to deserialise content data", e);
	}
	return "invalid resource";

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
    public String toStringFromElement(String elementName) throws MalformedResourceException {
	
	Document doc;
	try {
	    doc = this.getContent(); //maybe there exists only a string content
	} catch (Exception e)  {	    
	    //let's try to return the string content
	    return this.toString();	   
	}
	try {	    	    	    
	    NodeList nodelist = doc.getElementsByTagName(elementName);
	    //if the element is not present, the entire content is returned
	    if (nodelist == null || nodelist.getLength() == 0)
		return this.toString();
	    Node targetNode = nodelist.item(0);
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
		node = targetNode.getChildNodes().item(++index);		
	    }
	    return ret.toString();

	} catch (Exception e) {
	    logger.error("Unable to deserialise content data", e);
	    throw new MalformedResourceException("Unable to deserialise the resource");
	}

    }
   
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
	return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;	
	BaseDAIXResource other = (BaseDAIXResource) obj;
	if (resourceName == null) {
	    if (other.resourceName != null)
		return false;
	} else if (!resourceName.equals(other.resourceName))
	    return false;
	return true;
    }

    /**
     * {@inheritDoc}
     */
    public void deserializeFromIndexing(String content) throws MalformedResourceException {
	this.setContent(content);
	
    }

    /**
     * {@inheritDoc}
     */
    public String serializeForIndexing() {	
	return this.toString();
    }

        
}
