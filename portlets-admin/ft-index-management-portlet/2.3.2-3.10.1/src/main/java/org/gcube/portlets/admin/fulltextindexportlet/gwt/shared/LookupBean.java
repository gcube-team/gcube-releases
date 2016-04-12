package org.gcube.portlets.admin.fulltextindexportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A class used to transport IndexLookup information between the client and the
 * service
 */
public class LookupBean implements IsSerializable, Comparable<LookupBean> {
    /** an empty constructor */
    public LookupBean() {
    };

    /** The ID of the Index */
    private String id = null;

    /** The host the IndexLookup currently recides on */
    private String host = null;

    /** The current serialized EndPointReference of the IndexLookup */
    private String epr = null;

    /** The ConnectionID of the IndexLookup, -1 indicates that it is unset */
    private int connID = -1;

    /**
     * Gets the ID of the Index
     * 
     * @return the ID of the Index
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the ConnectionID of the IndexLookup
     * 
     * @return the ConnectionID of the IndexLookup
     */
    public int getConnectionID() {
        return connID;
    }

    /**
     * Gets the host the IndexLookup currently recides on
     * 
     * @return the host the IndexLookup currently recides on
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the current serialized EndPointReference of the IndexLookup
     * 
     * @return the current serialized EndPointReference of the IndexLookup
     */
    public String getEpr() {
        return epr;
    }

    /**
     * Sets the ID of the Index
     * 
     * @param id -
     *            the new ID to assign to this IndexLookup representation
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the ConnectionID of the IndexLookup
     * 
     * @param connID -
     *            the new ConnectionID to assign to this IndexLookup
     *            representation
     */
    public void setConnectionID(int connID) {
        this.connID = connID;
    }

    /**
     * Sets the Host the IndexLookup currently resides on
     * 
     * @param host -
     *            the new Host to assign to this IndexLookup representation
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the current EndPointReference of the IndexLookup
     * 
     * @param epr -
     *            the new EndPointReference to assign to this IndexLookup
     *            representation
     */
    public void setEpr(String epr) {
        this.epr = epr;
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public int compareTo(LookupBean arg0) {
        return this.connID - arg0.getConnectionID();
    }

}
