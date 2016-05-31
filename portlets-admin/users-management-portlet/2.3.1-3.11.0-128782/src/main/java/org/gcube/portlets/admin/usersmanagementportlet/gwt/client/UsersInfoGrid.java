package org.gcube.portlets.admin.usersmanagementportlet.gwt.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.FilterType;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.UserInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.LoadMask;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
import com.gwtextux.client.data.PagingMemoryProxy;

/**
 * This class creates the grid that contains all the registered users for a VO/VRE and the roles that are
 * either registered or not
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class UsersInfoGrid extends Composite{
	protected static final int numberOfAdditionalFields = 3;
	private int numberOfRoles;
	private String availableRoles[];
	// Keeps the current registered users. It is updated if a user is either deleted or added
	private ArrayList<UserInfo> registeredUsersInfo;
	private ArrayList<UserInfo> initialUsersList;
	private int numberOfRecordsPerPage = 15;

	/*
	 * UI ELements
	 */
	//private ScrollPanel scroller = new ScrollPanel();
	private VerticalPanel panel;
	//private Panel panel;
	private HorizontalPanel tp = new HorizontalPanel();
	private VerticalPanel gridHostPanel = new VerticalPanel();
	private HorizontalPanel buttonsPanel;
	private static GridPanel grid = new GridPanel();
	private static LoadMask loadMask = new LoadMask("UsersDiv", "Loading, please wait...");
	private UserEditContextMenu cMenu =  new UserEditContextMenu(grid, UsersInfoGrid.this);
	private static RecordDef rdef;

	private FilterPanel filterPanel = null;

	public UsersInfoGrid() {
		panel = new VerticalPanel();
		//panel = new Panel();
		buttonsPanel = new HorizontalPanel();
		buttonsPanel.setSpacing(15);
		gridHostPanel.setWidth("100%");
		tp.setWidth("100%");
		//panel.setAutoWidth(true);
		//panel.setAutoScroll(true);
		panel.setWidth("100%");
		panel.add(tp);

		/* 
		 * Retrieves the available roles for the current VO. This information is needed in order to know how many roles will be
		 * displayed on the grid panel.
		 * Then retrieves all the registered users of this VO/VRE and their roles.
		 * Create the gridPanel's data depending on this information
		 */
		AsyncCallback<String[]> getAvailableRolesCallback = new AsyncCallback<String[]>() {

			public void onFailure(Throwable caught) {
				loadMask.hide();
				displayErrorWindow("Failed to retrieve the available roles for the current VO/VRE. Please try again.", caught);
			}

			public void onSuccess(String[] result) {
				loadMask.hide();
				if (result != null) {
					numberOfRoles = result.length;
					availableRoles = result;

					filterPanel = new FilterPanel(getFilters());
					tp.add(filterPanel);
					tp.setSpacing(5);
					tp.add(gridHostPanel);

					filterPanel.setFilterButtonClickHandler(new ButtonListenerAdapter() {
						public void onClick(Button button, EventObject e) {
							//ArrayList<UserInfo> filteredUsers = filterPanel.filterData(registeredUsersInfo);
							ArrayList<UserInfo> filteredUsers = filterPanel.filterData(initialUsersList);
							if (filteredUsers.size() > 0) {
								updateGridData(filteredUsers);
								updateGridTitle();
							}
							else
								MessageBox.alert("No records match the given filters");

						}
					});

					filterPanel.setResetButtonClickHandler(new ButtonListenerAdapter() {
						public void onClick(Button button, EventObject e) {
							updateGridData(initialUsersList);
							updateGridTitle();
							filterPanel.resetFields();
						}
					});

					filterPanel.setKeyboardListenerOnFields(new FieldListenerAdapter() {
						public void onSpecialKey(Field field, EventObject e) {  
							if (e.getCharCode() == EventObject.ENTER) {  
								ArrayList<UserInfo> filteredUsers = filterPanel.filterData(initialUsersList);
								if (filteredUsers.size() > 0) {
									updateGridData(filteredUsers);
									updateGridTitle();
								}
								else
									MessageBox.alert("No records match the given filters");
							}  
						} 
					});

					createGrid();
				}
			}
		};UsersManagement.userService.getAvailableRolesByCurrentGroup(getAvailableRolesCallback);
		loadMask.show();

		initWidget(panel);

//		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {
//			public void onResize(ResizeEvent event) {
//				updateSize();
//			}
//		});
	}

	private void updateSize() {
//		RootPanel root = RootPanel.get("UsersDiv");
//
//		int leftBorder = root.getAbsoluteLeft();
//		int rightScrollBar = 17;
//		int rootWidth = com.google.gwt.user.client.Window.getClientWidth() - 2* leftBorder - rightScrollBar;
//
//		tp.setWidth(new Integer(rootWidth).toString());
//		int atomWidth = rootWidth / 4;
//		//grid.setWidth((atomWidth * 3));
//		//grid.setAutoWidth(true);
//		filterPanel.setWidth(new Integer(atomWidth).toString());
	}

	private void createGrid() {

		final CheckboxSelectionModel cbSelectionModel = new CheckboxSelectionModel(); 
		// The number of fields for the grid panel depends on the number of roles
		final FieldDef[] fieldDef = new FieldDef[numberOfRoles+numberOfAdditionalFields];
		fieldDef[0] = new StringFieldDef("username");
		fieldDef[1] = new StringFieldDef("email");
		fieldDef[2] = new StringFieldDef("fullname");
		for (int i=numberOfAdditionalFields; i<numberOfRoles+numberOfAdditionalFields; i++) {
			fieldDef[i] = new StringFieldDef(availableRoles[i-numberOfAdditionalFields]);
		}

		BaseColumnConfig[] columns = new BaseColumnConfig[fieldDef.length+1];  
		columns[0] = new CheckboxColumnConfig(cbSelectionModel);
		columns[1] = new ColumnConfig("Username", "username", 140, true, null, "username");
		columns[2] = new ColumnConfig("Email", "email", 200, true);
		columns[3] = new ColumnConfig("Full name", "fullname", 160, true);
		for (int i=4; i<columns.length; i++) {
			columns[i] = new ColumnConfig(availableRoles[i-4], availableRoles[i-4],85, true,null, availableRoles[i-4]);
		}
		final RecordDef recordDef = new RecordDef(fieldDef);
		final ArrayReader reader = new ArrayReader(recordDef);
		final ColumnModel columnModel = new ColumnModel(columns);  
		rdef = recordDef;

		// retrieve the users that are currently registered to the current VO
		AsyncCallback<ArrayList<UserInfo>> getUsersAndRolesCallback = new AsyncCallback<ArrayList<UserInfo>>() {

			public void onFailure(Throwable caught) {
				loadMask.hide();
				displayErrorWindow("Failed to retrieve the registered users of the current VO/VRE. Please try again.", caught);
			}

			@SuppressWarnings("unchecked")
			public void onSuccess(ArrayList<UserInfo> result) {
				loadMask.hide();
				if (result != null && result.size()>0) {
					// Create the data here that will be inserted into the grid
					registeredUsersInfo = result;
					initialUsersList = result;

					String[][] dataAsObject = getDataAsObject();
					PagingMemoryProxy mProxy = 	new PagingMemoryProxy(dataAsObject);
					Store store = new Store(mProxy, reader);
					grid.setColumnModel(columnModel);  
					grid.setFrame(true);  
					grid.setStripeRows(true);  
					grid.setSelectionModel(cbSelectionModel);  
					//grid.setWidth(1200);
					grid.setAutoWidth(true);
					grid.setFrame(true); 
					grid.setAutoHeight(true);
					grid.setAutoScroll(true);


					grid.addTool(new Tool(Tool.SAVE, new Function() {  
						public void execute() {  
							com.google.gwt.user.client.Window.open(GWT.getModuleBaseURL()+"DownloadService", "Users Information", "");
						}  
					}, "Save registered users' info")); 

					final PagingToolbar pagingToolbar = new PagingToolbar(store);  
					pagingToolbar.setPageSize(numberOfRecordsPerPage);  
					pagingToolbar.setDisplayInfo(true);  
					pagingToolbar.setEmptyMsg("No records to display");  

					final NumberField pageSizeField = new NumberField();  
					pageSizeField.setWidth(40);  
					pageSizeField.setAllowDecimals(false);
					pageSizeField.setAllowNegative(false);
					pageSizeField.setSelectOnFocus(true);  
					pageSizeField.addListener(new FieldListenerAdapter() {
						public void onSpecialKey(Field field, EventObject e) {  
							if (e.getCharCode() == EventObject.ENTER) {  
								if (field.getXType().toLowerCase().equals("numberfield")) {
									String value = ((NumberField)field).getText();
									try {
										numberOfRecordsPerPage = Integer.parseInt(value.trim());
									} catch (NumberFormatException e2) {

									}
									pagingToolbar.setPageSize(numberOfRecordsPerPage);  
									reloadStore(grid.getStore(),getDataAsObject());
								}
							}  
						}  
					});  

					ToolTip toolTip = new ToolTip("Enter page size");  
					toolTip.applyTo(pageSizeField);  

					pagingToolbar.addField(pageSizeField);  
					grid.setBottomToolbar(pagingToolbar);  
					grid.setStore(store);
					store.load(0, pagingToolbar.getPageSize());
					pagingToolbar.doOnRender(new Function() {
						public void execute() {
							pagingToolbar.getRefreshButton().addListener(new ButtonListenerAdapter() {
								public void onClick(Button button, EventObject e) {

									try {
										numberOfRecordsPerPage = Integer.parseInt(pageSizeField.getText().trim());
									} catch (NumberFormatException e2) {

									}
									pagingToolbar.setPageSize(numberOfRecordsPerPage);
									reloadStore(grid.getStore(),getDataAsObject());
								}
							});
						}
					});

					updateGridTitle();

					//scroller.add(grid);
					gridHostPanel.add(grid);
					//gridHostPanel.add(scroller);
					gridHostPanel.add(buttonsPanel);

					// Button for removing selected users
					Button removeSelUsersBtn = new Button("Remove selected users", new ButtonListenerAdapter() {  
						public void onClick(Button button, EventObject e) {  
							if (cbSelectionModel.getSelections().length < 1)
								MessageBox.alert("There are no selected users to remove");
							else {
								MessageBox.confirm("Confirm Changes", "Are you sure you want to remove the selected users?", new MessageBox.ConfirmCallback() {  
									public void execute(String btnID) {
										if(btnID.equalsIgnoreCase("yes")) {
											final Record[] records = cbSelectionModel.getSelections();
											final String usersToBeRemoved[] = new String[records.length];
											for (int i = 0; i < records.length; i++) {  
												usersToBeRemoved[i] = records[i].getAsString("username");  
											}  
											// add the callback that deletes the users from this VO
											AsyncCallback<Boolean> deleteUsersCallback = new AsyncCallback<Boolean>() {

												public void onFailure(Throwable caught) {

													displayErrorWindow("Failed to remove the selected users from the current VO/VRE. Please try again.", caught);
												}

												public void onSuccess(Boolean result) {
													MessageBox.alert("Selected users have been deleted succesfully from the VO/VRE");
													removeLocalRegisteredUsers(usersToBeRemoved);
													reloadStore(grid.getStore(), getDataAsObject());
												}
											};UsersManagement.userService.removeUsersFromVO(usersToBeRemoved, deleteUsersCallback);
										}
									}});
							}
						}  
					}); 
					removeSelUsersBtn.setTooltip("Removes the selected users from the VO/VRE");
					buttonsPanel.add(removeSelUsersBtn);

					//					// Button for adding new users
					//					Button addNewUsersBtn = new Button("Add new users", new ButtonListenerAdapter() {  
					//						public void onClick(Button button2, EventObject e2) {
					//							AsyncCallback<ArrayList<UserInfo>> getUnregisteredUsersCallback = new AsyncCallback<ArrayList<UserInfo>>() {
					//
					//								public void onFailure(Throwable caught) {
					//									loadMask.hide();
					//									displayErrorWindow("Failed to retrieve the unregistered users. Please try again.", caught);
					//								}
					//
					//								public void onSuccess(ArrayList<UserInfo> result) {
					//									loadMask.hide();
					//									UnregisteredUsersWindow addUsersWindow = new UnregisteredUsersWindow(UsersInfoGrid.this, grid, result);
					//									addUsersWindow.show();
					//								}
					//
					//							};UsersManagement.userService.getUnregisteredUsersForVO(getUnregisteredUsersCallback);
					//							loadMask.show();
					//						}  
					//					});   
					//					addNewUsersBtn.setTooltip("Adds new users to the current VO/VRE");
					//					buttonsPanel.add(addNewUsersBtn);
				}
				
				updateSize();
			}
		};UsersManagement.userService.getRegisteredUsersForaVO(getUsersAndRolesCallback);

		// Button for editing user's roles
		Button editUserRolesBtn = new Button("Edit Selected User Roles", new ButtonListenerAdapter() {  
			public void onClick(Button button2, EventObject e2) {
				if(grid.getSelectionModel().getCount()>1)
					MessageBox.alert("Can only edit one user per time");
				else if (grid.getSelectionModel().getCount() < 1)
					MessageBox.alert("There is no selected user to edit");
				else {
					String fields[] = grid.getSelectionModel().getSelected().getFields();
					String username = grid.getSelectionModel().getSelected().getAsString("username");
					String rolesValues[] = new String[fields.length-numberOfAdditionalFields];
					String rolesNames[] = new String[fields.length-numberOfAdditionalFields];
					// find the values for each role
					for (int i=0; i<rolesValues.length; i++) {
						rolesNames[i] = fields[i+numberOfAdditionalFields];
						rolesValues[i] = grid.getSelectionModel().getSelected().getAsString(fields[i+numberOfAdditionalFields]);
					}
					Window form= new EditUsersRolesWindow(username, rolesNames, rolesValues, grid, UsersInfoGrid.this);
					form.show();	
				}
			}  
		});   
		editUserRolesBtn.setTooltip("Edits the selected user's roles");
		buttonsPanel.add(editUserRolesBtn);

		/*
		 * Add a listener on grid's rows.
		 * When a row is clicked show a context menu for managing the users roles
		 */
		grid.addGridRowListener(new GridRowListenerAdapter() {
			public void onRowContextMenu(GridPanel grid, int rowIndex, EventObject e) {
				grid.getSelectionModel().selectRow(rowIndex);
				cMenu.showMenu(grid,rowIndex,e);
				e.stopPropagation();
				e.stopEvent();	
			}
		});
	}

	protected void updateGridData(ArrayList<UserInfo> usersData) {
		if (usersData != null)
			this.registeredUsersInfo = usersData;
		reloadStore(grid.getStore(), getDataAsObject());

	}

	protected void updateGridTitle() {
		grid.setTitle("Number of users: " + grid.getStore().getTotalCount());
	}

	/**
	 * reloads the given store with the given data
	 * 
	 * @param store
	 * @param data
	 */
	protected void reloadStore(Store store, Object[][] data) {	
		store.removeAll();
		store.commitChanges(); 
		PagingMemoryProxy proxy = new PagingMemoryProxy(data);
		store.setDataProxy(proxy);
		store.commitChanges();
		store.load(0, numberOfRecordsPerPage);
		updateGridTitle();
	} 

	/**
	 * Updates the current registered users with the new added users
	 * 
	 * @param newUsers
	 */
	protected void updateLocalRegisteredUsersInfo(ArrayList<UserInfo> newUsers) {
		for (UserInfo u : newUsers)
			this.registeredUsersInfo.add(u);
	}

	/**
	 * Updated the current registered users by removing the given users
	 * 
	 * @param usersToBeRemoved
	 */
	protected void removeLocalRegisteredUsers(String[] usersToBeRemoved) {
		for (int i=0; i<usersToBeRemoved.length; i++) {
			int index = 0;
			for (UserInfo u : this.registeredUsersInfo) {
				if (u.getUsername().equals(usersToBeRemoved[i])) {
					this.registeredUsersInfo.remove(index);
					break;
				}
				index++;
			}
		}
	}

	protected void updateLocalRegisteredUserRoles(String username, ArrayList<String> newRoles) {
		for (UserInfo u : this.registeredUsersInfo) {
			if (u.getUsername().equals(username)) {
				u.setAssignedRoles(newRoles);
				break;
			}
		}
		for (UserInfo u : this.initialUsersList) {
			if (u.getUsername().equals(username)) {
				u.setAssignedRoles(newRoles);
				break;
			}
		}
	}

	protected String[][] getDataAsObject() {
		String[][] data = new String[this.registeredUsersInfo.size()][];
		int j = 0;
		for (UserInfo u : this.registeredUsersInfo) {
			String[] rowData = new String[numberOfRoles+numberOfAdditionalFields];	
			rowData[0] = u.getUsername();
			rowData[1] = u.getEmail();
			rowData[2] = u.getFullname();
			ArrayList<String> usersRoles = u.getAssignedRoles();
			for (int i=0; i<numberOfRoles; i++) {
				if (usersRoles != null) {
					if (usersRoles.contains(availableRoles[i]))
						rowData[i+numberOfAdditionalFields] = "YES";
					else
						rowData[i+numberOfAdditionalFields] = "NO";
				}
				// This is for users that will be added now to the VO and they don't have any role
				else
					rowData[i+numberOfAdditionalFields] = "NO";
			}
			data[j] = rowData;
			j++;
		}
		return data;
	}

	protected HashMap<String,FilterType> getFilters() {
		HashMap<String, FilterType> filters = new HashMap<String, FilterType>();
		filters.put("username", FilterType.LITERAL);
		filters.put("fullname", FilterType.LITERAL);
		filters.put("email", FilterType.LITERAL);
		for (String role : this.availableRoles) {
			filters.put(role, FilterType.BOOLEAN);
		}
		return filters;
	}

	/**
	 * 
	 * @return The gridPanel
	 */
	protected static  GridPanel getGridPanel() {
		return grid;
	}

	protected static RecordDef getRecordDef() {
		return rdef;
	}

	protected void refreshGrid()  {

		AsyncCallback<ArrayList<UserInfo>> getUsersCallback = new AsyncCallback<ArrayList<UserInfo>>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(ArrayList<UserInfo> result) {

				updateGridData(result);
			}

		};UsersManagement.userService.getRegisteredUsersForaVO(getUsersCallback);
	}

	protected static void displayErrorWindow(String userMsg, Throwable caught) {
		ExceptionAlertWindow alertWindow = new ExceptionAlertWindow(userMsg, true);
		alertWindow.addDock(caught);
		int left = com.google.gwt.user.client.Window.getClientWidth()/2;
		int top = com.google.gwt.user.client.Window.getClientHeight()/2;
		alertWindow.setPopupPosition(left, top);
		alertWindow.show();
	}

}
