package org.gcube.common.informationsystem.publisher.impl.generic;

import org.gcube.common.core.informationsystem.publisher.ISResource;
import org.w3c.dom.Document;

/**
 * 
 * Resource for WS-DAIX registrations
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class WSDAIXResource implements ISResource {

    private String collection;
    private String id;
    private String docName;
    private Document document;
    private ISRESOURCETYPE type = ISRESOURCETYPE.WSDAIX;
    
    /**
     * {@inheritDoc}
     */
    public String getCollection() {
	return this.collection;
    }

    /**
     * {@inheritDoc}
     */
    public Document getDocument() {
	return this.document;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
	return this.docName;
    }

    /**
     * {@inheritDoc}
     */
    public String getID() {
	return this.id;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setCollection(String collection) {
	    this.collection =  collection;	
    }

    /**
     * {@inheritDoc}
     */
    public void setDocument(Document document) {
	this.document = document;

    }

    /**
     * {@inheritDoc}
     */
    public void setName(String documentName) {
	this.docName = documentName;
    }

    /**
     * {@inheritDoc}
     */
    public void setID(String id) {	
	this.id = id;	
    }

    /**
     * {@inheritDoc}
     */
    public ISRESOURCETYPE getType() {
	return this.type;
    }

    /**
     * {@inheritDoc}
     */
    public void setType(ISRESOURCETYPE type) {
	this.type = type;
	
    }

    
}
