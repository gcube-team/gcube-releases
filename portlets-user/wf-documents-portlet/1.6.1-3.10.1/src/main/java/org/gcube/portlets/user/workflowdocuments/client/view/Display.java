package org.gcube.portlets.user.workflowdocuments.client.view;

import java.util.List;

import org.gcube.portlets.user.workflowdocuments.shared.WorkflowDocument;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.google.gwt.user.client.ui.Widget;

public interface Display {
	void maskCenterPanel(String message, boolean mask);
	void setData(List<WorkflowDocument> data);
	Widget asWidget();
	void updateSize();
	
	Button getViewButton();
	Button getEditButton();
	Button getAddCommentsButton();
	Button getViewCommentsButton();
	Button getForwardButton();
	Button getRefreshButton();
	
	GridSelectionModel<WorkflowDocument> getGridSelectionModel();
	
	void doSelectRow(String rowId);
}
