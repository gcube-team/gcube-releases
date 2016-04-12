package org.gcube.portlets.admin.rolesmanagementportlet.gwt.client;

import java.util.ArrayList;

import org.gcube.portlets.admin.rolesmanagementportlet.gwt.shared.RoleInfo;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.LoadMask;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.layout.VerticalLayout;

/**
 * This class creates a grid that contains the system's available roles
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class AvailableRolesGrid extends Composite {
	
	private Panel panel = new Panel();
	private VerticalPanel vPanel = new VerticalPanel();
	private Object data[][];
	private static GridPanel grid = new GridPanel();
	private static RecordDef recordDef;
	private static LoadMask loadMask = new LoadMask("RolesDiv", "Loading, please wait...");
	
	private boolean areRolesAvailable = false;
	private ArrayList<Record> deletedRecords = new ArrayList<Record>();
	
	/* UI Elements - Buttons */
	private static Button removeBtn = new Button("Remove selected roles");
	private static Button editBtn = new Button("Edit selected role");
	private static com.google.gwt.user.client.ui.Button createBtn = new com.google.gwt.user.client.ui.Button();
	
	private HTML infoMsg = new HTML("<span style=\"color: darkblue\">There was an error while trying to retrieve the available roles. Please try again by refreshing the page</span>");

	public AvailableRolesGrid() {

		panel.setBorder(false);
		panel.setMargins(5);
		panel.setLayout(new VerticalLayout());
		
		createBtn.setStyleName("addRoleButton");
		createBtn.setTitle("Creates a new role");
		
		editBtn.setTooltip("Edits the selected role");
		removeBtn.setTooltip("Removes the selected roles");

		final CheckboxSelectionModel cbSelectionModel = new CheckboxSelectionModel(); 

		AsyncCallback<ArrayList<RoleInfo>> availableRolesCallback = new AsyncCallback<ArrayList<RoleInfo>>() {

			public void onFailure(Throwable caught) {
				loadMask.hide();
				panel.removeAll();
				panel.add(infoMsg);
				RolesManagement.displayErrorWindow("Failed to retrieve the available roles. Please try again", caught);
			}

			public void onSuccess(ArrayList<RoleInfo> result) {
				loadMask.hide();
				if (result != null) {
					StringFieldDef strFieldDefRName= new StringFieldDef("rolename");
					StringFieldDef strFieldDefRDesc= new StringFieldDef("roledesc");
					FieldDef fields[] = {strFieldDefRName, strFieldDefRDesc};
					recordDef = new RecordDef(fields);
					areRolesAvailable = true;
					changeUIstate(areRolesAvailable);
					
					BaseColumnConfig[] columns = new BaseColumnConfig[]{  
							new CheckboxColumnConfig(cbSelectionModel),
							new ColumnConfig("Role name", "rolename", 130, true, null, "rolename"),
							new ColumnConfig("Role description", "roledesc", 300, true, null, "roledesc")
					};

					ColumnModel columnModel = new ColumnModel(columns);  
					data = getRolesData(result);
					MemoryProxy proxy = new MemoryProxy(data);

					ArrayReader reader = new ArrayReader(recordDef);
					Store store = new Store(proxy, reader);  
					
				
					grid.setColumnModel(columnModel);  
					grid.setFrame(true);  
					grid.setStripeRows(true);  
					grid.setAutoExpandColumn("roledesc");  

					grid.setSelectionModel(cbSelectionModel);  
					grid.setWidth(1200);
					grid.setFrame(true);  
					grid.setTitle("Available Roles");
					grid.setAutoHeight(true);
					grid.setAutoScroll(true);
					
					grid.setStore(store);
					store.load();
					vPanel.add(grid);
				}
			}

		};RolesManagement.rolesService.getAvailableRoles(availableRolesCallback);
		loadMask.show();

		/**
		 * Clicking on the 'delete' button to remove all the selected roles
		 */
		removeBtn.addListener(new ButtonListenerAdapter() {  
			public void onClick(Button button, EventObject e) { 
				if (cbSelectionModel.getSelections().length < 1)
					MessageBox.alert("There are no selected roles to delete");
				else {
					MessageBox.confirm("Confirm Changes", "Are you sure you want to delete the selected roles?", new MessageBox.ConfirmCallback() {  
						public void execute(String btnID) {
							if(btnID.equalsIgnoreCase("yes")) {
								deletedRecords.clear();
								final Record[] records = cbSelectionModel.getSelections();
								
								// Check if someone tries to delete the system Roles. Do not allow the deletion of system roles!!!
								AsyncCallback<ArrayList<RoleInfo>> listAllowedRolesCallback = new AsyncCallback<ArrayList<RoleInfo>>() {

									public void onFailure(Throwable caught) {
										//RolesManagement.displayErrorWindow("An unexpected error occurred. Please try again", caught);
										
									}

									public void onSuccess(ArrayList<RoleInfo> result) {
										if (result != null) {
											final ArrayList<String> rolesToBeRemoved = new ArrayList<String>();
											ArrayList<String> rolesCannotBeDeleted = new ArrayList<String>();
											for (int i = 0; i < records.length; i++) {  
												String curRoleName = records[i].getAsString("rolename");
												// Only non system roles will be deleted
												if (!isSystemRole(curRoleName, result)) {
													rolesToBeRemoved.add(curRoleName);
													deletedRecords.add(records[i]);
												}
												else
													rolesCannotBeDeleted.add(curRoleName);
											}
											MessageBox.alert("Info message", "System roles cannot be deleted. Excluding all system roles from the deletion list", new MessageBox.AlertCallback() {
												
												public void execute() {
													// add the callback that deletes the roles
													AsyncCallback<Boolean> deleteRolesCallback = new AsyncCallback<Boolean>() {

														public void onFailure(Throwable caught) {

															RolesManagement.displayErrorWindow("Failed to delete the selected roles. Please try again", caught);
														}

														public void onSuccess(Boolean result) {
															if (result.booleanValue() == true) {
																MessageBox.alert("Selected roles have been deleted succesfully");
																for (Record r : deletedRecords) {
																	//com.google.gwt.user.client.Window.alert("Removing record --> " + r.getAsString("rolename"));
																	grid.getStore().remove(r);
																}
																if (grid.getStore().getCount() <= 0) {
																	areRolesAvailable = false;
																	changeUIstate(areRolesAvailable);
																}
															}
															else
																MessageBox.alert("Failed to delete the selected roles. Please try again");
														}

													};RolesManagement.rolesService.deleteRoles(rolesToBeRemoved, deleteRolesCallback);
													
												}
											});
										
											
											
										}	
									}
								};RolesManagement.rolesService.listAllowedRoles(listAllowedRolesCallback);
							}
						}});
				}
			}  
		}); 
		
		editBtn.addListener(new ButtonListenerAdapter() {

			public void onClick(Button button, EventObject e) {
				if (cbSelectionModel.getSelections().length < 1)
					MessageBox.alert("There is no selected role to edit");
				else if (cbSelectionModel.getSelections().length > 1)
					MessageBox.alert("Only one role can be edited per time");
				else
				{		
					
					AsyncCallback<ArrayList<RoleInfo>> listAllowedRolesCallback = new AsyncCallback<ArrayList<RoleInfo>>() {

						public void onFailure(Throwable caught) {
							RolesManagement.displayErrorWindow("An unexpected error occurred. Please try again", caught);
							
						}

						public void onSuccess(ArrayList<RoleInfo> result) {
							if (result != null) {
								String roleName = grid.getSelectionModel().getSelected().getAsString("rolename");
								String roleDesc = grid.getSelectionModel().getSelected().getAsString("roledesc");
								boolean isSystem = isSystemRole(roleName, result);
								EditRoleWindow roleWindow = new EditRoleWindow(roleName, roleDesc, isSystem);
								roleWindow.show();
							}	
						}
					};RolesManagement.rolesService.listAllowedRoles(listAllowedRolesCallback);
				}	
			}
		});
		
		createBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				CreateRoleWindow roleWindow = new CreateRoleWindow();
				roleWindow.show();
			}
			
		});
		
