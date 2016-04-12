package org.gcube.portlets.user.workflowdocuments.client.presenter;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.UserComment;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.user.workflowdocuments.client.WfDocumentsLibraryServiceAsync;
import org.gcube.portlets.user.workflowdocuments.client.view.Display;
import org.gcube.portlets.user.workflowdocuments.client.view.dialog.AddCommentDialog;
import org.gcube.portlets.user.workflowdocuments.client.view.dialog.ForwardWorkflowDialog;
import org.gcube.portlets.user.workflowdocuments.client.view.dialog.ViewCommentsDialog;
import org.gcube.portlets.user.workflowdocuments.shared.LockInfo;
import org.gcube.portlets.user.workflowdocuments.shared.WorkflowDocument;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class WorkflowDocumentsPresenter implements Presenter {
	private final WfDocumentsLibraryServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;
	String location = null;

	public WorkflowDocumentsPresenter(WfDocumentsLibraryServiceAsync rpcService, HandlerManager eventBus, Display display) {
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = display;
	}
	public void bind() {
		///*** BUTTONS
		display.getViewButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				String documentName = display.getGridSelectionModel().getSelectedItem().getName();
				doOpenDocument(documentName, display.getGridSelectionModel().getSelectedItem().getId(), true);
			}  
		}); 
		display.getEditButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				String documentName = display.getGridSelectionModel().getSelectedItem().getName();
				doOpenDocument(documentName, display.getGridSelectionModel().getSelectedItem().getId(), false);
			}  
		});  
		display.getForwardButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				fetchWorkflowGraph(display.getGridSelectionModel().getSelectedItem());
			}  
		});  
		display.getAddCommentsButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				AddCommentDialog dlg = new AddCommentDialog(eventBus, 
						display.getGridSelectionModel().getSelectedItem().getName(), 
						display.getGridSelectionModel().getSelectedItem().getId());
				dlg.show();
			}  
		});  

		display.getViewCommentsButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				fetchUserComments(display.getGridSelectionModel().getSelectedItem());
			}  
		});  
		display.getRefreshButton().addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				Window.Location.reload();
			}  
		}); 

		///*** GRID
		display.getGridSelectionModel().addSelectionChangedListener(new SelectionChangedListener<WorkflowDocument>() {			
			public void selectionChanged(SelectionChangedEvent<WorkflowDocument> event) {
				if (event.getSelectedItem() != null)
					enableActionButtons(event.getSelectedItem());
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
		fetchWfUserDocuments();
	}
	/**
	 * fetch all the workflow documents belongin to this user
	 */
	private void fetchWfUserDocuments() {
		display.maskCenterPanel("Loading Your Workflows Documents, please wait", true);
		rpcService.getUserWfDocuments(new AsyncCallback<ArrayList<WorkflowDocument>>() {			
			@Override
			public void onSuccess(ArrayList<WorkflowDocument> docs) {
				display.maskCenterPanel("", false);
				display.setData(docs);				
			}			
			@Override
			public void onFailure(Throwable arg0) {	
				display.maskCenterPanel("", false);
				Window.alert("Failed to get Documents from Library " + arg0.getMessage());				
			}
		});
	}
	/**
	 * 
	 * @param workflowid
	 */
	private void fetchWorkflowGraph(final WorkflowDocument wfDoc) {
		display.maskCenterPanel("Fetching Workflow details, please wait", true);
		rpcService.getWorkflowById(wfDoc.getId(), new AsyncCallback<WfTemplate>() {
			public void onFailure(Throwable caught) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to fetch Workflow details: " + caught.getMessage());	
			}
			@Override
			public void onSuccess(WfTemplate result) {
				display.maskCenterPanel("", false);
				ForwardWorkflowDialog dlg = new ForwardWorkflowDialog(eventBus, wfDoc, result);
				dlg.show();
			}
		});
	}
	/**
	 * 
	 * @param wfDoc the workflow document
	 */
	private void fetchUserComments(final WorkflowDocument wfDoc) {
		display.maskCenterPanel("Loading User Comments, please wait", true);
		rpcService.getUserComments(wfDoc.getId(), new AsyncCallback<ArrayList<UserComment>>() {
			@Override
			public void onFailure(Throwable caught) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to fetch comments: " + caught.getMessage());			
			}

			@Override
			public void onSuccess(ArrayList<UserComment> comments) {
				display.maskCenterPanel("", false);
				if (comments != null) {
					ViewCommentsDialog dlg = new ViewCommentsDialog(wfDoc, comments);
					dlg.show();
				}
				else
					Info.display("Fetching Comments ERROR", "User comments were not retrieved");	
			}
		});
	}
	/**
	 * 
	 * @param wfDoc the workflow document
	 */
	private void enableActionButtons(WorkflowDocument wfDoc) {
		display.getViewButton().setEnabled(wfDoc.hasView());
		display.getEditButton().setEnabled(wfDoc.hasUpdate());
		display.getForwardButton().setEnabled(true);
		display.getViewCommentsButton().setEnabled(wfDoc.hasComments());
		display.getAddCommentsButton().setEnabled(wfDoc.hasAddComments());
		
		if (wfDoc.hasUpdate() && wfDoc.isLocked() && !wfDoc.isOwner()) {			
			display.getEditButton().setEnabled(false);
			display.getEditButton().setToolTip("This Document is Locked");
		}
	}
	/**
	 * add the comment in the database
	 */
	@Override
	public void doAddComment(String comment, String workflowid) {
		display.maskCenterPanel("Adding Your Comment, please wait", true);
		rpcService.addUserComment(workflowid, comment, new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to add comment: " + caught.getMessage());					
			}
			public void onSuccess(Boolean result) {
				display.maskCenterPanel("", false);
				MessageBox mb = MessageBox.info("Operation completed", "Your comment was succesfully added", null);	
				mb.show();
				fetchWfUserDocuments();
			}
		});
	}
	@Override
	public void doForward(WorkflowDocument wfDoc, String toStepLabel) {
		display.maskCenterPanel("Communicating forward to the server, please wait", true);
		rpcService.forward(wfDoc, toStepLabel, new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to do forward: " + caught.getMessage());			
			}
			@Override
			public void onSuccess(Boolean result) {		
				display.maskCenterPanel("", false);
				MessageBox mb = null;
				if (result) { //all users have forwarded
					mb = MessageBox.info("Forward operation completed", "The workflow report has been advanced to the next step", null);	
					mb.show();
					fetchWfUserDocuments();
				}
				else {
					mb = MessageBox.info("Forward operation result", "You forward operation was successful", null);	
					mb.show();
				}			
			}			
		});		
	}
	/**
	 * 
	 * @param workflowid
	 * @param readonly
	 */
	void doOpenDocument(String documentName, String workflowid, boolean readonly) {
		display.maskCenterPanel("Loading report application, please wait", true);
		rpcService.setWorkflowInSession(documentName, workflowid, readonly, new AsyncCallback<LockInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				display.maskCenterPanel("", false);
				Window.alert("Failed to do forward: " + caught.getMessage());							
			}

			@Override
			public void onSuccess(LockInfo lock) {
				if (lock.isLocked()) {
					display.maskCenterPanel("", false);
					MessageBox.alert("Error", "Sorry, user: " + lock.getLockedby() + " has locked the document in the meantime. Please refresh", null);
				}
				else
					loadReportApp();
			}
		});
	}
	
	/**
	 * Redirect to VRE Deployer Portlet
	 */
	private void loadReportApp(){
		getUrl();
		if (location.contains("?"))
			location = location.substring(0, location.indexOf("?"));
		location += "/../report-generation";
		Window.open(location, "_self", "");		
	}
	/**
	 * Get URL from browser
	 */
	public native void getUrl()/*-{
			this.@org.gcube.portlets.user.workflowdocuments.client.presenter.WorkflowDocumentsPresenter::location = $wnd.location.href;
	}-*/;
}
