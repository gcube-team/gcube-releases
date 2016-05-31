package org.gcube.portlets.admin.usersmanagementportlet.gwt.client;

import java.util.ArrayList;

import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.UserInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.LoadMask;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.grid.GridPanel;

/**
 * This class extends the Window class and creates a window that contains all the users that are registered to portal
 *  but are not registered to the current VO/VRE
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class UnregisteredUsersWindow extends Window {
	private GridPanel referenceGrid;
	private UsersInfoGrid referenceWidget;
	
	private static LoadMask loadMask = new LoadMask("UsersDiv", "Updating, please wait...");
	
	protected Window getWindow(){return this;}

	public UnregisteredUsersWindow(UsersInfoGrid uiGrid, GridPanel existingUsersGrid, final ArrayList<UserInfo> unregisteredUsers) {
		this.referenceWidget = uiGrid;
		this.referenceGrid = existingUsersGrid;
		final FormPanel formPanel = new FormPanel(Position.LEFT);
		formPanel.setAutoWidth(true);
		formPanel.setAutoScroll(true);
		formPanel.setHeight(500);
		formPanel.setLabelWidth(1);

		for (UserInfo user : unregisteredUsers) {
			Checkbox cb = new Checkbox(user.getUsername());
			cb.setId(user.getUsername());
			cb.setBoxLabel(user.getUsername());
			formPanel.add(cb);
		}


		final Button saveBtn= new Button("Add Users", new ButtonListenerAdapter() {  
			public void onClick(Button button, EventObject e) {
				MessageBox.confirm("Confirm Changes", "Are you sure you want to add the selected users to the current VO/VRE?", new MessageBox.ConfirmCallback() {  
					public void execute(String btnID) {
						if(btnID.equalsIgnoreCase("yes")){
							//save the changes for the user....
							Field f[] = formPanel.getFields();
							final ArrayList<String> usersToBeAdded = new ArrayList<String>();
							// TODO:
							final ArrayList<UserInfo> newUserInfoUsers = new ArrayList<UserInfo>();
							for (int i=0; i<f.length; i++) {
								if (f[i] instanceof Checkbox) {
									// this user should be added to the VO. It is checked
									if (f[i].getValueAsString().toLowerCase().equals("true")) {
										usersToBeAdded.add(f[i].getId());
									}
								}
							}
							AsyncCallback<Boolean> addUsersToVOCallback = new AsyncCallback<Boolean>() {

								public void onFailure(Throwable caught) {
									loadMask.hide();
									UsersInfoGrid.displayErrorWindow("Failed to add the new users to the current VO/VRE. Please try again.", caught);
								}

								public void onSuccess(Boolean result) {
									loadMask.hide();
									// Update the grid with the new users
									// The new users will be added with no roles now
									for (String newUser : usersToBeAdded) {								
										for (UserInfo ui : unregisteredUsers) {
											if (ui.getUsername().equals(newUser)) {
												newUserInfoUsers.add(ui);
												break;
											}
										}										
									}
									referenceWidget.updateLocalRegisteredUsersInfo(newUserInfoUsers);
									referenceWidget.reloadStore(referenceGrid.getStore(), referenceWidget.getDataAsObject());
								}
							};UsersManagement.userService.addNewUsersToVO(usersToBeAdded, addUsersToVOCallback);
							loadMask.show();
							getWindow().close();
						}
						else
							getWindow().close();
					}});
			}  
		}); 
		
		
		final Button cancelBtn= new Button("Cancel", new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				getWindow().close();
			}
		});
		
		saveBtn.setFormBind(true);
		formPanel.addButton(saveBtn);
		formPanel.addButton(cancelBtn);
		formPanel.setMonitorValid(true);
		this.add(formPanel);
		this.setResizable(true);
		this.setAutoHeight(true);
		this.setTitle("Add New Users");
	}
}

