package org.gcube.portlets.admin.manageusers.client.view;

import java.util.List;

import org.gcube.portlets.admin.manageusers.shared.PortalUserDTO;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public interface Display {
	void maskCenterPanel(String message, boolean mask);
	void setData(List<PortalUserDTO> data);
	Widget asWidget();
	void updateSize();
	void enableActionButtons(PortalUserDTO selectedItem);

	void setGridContextMenu() ;
	Button getApproveButton();
	Button getInviteButton();
	Button getRefreshButton();

	MenuItem getApproveMenu();
	
	GridSelectionModel<PortalUserDTO> getGridSelectionModel();
	
	void displayInviteUsersPanel();
}
