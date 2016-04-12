package org.gcube.portlets.admin.wfroleseditor.client.view;

import org.gcube.portlets.admin.wfroleseditor.client.presenter.EditWfRolePresenter;
import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditWfRoleView extends Composite implements EditWfRolePresenter.Display {
	private GCubePanel framePanel;
	private final TextBox roleName;
	private final TextArea description;
	private final FlexTable detailsTable;
	private final Button saveButton;
	private final Button cancelButton;
	
	public EditWfRoleView() {
		framePanel = new GCubePanel("Edit Role","http://d4science.eu");
		initWidget(framePanel);

		VerticalPanel contentDetailsPanel = new VerticalPanel();
		contentDetailsPanel.setSpacing(5);
		contentDetailsPanel.setWidth("100%");

		// Create the roles list
		//
		detailsTable = new FlexTable();
		detailsTable.setCellSpacing(5);
		detailsTable.setCellPadding(5);
		detailsTable.setWidth("100%");
		detailsTable.addStyleName("roles-ListContainer");
		detailsTable.getColumnFormatter().addStyleName(1, "add-role-input");
		roleName = new TextBox();
		roleName.setWidth("100%");
		description = new TextArea();
		description.setSize("100%", "150px");
		description.setStyleName("add-role-input");
		initDetailsTable();
		contentDetailsPanel.add(detailsTable);

		HorizontalPanel menuPanel = new HorizontalPanel();
		saveButton = new Button("Save");
		cancelButton = new Button("Cancel");
		menuPanel.add(saveButton);
		menuPanel.add(new HTML("&nbsp;", true));
		menuPanel.add(cancelButton);
		menuPanel.setStyleName("buttonsPanel");
		contentDetailsPanel.add(menuPanel);
		framePanel.add(contentDetailsPanel);

		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				updateSize();
			}
		});
		updateSize();
	}

	private void initDetailsTable() {
		detailsTable.setWidget(0, 0, new HTML("<nobr>Role Name: *&nbsp;</nobr>", true));
		detailsTable.setWidget(0, 1, roleName);
		detailsTable.setWidget(1, 0, new HTML("<nobr>Description: *&nbsp;</nobr>", true));
		detailsTable.setWidget(1, 1, description);
		roleName.setFocus(true);
	}

	public HasValue<String> getDescription() {
		return description;
	}

	public HasValue<String> getRoleName() {
		return roleName;
	}

	public HasClickHandlers getSaveButton() {
		return saveButton;
	}

	public HasClickHandlers getCancelButton() {
		return cancelButton;
	}
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void updateSize() {
		framePanel.setWidth("100%");		
	}
}
