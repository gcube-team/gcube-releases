package org.gcube.portlets.admin.rolesmanagementportlet.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;

public class CreateRoleWindow extends Window {

	protected Window getWindow() {return this;}

	public CreateRoleWindow() {

		final FormPanel formPanel = new FormPanel(Position.LEFT);
		formPanel.setSize(600,300);
		formPanel.setAutoHeight(true);
		formPanel.setLabelWidth(75);
		formPanel.setPaddings(5);

		final TextField roleName = new TextField("Role's Name", "rolename", 230);  
		roleName.setAllowBlank(false);  
		formPanel.add(roleName);

		final TextField roleDesc = new TextField("Role's Description", "roledesc", 500);  
		roleName.setAllowBlank(true);  
		formPanel.add(roleDesc);

		final Button createBtn= new Button("Create", new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				if (roleName.getText().trim().length() <= 0)
					MessageBox.alert("Role's name cannot be blank. Please type a name.");
				else if (roleName.getText().trim().contains(" ")) {
					MessageBox.alert("Role's name cannot contain blank characters. Please provide a different name");
				}
				else {
					AsyncCallback<Boolean> createRoleCallback = new AsyncCallback<Boolean>() {

						public void onFailure(Throwable caught) {
							RolesManagement.displayErrorWindow("Failed to create the new role. Please try again", caught);
							getWindow().close();
						}

						public void onSuccess(Boolean result) {
							if (result.booleanValue() == true) {
								// add this new role to the available roles grid
								MessageBox.alert("New role has been created succesfully");
								// TODO the new role should be added to the existing grid.........
								AvailableRolesGrid.addRecordToGrid(roleName.getText(), roleDesc.getText());
								getWindow().close();
							}
							else {
								MessageBox.alert("Failed to create the new role. Please try again");
								getWindow().close();
							}
						}
					};RolesManagement.rolesService.createNewRole(roleName.getText().trim(), roleDesc.getText(), createRoleCallback);
				}
			}
		});

		formPanel.addButton(createBtn);

		final Button cancelBtn= new Button("Cancel", new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				getWindow().close();
			}
		});

		formPanel.addButton(cancelBtn);

		this.add(formPanel);
		this.setDraggable(true);
		this.setResizable(false);
		this.setAutoHeight(true);
		this.setTitle("Create new Role");
	}
}
