package org.gcube.portlets.admin.wfdocviewer.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.admin.wfdocviewer.shared.RoleStep;
import org.gcube.portlets.admin.wfdocviewer.shared.UserBean;
import org.gcube.portlets.admin.wfdocviewer.shared.WfDocumentBean;
import org.gcube.portlets.admin.wfdocviewer.shared.WfTemplateBean;
import org.gcube.portlets.widgets.lighttree.client.load.WorkspaceLightTreeLoadPopup;

import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;

public interface Display {

	Widget asWidget();

	//display methods
	void maskCenterPanel(String message, boolean mask);
	void maskWestPanel(String message, boolean mask);
	void showAddnewWfDocPanel();
	void updateSize();
	void setData(List<WfDocumentBean> data);
	void showInstanciateNewWorkflowPanel(String reportid, String reportName, ArrayList<WfTemplateBean> templates);
	void showWfTemplateToInstanciate(WfTemplate template, ClickHandler clickHandler);
	void addRoleToPermissionTable(ArrayList<WfRole> roles);
	void showAssignRolesToUsersPanel(ArrayList<WfRole> roles, ArrayList<UserBean> users);
	void showWfReportDetails(WfTemplate template, String status);
		
	//the permission table column
	Column<RoleStep, Boolean> getViewCheckColumn();
	Column<RoleStep, Boolean> getUpdateCheckColumn();
	Column<RoleStep, Boolean> getDeleteCheckColumn();
	Column<RoleStep, Boolean> getEditPermissionCheckColumn();
	Column<RoleStep, Boolean> getAddCommentsCheckColumn();
	Column<RoleStep, Boolean> getUpdateCommentsCheckColumn();
	Column<RoleStep, Boolean> getDeleteCommentsCheckColumn();
	
	
	//buttons, combos and trees
	Button getDetailsButton();
	Button getAddnewButton();
	Button getActionsLogButton();
	Button getNextButton();
	Button getDeleteButton();
	Button getCreateNewWfReportButton();
	HasClickHandlers getAddRoleButton();
	ComboBox<WfTemplateBean> getSelectWfTemplateCombo();
	GridSelectionModel<WfDocumentBean> getGridSelectionModel();
	WorkspaceLightTreeLoadPopup getWSTreepopup();
	HashMap<String, ListView<UserBean>> getUsersAndRoles();

	
}
