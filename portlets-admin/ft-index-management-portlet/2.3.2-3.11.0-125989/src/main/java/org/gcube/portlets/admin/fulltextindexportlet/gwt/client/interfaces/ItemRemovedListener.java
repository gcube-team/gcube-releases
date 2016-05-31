package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces;

/**
 * An interface for objects who need information about the removal of Index
 * related Items
 */
public interface ItemRemovedListener {

    /**
     * Handles instances where an index related item has been removed
     * 
     * @param content -
     *            information about the item which has been removed
     */
    public void itemRemoved(String itemID);
}
