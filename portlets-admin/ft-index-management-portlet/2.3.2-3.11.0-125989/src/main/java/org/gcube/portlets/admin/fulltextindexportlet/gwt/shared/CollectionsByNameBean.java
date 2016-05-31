/*package org.gcube.portlets.admin.fulltextindexportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

*//**
 * A class representing all collections with a single name.
 * 
 *//*
public class CollectionsByNameBean implements IsSerializable, Comparable<CollectionsByNameBean> {

    *//** The collection Name of the collections represented by this instance *//*
    private String name = null;

    *//**
     * All collections marked as using the specified collection name
     *//*
    private ArrayList<CollectionBean> collections = new ArrayList<CollectionBean>();

    *//** an empty constructor *//*
    public CollectionsByNameBean() {
    };

    *//**
     * A method to retrieve the collection name used by the collections under
     * this instance
     * 
     * @return - the collection name used by the collections under this instance
     *//*
    public String getName() {
        return name;
    }

    *//**
     * A method to retrieve a list of all collections marked as using the
     * specified collection name
     * 
     * @return a list of all collections marked as using the specified
     *         collection name
     *//*
    public List<CollectionBean> getCollections() {
        return collections;
    }

    *//**
     * A method to specify the collection name this instance should manage
     * 
     * @param name
     *            -the collection name this instance should manage
     *//*
    public void setName(String name) {
        this.name = name;
    }

    *//**
     * Sets the list of all collections marked as using the specified collection
     * name
     * 
     * @param collections
     *//*
    public void setCollections(ArrayList<CollectionBean> collections) {
        this.collections = collections;
    }

    *//**
     * Adds a collection to the list of all collections marked as using the
     * specified collection name
     * 
     * @param collections -
     *            the collection to be added
     *//*
    public void addCollection(CollectionBean collections) {
        this.collections.add(collections);
    }
    
    public void sort() {
        Collections.sort(this.collections);
        for(int i = 0; i < collections.size(); i++ ){
            collections.get(i).sort();
        }
    }
    
    *//**
     * {@inheritDoc}
     * 
     *//*
    public int compareTo(CollectionsByNameBean arg0) {
        return this.name.compareTo(arg0.getName());
    }
}
*/