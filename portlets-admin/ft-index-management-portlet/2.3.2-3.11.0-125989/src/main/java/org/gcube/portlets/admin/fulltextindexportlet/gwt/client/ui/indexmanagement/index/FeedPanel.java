package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.indexmanagement.index;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementService;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel with the functionality and GUI components needed to start the feeding
 * of an Index
 */
public class FeedPanel extends Composite implements ClickListener {

    /** An GWT RPC interface to the UpdaterService */
	private ManagementServiceAsync mgmtService = null;

    /** the indexID of the Index to feed */
    private String indexID = null;
    
    private String collectionID = null;

    /** The outer panel holding all the widgets of the FeedPanel */
    private VerticalPanel outerPanel;

    /**
     * A text box allowing the user to specify the rsLocator of the ResultSet of
     * ROWSETs the Index should be fed from
     */
    private TextBox rsLocatorBox;

    /** A button for the user to push in order to feed the Index */
    private Button updateButton;

    /**
     * A Label informing the user that the feeding process was successfully
     * started
     */
    private Label successLabel;

    /**
     * A Label informing the user that there was an error starting the feeding
     * process
     */
    private Label errorLabel;

    /** A constructor, initializing the panel */
    public FeedPanel() {
        outerPanel = new VerticalPanel();
        rsLocatorBox = new TextBox();
        updateButton = new Button("Update");
        updateButton.addClickListener(this);
        successLabel = new Label();
        errorLabel = new Label();
        rsLocatorBox.setVisibleLength(30);

        mgmtService = (ManagementServiceAsync) GWT.create(ManagementService.class);

        successLabel.setVisible(false);
        successLabel.addStyleName("diligent-success");
        errorLabel.setVisible(false);
        errorLabel.addStyleName("diligent-error");

        Grid grid = new Grid(1, 3);
        grid.setWidget(0, 0, new Label("RSLocator: "));
        grid.setWidget(0, 1, rsLocatorBox);
        grid.setWidget(0, 2, updateButton);
        
        outerPanel.add(grid);
        outerPanel.add(successLabel);
        outerPanel.add(errorLabel);

        ServiceDefTarget endpoint = (ServiceDefTarget) mgmtService;
        String moduleRelativeURL = GWT.getModuleBaseURL() + "/MgmtService";
        endpoint.setServiceEntryPoint(moduleRelativeURL);

        initWidget(outerPanel);
    }

    /**
     * A method which invokes the UpdaterService asynchronously in order to feed
     * the current Index with the rsLocator currently specified in the
     * rsLocatorBox.
     */
    private void startUpdate(String indexID, String rsLocator) {
        /**
         * The callback which will handle the result of the UpdaterService call
         * in accordance with the GWT RPC framework
         */
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            /**
             * {@inheritDoc}
             */
            public void onSuccess(String result) {
                successLabel.setText("Feeding was successfully started");
                successLabel.setVisible(true);
                errorLabel.setVisible(false);
                updateButton.setEnabled(true);
            }

            /**
             * {@inheritDoc}
             */
            public void onFailure(Throwable caught) {
                errorLabel.setText("Error: " + caught.getMessage());
                successLabel.setVisible(false);
                errorLabel.setVisible(true);
                updateButton.setEnabled(true);
            }
        };
        updateButton.setEnabled(false);
        mgmtService.updateIndex(indexID, collectionID, rsLocator, callback);
    }

    /**
     * Updates internal state and the GUI components when a new Index has been
     * selected.
     * 
     * @param indexID -
     *            the ID of the selected Index.
     */
    public void setCurrentIndexID(String indexID, String collectionID) {
        if (!indexID.equals(this.indexID)) {
            this.indexID = indexID;
            this.collectionID = collectionID;
            rsLocatorBox.setText("");
            successLabel.setVisible(false);
            errorLabel.setVisible(false);
            updateButton.setEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(Widget sender) {
        startUpdate(indexID, rsLocatorBox.getText().trim());
    }
}
