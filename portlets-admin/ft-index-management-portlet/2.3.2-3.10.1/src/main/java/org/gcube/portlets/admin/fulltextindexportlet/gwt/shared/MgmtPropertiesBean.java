package org.gcube.portlets.admin.fulltextindexportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A class used to transport IndexManagement ResourceProperty information
 * between the client and the service
 */
public class MgmtPropertiesBean implements IsSerializable {

    /** The Creation Time of the IndexManagement */
    private String created;

    /** The Modified Time of the IndexManagement */
    private String modified;

    /** The Status property of the IndexManagement */
    private String clusterID;
    
    /** The host of the index resource */
    private String host;
    
    /** An empty constructor */
    public MgmtPropertiesBean() {
    }

    
    /**
     * Gets the Creation Time of the IndexManagement
     * 
     * @return the Creation Time of the IndexManagement
     */
    public String getCreated() {
        return created;
    }

    /**
     * Gets the Modified Time of the IndexManagement
     * 
     * @return the Modified Time of the IndexManagement
     */
    public String getModified() {
        return modified;
    }

    /**
     * Gets the Status property of the IndexManagement
     * 
     * @return the Status property of the IndexManagement
     */
    public String getClusterID() {
        return clusterID;
    }
    
    /**
     * Gets the name of the host where the index resource resides
     * @return the host name
     */
    public String getHost() {
    	return host;
    }
    
    /**
     * Gets the Creation Time of the IndexManagement
     * 
     * @param created
     *            -Creation Time of the IndexManagement
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     * Gets the Modified Time of the IndexManagement
     * 
     * @param modified -
     *            the new Modified Time of the IndexManagement
     */
    public void setModified(String modified) {
        this.modified = modified;
    }

    /**
     * Gets the Status property of the IndexManagement
     * 
     * @param status -
     *            the new Status property of the IndexManagement
     */
    public void setClusterID(String clusterID) {
        this.clusterID = clusterID;
    }
    
    /**
     * Sets the name of the host where the index resource resides
     * 
     * @param host the host name
     */
    public void setHost(String host) {
    	this.host = host;
    }
}
