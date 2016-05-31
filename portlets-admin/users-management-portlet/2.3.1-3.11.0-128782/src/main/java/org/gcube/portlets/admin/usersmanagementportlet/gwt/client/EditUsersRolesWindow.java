package org.gcube.portlets.admin.usersmanagementportlet.gwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.UserStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.LoadMask;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.event.CheckboxListenerAdapter;
import com.gwtext.client.widgets.grid.GridPanel;

/**
 * This class extends the Window class and creates a window for a specific user and the available roles that
 * this user can have
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class EditUsersRolesWindow extends Window{
	private int  numberOfRoles;
	private GridPanel referenceGrid;
	private UsersInfoGrid referenceWidget;
	private String username;
	
	/**
	 * A hashmap that contains the roles that are actually updated.
	 */
	private HashMap<String,String> updatedRoles = new HashMap<String,String>();
	
	private static LoadMask loadMask = new LoadMask("UsersDiv", "Updating, please wait...");
	
	public EditUsersRolesWindow(String user, final String rolesNames[], final String rolesValues[], GridPanel grid, UsersInfoGrid uiGrid) {
		this.referenceGrid = grid;
		this.referenceWidget = uiGrid;
		final FormPanel formPanel = new FormPanel(Position.LEFT);
		formPanel.setSize(250,300);
		formPanel.setAutoHeight(true);
		formPanel.setLabelWidth(1);
		this.username = user;
		this.numberOfRoles = rolesNames.length;
		for (int i=0; i<numberOfRoles; i++) {
			Checkbox cb = new Checkbox(rolesNames[i]);
			cb.setId(rolesNames[i]);
			cb.setBoxLabel(rolesNames[i]);
			if (rolesValues[i].equals("YES")) {
				cb.setChecked(true);
				cb.setValue(true);
			}
			formPanel.add(cb);
			
			cb.addListener(new CheckboxListenerAdapter() {
				public void onCheck(Checkbox field, boolean checked) {
						String roleName = field.getId();
						String roleValue = field.getValueAsString();
						// If the role's state is different than the previous state then update the hashmap, else remove it from the hashmap
						// if it exists
						if (isRoleReallyUpdated(roleName, roleValue, rolesNames, rolesValues)) {
							updatedRoles.put(roleName, roleValue);
						}
						else {
							updatedRoles.remove(roleName);
						}
				}
			});
		}	
		
		final Button saveBtn= new Button("Save", new ButtonListenerAdapter() {  
			public void onClick(Button button, EventObject e) {
				MessageBox.confirm("Confirm Changes", "Are you sure you want to change user's roles?", new MessageBox.ConfirmCallback() {  
					public void execute(String btnID) {
						if(btnID.equalsIgnoreCase("yes")){
							if (updatedRoles == null || updatedRoles.size() <= 0) {
								referenceGrid.getSelectionModel().clearSelections();
								getWindow().close();
							}
							else {
								//save the changes for the user....
								Field f[] = formPanel.getFields();
								// This variable contains all the roles and is used to update the grid's record
								final String changedRolesAndValues[][] = new String[numberOfRoles][2];
								final ArrayList<String> newRoles = new ArrayList<String>();
								for (int i=0; i<f.length; i++) {
									if (f[i] instanceof Checkbox) {
										// name of the Role
										changedRolesAndValues[i][0] = f[i].getId();
										// True or False depending on the selection
										changedRolesAndValues[i][1] = f[i].getValueAsString();
										
										//TODO
										if (f[i].getValueAsString().equalsIgnoreCase("true"))
											newRoles.add(f[i].getId());
										//TODO
									}
								}
								// This variable will be sent to the server for the update
								final String changedRolesAndValuesR[][] = new String[updatedRoles.size()][2];
								Iterator<String> it = updatedRoles.keySet().iterator();
								int i = 0;
								while (it.hasNext()) {
									String roleName = it.next();
									changedRolesAndValuesR[i][0] = roleName;
									changedRolesAndValuesR[i][1] = updatedRoles.get(roleName);
									i++;
								}
								
								AsyncCallback<String> changeUserRolesCallback = new AsyncCallback<String>() {
	
									public void onFailure(Throwable caught) {
	
										loadMask.hide();
										UsersInfoGrid.displayErrorWindow("Failed to update user's roles. Please try again.", caught);
										//MessageBox.alert("Failed to update the roles. Please try again");
									}
	
									public void onSuccess(String result) {
										loadMask.hide();
										if (result.equals(UserStatus.EDIT_OK)) {
											//TODO
											referenceWidget.updateLocalRegisteredUserRoles(username, newRoles);
											referenceWidget.reloadStore(referenceGrid.getStore(), referenceWidget.getDataAsObject());
											
										/*	Record selectedRecord = referenceGrid.getSelectionModel().getSelected();
											selectedRecord.beginEdit();
											for (int k=0; k<changedRolesAndValues.length; k++) {
												String value;
												if (changedRolesAndValues[k][1].equals("true"))
													value = "YES";
												else
													value = "NO";
												selectedRecord.set(changedRolesAndValues[k][0], value);
												selectedRecord.endEdit();
											}*/
											referenceGrid.getSelectionModel().clearSelections();
											MessageBox.alert("User's roles have been updated succesfully");
											
											
										}
										else if (result.equals(UserStatus.EDIT_FAILED)){
											MessageBox.alert("Failed to update the roles. Please try again");
										}
										// User has been removed from the VO remove this record from the grid
										else {
											Record selectedRecord = referenceGrid.getSelectionModel().getSelected();
											referenceGrid.getStore().remove(selectedRecord);
										}
	
									}
	
								};UsersManagement.userService.updateUserRoles(username, changedRolesAndValuesR, changeUserRolesCallback);
								loadMask.show();
								
								getWindow().close();
							}
						}
						else {
							referenceGrid.getSelectionModel().clearSelections();
							getWindow().close();
						}
					}});

			}  
		}); 
		
		final Button cancelBtn= new Button("Cancel", new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				referenceGrid.getSelectionModel().clearSelections();
				getWindow().close();
			}
		});
		
		saveBtn.setFormBind(true);
		formPanel.setMonitorValid(true);
		formPanel.addButton(saveBtn);
		formPanel.addButton(cancelBtn);
		this.add(formPanel);		
		this.setResizable(false);
		this.setTitle("Edit " + user + "'s Roles");
		
		/*this.addListener(new WindowListenerAdapter(){
			public void onMaximize(Window source){
				
			}
			public void onRestore(Window source){				
				bodyField.setSize(bodyWidth, bodyHeight);		
				descriptionField.setWidth(bodyWidth);
			}
		});*/
	}

	/**
	 * Checks if a given role with a given value is contained at the existing roles and values arrays
	 * 
	 * @param roleName The role name to be checked
	 * @param value The value of the role to be checked
	 * @param roles An array with all role names
	 * @param rolesValues An array with all role values
	 * 
	 * @return True if the given role and value exist , else false
	 */
	private boolean isRoleReallyUpdated(String roleName, String value, String rolesNames[], String rolesValues[]) {
		boolean ret = true;
		for (int i=0; i<rolesNames.length; i++) {
			String v = "false";
			if (rolesNames[i].equals(roleName)) {
				if (rolesValues[i].equals("YES"))
					v = "true";
				if (v.equals(value)) {
					ret = false;
					return ret;
				}
				break;
			}
		}
		return ret;
		
	}
	
	protected Window getWindow(){return this;}	
}
