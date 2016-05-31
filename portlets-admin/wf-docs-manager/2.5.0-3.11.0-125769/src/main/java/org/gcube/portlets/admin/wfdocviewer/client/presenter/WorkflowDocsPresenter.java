package org.gcube.portlets.admin.wfdocviewer.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.wfdocslibrary.shared.PermissionType;
import org.gcube.portlets.admin.wfdocslibrary.shared.Step;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.admin.wfdocviewer.client.WorkflowDocServiceAsync;
import org.gcube.portlets.admin.wfdocviewer.client.event.SelectedReportEvent;
import org.gcube.portlets.admin.wfdocviewer.client.view.dialog.AddRolesDialog;
import org.gcube.portlets.admin.wfdocviewer.client.view.dialog.ShowUserActionsDialog;
import org.gcube.portlets.admin.wfdocviewer.shared.ActionLogBean;
import org.gcube.portlets.admin.wfdocviewer.shared.RoleStep;
import org.gcube.portlets.admin.wfdocviewer.shared.UserBean;
import org.gcube.portlets.admin.wfdocviewer.shared.WfDocumentBean;
import org.gcube.portlets.admin.wfdocviewer.shared.WfTemplateBean;
import org.gcube.portlets.widgets.lighttree.client.Item;
import org.gcube.portlets.widgets.lighttree.client.event.PopupEvent;
import org.gcube.portlets.widgets.lighttree.client.event.PopupHandler;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 *
 */
public class WorkflowDocsPresenter implements Presenter {
	
	private final static boolean testing = false;

	private ArrayList<WfRoleDetails> rolesCache = null;
	private final WorkflowDocServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;
	private WfGraph displayingWorkflow;
	private String selectedReportID;
	private String selectedReportName;
	private String selecteTemplateName;



	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param view
	 */
	public WorkflowDocsPresenter(WorkflowDocServiceAsync rpcService, HandlerManager eventBus, Display view) {
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = view;
	}


