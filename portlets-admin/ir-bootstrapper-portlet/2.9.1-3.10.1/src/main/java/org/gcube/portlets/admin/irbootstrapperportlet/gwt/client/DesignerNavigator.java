/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client;

import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.AssignUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobTypeUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionEntityUIElement.UIExecutionState;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete.JobAutoCompleteData;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobAttributesChangeListener;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobListUpdatedListener;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobStatusChangeListener;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobViewMode;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobsDesignerNavigatorListener;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.data.NodeTraversalCallback;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.AccordionLayout;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class DesignerNavigator extends Composite implements JobStatusChangeListener, JobAttributesChangeListener {

	/** A reference to the main portlet object */
	private IRBootstrapperPortletG portlet;
	
	/** The tree that contains the available job types and jobs per job type */
	private TreePanel availableJobTypesTree;
	
	/** The UI element that represents the currently selected job in the tree */
	private JobUIElement selectedJob;
	
	/** The currently selected node in the tree */
	private TreeNode selectedNode;
	
	/** The registered listener for events related to this navigator object */
	private JobsDesignerNavigatorListener listener;
	
	/** The registered listener for the "job list updated" event */
	private JobListUpdatedListener jobListUpdatedListener;
	
	/** The "new job" button */
	private ToolbarButton newJobButton;
	
	/** The "clone job" button */
	private ToolbarButton cloneJobButton;
	
	/** The "delete job" button */
	private ToolbarButton deleteJobButton;
	
	/** The "view job tree" button */
	private ToolbarButton viewJobButton;
	
	/** The job tree window */
	private com.gwtext.client.widgets.Window jobTreeWindow;
	
	/** The job visualization panel */
	JobVisualization jobViz;

	private List<String> availJobTypes = new LinkedList<String>();
	private List<String[]> availJobNames = new LinkedList<String[]>();
	
	public DesignerNavigator(IRBootstrapperPortletG portlet) {
		this.portlet = portlet;
		this.listener = null;
		this.selectedJob = null;
		this.selectedNode = null;
		this.jobListUpdatedListener = null;
		
		Panel accordionPanel = new Panel();  
		accordionPanel.setLayout(new AccordionLayout(true));  
		Panel panelOne = new Panel("Available Jobs");  
		accordionPanel.add(panelOne);
		accordionPanel.setHeight(450);
		
		availableJobTypesTree = new TreePanel();
		availableJobTypesTree.setRootNode(new TreeNode("Job Types"));
		availableJobTypesTree.setTopToolbar(createAvailableJobsToolbar());
		panelOne.add(availableJobTypesTree);
		
		initWidget(accordionPanel);
		
		jobTreeWindow = createJobTreeWindow();
		
		availableJobTypesTree.addListener(new TreePanelListenerAdapter() {
			
			@Override
			public void onClick(TreeNode node, EventObject e) {
				if (node.isSelected() && (node.getUserObject() instanceof JobUIElement)) {
					selectedJob = (JobUIElement) node.getUserObject();
					selectedNode = node;
					cloneJobButton.setIconCls("clone-icon");
					cloneJobButton.setDisabled(false);
					deleteJobButton.setIconCls("remove-icon");
					deleteJobButton.setDisabled(false);
					viewJobButton.setIconCls("view-icon");
					viewJobButton.setDisabled(false);
					listener.onJobSelected((JobUIElement) node.getUserObject());
				}
				else {
					/* A non-job node has been selected */
					selectedJob = null;
					selectedNode = null;
					cloneJobButton.setIconCls("clone-disabled-icon");
					cloneJobButton.setDisabled(true);
					deleteJobButton.setIconCls("remove-disabled-icon");
					deleteJobButton.setDisabled(true);
					viewJobButton.setIconCls("view-disabled-icon");
					viewJobButton.setDisabled(true);
					listener.onJobSelected(null);
				}
			}
			
			@Override
			public boolean doBeforeClick(TreeNode node, EventObject e) {
				JobUIElement job = null;
				if (node.getUserObject() instanceof JobUIElement)
					job = (JobUIElement) node.getUserObject();
				return listener.beforeJobSelected(job);
			}
		});
	}
	
	/**
	 * Creates and returns the window that displays the job tree
	 * @return
	 */
	private com.gwtext.client.widgets.Window createJobTreeWindow() {
		com.gwtext.client.widgets.Window w = new com.gwtext.client.widgets.Window("Job execution tree", 800, 500, true, true);
		jobViz = new JobVisualization(null, 600, false);
		w.setAutoScroll(true);
		w.add(jobViz);
		return w;
	}
	
	/**
	 * Creates the toolbar of the "available jobs" tree
	 * @return
	 */
	private Toolbar createAvailableJobsToolbar() {
		Toolbar tbAvailable = new Toolbar();
		
		/* Create the "refresh jobs" button */
		ToolbarButton btnRefresh = new ToolbarButton();
		btnRefresh.setIconCls("refresh-icon");
		btnRefresh.setCls("x-btn-icon");
		btnRefresh.setTooltip("<b>Refresh</b><br/>Refreshes the list of available job types");
		btnRefresh.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				refreshAvailableJobTypesTree();
			}
		});
		tbAvailable.addButton(btnRefresh);
		
		/* Create the "new job" button */
		newJobButton = new ToolbarButton();
		newJobButton.setIconCls("new-icon");
		newJobButton.setCls("x-btn-icon");
		newJobButton.setTooltip("<b>New</b><br/>Creates a new job");
		newJobButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				if (selectedNode != null)
					selectedNode.unselect();
				selectedJob = null;
				selectedNode = null;
				cloneJobButton.setIconCls("clone-disabled-icon");
				cloneJobButton.setDisabled(true);
				deleteJobButton.setIconCls("remove-disabled-icon");
				deleteJobButton.setDisabled(true);
				viewJobButton.setIconCls("view-disabled-icon");
				viewJobButton.setDisabled(true);
				
				JobUIElement newJob = new JobUIElement();
				newJob.setUID(null);
				newJob.setName("New job");
				newJob.setJobTypeName(null);
				newJob.setJobExtends(null);
				newJob.setInitAssignments(new LinkedList<AssignUIElement>());
				listener.onJobSelected(newJob);
			}
		});
		tbAvailable.addButton(newJobButton);

		/* Create the "clone job" button */
		cloneJobButton = new ToolbarButton();
		cloneJobButton.setIconCls("clone-disabled-icon");
		cloneJobButton.setCls("x-btn-icon");
		cloneJobButton.setTooltip("<b>Clone</b><br/>Creates an exact copy of the selected job");
		cloneJobButton.setDisabled(true);
		cloneJobButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				JobUIElement clonedJob = selectedJob;
				selectedJob = null;
				selectedNode = null;
				cloneJobButton.setIconCls("clone-disabled-icon");
				cloneJobButton.setDisabled(true);
				deleteJobButton.setIconCls("remove-disabled-icon");
				deleteJobButton.setDisabled(true);
				viewJobButton.setIconCls("view-disabled-icon");
				viewJobButton.setDisabled(true);
				
				JobUIElement newJob = new JobUIElement();
				newJob.setUID(null);
				newJob.setName(clonedJob.getName() + " - Copy");
				newJob.setJobTypeName(clonedJob.getJobTypeName());
				newJob.setJobExtends(clonedJob.getJobExtends());
				List<AssignUIElement> initAssignments = new LinkedList<AssignUIElement>();
				for (AssignUIElement a : clonedJob.getInitAssignments())
					initAssignments.add(new AssignUIElement(a.getAssignFrom(), a.getAssignTo(), a.requiresUserInput(), a.getUserInputLabel()));
				newJob.setInitAssignments(initAssignments);
				listener.onJobSelected(newJob);
			}
		});
		tbAvailable.addButton(cloneJobButton);

		/* Create the "delete job" button */
		deleteJobButton = new ToolbarButton();
		deleteJobButton.setIconCls("remove-disabled-icon");
		deleteJobButton.setCls("x-btn-icon");
		deleteJobButton.setTooltip("<b>Delete</b><br/>Deletes the selected job");
		deleteJobButton.setDisabled(true);
		deleteJobButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				if (selectedJob == null) {
					Window.alert("An internal error has occured. Please reload the portlet.");
					return;
				}
				
				MessageBox.confirm("Confirm deletion", "Are you sure you want to delete the selected job permanently? This operation cannot be undone.",  
						new MessageBox.ConfirmCallback() {
							public void execute(String btnID) {
								if (btnID.equals("yes")) {
									/* The callback object for invoking the 'deleteJob' servlet method */
									AsyncCallback<Void> callback = new AsyncCallback<Void>() {
										/* (non-Javadoc)
										 * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
										 */
										public void onSuccess(Void arg0) {
											
											/* Remove the deleted job from the list of available job names */
											for (String[] jobDesc : availJobNames) {
												if (((String) jobDesc[0]).equals(selectedJob.getName())) {
													availJobNames.remove(jobDesc);
													break;
												}
											}
											jobListUpdatedListener.onJobsInfoLoaded(availJobTypes, availJobNames);
											
											selectedNode.unselect();
											selectedNode.remove();
											selectedJob = null;
											selectedNode = null;
											listener.onJobSelected(null);
											
											cloneJobButton.setIconCls("clone-disabled-icon");
											cloneJobButton.setDisabled(true);
											deleteJobButton.setIconCls("remove-disabled-icon");
											deleteJobButton.setDisabled(true);
											viewJobButton.setIconCls("view-disabled-icon");
											viewJobButton.setDisabled(true);
										}
										
										/* (non-Javadoc)
										 * @see com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable)
										 */
										public void onFailure(Throwable arg0) {
											Window.alert("Failed to delete the selected job.\n" + arg0);
										}
									};
									
									IRBootstrapperPortletG.bootstrapperService.deleteJob(selectedJob, callback);
								}
							}
					}
				);
			}
		});
		tbAvailable.addButton(deleteJobButton);
		
		/* Create the "view job tree" button */
		viewJobButton = new ToolbarButton();
		viewJobButton.setIconCls("view-disabled-icon");
		viewJobButton.setDisabled(true);
		viewJobButton.setCls("x-btn-icon");
		viewJobButton.setTooltip("<b>View job tree</b><br/>Displays the execution tree of tasks that the selected job consists of");
		viewJobButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				jobViz.setJobToVisualize(selectedJob, JobViewMode.MODE_AVAILABLE);
				jobTreeWindow.show();
			}
		});
		tbAvailable.addButton(viewJobButton);
		
		return tbAvailable;
	}
	
	/**
	 * Initializes the tree structure with data from the current scope
	 */
	public void initialize() {		
		refreshAvailableJobTypesTree();
	}
	
	public void refreshAvailableJobTypesTree() {
		portlet.showLoadMask();
        
		/* The callback object for invoking the 'getAllJobsPerJobType' servlet method */
		AsyncCallback<List<JobTypeUIElement>> callback = new AsyncCallback<List<JobTypeUIElement>>() {
            /**
             * {@inheritDoc}
             */
            public void onSuccess(List<JobTypeUIElement> result) {
            	availJobTypes = new LinkedList<String>();
            	availJobNames = new LinkedList<String[]>();
            	availJobNames.add(new String[] { "(none)", "*" });
            	
            	removeAllRootNodeChildrenInTree(availableJobTypesTree);
            	TreeNode root = availableJobTypesTree.getRootNode();
            	
            	/* Add the returned job types as nodes to the tree */
            	for (JobTypeUIElement jobType : result) {
            		availJobTypes.add(jobType.getJobTypeName());
            		TreeNode jobTypeNode = new TreeNode(jobType.getJobTypeName());
            		jobTypeNode.setUserObject(jobType);
            		root.appendChild(jobTypeNode);
            		
            		/* For each job type, add the returned jobs as
            		 * its sub-nodes in the tree.
            		 */
            		for (JobUIElement job : jobType.getJobs()) {
            			availJobNames.add(new String[] { job.getName(), jobType.getJobTypeName() });
            			TreeNode jobNode = new TreeNode(job.getName());
            			jobNode.setUserObject(job);
            			jobTypeNode.appendChild(jobNode);
            		}
            	}
            	
            	jobListUpdatedListener.onJobsInfoLoaded(availJobTypes, availJobNames);
            	
            	/* The callback object for invoking the 'getAutoCompleteData' servlet method */
        		AsyncCallback<JobAutoCompleteData> callback = new AsyncCallback<JobAutoCompleteData>() {
        			/**
                     * {@inheritDoc}
                     */
        			public void onSuccess(JobAutoCompleteData arg0) {
        				portlet.getDesigner().getJobEditor().initialize(arg0);
        				portlet.hideLoadMask();        				
        			}
        			
        			/**
                     * {@inheritDoc}
                     */
        			public void onFailure(Throwable arg0) {
        				Window.alert("Failed to retrieve auto-complete data from the server. The auto complete feature will not be available in the job editor pane.\n" + arg0);
        				portlet.hideLoadMask();
        			}
        		};

        		IRBootstrapperPortletG.bootstrapperService.getAutoCompleteData(callback);
            }

            /**
             * {@inheritDoc}
             */
            public void onFailure(Throwable caught) {
            	Window.alert("Failed to retrieve available resources and jobs." + caught);
            	portlet.hideLoadMask();
            }
        };

		IRBootstrapperPortletG.bootstrapperService.getAllJobsPerJobType(callback);

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
	 * Sets the listener for events related to this navigator object
	 * @param listener the events listener
	 */
	public void setJobSelectionListener(JobsDesignerNavigatorListener listener) {
		this.listener = listener;
	}
	
	/**
	 * Sets the listener for the "job list updated" event
	 * @param listener the event listener
	 */
	public void setJobListUpdatedListener(JobListUpdatedListener listener) {
		jobListUpdatedListener = listener;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobStatusChangeListener#onJobStatusChanged(org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement, org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionEntityUIElement.ExecutionState)
	 */
	public void onJobStatusChanged(JobUIElement job, UIExecutionState newStatus) {
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobAttributesChangeListener#jobNameChanged(org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement, java.lang.String, java.lang.String)
	 */
	public void jobNameChanged(JobUIElement job, String oldName, String newName) {
		/* Update the name of the currently selected tree node. If this event is fired,
		 * a job node should be selected. Something is terribly wrong otherwise.
		 */
		if (selectedNode == null)
			Window.alert("An internal error has occured. Please reload the portlet.");
		else {
			selectedNode.setText(newName);
			
			for (String[] jobDesc : availJobNames) {
				if (jobDesc[0].equals(oldName)) {
					jobDesc[0] = newName;
					break;
				}
			}
			jobListUpdatedListener.onJobsInfoLoaded(availJobTypes, availJobNames);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobAttributesChangeListener#jobTypeChanged(org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement, java.lang.String, java.lang.String)
	 */
	public void jobTypeChanged(JobUIElement job, String oldType, String newType) {
		/* Move the currently selected tree node under another parent, since the
		 * job type has been modified. If this event is fired, a job node should
		 * be selected. Something is terribly wrong otherwise.
		 */
		if (selectedNode == null)
			Window.alert("An internal error has occured. Please reload the portlet.");
		else {
			TreeNode n = selectedNode;
			
			/* Find the node that describes the new job type */
			final String type = newType;
			Node newParent = availableJobTypesTree.getRootNode().findChildBy(new NodeTraversalCallback() {
				public boolean execute(Node node) {
					Object o = node.getUserObject();
					if ((o!=null) && (o instanceof JobTypeUIElement)) {
						JobTypeUIElement jobTypeEl = (JobTypeUIElement) o;
						if (jobTypeEl.getJobTypeName().equals(type)) {
							return true;
						}
					}
					return false;
				}
			});
			if (newParent == null) {
				Window.alert("An internal error has occured. Please reload the portlet.");
				return;
			}
			
			/* Remove the job node from its old position, and append it under the
			 * found job type node.
			 */
			newParent.appendChild(n);
			n.ensureVisible();
			n.select();
			
			for (String[] jobDesc : availJobNames) {
				if (jobDesc[1].equals(oldType)) {
					jobDesc[1] = newType;
					break;
				}
			}
			jobListUpdatedListener.onJobsInfoLoaded(availJobTypes, availJobNames);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobAttributesChangeListener#newJobCreated(org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement)
	 */
	public void newJobCreated(JobUIElement job) {
		/* Create a new tree node for the new job, and place it under the node that
		 * represents the job type.
		 */
		TreeNode jobNode = new TreeNode(job.getName());
		jobNode.setUserObject(job);
		
		/* Locate the node that describes the job type. */
		final String type = job.getJobTypeName();
		Node newParent = availableJobTypesTree.getRootNode().findChildBy(new NodeTraversalCallback() {
			public boolean execute(Node node) {
				Object o = node.getUserObject();
				if ((o!=null) && (o instanceof JobTypeUIElement)) {
					JobTypeUIElement jobTypeEl = (JobTypeUIElement) o;
					if (jobTypeEl.getJobTypeName().equals(type)) {
						return true;
					}
				}
				return false;
			}
		});
		if (newParent == null) {
			Window.alert("An internal error has occured. Please reload the portlet.");
			return;
		}
		
		/* Append the new node under the found job type node and select it. */
		newParent.appendChild(jobNode);
		jobNode.ensureVisible();
		jobNode.select();
		
		selectedJob = job;
		selectedNode = jobNode;
		
		cloneJobButton.setIconCls("clone-icon");
		cloneJobButton.setDisabled(false);
		deleteJobButton.setIconCls("remove-icon");
		deleteJobButton.setDisabled(false);
		viewJobButton.setIconCls("view-icon");
		viewJobButton.setDisabled(false);
		
		/* Add the new job to the list of available job names */
		availJobNames.add(new String[] { job.getName(), job.getJobTypeName() });
		jobListUpdatedListener.onJobsInfoLoaded(availJobTypes, availJobNames);
	}
}
