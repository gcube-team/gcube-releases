package org.gcube.informationsystem.collector.impl.resources;

import java.net.URI;

import org.w3c.dom.Document;

/**
 * 
 * DAIXResource interface
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface DAIXResource {
    
    /**
     * Sets the resource unique name
     * @param resourceName the resource name
     */
    public  void setResourceName(String resourceName);
    
    public String getResourceName() throws MalformedResourceException;
    
    public URI getResourceURI();
    
    public String toString();       
    
    public String toStringFromElement(String elementName) throws MalformedResourceException;
    
    public void setCollectionName(String collectionName);
    
    public String getCollectionName() throws MalformedResourceException;
    
    public void setContent(Document content) throws MalformedResourceException;
    
    public void setContent(String content) throws MalformedResourceException;
    
    /**
     * Creates a serialization of the resource to be indexed
     * @return the serialized resource
     */
    public String serializeForIndexing() throws MalformedResourceException;
    
    /**
     * Deserializes the content retrieved from the XML storage
     * @param content the content 
     */
    public void deserializeFromIndexing(String content) throws MalformedResourceException;
    
    /**
     * The resource content as {@link Document}
     * @return
     * @throws MalformedResourceException
     */
    public Document getContent() throws MalformedResourceException;
    
    /**
     * Indicates whether some other resource is "equal to" this one 
     * @param obj the other resource to compare
     * @return true if the resource is the same to this one
     */
    public boolean equals(Object obj);
    
    public int hashCode();
    

    /** 
     * 
     * Malformed resource exception
     *
     * @author Manuele Simi (ISTI-CNR)
     *
     */
    public static class MalformedResourceException extends Exception {
	private static final long serialVersionUID = 1L;
	public MalformedResourceException(Exception e) {super(e);}
	public MalformedResourceException(String message) {super(message);}
    }
   
}
