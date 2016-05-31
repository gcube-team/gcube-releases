package org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.indexmanagement.index;

import java.util.List;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementService;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ManagementServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel with the functionality and GUI components needed to query an
 * IndexLookup
 */
public class QueryPanel extends Composite implements ClickListener {

    /** An GWT RPC interface to the LookupService */
    private ManagementServiceAsync mgmtService = null;

    /** the indexID of the IndexLookup to query */
    private String indexID = null;

    /** The outer panel holding all the widgets of the QueryPanel */
    private VerticalPanel outerPanel;

    /** The panel used to display the results of a successful query */
    private VerticalPanel resultPanel;

    /** A text box allowing the user to specify a query */
    private TextBox queryBox;

    /** A button for the user to push in order to query the IndexLookup */
    private Button queryButton;

    /**
     * A Label informing the user that there was an error querying the
     * IndexLookup
     */
    private Label errorLabel;

    /** A constructor, initializing the panel */
    public QueryPanel() {
        outerPanel = new VerticalPanel();
        queryBox = new TextBox();
        queryButton = new Button("Query");
        queryButton.addClickListener(this);
        errorLabel = new Label();
        queryBox.setVisibleLength(30);

        mgmtService = (ManagementServiceAsync) GWT.create(ManagementService.class);

        errorLabel.setVisible(false);
        errorLabel.addStyleName("diligent-error");

        resultPanel = new VerticalPanel();
        resultPanel.addStyleName("diligent-index-info");

        Grid grid = new Grid(1, 3);
        //TODO changed to Query and to QueryTerm
        Label header = new Label("Enter Query: ");
        header.addStyleName("diligent-index-info");
        header.setWordWrap(false);
        grid.setWidget(0, 0, header);
        grid.setWidget(0, 1, queryBox);
        grid.setWidget(0, 2, queryButton);
        
        outerPanel.add(grid);
        outerPanel.add(resultPanel);
        outerPanel.add(errorLabel);

        ServiceDefTarget endpoint = (ServiceDefTarget) mgmtService;
        String moduleRelativeURL = GWT.getModuleBaseURL() + "/MgmtService";
        endpoint.setServiceEntryPoint(moduleRelativeURL);

        initWidget(outerPanel);
    }

    /**
     * A method which invokes the LookupService asynchronously in order to query
     * the current IndexLookup with the queryString currently specified in the
     * queryBox.
     */
    private void query(String query) {
        /**
         * The callback which will handle the result of the LookupService call
         * in accordance with the GWT RPC framework
         */
        AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
            /**
             * {@inheritDoc}
             */
            public void onSuccess(List<String> result) {
                queryButton.setEnabled(true);
                try {
                    resultPanel.clear();
                    errorLabel.setVisible(false);
                    if (result.size() > 0) {
                    	FlexTable resultTable = new FlexTable();
                    	resultPanel.add(resultTable);
                        for (int i = 0; i < result.size(); i++) {
                            resultTable.setWidget(i, 0, new Label(result.get(i)));
                        }
                    } else {
                        resultPanel.add(new Label("No documents found"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * {@inheritDoc}
             */
            public void onFailure(Throwable caught) {
                errorLabel.setText("Error: " + caught.getMessage());
                resultPanel.clear();
                errorLabel.setVisible(true);
                queryButton.setEnabled(true);
            }
        };
        queryButton.setEnabled(false);
        mgmtService.query(query, indexID, callback);
    }

    /**
     * Updates internal state and the GUI components when a new IndexLookup has
     * been selected.
     * 
     * @param indexID -
     *            the Index ID of the selected IndexLookup.
     * @param connectionID -
     *            the Connection ID of the selected IndexLookup.
     */
    public void setCurrentID(String indexID) throws Exception {
        if (this.indexID == null
                || !(this.indexID.equals(indexID))) {
            this.indexID = indexID;
            queryBox.setText("");
            resultPanel.clear();
            errorLabel.setVisible(false);
            queryButton.setEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(Widget sender) {
        query(queryBox.getText());
    }
}
