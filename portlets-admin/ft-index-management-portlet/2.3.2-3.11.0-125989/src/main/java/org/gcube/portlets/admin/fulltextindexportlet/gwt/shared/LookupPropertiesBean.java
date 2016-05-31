package org.gcube.portlets.admin.fulltextindexportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A class used to transport IndexLookup ResourceProperty information between
 * the client and the service
 */
public class LookupPropertiesBean implements IsSerializable {

    /** The IndextypeID of the IndexLookup */
    private String indexTypeID;

    /** The IndexFormat of the IndexLookup */
    private String indexFormat;

    /** The Content Type of the IndexLookup */
    private String contentType;

    /** The Creation Time of the IndexLookup */
    private String created;

    /** The Modified Time of the IndexLookup */
    private String modified;

    /** The Status property of the IndexLookup */
    private String status;

    /** The number of currently indexed documents */
    private int numDocs;
    
    /** The host of the index resource */
    private String host;
    
    /** An empty constructor */
    public LookupPropertiesBean() {
    }

    /**
     * Gets IndextypeID of the IndexLookup
     * 
     * @return the IndextypeID of the IndexLookup
     */
    public String getIndexTypeID() {
        return indexTypeID;
    }

    /**
     * Gets the IndexFormat of the IndexLookup
     * 
     * @return the IndexFormat of the IndexLookup
     */
    public String getIndexFormat() {
        return indexFormat;
    }

    /**
     * Gets the Content Type of the IndexLookup
     * 
     * @return the Content Type of the IndexLookup
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Gets the Creation Time of the IndexLookup
     * 
     * @return the Creation Time of the IndexLookup
     */
    public String getCreated() {
        return created;
    }

    /**
     * Gets the Modified Time of the IndexLookup
     * 
     * @return the Modified Time of the IndexLookup
     */
    public String getModified() {
        return modified;
    }

    /**
     * Gets the Status property of the IndexLookup
     * 
     * @return the Status property of the IndexLookup
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the number of currently indexed documents
     * @return the number of indexed documents
     */
    public int getDocumentCount() {
    	return numDocs;
    }
    
    /**
     * Gets the name of the host where the lookup resource resides
     * @return the host name
     */
    public String getHost() {
    	return host;
    }
    
    /**
     * Sets IndextypeID of the IndexLookup
     * 
     * @param indexTypeID -
     *            the new IndextypeID of the IndexLookup
     */
    public void setIndexTypeID(String indexTypeID) {
        this.indexTypeID = indexTypeID;
    }

    /**
     * Gets the IndexFormat of the IndexLookup
     * 
     * @param indexFormat -
     *            the new IndexFormat of the IndexLookup
     */
    public void setIndexFormat(String indexFormat) {
        this.indexFormat = indexFormat;
    }

    /**
     * Gets the Content Type of the IndexLookup
     * 
     * @param contentType -
     *            the new Content Type of the IndexLookup
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the Creation Time of the IndexLookup
     * 
     * @param created
     *            -Creation Time of the IndexLookup
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     * Gets the Modified Time of the IndexLookup
     * 
     * @param modified -
     *            the new Modified Time of the IndexLookup
     */
    public void setModified(String modified) {
        this.modified = modified;
    }

    /**
     * Gets the Status property of the IndexLookup
     * 
     * @param status -
     *            the new Status property of the IndexLookup
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the number of currently indexed documents
     * 
     * @param docCount the number of indexed documents
     */
    public void setDocumentCount(int docCount) {
    	this.numDocs = docCount;
    }
    
    /**
     * Sets the name of the host where the lookup resource resides
     * 
     * @param host the host name
     */
    public void setHost(String host) {
    	this.host = host;
    }
}
