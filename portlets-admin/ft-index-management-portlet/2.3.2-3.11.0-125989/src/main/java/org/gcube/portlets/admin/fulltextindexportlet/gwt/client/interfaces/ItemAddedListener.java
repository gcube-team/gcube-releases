package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces;

/**
 * An interface for objects who need information about the addition of index
 * related items
 */
public interface ItemAddedListener {

    /**
     * Handles instances where an index related item has been added
     * 
     * @param content -
     *            information about the item which has been added
     */
    public void itemAdded(ItemContent content);
}
