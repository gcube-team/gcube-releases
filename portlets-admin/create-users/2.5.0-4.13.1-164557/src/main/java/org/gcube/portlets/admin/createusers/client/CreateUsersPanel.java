package org.gcube.portlets.admin.createusers.client;
import java.util.List;

import org.gcube.portlets.admin.createusers.client.ui.AddUserForm;
import org.gcube.portlets.admin.createusers.client.ui.LoadingText;
import org.gcube.portlets.admin.createusers.client.ui.RegisteredUsersTable;
import org.gcube.portlets.admin.createusers.shared.VreUserBean;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.TabPane;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The panel that contains the list of users already registered and the form to add new ones. 
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CreateUsersPanel extends Composite {

	// main vertical panel
	private VerticalPanel mainPanel = new VerticalPanel();

	// Tab panel
	private TabPanel navTabs = new TabPanel();

	// add user subpanel
	private TabPane addUserSubPanel = new TabPane("Create New User");

	// registered users subpanel
	private TabPane registeredUsersSubPanel = new TabPane("Already Created Users") ;

	// Create a remote service proxy to talk to the server-side user manager service.
	private final HandleUsersServiceAsync userServices = GWT.create(HandleUsersService.class);

	// table of registered users
	private RegisteredUsersTable registeredUsersTable;
	
	// event bus
	private final HandlerManager eventBus = new HandlerManager(null);

	public CreateUsersPanel(){

		super();
		initWidget(mainPanel);

		// form panel
		AddUserForm addUserForm = new AddUserForm(userServices, eventBus, this);
		addUserSubPanel.add(addUserForm);

		// add temporary loader for registered users table
		LoadingText loader = new LoadingText();
		loader.setVisible(true);
		registeredUsersSubPanel.add(loader);

		// add stuff to the main panel
		navTabs.add(addUserSubPanel);
		navTabs.add(registeredUsersSubPanel);
		mainPanel.add(navTabs);

		// select add user form tab
		navTabs.selectTab(0);
		
		// enlarge navTabs
		mainPanel.setWidth("100%");

		// require already registered users
		userServices.getAlreadyRegisterdUsers(new AsyncCallback<List<VreUserBean>>() {

			@Override
			public void onSuccess(List<VreUserBean> result) {

				if(result == null){
					showProblemsRetrievingList();
					return;
				}

				GWT.log("List of registered users received!");
				registeredUsersTable = new RegisteredUsersTable(result, eventBus, userServices);
				registeredUsersSubPanel.clear();
				registeredUsersSubPanel.add(registeredUsersTable);
			}

			@Override
			public void onFailure(Throwable caught) {

				GWT.log("Unable to retrieve list of registered users!" + caught.toString());
				showProblemsRetrievingList();

			}
		});
	}

	/**
	 * Show error block if retrieving registered data fails.
	 */
	private void showProblemsRetrievingList() {

		registeredUsersSubPanel.clear();
		AlertBlock error = new AlertBlock(AlertType.ERROR);
		error.setText("It is not possible to retrieve the requested data at the moment. Retry later...");
		error.setAnimation(true);
		error.setClose(false);
		registeredUsersSubPanel.add(error);

	}

	/**
	 * Determine if a user with this email has been already registered
	 * @param actualEmail
	 * @return
	 */
	public boolean isUserPresent(String actualEmail) {
		return registeredUsersTable.isUserPresent(actualEmail);
	}

}