/*
		grid.addTool(new Tool(Tool.PLUS, new Function() {  
			public void execute() {  
				CreateRoleWindow roleWindow = new CreateRoleWindow();
				roleWindow.show();
			}  
		}, "Create new role")); */
		
		panel.add(createBtn);
		panel.add(vPanel);
		panel.addButton(editBtn);
		panel.addButton(removeBtn);
		
		//changeUIstate(areRolesAvailable);

		initWidget(panel);
		updateSize();
		
		/* Add a listener for the resizing of the window */
		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler(){
			public void onResize(ResizeEvent event) {
				updateSize();
			}
		});


	}
	
	/*******************************************************************************************************
	 * 											
	 * 												HELPED METHODS
	 * 
	 ******************************************************************************************************/
	
	/**
	 * Updates the size of the grid depending on the current Window size
	 */
	private void updateSize() {
		RootPanel root = RootPanel.get("RolesDiv");

		int leftBorder = root.getAbsoluteLeft();
		int rightScrollBar = 17;
		int rootWidth = com.google.gwt.user.client.Window.getClientWidth() - 2* leftBorder - rightScrollBar;
		
		if (rootWidth < 1200)
			grid.setWidth(rootWidth);
		else
			grid.setWidth(1200);
	}

	/**
	 * Returns the Roles info into an array of Objects
	 * 
	 * @param rolesInfo
	 * @return
	 */
	private Object[][] getRolesData(ArrayList<RoleInfo> rolesInfo) {  
		Object[][] dataObjects = new Object[rolesInfo.size()][];
		int i = 0;
		for (RoleInfo rInfo : rolesInfo) {
			String[] rowData = new String[2];
			rowData[0] = rInfo.getRoleName();
			rowData[1] = rInfo.getRoleDescription();
			dataObjects[i] = rowData;
			i++;
		}
		return dataObjects;
	}
	
	/**
	 * Changed the state if the UI buttons depending on the current available roles
	 * 
	 * @param areRolesAvailable
	 */
	private static void changeUIstate(boolean areRolesAvailable) {
		if (!areRolesAvailable) {
			removeBtn.setDisabled(true);
			editBtn.setDisabled(true);
		}
		else {
			removeBtn.setDisabled(false);
			editBtn.setDisabled(false);
		}
	}

	/**
	 * Adds a new record to the grid with the given name and description
	 * @param name The role's name
	 * @param desc The role's description
	 */
   protected static void addRecordToGrid(String name, String desc) {
		String[] rowData = new String[2];
		rowData[0] = name;
		rowData[1] = desc;
		grid.getStore().add(getRecordDef().createRecord(rowData));
		changeUIstate(true);
	}
   
   /**
    * Updates the current selected record of the grid with the given name and description
    * 
    * @param name The role's name to be set
    * @param desc The role's description to be set
    */
   protected static void updateSelectedRecordOfGrid(String name, String desc) {
	   Record selectedRecord = grid.getSelectionModel().getSelected();
	   selectedRecord.beginEdit();
	   selectedRecord.set("rolename", name);
	   selectedRecord.set("roledesc", desc);
	   selectedRecord.endEdit();
	}
   
   /**
    * Returns the grid's record definition
    * @return
    */
   protected static RecordDef getRecordDef() {
	   return recordDef;
   }


   /**
    *  Returns true if the given role name is a system role, else it returns false
    *  
    * @param roleName The role's name to be checked
    * @param systemRoles The list of the system roles
    * @return
    */
   private boolean isSystemRole(String roleName, ArrayList<RoleInfo> systemRoles) {
	   boolean isSystem = false;
	   for (RoleInfo ri : systemRoles) {
		   if (ri.getRoleName().trim().equals(roleName.trim())) {
			   isSystem = true;
			   break;
		   }
	   }
	   return isSystem;
   }
}
