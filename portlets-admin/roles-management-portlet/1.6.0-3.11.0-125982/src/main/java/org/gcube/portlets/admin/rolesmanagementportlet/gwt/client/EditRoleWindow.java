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

public class EditRoleWindow extends Window {

protected Window getWindow() {return this;}
	
	public EditRoleWindow(final String initialRoleName, String initialRoleDesc, boolean isSystem) {
		
		final FormPanel formPanel = new FormPanel(Position.LEFT);
		formPanel.setSize(600,300);
		formPanel.setAutoHeight(true);
		formPanel.setLabelWidth(75);
		formPanel.setPaddings(5);
		
		final TextField roleName = new TextField("Role's Name", "rolename", 230); 
	
		roleName.setAllowBlank(false);
		roleName.setValue(initialRoleName);
		// The name of system roles cannot be updated
		if (isSystem)
			roleName.setDisabled(true);
		formPanel.add(roleName);
		
		final TextField roleDesc = new TextField("Role's Description", "roledesc", 500);  
		roleDesc.setAllowBlank(true);
		roleDesc.setValue(initialRoleDesc);
		formPanel.add(roleDesc);

		final Button editBtn= new Button("Update", new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				MessageBox.confirm("Confirm Changes", "Are you sure you want to edit the selected role?", new MessageBox.ConfirmCallback() {  
					public void execute(String btnID) {
						if(btnID.equalsIgnoreCase("yes")){
							if (roleName.getText().trim().length() <= 0)
								MessageBox.alert("Role's name cannot be blank. Please type a name.");
							else if (roleName.getText().trim().contains(" ")) {
								MessageBox.alert("Role's name cannot contain blank characters. Please provide a different name");
								roleName.setValue(initialRoleName);
							}
							else {
								AsyncCallback<Boolean> editRoleCallback = new AsyncCallback<Boolean>() {

									public void onFailure(Throwable caught) {
										RolesManagement.displayErrorWindow("Failed to update the role. Please try again", caught);
									}

									public void onSuccess(Boolean result) {
										if (result.booleanValue() == true) {
											// add this new role to the available roles grid
											MessageBox.alert("Role has been updated succesfully");
											// TODO the new values should be edited to the existing grid.........
											AvailableRolesGrid.updateSelectedRecordOfGrid(roleName.getText(), roleDesc.getText());
											getWindow().close();
										}
										else {
											MessageBox.alert("Failed to update the role. Please try again");
											getWindow().close();
										}
									}
								};RolesManagement.rolesService.updateRole(initialRoleName, roleName.getText(), roleDesc.getText(), editRoleCallback);
							}
						}
						else 
							getWindow().close();
					}});
			}
		});

		formPanel.addButton(editBtn);
		
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
		this.setTitle("Edit " + initialRoleName + " role");
	}
}
