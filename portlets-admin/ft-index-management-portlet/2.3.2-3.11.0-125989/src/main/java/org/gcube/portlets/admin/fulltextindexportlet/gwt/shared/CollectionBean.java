package org.gcube.portlets.admin.fulltextindexportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A class used to transport collection information between the client and the
 * service
 * 
 */
public class CollectionBean implements IsSerializable, Comparable<CollectionBean> {
    /** A boolean used to decide whether this collection actually exists */
    private boolean isReal = false;

    /** The Collection's ID */
    private String id = null;

    /** The Collection's Name */
    private String name = null;

    /** The Collection's schema */
    private String schema = null;
    
    /** The Collection's language */
    private String language = null;
    
    /**
     * A list of all the indices (in the form of IndexBeans) valid for this
     * collection
     */
    private ArrayList<IndexBean> indices = new ArrayList<IndexBean>();

    /** Empty constructor */
    public CollectionBean() {
    };

    /**
     * A method to get the Collection ID
     * 
     * @return the ID of the collection
     */
    public String getId() {
        return id;
    }

    /**
     * A method to get the Collection Name
     * 
     * @return the name of the collection
     */
    public String getName() {
        return name;
    }

    /**
     * A method to get the Collection schema
     * 
     * @return the schema of the collection
     */
    public String getSchema() {
    	return schema;
    }
    
    /**
     * A method to get the Collection language
     * 
     * @return the language of the collection
     */
    public String getLanguage() {
    	return language;
    }
    
    /**
     * A method indicating whether this collection actually exists
     * 
     * @return true if the Collection actually exists
     */
    public boolean getIsReal() {
        return isReal;
    }

    /**
     * A method used to get a list of all the indices valid for this collection
     * 
     * @return a list of the indices connected with this collection
     */
    public ArrayList<IndexBean> getIndices() {
        return indices;
    }

    /**
     * Assigns an ID to the collection
     * 
     * @param id -
     *            the ID to assign to this collection
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Assigns a name to the collection
     * 
     * @param name -
     *            the name to assign to this collection
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Assigns a schema to the collection
     * 
     * @param schema - the schema to assign to this collection
     */
    public void setSchema(String schema) {
    	this.schema = schema;
    }
    
    /**
     * Assigns a language to the collection
     * 
     * @param language - the language to assign to this collection
     */
    public void setLanguage(String language) {
    	this.language = language;
    }
    
    /**
     * Marks the collection as existing or non-existing
     * 
     * @param isReal -
     *            an indicator of whether the collection exists or not
     */
    public void setIsReal(boolean isReal) {
        this.isReal = isReal;
    }

    /**
     * Sets which indices should be connected to the collection
     * 
     * @param indices -
     *            the list of indices to be connected with this collection
     */
    public void setIndices(ArrayList<IndexBean> indices) {
        this.indices = indices;
    }

    /**
     * Adds one index to the list of indices to be connected with this
     * collection
     * 
     * @param index -
     *            the index to be added to the list of indices connected with
     *            this collection
     */
    public void addIndex(IndexBean index) {
        this.indices.add(index);
    }
    
    public void sort() {
        Collections.sort(this.indices);
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public int compareTo(CollectionBean arg0) {
        return this.id.compareTo(arg0.getId());
    }
}
