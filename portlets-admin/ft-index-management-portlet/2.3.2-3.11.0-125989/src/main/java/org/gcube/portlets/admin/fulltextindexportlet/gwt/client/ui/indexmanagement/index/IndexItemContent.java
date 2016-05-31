package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.indexmanagement.index;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ItemContent;

/** An ItemContent implementation used to hold information about an IndexItem */
public class IndexItemContent implements ItemContent {
    /** The collectionID of the collection this IndexItem belongs to */
	//TODO it can be null
    private String collectionID;

    /** The IndexID of the Index this IndexItem describes */
    private String indexID;

    /**
     * Constructor
     * 
     * @param collectionID
     *            -The collectionID of the collection to which the IndexItem
     *            should belong
     * @param indexID -
     *            The IndexID of the Index this IndexItem describes
     */
    public IndexItemContent(String collectionID, String indexID) {
        this.indexID = indexID;
        this.collectionID = collectionID;
    }

    /**
     * {@inheritDoc}
     */
    public String getID() {
        return indexID;
    }

    /**
     * A getter method for the CollectionID
     * 
     * @return - the collectionID of the collection to which the IndexItem
     *         belongs
     */
    public String getCollectionID() {
        return collectionID;
    }
}
