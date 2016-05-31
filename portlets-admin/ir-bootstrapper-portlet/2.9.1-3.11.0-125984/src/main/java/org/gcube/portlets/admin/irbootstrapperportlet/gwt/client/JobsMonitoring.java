/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElements.SubmittedJobInfoPanel;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.SubmittedJobInfoUIElement;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class JobsMonitoring extends Composite {

	/** A reference to the main portlet object */
	private IRBootstrapperPortletG portlet;

	/** The mainPanel */
	//private DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.EM);

	private VerticalPanel dataPanel = new VerticalPanel();

	private HTML infoLabel = new HTML("<span style=\"color:darkblue\">Select a job from the tree to display its information</span>");

	/** The tree that contains the available jobs per resource */
	private TreePanel submittedJobsTree;


	/** The UI element that represents the currently selected job in the tree */
	private SubmittedJobInfoUIElement selectedJob;

	/** The selected node in the "submitted jobs" tree */
	private TreeNode selectedNodeInSubmittedJobsTree;
	
	private ToolbarButton clearJobsButton = new ToolbarButton();

	/**
	 * Class constructor
	 */
	public JobsMonitoring(IRBootstrapperPortletG portlet) {
		this.portlet = portlet;
		this.selectedJob = null;


		HorizontalSplitPanel mainPanel = new HorizontalSplitPanel();
		mainPanel.setSize("99%", "460px");
		mainPanel.setSplitPosition("40%");
		mainPanel.setWidth("100%");
		dataPanel.add(infoLabel);
		dataPanel.setCellHorizontalAlignment(infoLabel, HasHorizontalAlignment.ALIGN_CENTER);

		Panel jobsPanel = new Panel();
		jobsPanel.setAutoScroll(true);

		submittedJobsTree = new TreePanel();
		submittedJobsTree.setRootNode(new TreeNode("Job Types"));
		submittedJobsTree.setTopToolbar(createSubmittedJobsToolbar());
		jobsPanel.add(submittedJobsTree);

		mainPanel.setLeftWidget(jobsPanel);
		mainPanel.setRightWidget(dataPanel);
		//refreshSubmittedJobsTree();

		initWidget(mainPanel);

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
		if (assocObject instanceof SubmittedJobInfoUIElement) {
			selectedJob = (SubmittedJobInfoUIElement) node.getUserObject();
			selectedNodeInSubmittedJobsTree = node;
			dataPanel.clear();
			dataPanel.add(new SubmittedJobInfoPanel(selectedJob));
		}
		else {
			selectedJob = null;
			selectedNodeInSubmittedJobsTree = null;
			dataPanel.clear();
			dataPanel.add(infoLabel);
			dataPanel.setCellHorizontalAlignment(infoLabel, HasHorizontalAlignment.ALIGN_CENTER);
		}
	}

	/**
	 * Initializes the tree structure with data from the current scope
	 */
	public void initialize() {		
		refreshSubmittedJobsTree();
	}

	/**
	 * Returns an ArrayList of JobUIElements that are currently checked
	 * 
	 * @return
	 */
	private ArrayList<SubmittedJobInfoUIElement> getSelectedJobs() {
		TreeNode checkedNodes[] = submittedJobsTree.getChecked();
		ArrayList<SubmittedJobInfoUIElement> selectedJobs = new ArrayList<SubmittedJobInfoUIElement>();
		for (int i=0; i<checkedNodes.length; i++) {
			SubmittedJobInfoUIElement sJob = (SubmittedJobInfoUIElement)checkedNodes[i].getUserObject();
			selectedJobs.add(sJob);
		}
		return selectedJobs;
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
		
		/** The "stop submitted job" button */
		//ToolbarButton clearJobsButton = new ToolbarButton();
		clearJobsButton.setIconCls("remove-disabled-icon");
		clearJobsButton.setCls("x-btn-icon");
		clearJobsButton.setTooltip("<b>Clear</b><br/>Deletes the persisted logs");
		clearJobsButton.setDisabled(true);
		clearJobsButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {

				/* The callback object */
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

					public void onFailure(Throwable arg0) {
						Window.alert("Failed to delete the logs");
					}

					public void onSuccess(Boolean arg0) {
						if (arg0)
							refreshSubmittedJobsTree();
					}
				};
				IRBootstrapperPortletG.bootstrapperService.clearPersistedLogs(callback);
			}
		});
		tbSubmitted.addButton(clearJobsButton);

		return tbSubmitted;
	}

	/**
	 * Refreshes the tree of submitted jobs
	 */
	private void refreshSubmittedJobsTree() {
		this.selectedJob = null;
		this.selectedNodeInSubmittedJobsTree = null;
		dataPanel.clear();

		/* The callback object for invoking the 'getSubmittedJobs' servlet method */
		AsyncCallback<HashMap<String, ArrayList<SubmittedJobInfoUIElement>>> callbackSubmitted = new AsyncCallback<HashMap<String, ArrayList<SubmittedJobInfoUIElement>>>() {
			/**
			 * {@inheritDoc}
			 */
			public void onSuccess(HashMap<String, ArrayList<SubmittedJobInfoUIElement>> result) {
				removeAllRootNodeChildrenInTree(submittedJobsTree);
				TreeNode root = submittedJobsTree.getRootNode();
				Iterator<Entry<String, ArrayList<SubmittedJobInfoUIElement>>> it = result.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, ArrayList<SubmittedJobInfoUIElement>> pairs = it.next();
					String jType = pairs.getKey();
					ArrayList<SubmittedJobInfoUIElement> sJobs = pairs.getValue();
					TreeNode jobTypeNode = new TreeNode(jType);
					root.appendChild(jobTypeNode);
					/* Add the returned resource types as nodes to the tree */
					for (SubmittedJobInfoUIElement jobInfo : sJobs) {
						TreeNode jobNode = new TreeNode(jobInfo.getName() + " - " + jobInfo.getStartDate());
						jobNode.setUserObject(jobInfo);
						jobTypeNode.appendChild(jobNode);
					}

				}
				if (submittedJobsTree.getRootNode().getChildNodes().length > 0) {
					clearJobsButton.setIconCls("remove-icon");
					clearJobsButton.setDisabled(false);
				}
				else {
					clearJobsButton.setIconCls("remove-disabled-icon");
					clearJobsButton.setDisabled(true);
				}
			}

			/**
			 * {@inheritDoc}
			 */
			public void onFailure(Throwable caught) {
				Window.alert("Error retrieving the list with the persisted submitted jobs." + caught);
			}
		};

		IRBootstrapperPortletG.bootstrapperService.getPersistedSubmittedJobs(callbackSubmitted);
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
}
