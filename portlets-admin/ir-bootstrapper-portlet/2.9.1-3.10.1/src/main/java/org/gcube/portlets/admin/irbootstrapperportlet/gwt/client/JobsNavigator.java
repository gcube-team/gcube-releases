/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElements.UserInputsWindow;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ResourceTypeUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ResourceUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionEntityUIElement.UIExecutionState;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobStatusChangeListener;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobViewMode;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobsNavigatorListener;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.AccordionLayout;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class JobsNavigator extends Composite implements JobStatusChangeListener {

	/** A reference to the main portlet object */
	private IRBootstrapperPortletG portlet;
	
	/** The tree that contains the available jobs per resource */
	private TreePanel availableJobsTree;
	
	/** The tree that contains the submitted jobs per resource */
	private TreePanel submittedJobsTree;
	
	/** The registered listener for events related to this jobs navigator object */
	private JobsNavigatorListener listener;
	
	/** The "start job" button */
	private ToolbarButton startJobButton;
	
	/** The "start job" button */
	private ToolbarButton submitJobsButton;
	
	/** The "stop submitted job" button */
	private ToolbarButton cancelJobButton;
	
	/** The "remove stopped job" button */
	private ToolbarButton removeJobButton;
	
	/** The UI element that represents the currently selected job in the tree */
	private JobUIElement selectedJob;
	
	/** A list that keeps the currently selected jobs in the tree based on the status of the CheckBox */
	private ArrayList<JobUIElement> selectedJobs = new ArrayList<JobUIElement>();
	
	/** The selected node in the "submitted jobs" tree */
	private TreeNode selectedNodeInSubmittedJobsTree;
	
	
	/**
	 * Class constructor
	 */
	public JobsNavigator(IRBootstrapperPortletG portlet) {
		this.listener = null;
		this.portlet = portlet;
		this.selectedJob = null;
		this.selectedNodeInSubmittedJobsTree = null;
		
		Panel accordionPanel = new Panel();  
		accordionPanel.setLayout(new AccordionLayout(true));  
		Panel panelOne = new Panel("Available Jobs");
		panelOne.setAutoScroll(true);
		accordionPanel.add(panelOne);
		Panel panelTwo = new Panel("Current Submitted Jobs");  
		accordionPanel.add(panelTwo);  
		accordionPanel.setHeight(450);
		
		availableJobsTree = new TreePanel();
		availableJobsTree.setRootNode(new TreeNode("Resource Types"));
		availableJobsTree.setTopToolbar(createAvailableJobsToolbar());
		panelOne.add(availableJobsTree);
		
		submittedJobsTree = new TreePanel();
		submittedJobsTree.setRootNode(new TreeNode("Resource Types"));
		submittedJobsTree.setTopToolbar(createSubmittedJobsToolbar());
		panelTwo.add(submittedJobsTree);
		
		initWidget(accordionPanel);
		
		
		availableJobsTree.addListener(new TreePanelListenerAdapter() {
			public void onClick(TreeNode node, EventObject e) {
				if (node.isSelected() && (node.getUserObject() instanceof JobUIElement)) {
					selectedJob = (JobUIElement) node.getUserObject();
					startJobButton.setIconCls("start-icon");
					startJobButton.setDisabled(false);
					listener.onJobSelected((JobUIElement) node.getUserObject(), JobViewMode.MODE_AVAILABLE);
				}
				else {
					/* A non-job node has been selected */
					selectedJob = null;
					startJobButton.setIconCls("start-disabled-icon");
					startJobButton.setDisabled(true);
					listener.onJobSelected(null, JobViewMode.MODE_AVAILABLE);
				}
			}
		});
		
		
		submittedJobsTree.addListener(new TreePanelListenerAdapter() {
			public void onClick(TreeNode node, EventObject e) {
				submittedJobsTreeNodeSelected(node);
			}
		});
	}
	
	/**
	 * Invoked when the user selects a node of the submitted jobs tree, by clicking on it.
	 * @param node the selected submitted jobs tree node
	 */
	private void submittedJobsTreeNodeSelected(final TreeNode node) {
		
		/* If a job was not selected, invoke the registered listener with a null parameter.
		 * If a job was selected, retrieve its most current snapshot through the
		 * servlet and invoke the listener with this snapshot.
		 */
		final Object assocObject = node.getUserObject();
		if (assocObject instanceof JobUIElement) {
			selectedJob = (JobUIElement) node.getUserObject();
			selectedNodeInSubmittedJobsTree = node;
			AsyncCallback<JobUIElement> getJobCallback = new AsyncCallback<JobUIElement>() {

				public void onFailure(Throwable arg0) {
					Window.alert("Error while retrieving job status." + arg0);
					portlet.hideLoadMask();
					if (listener != null)
						listener.onJobSelected(null, JobViewMode.MODE_SUBMITTED);
				}

				public void onSuccess(JobUIElement arg0) {
					portlet.hideLoadMask();
					if (listener != null)
						listener.onJobSelected(arg0, JobViewMode.MODE_SUBMITTED);
					enableOrDisableSubmittedJobsToolbarButtons(arg0);
					setSubmittedTreeNodeIcon(selectedNodeInSubmittedJobsTree);
				}
			};
			
			portlet.showLoadMask();
			IRBootstrapperPortletG.bootstrapperService.getSubmittedJobByUID(
					((JobUIElement) assocObject).getUID(), getJobCallback);
		}
		else {
			selectedJob = null;
			selectedNodeInSubmittedJobsTree = null;
			enableOrDisableSubmittedJobsToolbarButtons(null);
			if (listener != null)
				listener.onJobSelected(null, JobViewMode.MODE_SUBMITTED);
		}
	}
	
	/**
	 * Creates the toolbar of the "available jobs" tree
	 * @return
	 */
	private Toolbar createAvailableJobsToolbar() {
		Toolbar tbAvailable = new Toolbar();
		ToolbarButton btnRefresh = new ToolbarButton();
		btnRefresh.setIconCls("refresh-icon");
		btnRefresh.setCls("x-btn-icon");
		btnRefresh.setTooltip("<b>Refresh</b><br/>Refreshes the list of available jobs per resource");
		btnRefresh.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				refreshAvailableJobsTree();
			}
		});
		tbAvailable.addButton(btnRefresh);
		
		startJobButton = new ToolbarButton();
		startJobButton.setIconCls("start-disabled-icon");
		startJobButton.setCls("x-btn-icon");
		startJobButton.setTooltip("<b>Start</b><br/>Submits the selected job for execution");
		startJobButton.setDisabled(true);
		startJobButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				
				/* The callback object */
				AsyncCallback<String> callback = new AsyncCallback<String>() {

					public void onFailure(Throwable arg0) {
						Window.alert("Error while submitting selected job for execution. The job was not found.");
					}

					public void onSuccess(String arg0) {
						MessageBox.alert("The selected job has been submitted for execution. You can monitor its status through the 'Submitted Jobs' view, which can be enabled in the lower-left corner of the window.");
						refreshSubmittedJobsTree();
					}
				};
				IRBootstrapperPortletG.bootstrapperService.submitJobForExecution(selectedJob.getUID(),null, callback);
			}
		});
		tbAvailable.addButton(startJobButton);
		
		/**
		 * This is the button to submit multiple jobs at once
		 */
		submitJobsButton = new ToolbarButton();
		submitJobsButton.setIconCls("start-many-disabled-icon");
		submitJobsButton.setCls("x-btn-icon");
		submitJobsButton.setTooltip("<b>Start</b><br/>Submits the selected jobs for batch execution");
		submitJobsButton.setDisabled(true);
		submitJobsButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				ArrayList<JobUIElement> jobs = getSelectedJobs();
				if (validateParallelSubmission(jobs)) {
					UserInputsWindow inputsWindow = new UserInputsWindow(jobs);
					inputsWindow.show();
				}
				else
					MessageBox.alert("Only Jobs of the same type can be submitted for batch execution");
			}
		});
		tbAvailable.addButton(submitJobsButton);
		/****
		 * 
		 */
		
		return tbAvailable;
	}
	
	/**
	 * Creates the toolbar of the "submitted jobs" tree
	 * @return
	 */
	private Toolbar createSubmittedJobsToolbar() {
		Toolbar tbSubmitted = new Toolbar();
		ToolbarButton btnRefresh = new ToolbarButton();  
		btnRefresh.setIconCls("refresh-icon"); 
		btnRefresh.setCls("x-btn-icon");  
		btnRefresh.setTooltip("<b>Refresh</b><br/>Refreshes the list of submitted jobs");
		btnRefresh.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				refreshSubmittedJobsTree();
			}
		});
		tbSubmitted.addButton(btnRefresh);

		cancelJobButton = new ToolbarButton();
		cancelJobButton.setIconCls("stop-disabled-icon");
		cancelJobButton.setCls("x-btn-icon");
		cancelJobButton.setTooltip("<b>Cancel</b><br/>Cancels the selected running job");
		cancelJobButton.setDisabled(true);
		cancelJobButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {

				/* The callback object */
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {

					public void onFailure(Throwable arg0) {
						Window.alert("Error while cancelling the selected job.");
					}

					public void onSuccess(Void arg0) {
						submittedJobsTreeNodeSelected(selectedNodeInSubmittedJobsTree);
					}
				};
				IRBootstrapperPortletG.bootstrapperService.cancelSubmittedJob(selectedJob.getUID(), callback);
			}
		});
		tbSubmitted.addButton(cancelJobButton);
		
		removeJobButton = new ToolbarButton();
		removeJobButton.setIconCls("remove-disabled-icon");
		removeJobButton.setCls("x-btn-icon");
		removeJobButton.setTooltip("<b>Remove</b><br/>Removes the selected stopped job");
		removeJobButton.setDisabled(true);
		removeJobButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				
				if (listener != null)
					listener.onJobSelected(null, JobViewMode.MODE_SUBMITTED);
				
				/* The callback object */
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {

					public void onFailure(Throwable arg0) {
						Window.alert("Error while removing the selected job.");
					}

					public void onSuccess(Void arg0) {
						refreshSubmittedJobsTree();
					}
				};
				IRBootstrapperPortletG.bootstrapperService.removeSubmittedJob(selectedJob.getUID(), callback);
			}
		});
		tbSubmitted.addButton(removeJobButton);
		
		return tbSubmitted;
	}
	
	/**
	 * Enables or disables the various buttons in the submitted jobs view toolbar, depending on the
	 * current status of the currently selected job
	 * @param selectedJob the selected job
	 */
	private void enableOrDisableSubmittedJobsToolbarButtons(JobUIElement selectedJob) {
		boolean bDisableStop = false;
		boolean bDisableRemove = false;
		
		/* If no job is selected, disable both the "stop" and "remove" buttons. If a running job
		 * was selected, disable only the "remove" button. If a completed job was selected, disable
		 * only the "stop" button. */
		if (selectedJob == null) {
			bDisableStop = true;
			bDisableRemove = true;
		}
		else {
			UIExecutionState execState = selectedJob.getExecutionState();
			if (execState!=UIExecutionState.NOT_STARTED && execState!= UIExecutionState.RUNNING) {
				bDisableStop = true;
				bDisableRemove = false;
			}
			else {
				bDisableStop = false;
				bDisableRemove = true;
			}
		}
		
		/* Enable or disable the stop button */
		if (bDisableStop) {
			cancelJobButton.setIconCls("stop-disabled-icon");
			cancelJobButton.setDisabled(true);
		}
		else {
			cancelJobButton.setIconCls("stop-icon");
			cancelJobButton.setDisabled(false);			
		}

		/* Enable or disable the remove button */
		if (bDisableRemove) {
			removeJobButton.setIconCls("remove-disabled-icon");
			removeJobButton.setDisabled(true);
		}
		else {
			removeJobButton.setIconCls("remove-icon");
			removeJobButton.setDisabled(false);			
		}
	}
	
	/**
	 * Sets the icon of the given node in the "submitted jobs" tree,
	 * depending on the state of the corresponding job.
	 */
	private void setSubmittedTreeNodeIcon(TreeNode node) {
		if (node!=null) {
			Object o = node.getUserObject();
			if (o!=null && (o instanceof JobUIElement)) {
				String iconCls = null;
				JobUIElement jobElement = (JobUIElement) o;
				UIExecutionState execState = jobElement.getExecutionState();
				if (execState == UIExecutionState.RUNNING)
					iconCls = "running-icon";
				else if (execState == UIExecutionState.COMPLETED_SUCCESS)
					iconCls = "success-icon";
				else if (execState == UIExecutionState.COMPLETED_FAILURE)
					iconCls = "error-icon";
				else if (execState == UIExecutionState.COMPLETED_WARNINGS)
					iconCls = "warning-icon";
				else if (execState == UIExecutionState.CANCELLED)
					iconCls = "cancel-icon";
				
				if (iconCls != null) {
					node.setIconCls(iconCls);
				}
			}
		}
	}
	
	/**
	 * Initializes the tree structure with data from the current scope
	 */
	public void initialize() {		
		refreshAvailableJobsTree();
		refreshSubmittedJobsTree();
	}
	
	/**
	 * Refreshes the tree of available jobs
	 */
	private void refreshAvailableJobsTree() {
		this.selectedJob = null;
        if (listener != null)
        	listener.onJobSelected(null, JobViewMode.MODE_AVAILABLE);
        startJobButton.setIconCls("start-disabled-icon");
		startJobButton.setDisabled(true);
		portlet.showLoadMask();
        
		/* The callback object for invoking the 'getJobs' servlet method */
		AsyncCallback<List<ResourceTypeUIElement>> callback = new AsyncCallback<List<ResourceTypeUIElement>>() {
            /**
             * {@inheritDoc}
             */
            public void onSuccess(List<ResourceTypeUIElement> result) {
            	removeAllRootNodeChildrenInTree(availableJobsTree);
            	TreeNode root = availableJobsTree.getRootNode();

            	/* Add the returned resource types as nodes to the tree */
            	for (ResourceTypeUIElement resType : result) {
            		TreeNode resTypeNode = new TreeNode(resType.getName());
            		resTypeNode.setUserObject(resType);
            		root.appendChild(resTypeNode);
            		
            		/* For each resource type, add the returned resources as
            		 * its sub-nodes in the tree.
            		 */
            		for (ResourceUIElement resource : resType.getResources()) {
            			TreeNode resNode = new TreeNode(resource.getName());
            			resNode.setUserObject(resource);
            			resTypeNode.appendChild(resNode);
            			
            			/* For resource, add the jobs taking it as an input as
            			 * its sub-nodes in the tree.
            			 */
            			for (JobUIElement job : resource.getJobs()) {
            				
            				TreeNode jobNode = new TreeNode(job.getName());
            				jobNode.setUserObject(job);
            				//TODO ADDED
             				jobNode.setChecked(false);
            				resNode.appendChild(jobNode);
            				
            				//TODO ADDED
            				// Adds a listener to the job node regarding the check box
            				jobNode.addListener(new TreeNodeListenerAdapter() {
            					public void onCheckChanged(Node node, boolean checked) {
            						if (checked) {
            							submitJobsButton.setIconCls("start-many-icon");
            							submitJobsButton.setDisabled(false);
            							// When the user has selected more than one jobs then allow only batch execution
            							if (getSelectedJobs().size() > 1) {
            								startJobButton.setIconCls("start-disabled-icon");
            								startJobButton.setDisabled(true);
            							}
            						}
            						else {
            							int numberOfSelJobs = getSelectedJobs().size();
            							if (numberOfSelJobs <= 1) {
            								startJobButton.setIconCls("start-icon");
            								startJobButton.setDisabled(false);
            								if  (numberOfSelJobs <= 0) {
            									submitJobsButton.setIconCls("start-many-disabled-icon");
            									submitJobsButton.setDisabled(true);
            								}
            							}
            						}
            					}
            				});
            			}
            		}
            	}
            	portlet.hideLoadMask();
            }

            /**
             * {@inheritDoc}
             */
            public void onFailure(Throwable caught) {
            	Window.alert("Failed to retrieve available resources and jobs." + caught);
            	portlet.hideLoadMask();
            }
        };IRBootstrapperPortletG.bootstrapperService.getJobs(callback);
	}
	
	/**
	 * Returns an ArrayList of JobUIElements that are currently checked
	 * 
	 * @return
	 */
	private ArrayList<JobUIElement> getSelectedJobs() {
		TreeNode checkedNodes[] = availableJobsTree.getChecked();
		ArrayList<JobUIElement> selectedJobs = new ArrayList<JobUIElement>();
		for (int i=0; i<checkedNodes.length; i++) {
			JobUIElement sJob = (JobUIElement)checkedNodes[i].getUserObject();
			selectedJobs.add(sJob);
		}
		return selectedJobs;
	}
	
	private boolean validateParallelSubmission(ArrayList<JobUIElement> jobs) {
		for (int i=0; i<jobs.size(); i++) {
			if (i<jobs.size()-1)
				if (!jobs.get(i).getJobTypeName().equals(jobs.get(i+1).getJobTypeName()))
					return false;
		}
		return true;
	}
	
	/**
	 * Refreshes the tree of submitted jobs
	 */
	private void refreshSubmittedJobsTree() {
		this.selectedJob = null;
		this.selectedNodeInSubmittedJobsTree = null;
        if (listener != null)
        	listener.onJobSelected(null, JobViewMode.MODE_SUBMITTED);
        enableOrDisableSubmittedJobsToolbarButtons(null);
        portlet.showLoadMask();
		
		/* The callback object for invoking the 'getSubmittedJobs' servlet method */
		AsyncCallback<List<ResourceTypeUIElement>> callbackSubmitted = new AsyncCallback<List<ResourceTypeUIElement>>() {
            /**
             * {@inheritDoc}
             */
            public void onSuccess(List<ResourceTypeUIElement> result) {
            	removeAllRootNodeChildrenInTree(submittedJobsTree);
            	TreeNode root = submittedJobsTree.getRootNode();
            	
            	/* Add the returned resource types as nodes to the tree */
            	for (ResourceTypeUIElement resType : result) {
            		TreeNode resTypeNode = new TreeNode(resType.getName());
            		resTypeNode.setUserObject(resType);
            		root.appendChild(resTypeNode);
            		
            		/* For each resource type, add the returned resources as
            		 * its sub-nodes in the tree.
            		 */
            		for (ResourceUIElement resource : resType.getResources()) {
            			TreeNode resNode = new TreeNode(resource.getName());
            			resNode.setUserObject(resource);
            			resTypeNode.appendChild(resNode);
            			
            			/* For resource, add the jobs taking it as an input as
            			 * its sub-nodes in the tree.
            			 */
            			for (JobUIElement job : resource.getJobs()) {
            				TreeNode jobNode = new TreeNode(job.getName());
            				jobNode.setUserObject(job);
            				setSubmittedTreeNodeIcon(jobNode);
            				resNode.appendChild(jobNode);
            			}
            		}
            	}
            	portlet.hideLoadMask();
            }

            /**
             * {@inheritDoc}
             */
            public void onFailure(Throwable caught) {
            	Window.alert("Error retrieving available resources and jobs." + caught);
            	portlet.hideLoadMask();
            }
        };
        
        IRBootstrapperPortletG.bootstrapperService.getSubmittedJobs(callbackSubmitted);
	}
	
	/**
	 * Removes all child nodes of the tree's root node, keeping only the root in the tree
	 * @param tree
	 */
	private void removeAllRootNodeChildrenInTree(TreePanel tree) {
		TreeNode root = tree.getRootNode();
		for (Node n : root.getChildNodes())
			n.remove();
	}
	
	/**
	 * Sets the listener for events related to this {@link JobsNavigator} object
	 * @param listener the events listener
	 */
	public void setJobSelectionListener(JobsNavigatorListener listener) {
		this.listener = listener;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobStatusChangeListener#onJobStatusChanged(org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement, org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionEntityUIElement.ExecutionState)
	 */
	public void onJobStatusChanged(JobUIElement job, UIExecutionState newStatus) {
		enableOrDisableSubmittedJobsToolbarButtons(job);
		setSubmittedTreeNodeIcon(selectedNodeInSubmittedJobsTree);
	}
}
