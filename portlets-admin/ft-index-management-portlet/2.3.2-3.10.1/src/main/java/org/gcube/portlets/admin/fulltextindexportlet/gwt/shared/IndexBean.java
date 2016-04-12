package org.gcube.portlets.admin.fulltextindexportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A class used to transport index information between the client and the
 * service
 */
public class IndexBean implements IsSerializable, Comparable<IndexBean> {
    /** an empty constructor */
    public IndexBean() {
    };

    /** The ID of the index */
    private String id = null;

    /** The name of the index */
    private String name = null;

    /** The host the IndexManagement currently recides on */
    private String host = null;

    /** The current serialized EndPointReference of the IndexManagement */
    private String epr = null;

    /**
     * Gets the ID of the index
     * 
     * @return the ID of the index
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of the index
     * 
     * @return the name of the index
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the host the IndexManagement currently recides on
     * 
     * @return the host the IndexManagement currently recides on
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the current serialized EndPointReference of the IndexManagement
     * 
     * @return the current serialized EndPointReference of the IndexManagement
     */
    public String getEpr() {
        return epr;
    }

    /**
     * Sets the ID of the index
     * 
     * @param id -
     *            the new ID to assign to this index representation
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the Name of the index
     * 
     * @param name -
     *            the new Name to assign to this index representation
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the Host the IndexManagement currently resides on
     * 
     * @param host -
     *            the new Host to assign to this index representation
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the current EndPointReference of the IndexManagement
     * 
     * @param epr -
     *            the new EndPointReference to assign to this index
     *            representation
     */
    public void setEpr(String epr) {
        this.epr = epr;
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public int compareTo(IndexBean arg0) {
        return this.id.compareTo(arg0.getId());
    }

}
