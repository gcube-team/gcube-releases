package org.gcube.portlets.admin.manageusers.client.presenter;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.manageusers.client.ManageUsersServiceAsync;
import org.gcube.portlets.admin.manageusers.client.view.Display;
import org.gcube.portlets.admin.manageusers.shared.PortalUserDTO;
import org.gcube.portlets.widgets.inviteswidget.client.ui.InviteWidget;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * 
 * @author Massimiliano Assante (assante@isti.cnr.it)
 *
 */
public class VREDeploymentPresenter implements Presenter {
	private final ManageUsersServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;
	String location = null;
	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public VREDeploymentPresenter(ManageUsersServiceAsync rpcService, HandlerManager eventBus, Display display) {
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = display;
	}
	@SuppressWarnings("rawtypes")
	public void bind() {
		///*** BUTTONS & Menu
		SelectionListener approvesl = new SelectionListener<ComponentEvent>() {  
			public void componentSelected(ComponentEvent ce) {  
				List<PortalUserDTO> selectedUsers = display.getGridSelectionModel().getSelectedItems();
				if (selectedUsers.size() > 0) {
					doApprove(selectedUsers);
				}
			}  
		};

		///*** BUTTONS & Menu
		SelectionListener inviteButtonListener = new SelectionListener<ComponentEvent>() {  
			public void componentSelected(ComponentEvent ce) {  
				doOpenInviteWidget();
			}  
		};

		display.getApproveButton().addSelectionListener(approvesl);
		display.getApproveMenu().addSelectionListener(approvesl);
		display.getInviteButton().addSelectionListener(inviteButtonListener);

		// REFRESH
		display.getRefreshButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				fetchAvailableUsers();
			}  
		}); 

		///*** GRID
		display.getGridSelectionModel().addSelectionChangedListener(new SelectionChangedListener<PortalUserDTO>() {			
			public void selectionChanged(SelectionChangedEvent<PortalUserDTO> event) {
				if (event.getSelectedItem() != null)
					display.setGridContextMenu();
				display.enableActionButtons(event.getSelectedItem());
			}			
		});

	}

	/**
	 * go method
	 */
	@Override
	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
		fetchAvailableUsers();
	}
	/**
	 * fetch all the workflow documents belongin to this user
	 */
	private void fetchAvailableUsers() {
		display.maskCenterPanel("Loading available Users, please wait", true);
		rpcService.getAvailableUsers(new AsyncCallback<ArrayList<PortalUserDTO>>() {			
			@Override
			public void onSuccess(ArrayList<PortalUserDTO> docs) {
				display.maskCenterPanel("", false);
				display.setData(docs);				
			}			
			@Override
			public void onFailure(Throwable arg0) {	
				display.maskCenterPanel("", false);
				com.google.gwt.user.client.Window.alert("Failed to get users list from service " + arg0.getMessage());				
			}
		});
	}

	private void doOpenInviteWidget() {
		display.displayInviteUsersPanel();
	}

	private void doApprove(final List<PortalUserDTO> selectedUsers) {
		if (selectedUsers != null) {
			String users = "";
			for (int i = 0; i < selectedUsers.size(); i++) {
				PortalUserDTO user = selectedUsers.get(i);
				users += user.getLastName() + ", "; 

			}
			MessageBox.confirm("Please Confirm", "You are about to register: " +users+ "?", new Listener<MessageBoxEvent>() {  
				public void handleEvent(MessageBoxEvent ce) {  
					if (ce.getButtonClicked().getText().equals("Yes")) {
						if (selectedUsers.size() == 1)
							display.maskCenterPanel("Registering " + selectedUsers.get(0).getLastName() +", please wait ... ", true);
						else 
							display.maskCenterPanel("Registering selected users, please wait it may take a while ...", true);
						rpcService.registerUsers(selectedUsers, new AsyncCallback<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {
								MessageBox.info("Registering Operation", "User(s) were registrated successfully! ", null);  
								fetchAvailableUsers();					
							}

							@Override
							public void onFailure(Throwable caught) {
								com.google.gwt.user.client.Window.alert("Failed to register users:  " + caught.getMessage());					
							}		
						});
					}

				}  
			});			
		}
	}  
}