	public void bind() {
		///*** GRID
		display.getGridSelectionModel().addSelectionChangedListener(new SelectionChangedListener<WfDocumentBean>() {			
			public void selectionChanged(SelectionChangedEvent<WfDocumentBean> event) {
				if (event.getSelectedItem() != null)
					enableActionButtons(true);
			}
		});
		display.getDeleteButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				WfDocumentBean selected = display.getGridSelectionModel().getSelectedItem();
				if (Window.confirm("You are about to delete " + selected.getName() +  " workflow document, please confirm")) {
					doDeleteWfReport(selected);
				}
			}			
		}); 
		display.getDetailsButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				WfDocumentBean  selected = display.getGridSelectionModel().getSelectedItem();
				doLoadWfReport(selected.getId(), selected.getStatus());
			}  
		});  

		display.getAddnewButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				if (!testing)
					display.showAddnewWfDocPanel();
				else {
					selectedReportID = "1111";
					selectedReportName = "PIPPO";
					eventBus.fireEvent(new SelectedReportEvent(selectedReportID, selectedReportName));
				}
			}  
		});  

		display.getNextButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				Step missingStep = checkAllPermissions();
				if ( missingStep == null)
					doLoadRolesAndUsers();
				else
					MessageBox.alert("No permission defined for step: " + missingStep.getLabel(), "You must define at least one permission for each step", null);  
			}
		});  

		display.getActionsLogButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				WfDocumentBean  selected = display.getGridSelectionModel().getSelectedItem();
				doLoadActionsLog(selected.getId());

			}

		});  

		display.getCreateNewWfReportButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				doCommitWfReport();
			}			
		});  

		display.getWSTreepopup().addPopupHandler(new PopupHandler() {
			public void onPopup(PopupEvent event) {
				if (! event.isCanceled()) {
					Item selectedReport = event.getSelectedItem();	
					selectedReportID = selectedReport.getId();
					selectedReportName = selectedReport.getName();
					eventBus.fireEvent(new SelectedReportEvent(selectedReportID, selectedReportName));
				}
			}
		});

		display.getSelectWfTemplateCombo().addSelectionChangedListener(new SelectionChangedListener<WfTemplateBean>() {			
			public void selectionChanged(SelectionChangedEvent<WfTemplateBean> se) {
				selecteTemplateName = se.getSelectedItem().getName();
				doLoadWfTemplateToInstanciate(se.getSelectedItem().getId());
			}
		});
		//checks on table columns
		display.getViewCheckColumn().setFieldUpdater(new FieldUpdater<RoleStep, Boolean>() {
			@Override
			public void update(int index, RoleStep rs, Boolean value) {
				updateStep(rs.getStep(), rs.getRole(), value, PermissionType.VIEW);
			}
		});

		display.getUpdateCheckColumn().setFieldUpdater(new FieldUpdater<RoleStep, Boolean>() {
			public void update(int index, RoleStep rs, Boolean value) {
				updateStep(rs.getStep(), rs.getRole(), value, PermissionType.UPDATE);
			}
		});

		display.getDeleteCheckColumn().setFieldUpdater(new FieldUpdater<RoleStep, Boolean>() {
			public void update(int index, RoleStep rs, Boolean value) {
				updateStep(rs.getStep(), rs.getRole(), value, PermissionType.DELETE);
			}
		});

		display.getEditPermissionCheckColumn().setFieldUpdater(new FieldUpdater<RoleStep, Boolean>() {
			public void update(int index, RoleStep rs, Boolean value) {
				updateStep(rs.getStep(), rs.getRole(), value, PermissionType.EDIT_PERMISSIONS);
			}
		});

		display.getAddCommentsCheckColumn().setFieldUpdater(new FieldUpdater<RoleStep, Boolean>() {
			public void update(int index, RoleStep rs, Boolean value) {
				updateStep(rs.getStep(), rs.getRole(), value, PermissionType.ADD_DISCUSSION);
			}
		});

		display.getDeleteCommentsCheckColumn().setFieldUpdater(new FieldUpdater<RoleStep, Boolean>() {
			public void update(int index, RoleStep rs, Boolean value) {
				updateStep(rs.getStep(), rs.getRole(), value, PermissionType.DELETE_DISCUSSION);
			}
		});

		display.getUpdateCommentsCheckColumn().setFieldUpdater(new FieldUpdater<RoleStep, Boolean>() {
			public void update(int index, RoleStep rs, Boolean value) {
				updateStep(rs.getStep(), rs.getRole(), value, PermissionType.UPDATE_DISCUSSION);
			}
		});

	}

	@Override
	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
		fetchWfDocumentsDetails();
		enableActionButtons(false);
	}

	/**
	 * 
	 */
	private void fetchWfDocumentsDetails() {
		display.maskWestPanel("Loading Workflows Documents", true);
		rpcService.getAllWfDocuments(new AsyncCallback<ArrayList<WfDocumentBean>>() {			
			@Override
			public void onSuccess(ArrayList<WfDocumentBean> docs) {
				display.maskWestPanel("", false);
				display.setData(docs);				
			}			
			@Override
			public void onFailure(Throwable arg0) {	
				display.maskWestPanel("", false);
				Window.alert("Failed to get WfDocs from HomeLibrary " + arg0.getMessage());				
			}
		});


	}

	@Override
	public void doInstanciateNewWorkflow(final String reportid, final String reportName) {
		display.maskCenterPanel("Retrieving workflow templates, please wait ... ", true);
		rpcService.getAllTemplates(new AsyncCallback<ArrayList<WfTemplateBean>>() {
			@Override
			public void onSuccess(ArrayList<WfTemplateBean> templates) {
				display.maskCenterPanel("", false);
				display.showInstanciateNewWorkflowPanel(reportid, reportName, templates);
			}
			@Override
			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to get Wf Templates from server " + arg0.getMessage());	
			}		
		});

	}
	private void doLoadWfReport(String workflowid, final String status) {
		display.maskCenterPanel("Retrieving selected workflow report details, please wait ... ", true);
		rpcService.getWfReport(workflowid, new AsyncCallback<WfTemplate>() {
			@Override
			public void onSuccess(WfTemplate template) {
				//save the current workflow to display
				displayingWorkflow = template.getGraph();
				display.maskCenterPanel("", false);
				display.showWfReportDetails(template, status);

			}
			@Override
			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to get Wf Report from server " + arg0.getMessage());	
			}			
		});
	}

	/**
	 * load a workflow template from the server
	 * @param id
	 */
	private void doLoadWfTemplateToInstanciate(String id) {
		display.maskCenterPanel("Retrieving selected workflow template, please wait ... ", true);
		rpcService.getWfTemplate(id, new AsyncCallback<WfTemplate>() {
			@Override
			public void onSuccess(WfTemplate template) {
				//save the current workflow to display
				displayingWorkflow = template.getGraph();
				display.maskCenterPanel("", false);
				ClickHandler chandler = new ClickHandler() {
					@Override
					public void onClick(ClickEvent arg0) {
						showAddRoleDialog();
					}
				};
				display.showWfTemplateToInstanciate(template, chandler);

			}
			@Override
			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to get Wf Template from server " + arg0.getMessage());	
			}			
		});
	}

	/**
	 * 
	 */
	private void showAddRoleDialog() {		
		if (rolesCache == null) {		
			rpcService.getRoleDetails(new AsyncCallback<ArrayList<WfRoleDetails>>() {
				@Override
				public void onSuccess(ArrayList<WfRoleDetails> roles) {
					AddRolesDialog dlg = new AddRolesDialog(eventBus, roles);
					dlg.show();
					rolesCache = roles;
				}
				@Override
				public void onFailure(Throwable arg0) {
					Info.display("Error on Server", "possible cause: "+arg0.getMessage());
				}
			});
		}
		else {
			AddRolesDialog dlg = new AddRolesDialog(eventBus, rolesCache);
			dlg.show();
		}
	}

	@Override
	public void doAddRolesToSelectedStep(ArrayList<WfRoleDetails> roles) {
		ArrayList<WfRole> rolesFull = new ArrayList<WfRole>();
		for (WfRoleDetails r : roles) {
			rolesFull.add(new WfRole(r.getId(), r.getDisplayName(), ""));
		}
		display.addRoleToPermissionTable(rolesFull);		
	}
	/**
	 * update the permission in the model
	 * @param toUpdate
	 * @param roleLabel
	 * @param value
	 * @param type
	 */
	private void updateStep(Step currStep, WfRole role, boolean value, PermissionType type) {
		Step[] allSteps = displayingWorkflow.getSteps();

		for (int i = 0; i < allSteps.length; i++) {
			if (allSteps[i].getLabel().compareTo(currStep.getLabel()) == 0) {
				currStep = allSteps[i];
			}
		}
		//get the permission map for the given step (toUpdate)
		Map<WfRole, ArrayList<PermissionType>> ps = currStep.getPermissions();
		//if null create new one
		if (ps == null)
			ps = new HashMap<WfRole, ArrayList<PermissionType>>();
		//if the role exists yet
		if (ps.containsKey(role)) {
			ArrayList<PermissionType> toUpdate = ps.get(role);
			//can be null if no permission are set
			if (toUpdate == null)
				toUpdate = new ArrayList<PermissionType>();
			if (toUpdate.contains(type)) 
				toUpdate.remove(type);
			if (value) //add an entry only if true
				toUpdate.add(type);				 
		}
		else { //need to create a new entry in the map
			if (value) { //add an entry only if true
				ArrayList<PermissionType> toCreate = new ArrayList<PermissionType>();
				toCreate.add(type);			
				ps.put(role, toCreate);
			}
		}
		currStep.setPermissions(ps);
		//		if (value)
		//			GWT.log("Step: " + currStep.getLabel() +  " role:" + role.getRolename() + " type: " + type);	
	}
	/**
	 * 
	 */
	private void doLoadRolesAndUsers() {
		display.maskCenterPanel("Loading users from system, pleas wait ... ", true);
		rpcService.getVREUsers(new AsyncCallback<ArrayList<UserBean>>() {

			@Override
			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);

			}

			@Override
			public void onSuccess(ArrayList<UserBean> users) {
				display.maskCenterPanel("", false);
				display.showAssignRolesToUsersPanel(getAllRolesFromTemplate(), users);
			}
		});

	}  
	/**
	 * helper method that retrieves all the roles definied in a workflow report
	 * @return
	 */
	private ArrayList<WfRole> getAllRolesFromTemplate() {
		ArrayList<WfRole> toRet = new ArrayList<WfRole>();
		Step[] steps = displayingWorkflow.getSteps();
		for (int i = 0; i < steps.length; i++) {
			Step curStep = steps[i];
			if (curStep.getPermissions() != null) {
				GWT.log("step : " + curStep.getLabel());
				for (WfRole role : curStep.getPermissions().keySet()) {
					boolean found = false;
					for (WfRole retRole : toRet) {
						if (retRole.getRoleid().equals(role.getRoleid())) {
							found = true;
							break;
						}						
					}			
					if (! found) {
						toRet.add(role);
					}
				}
			}
		}
		return toRet;
	}
	/**
	 * helper method that checks if user defined at least one permission per step
	 * @return
	 */
	private Step checkAllPermissions() {
		Step[] steps = displayingWorkflow.getSteps();
		for (int i = 0; i < steps.length; i++) {
			Step curStep = steps[i];
			if (curStep.getPermissions() == null) {
				return curStep;
			}
		}
		return null;
	}
	/**
	 * saves the wfReport and the roles
	 */
	private void doCommitWfReport() {
		HashMap<String, ListView<UserBean>> rolesWithUsers =  display.getUsersAndRoles();

		//gets the list of assigned users for each role
		HashMap<String, List<UserBean>> rolesWithUsersForService = new HashMap<String, List<UserBean>>();
		for (String roleName : rolesWithUsers.keySet()) {
			ListView<UserBean> list = rolesWithUsers.get(roleName);
			list.getSelectionModel().selectAll();
			if (list.getSelectionModel().getSelectedItems().size() == 0) {
				Window.alert("You must select at least a user for each role");
				return;
			}
			rolesWithUsersForService.put(roleName, list.getSelectionModel().getSelectedItems());
		}
		display.maskCenterPanel("Saving Workflow, please wait ... ", true);
		String reportName = selectedReportName;
		if (reportName.contains(".d4sR"))
			reportName = selectedReportName.substring(0, selectedReportName.length()-5);
		final String wfReportName = reportName + "-" + selecteTemplateName;
		rpcService.saveWorkflow(selectedReportID, wfReportName, displayingWorkflow, rolesWithUsersForService, new AsyncCallback<Boolean>() {
			public void onSuccess(Boolean result) {
				display.maskCenterPanel("", false);
				if (result) {
					Info.display("Saving Successful", wfReportName + " was correctly saved in the System");
					fetchWfDocumentsDetails();
				}
				else
					Info.display("Error on Server", "Ops! something went wrong, please try again");
			}

			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);
				Info.display("Error on Server", "possible cause: "+arg0.getMessage());
			}
		});
	}


	private void doLoadActionsLog(String workflowid) {
		display.maskCenterPanel("Fetching actions log, please wait ... ", true);
		rpcService.fetchActionsByWorkflowId(workflowid, new AsyncCallback<ArrayList<ActionLogBean>>() {

			@Override
			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);
				Info.display("Error on Server", "possible cause: "+arg0.getMessage());				
			}

			@Override
			public void onSuccess(ArrayList<ActionLogBean> actions) {
				display.maskCenterPanel("", false);
				GWT.log("logs: " + actions.size());
				ShowUserActionsDialog dlg = new ShowUserActionsDialog(actions);
				dlg.show();
			}
		});
	}
	/**
	 * 
	 * @param enable
	 */
	private void enableActionButtons(boolean enable) {
		display.getDetailsButton().setEnabled(enable);
		display.getActionsLogButton().setEnabled(enable);
		display.getDeleteButton().setEnabled(enable);

	}
	/**
	 * @param id
	 */
	private void doDeleteWfReport(final WfDocumentBean docBean) {
		display.maskCenterPanel("Deleting workflow documente, please wait ... ", true);
		rpcService.deleteWorkflowDocument(docBean, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable arg0) {
				display.maskCenterPanel("", false);
				Info.display("Error on Server", "possible cause: "+arg0.getMessage());						
			}

			@Override
			public void onSuccess(Boolean arg0) {
				display.maskCenterPanel("", false);
				Info.display("Delete Successful", docBean.getName() + " was correctly saved in the System");
				fetchWfDocumentsDetails();
			}
		});
	}  
}
