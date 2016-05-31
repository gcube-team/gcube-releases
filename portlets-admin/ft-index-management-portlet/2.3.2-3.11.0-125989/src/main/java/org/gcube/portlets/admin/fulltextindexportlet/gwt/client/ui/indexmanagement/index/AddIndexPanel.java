package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.indexmanagement.index;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ItemAddedListener;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementService;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementServiceAsync;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.RunningInstanceBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel with the functionality and GUI components needed to add an Index to
 * the current collection, or to a new collection, fake, collection if non is
 * selected
 */
public class AddIndexPanel extends Composite implements ClickListener {

    /** An GWT RPC interface to the ManagementService */
    private ManagementServiceAsync mgmtService = null;

    /** The outer panel holding all the widgets of the AddIndexPanel */
    private VerticalPanel outerPanel;

    /** A text box allowing the user to add the IndexID of the Index to be added */
    private TextBox indexIDBox;

    /**
     * A text box allowing the user to add the CollectionID of the Index to be
     * added. (if no collection has already been selected)
     */
 //   private TextBox collectionIDBox;

    /**
     * A label containing the CollectionID of the Index to be added. (if a
     * collection has been selected)
     */
 //   private Label collectionIDLabel;

    /**
     * A list box allowing the user to select the IndexTypeID of the Index to be
     * added
     */
 //   private ListBox indexTypeIDList;

    /**
     * A text box allowing the user to add the ContentType of the Index to be
     * added
     */
  //  private TextBox contentTypeBox;

    /**
     * A listbox allowing the user to select the Running Instance to use for creating
     * the new index
     */
    private ListBox runningInstanceList;
    
    /** A button for the user to push in order to create the Index */
    private Button createButton;

    /** A Label informing the user that the Index was successfully added */
    private Label successLabel;

    /** A Label informing the user that there was an error adding Index */
    private Label errorLabel;

    /** A List of listeners needing information of added Indices */
    private ArrayList listeners = new ArrayList();

    /** A constructor, initializing the panel */
    public AddIndexPanel() {
        outerPanel = new VerticalPanel();
        createButton = new Button("Create");
        createButton.addClickListener(this);
        successLabel = new Label();
        errorLabel = new Label();
        indexIDBox = new TextBox();
        indexIDBox.setVisibleLength(30);
  //      collectionIDBox = new TextBox();
  //      collectionIDBox.setVisibleLength(30);
  //      collectionIDLabel = new Label();
//        indexTypeIDList = new ListBox();
//        indexTypeIDList.setVisibleItemCount(0);
//        contentTypeBox = new TextBox();
//        contentTypeBox.setText("MetaData");
//        contentTypeBox.setVisibleLength(30);
        runningInstanceList = new ListBox();
        runningInstanceList.setVisibleItemCount(0);
        
        mgmtService = (ManagementServiceAsync) GWT.create(ManagementService.class);

        successLabel.setVisible(false);
        successLabel.addStyleName("diligent-success");
        errorLabel.setVisible(false);
        errorLabel.addStyleName("diligent-error");

        createButton.setEnabled(true);
        errorLabel.setVisible(false);

        
        Grid grid = new Grid(5, 2);

        grid.setWidget(0, 0, new Label("ClusterID (optional): "));
        grid.setWidget(0, 1, indexIDBox);

 //       HorizontalPanel hp = new HorizontalPanel();
  //      hp.add(collectionIDBox);
  //      hp.add(collectionIDLabel);
 //       grid.setWidget(1, 0, new Label("Collection ID: "));
 //       grid.setWidget(1, 1, hp);

   //     grid.setWidget(1, 0, new Label("IndexType ID: "));
    //    grid.setWidget(1, 1, indexTypeIDList);

     //   grid.setWidget(2, 0, new Label("ContentType: "));
      //  grid.setWidget(2, 1, contentTypeBox);
        
        Label lbl = new Label("Service Instance to use: ");
        lbl.setWordWrap(false);
        grid.setWidget(1, 0, lbl);
        grid.setWidget(1, 1, runningInstanceList);
        
        outerPanel.add(grid);
        outerPanel.add(createButton);
        outerPanel.add(successLabel);
        outerPanel.add(errorLabel);

        ServiceDefTarget endpoint = (ServiceDefTarget) mgmtService;
        String moduleRelativeURL = GWT.getModuleBaseURL() + "/MgmtService";
        endpoint.setServiceEntryPoint(moduleRelativeURL);

        /**
         * The callback which will handle the result of the ManagementService
         * call for retrieving the Index management running instances,
         * in accordance with the GWT RPC framework
         */
        AsyncCallback<List<RunningInstanceBean>> callback = new AsyncCallback<List<RunningInstanceBean>>() {
            /**
             * {@inheritDoc}
             */
            public void onSuccess(List<RunningInstanceBean> result) {
                createButton.setEnabled(true);
                errorLabel.setVisible(false);
                for (RunningInstanceBean ribean : result) {
                    runningInstanceList.addItem(ribean.getRunningInstanceEPR());
                }
            }

            /**
             * {@inheritDoc}
             */
            public void onFailure(Throwable caught) {
                errorLabel.setText("Error: " + caught.getMessage());
                successLabel.setVisible(false);
                errorLabel.setVisible(true);
                createButton.setEnabled(false);
            }
        };
        mgmtService.getRunningInstances(callback);
        
        /**
         * The callback which will handle the result of the ManagementService
         * call for retrieving the available index type IDs,
         * in accordance with the GWT RPC framework
//         */
//        AsyncCallback<List<FullTextIndexTypeBean>> callback2 = new AsyncCallback<List<FullTextIndexTypeBean>>() {
//            /**
//             * {@inheritDoc}
//             */
//            public void onSuccess(List<FullTextIndexTypeBean> result) {
//                createButton.setEnabled(true);
//                errorLabel.setVisible(false);
//                for (FullTextIndexTypeBean itbean : result) {
//                    indexTypeIDList.addItem(itbean.getIndexTypeID());
//                }
//            }
//
//            /**
//             * {@inheritDoc}
//             */
//            public void onFailure(Throwable caught) {
//                errorLabel.setText("Error: " + caught.getMessage());
//                successLabel.setVisible(false);
//                errorLabel.setVisible(true);
//                createButton.setEnabled(false);
//            }
//        };
//        mgmtService.getAvailableIndexTypeIDs(callback2);

        
        initWidget(outerPanel);
    }

