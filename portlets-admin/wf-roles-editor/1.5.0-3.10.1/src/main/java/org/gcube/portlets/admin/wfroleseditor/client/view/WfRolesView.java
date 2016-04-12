package org.gcube.portlets.admin.wfroleseditor.client.view;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.wfroleseditor.client.presenter.WfRolesPresenter;
import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class WfRolesView extends Composite implements WfRolesPresenter.Display {
	private final Button addButton;
	private final Button deleteButton;
	private FlexTable rolesTable;
	private final FlexTable contentTable;
	private GCubePanel roleViewPanel;
	private HTML loading = new HTML("Loading roles from system...  please wait");

	public WfRolesView() {
		roleViewPanel = new GCubePanel("WF Roles Editor", "https://gcube.wiki.gcube-system.org/gcube/index.php/My_Document_Workflows");
		initWidget(roleViewPanel);

		contentTable = new FlexTable();
		contentTable.setWidth("100%");
		contentTable.getCellFormatter().addStyleName(0, 0, "roles-ListContainer");
		contentTable.getCellFormatter().setWidth(0, 0, "100%");

		// Create the menu
		//
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setBorderWidth(0);
		hPanel.setSpacing(0);
		hPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		addButton = new Button("Add");
		hPanel.add(addButton);
		hPanel.add(new HTML("&nbsp;", true));
		deleteButton = new Button("Delete");
		hPanel.add(deleteButton);
		hPanel.setStyleName("buttonsPanel");
		contentTable.getCellFormatter().addStyleName(0, 0, "roles-ListMenu");
		contentTable.setWidget(0, 0, hPanel);

		// Create the roles list
		//
		rolesTable = new FlexTable();
		rolesTable.setCellSpacing(0);
		rolesTable.setCellPadding(0);
		rolesTable.setWidth("100%");
		rolesTable.addStyleName("roles-ListContents");
		rolesTable.getColumnFormatter().setWidth(0, "15px");
		contentTable.setWidget(1, 0, rolesTable);

		roleViewPanel.add(contentTable);

		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				updateSize();
			}
		});
		updateSize();
	}

	public void showLoading(boolean show) {
		if (show) {
			roleViewPanel.add(loading);
		}
		else {
			roleViewPanel.remove(loading);
		}
	}
	
	public HasClickHandlers getAddButton() {
		return addButton;
	}

	public HasClickHandlers getDeleteButton() {
		return deleteButton;
	}

	public HasClickHandlers getList() {
		return rolesTable;
	}

	public void setData(List<String> data) {
		rolesTable.removeAllRows();

		for (int i = 0; i < data.size(); ++i) {
			rolesTable.setWidget(i, 0, new CheckBox());
			Label toAdd = new Label(data.get(i));
			toAdd.setStyleName("selectable");
			rolesTable.setWidget(i, 1, toAdd);
		}
	}

	public int getClickedRow(ClickEvent event) {
		int selectedRow = -1;
		HTMLTable.Cell cell = rolesTable.getCellForEvent(event);

		if (cell != null) {
			// Suppress clicks if the user is actually selecting the 
			//  check box
			//
			if (cell.getCellIndex() > 0) {
				selectedRow = cell.getRowIndex();
			}
		}

		return selectedRow;
	}

	public List<Integer> getSelectedRows() {
		List<Integer> selectedRows = new ArrayList<Integer>();

		for (int i = 0; i < rolesTable.getRowCount(); ++i) {
			CheckBox checkBox = (CheckBox)rolesTable.getWidget(i, 0);
			if (checkBox.getValue()) {
				selectedRows.add(i);
			}
		}

		return selectedRows;
	}

	public Widget asWidget() {
		return this;
	}
	@Override
	public void updateSize() {
		roleViewPanel.setWidth("100%");		
	}
}
