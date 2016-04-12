package org.gcube.portlets.admin.usersmanagementportlet.gwt.client;

import java.util.ArrayList;


import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.UserInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.LoadMask;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBox.ConfirmCallback;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;

/**
 * This class creates a Grid that contains the users requests for registration
 * If there are no requests from users an information message is displayed together 
 * with a refresh button
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class UserRequestsGrid extends Composite {

//	private int numberOfRoles;
//	private String availableRoles[];

	private VerticalPanel panel;
	private HorizontalPanel buttonsPanel;
	private GridPanel grid = new GridPanel();
	HTML infoMsg = new HTML("<span style=\"color: darkblue\">There are no requests from users for this VO/VRE. Click the refresh button if you want to check again.</span>");
	Button refreshBtn = new Button("Refresh");
	private UsersInfoGrid referenceWidget;
	private static LoadMask loadMask = new LoadMask("UsersDiv", "Loading, please wait...");
	
	private boolean sendCustomMailForRejection = false;

	public UserRequestsGrid(UsersInfoGrid uiGrid) {
		this.referenceWidget = uiGrid;
		panel = new VerticalPanel();
		buttonsPanel = new HorizontalPanel();
		buttonsPanel.setSpacing(15);
		panel.setWidth("100%");

		refreshBtn.addListener(new ButtonListenerAdapter() {  
			public void onClick(Button button2, EventObject e2) {
				buttonsPanel.clear();
				panel.clear();
				createGrid();
			}  
		});  

		/*
		 * Retrieve the available roles for the current VO. This information is needed in order to know how many roles will be
		 * displayed on the grid panel.
		 * Then retrieve all the user requests of this VO with their roles.
		 * Create the gridPanel's data depending on this information
		 */
		AsyncCallback<String[]> getAvailableRolesCallback = new AsyncCallback<String[]>() {

			public void onFailure(Throwable caught) {
				loadMask.hide();
			}

			public void onSuccess(String[] result) {
				if (result != null) {
				//	numberOfRoles = result.length;
				//	availableRoles = result;

					createGrid();			
				}
			}
		};UsersManagement.userService.getAvailableRolesByCurrentGroup(getAvailableRolesCallback);
		loadMask.show();


		initWidget(panel);
	}


	/**
	 * Creates a grid with the users that have requested registration and with all the available roles.
	 * Depending on the user's request the roles cells can have either the "REQUESTED" or "NOT_REQUESTED" value
	 */
	private void createGrid() {
		final CheckboxSelectionModel cbSelectionModel = new CheckboxSelectionModel(); 
		// The number of fields for the grid panel depends on the number of roles
		//final FieldDef[] fieldDef = new FieldDef[numberOfRoles+UsersInfoGrid.numberOfAdditionalFields];
		final FieldDef[] fieldDef = new FieldDef[4];
		fieldDef[0] = new StringFieldDef("username");
		fieldDef[1] = new StringFieldDef("email");
		fieldDef[2] = new StringFieldDef("fullname");
		fieldDef[3] = new StringFieldDef("usercomments");
		
		/*	for (int i=1; i<numberOfRoles+UsersInfoGrid.numberOfAdditionalFields; i++) {
			fieldDef[i] = new StringFieldDef(availableRoles[i-1]);
		}*/
		final RecordDef recordDef = new RecordDef(fieldDef);

		// actual fields + the checkbox field
		BaseColumnConfig[] columns = new BaseColumnConfig[fieldDef.length+1];
		columns[0] = new CheckboxColumnConfig(cbSelectionModel);
		columns[1] = new ColumnConfig("Username", "username", 210, true, null, "username");
		columns[2] = new ColumnConfig("Email", "email", 260, true);
		columns[3] = new ColumnConfig("Fullname", "fullname", 260, true);
		columns[4] = new ColumnConfig("User's comments", "usercomments", 1330, false);

		final ColumnModel columnModel = new ColumnModel(columns);  
		final Store store = new Store(recordDef);

		// retrieve the users that are currently registered to the current VO
		AsyncCallback<ArrayList<UserInfo>> getPendingUsersCallback = new AsyncCallback<ArrayList<UserInfo>>() {

			public void onFailure(Throwable caught) {
				loadMask.hide();
				UsersInfoGrid.displayErrorWindow("Failed to retrieve the users' requests for the current VO/VRE. Please try again.", caught);
			}

			public void onSuccess(ArrayList<UserInfo> result) {
				loadMask.hide();
				if (result != null && result.size() > 0) {
					// Create the data here that will be inserted into the grid
					for (UserInfo pendingUser : result) {
						String rowData[] = new String[4];//1
						rowData[0] = pendingUser.getUsername();
						rowData[1] = pendingUser.getEmail();
						rowData[2] = pendingUser.getFullname();
						rowData[3] = pendingUser.getUserComment();
						store.add(recordDef.createRecord(rowData));
					}
					grid.setStore(store); 
					grid.setColumnModel(columnModel);  
					grid.setFrame(true);  
					grid.setStripeRows(true); 
					grid.setSelectionModel(cbSelectionModel);  
					//grid.setWidth(1400);
					grid.setAutoWidth(true);
					grid.setFrame(true);  
					grid.setTitle("Manage Users' Requests");
					grid.setAutoHeight(true);
					grid.setAutoScroll(true);
					panel.add(grid);

					/*
					 * Button for authorizing the selected users
					 * The users are added to the VO but with NO roles
					 *  
					 */
					Button authorizeUsersBtn = new Button("Authorize Selected Requests", new ButtonListenerAdapter() {  
						public void onClick(Button button, EventObject e) {  
							if (cbSelectionModel.getSelections().length < 1)
								MessageBox.alert("There are no selected users to authorize.");
							else {
								MessageBox.confirm("Confirm Changes", "Are you sure you want to authorize the selected users?", new MessageBox.ConfirmCallback() {  
									public void execute(String btnID) {
										if(btnID.equalsIgnoreCase("yes")) {
											final Record[] records = cbSelectionModel.getSelections();
											ArrayList<String> usersToBeAuthorized = new ArrayList<String>();
											final ArrayList<String[]> newUsers = new ArrayList<String[]>();
											final ArrayList<UserInfo> newUserInfoUsers = new ArrayList<UserInfo>();
											for (int i = 0; i < records.length; i++) {
												String[] tmpInfo = new String[3];
												tmpInfo[0] = records[i].getAsString("username");
												tmpInfo[1] = records[i].getAsString("email");
												tmpInfo[2] = records[i].getAsString("fullname");
												newUsers.add(tmpInfo);
												usersToBeAuthorized.add(tmpInfo[0]); //the username is needed
												// For the reference grid
												UserInfo ui = new UserInfo(tmpInfo[0], tmpInfo[2], tmpInfo[1], null, null, null);
												newUserInfoUsers.add(ui);
											}  

											AsyncCallback<Boolean> authorizeUsersCallback = new AsyncCallback<Boolean>() {

												public void onFailure(Throwable caught) {
													MessageBox.alert("Failed to authorize the selected users. Please try again");
													UsersInfoGrid.displayErrorWindow("Failed to authorize the selected users. Please try again.", caught);
												}

												public void onSuccess(Boolean result) {
													referenceWidget.updateLocalRegisteredUsersInfo(newUserInfoUsers);
													referenceWidget.reloadStore(UsersInfoGrid.getGridPanel().getStore(), referenceWidget.getDataAsObject());
													for (int j=0; j<records.length; j++) {
														store.remove(records[j]);
													}
													if (grid.getStore().getRecords().length <= 0) {
														createInfoMsg();
													}
													MessageBox.alert("The selected users have been added to the VO/VRE. In order to change their roles,  go to the 'Manage Registered Users' tab ");
												
													
												}
												
											};UsersManagement.userService.addUsersToVO(usersToBeAuthorized, authorizeUsersCallback); 
										}
											
									}});
							}

						}  
					});   
					authorizeUsersBtn.setTooltip("Adds the selected users to the VO/VRE with the 'VRE-User' role associated to them");
					buttonsPanel.add(authorizeUsersBtn);



					Button removeSelectedUsersBtn = new Button("Reject selected requests", new ButtonListenerAdapter() {  
						public void onClick(Button button2, EventObject e2) {

							if (cbSelectionModel.getSelections().length < 1)
								MessageBox.alert("There are no selected users");
							else {
								MessageBox.confirm("Confirm Changes", "Are you sure you want to reject the selected users?", new MessageBox.ConfirmCallback() {  
									public void execute(String btnID) {
										if(btnID.equalsIgnoreCase("yes")) {
											MessageBox.confirm("Email notification", "Would you like to send an email message to the rejected users?", new ConfirmCallback(){

												@Override
												public void execute(String btnID) {
													if(btnID.equalsIgnoreCase("yes")) {
														sendCustomMailForRejection = true;
														final Record[] records = cbSelectionModel.getSelections();
														ArrayList<String> usersEmails = new ArrayList<String>();
														for (int i = 0; i < records.length; i++) {
															usersEmails.add(records[i].getAsString("email"));
														}  
														EmailWindow ew = new EmailWindow(usersEmails);
														ew.show();
													}
													final Record[] records = cbSelectionModel.getSelections();
													String usersToBeRemoved[] = new String[records.length];
													for (int i = 0; i < records.length; i++) {  
														usersToBeRemoved[i] = records[i].getAsString("username");  
													}  
													// add the callback that deletes the users from this VO
													AsyncCallback<Boolean> deleteUsersCallback = new AsyncCallback<Boolean>() {

														public void onFailure(Throwable caught) {

															//MessageBox.alert("Failed to delete the selected users from the VO/VRE. Please try again");
														}

														public void onSuccess(Boolean result) {
															// remove the records from the grid
															for (Record r : records) {
																store.remove(r);

															}
															if (grid.getStore().getRecords().length <= 0) {
																createInfoMsg();
															}
															//MessageBox.alert("The selected users have been deleted succesfully from the requests list");
														}

													};UsersManagement.userService.denyRequests(usersToBeRemoved, sendCustomMailForRejection, deleteUsersCallback);
													
												}
												
											});
										}
									}});				
							}

						}  
					}); 
					removeSelectedUsersBtn.setTooltip("Removes the selected users' requests.");
					buttonsPanel.add(removeSelectedUsersBtn);
					panel.add(buttonsPanel);
				}
				/*
				 * If there are no requests from users show an alert message and a refresh button
				 */
				else {
					createInfoMsg();
				}
			}

		};UsersManagement.userService.getUsersRequests(getPendingUsersCallback);
		loadMask.show();

//		/* Add a listener for the resizing of the window */
//		com.google.gwt.user.client.Window.addWindowResizeListener(new WindowResizeListener(){
//
//			public void onWindowResized(int width, int height) {
//				RootPanel root = RootPanel.get("UsersDiv");
//
//				int leftBorder = root.getAbsoluteLeft();
//
//				int rightScrollBar = 17;
//
//				int rootWidth = com.google.gwt.user.client.Window.getClientWidth() - 2* leftBorder - rightScrollBar;
//				if (rootWidth > 400)
//					grid.setWidth(1300);
//				else
//					grid.setWidth(rootWidth);
//			}
//
//		});
	}

	private void createInfoMsg() {
		panel.clear();
		panel.add(infoMsg);
		panel.add(refreshBtn);
		panel.setSpacing(15);
	}
	
	/**
	 * This method refreshes the Grid.
	 * All the information is again retrieved from the server
	 */
	protected void refreshGrid() {
		buttonsPanel.clear();
		panel.clear();
		grid.clear();
		createGrid();
	}
}
