package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.indexmanagement.index;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementService;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementServiceAsync;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.MgmtPropertiesBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/** The panel used to show the Resource Properties of an Index */
public class InfoPanel extends Composite {

    /** An GWT RPC interface to the ManagementService */
    private ManagementServiceAsync mgmtService = null;

    /**
     * An outer panel holding all the widgets needed to show the resource
     * properties
     */
    private VerticalPanel propsPanel = new VerticalPanel();


    /** A label showing the Created Time of an Index. */
    private Label createdLabel = new Label();

    /** A label showing the Modified Time of an Index. */
    private Label modifiedLabel = new Label();

    /** A label showing the Status property of an Index. */
    private Label clusterIDLabel = new Label();
    
    /** A label showing the name of the host where the index resides */
    private Label hostNameLabel = new Label();
    
    
    /**
     * A bean holding all the ResourceProperties of the currently selected
     * Index.
     */
    private MgmtPropertiesBean props;

    /** A constructor, initializing the panel */
    public InfoPanel() {
        mgmtService = (ManagementServiceAsync) GWT.create(ManagementService.class);

        createdLabel.setWordWrap(false);
        modifiedLabel.setWordWrap(false);
        clusterIDLabel.setWordWrap(false);
        hostNameLabel.setWordWrap(false);
        
        propsPanel.addStyleName("diligent-index-info");
        
        Grid grid = new Grid(4, 2);

        grid.setWidget(0, 0, new Label("Created: "));
        grid.setWidget(0, 1, createdLabel);

        grid.setWidget(1, 0, new Label("Modified: "));
        grid.setWidget(1, 1, modifiedLabel);

        grid.setWidget(2, 0, new Label("Cluster: "));
        grid.setWidget(2, 1, clusterIDLabel);

        grid.setWidget(3, 0, new Label("Host: "));
        grid.setWidget(3, 1, hostNameLabel);

        propsPanel.add(grid);
        
        ServiceDefTarget endpoint = (ServiceDefTarget) mgmtService;
        String moduleRelativeURL = GWT.getModuleBaseURL() + "/MgmtService";
        endpoint.setServiceEntryPoint(moduleRelativeURL);

        initWidget(propsPanel);
    }

    /**
     * Updates the GUI components when a new Index has been selected, asking the
     * ManagementService for the Resource Properties of the selected Index
     * 
     * @param indexID -
     *            the ID of the selected Index.
     */
    public void update(String indexID) {

        /**
         * The callback which will handle the result of the ManagementService
         * call in accordance with the GWT RPC framework
         */
        AsyncCallback<MgmtPropertiesBean> callback = new AsyncCallback<MgmtPropertiesBean>() {

            /**
             * {@inheritDoc}
             */
            public void onSuccess(MgmtPropertiesBean result) {
            	props = result;
                createdLabel.setText(props.getCreated());
                modifiedLabel.setText(props.getModified());
                clusterIDLabel.setText(props.getClusterID());
                hostNameLabel.setText(props.getHost());
            }

            /**
             * {@inheritDoc}
             */
            public void onFailure(Throwable caught) {
                setFailed();
            }
        };

        mgmtService.getResourceProperties(indexID, callback);
    }

    /** Sets the GUI to "waiting" mode */
    public void setWaiting() {
        createdLabel.setText("Please wait...");
        modifiedLabel.setText("Please wait...");
        clusterIDLabel.setText("Please wait...");
        hostNameLabel.setText("Please wait...");
    }

    /** Sets the GUI to "failed" mode */
    public void setFailed() {
        createdLabel.setText("Unable to retrieve data");
        modifiedLabel.setText("Unable to retrieve data");
        clusterIDLabel.setText("Unable to retrieve data");
        hostNameLabel.setText("Unable to retrieve data");
    }

    /** Sets the GUI to "empty" mode */
    public void setEmpty() {
        createdLabel.setText("");
        modifiedLabel.setText("");
        clusterIDLabel.setText("");
    }
}
