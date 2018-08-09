package org.gcube.portlets.admin.vredeployment.client.view;

import java.util.List;

import org.gcube.portlets.admin.vredeployment.shared.VREDefinitionBean;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public interface Display {
	void maskCenterPanel(String message, boolean mask);
	void setData(List<VREDefinitionBean> data);
	Widget asWidget();
	void updateSize();
	void enableActionButtons(VREDefinitionBean selectedItem);
	void showDetailsDialog(String html2Show);
	void setGridContextMenu(String vreStatus) ;
	
	Button getViewButton();
	Button getEditButton();
	Button getViewReportButton();
	Button getRemoveButton();
	Button getApproveButton();
	Button getRefreshButton();
	Button getViewTextualReportButton();
	Button getPostPoneButton();
	Button getUndeployButton();
	
	
	MenuItem getViewMenu();
	MenuItem getEditMenu();
	MenuItem getViewReportMenu();
	MenuItem getRemoveMenu();
	MenuItem getApproveMenu();
	
	GridSelectionModel<VREDefinitionBean> getGridSelectionModel();
}
