package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.indexmanagement.index;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ItemRemovedListener;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;

/** A panel to hold all the panels used to administer Indices */
public class IndexDetail extends Composite {

    /**
     * An outer TabPanel allowing the user to choose between the other Index
     * administration panels
     */
    private TabPanel detailPanel = new TabPanel();

    /** The panel used to show the Resource Properties of an Index */
    private InfoPanel propPanel = new InfoPanel();

    /** The panel used to start the feeding of an Index */
    private FeedPanel feedPanel = new FeedPanel();

    /** The panel used to remove/destroy an Index */
    private RemovePanel removePanel = new RemovePanel();
    
    /** The panel used to query an IndexLookup */
    private QueryPanel queryPanel = new QueryPanel();

    /** A constructor, initializing the all the panels */
    public IndexDetail() {
        detailPanel.add(propPanel, "Info");
        detailPanel.add(feedPanel, "Feed");
        detailPanel.add(queryPanel, "Query");
        detailPanel.add(removePanel, "Remove");
        detailPanel.selectTab(0);
        initWidget(detailPanel);
        // setStyleName("diligent-index-Detail");
    }

    /**
     * Updates internal state and the GUI components of all the panels when a
     * new Index has been selected.
     * 
     * @param indexID -
     *            the ID of the selected Index.
     */
    public void updateDetail(String indexID, String collectionID) {
        try {
        	//Setting the indexID to the query panel
            propPanel.update(indexID);
            feedPanel.setCurrentIndexID(indexID, collectionID);
            removePanel.setCurrentIndexID(indexID, collectionID);
            queryPanel.setCurrentID(indexID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Sets the panels to "waiting" mode */
    public void setWaiting() {
        propPanel.setWaiting();
    }

    /** Sets the panels to "failed" mode */
    public void setFailed() {
        propPanel.setFailed();
    }

    /** Sets the panels to "empty" mode */
    public void setEmpty() {
        propPanel.setEmpty();
    }

    /**
     * A method to add a listener needing information of removed Indices to the
     * list of listeners of the panels capable of destroying indices.
     * 
     * @param listener -
     *            the listener to be added
     */
    public void addItemRemovedListener(ItemRemovedListener listener) {
        removePanel.addItemRemovedListener(listener);
    }
}