    /**
     * A method which invokes the ManagementService asynchronously in order to
     * create a new Index using the current values of the textBoxes.
     * 
     */
    private void create() {
        /** The collectionID to be used by the callback inner class */
    //    final String collectionID;
    //    if (collectionIDBox.isVisible()) {
    //        collectionID = collectionIDBox.getText();
     //   } else {
      //      collectionID = collectionIDLabel.getText();
       // }
        /** The indexID to be used by the callback inner class */
        final String indexID = indexIDBox.getText();
        /** The indexTypeID to be used by the callback inner class */
 //       final String indexTypeID = indexTypeIDList.getItemText(indexTypeIDList.getSelectedIndex());
        /** The contentType to be used by the callback inner class */
 //       final String contentType = contentTypeBox.getText();
        /** The RI EPR to be used */
        final String RIEPR = runningInstanceList.getItemText(runningInstanceList.getSelectedIndex());

        /**
         * The callback which will handle the result of the ManagementService
         * call in accordance with the GWT RPC framework
         */
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            /**
             * {@inheritDoc}
             */
            public void onSuccess(String result) {
               // IndexItemContent content = new IndexItemContent(collectionID, result);
            	 IndexItemContent content = new IndexItemContent(null, result);
                successLabel.setText("Index was successfully created");
                successLabel.setVisible(true);
                createButton.setEnabled(true);
                errorLabel.setVisible(false);
                for (int i = 0; i < listeners.size(); i++) {
                    ((ItemAddedListener) listeners.get(i)).itemAdded(content);
                }
            }

            /**
             * {@inheritDoc}
             */
            public void onFailure(Throwable caught) {
                errorLabel.setText("Error: " + caught.getMessage());
                successLabel.setVisible(false);
                errorLabel.setVisible(true);
                createButton.setEnabled(true);
            }
        };

        createButton.setEnabled(false);
       // mgmtService.createIndex(indexID, collectionID, indexTypeID, contentType, RIEPR, callback);
        mgmtService.createIndex(indexID, null, RIEPR, callback);
    }

    /**
     * Updates the panels when a new collection has been selected, or a "New
     * Fake Collection" has been requested.
     * 
     * @param collectionID -
     *            the ID of the selected collection. NULL if none is selected.
     */
    public void update(String collectionID) {
        indexIDBox.setText("");
    //    collectionIDBox.setText("");
     //   contentTypeBox.setText("gDocContent");
        successLabel.setVisible(false);
        errorLabel.setVisible(false);
        createButton.setEnabled(true);
       /* if (collectionID == null) {
            collectionIDLabel.setVisible(false);
            collectionIDBox.setVisible(true);
        } else {
            collectionIDBox.setVisible(false);
            collectionIDLabel.setText(collectionID);
            collectionIDLabel.setVisible(true);
        }*/
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(Widget sender) {
        create();
    }

    /**
     * A method to add a listener needing information of added Indices to the
     * list of listeners.
     * 
     * @param listener -
     *            the listener to be added
     */
    public void addItemAddedListener(ItemAddedListener listener) {
        listeners.add(listener);
    }
}
