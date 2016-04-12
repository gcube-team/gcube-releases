/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElements.ExecutionLogWindow;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElements.TaskTreeNodePanel;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionEntityUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionTypeUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionEntityUIElement.UIExecutionState;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobStatusChangeListener;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobViewMode;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class ViewerJobVisualization extends Composite {

	private static final int CONNECTOR_HEIGHT = 40;
	private static final int CONNECTOR_LINE_WIDTH = 2;
	private static final int HORZ_CELL_PADDING = 20;
	private static final int NODE_WIDGET_WIDTH = 220;
	private static final int NODE_WIDGET_HEIGHT = 120;
	private static final int UPDATE_TIMER_INTERVAL = 10000;
	
	private class EntityPositionInTable {
		private ExecutionEntityUIElement entity;
		private int row;
		private int column;

		public EntityPositionInTable(ExecutionEntityUIElement entity, int row, int column) {
			this.entity = entity;
			this.row = row;
			this.column = column;
		}

		public int getRow() { return row; }
		public int getColumn() { return column; }
		public ExecutionEntityUIElement getEntity() { return entity; }
	}


	private Map<String, ExecutionEntityUIElement> UIDAndEntityPairs = new HashMap<String, ExecutionEntityUIElement>();
	private Map<String, EntityPositionInTable> UIDAndPositionPairs = new HashMap<String, EntityPositionInTable>();
	private Map<String, TaskTreeNodePanel> UIDAndNodePanelPairs = new HashMap<String, TaskTreeNodePanel>();
	private FlexTable tbl = new FlexTable();
	private Panel legendPanel = new Panel();
	private Panel container = new Panel();
	private JobUIElement visualizedJob;
	private JobViewMode viewMode;
	private Timer updateTimer;
	private JobStatusChangeListener visualizedJobChangeListener;
	private ExecutionLogWindow logWindow = new ExecutionLogWindow();
	
	public ViewerJobVisualization(JobUIElement job) {
		this.visualizedJob = null;
		this.updateTimer = null;
		this.visualizedJobChangeListener = null;
		this.viewMode = JobViewMode.MODE_AVAILABLE;
		tbl.setStyleName("treeTable");
		setJobToVisualize(job, viewMode);
		
		Panel p = new Panel();
		p.setBorder(false);
		p.add(tbl);
		p.setAutoScroll(true);
		p.setAutoWidth(true);
		container.setBorder(false);
		container.setHeight(460);
		container.setAutoWidth(true);
		container.setLayout(new AnchorLayout());
		container.add(p, new AnchorLayoutData("0 -25"));
		container.add(legendPanel, new AnchorLayoutData("0"));
		fillLegendPanel();
		
		initWidget(container);
	}

	/**
	 * Sets the listener to be invoked whenever the status of the currently visualized job changes
	 * @param listener the job status change listener
	 */
	public void setJobStatusChangeListener(JobStatusChangeListener listener) {
		this.visualizedJobChangeListener = listener;
	}
	
	/**
	 * Sets the job to render
	 * @param job the job to render
	 * @param viewMode the current view mode (available/submitted jobs)
	 */
	public void setJobToVisualize(JobUIElement job, JobViewMode viewMode) {
		this.visualizedJob = job;
		UIDAndEntityPairs.clear();
		UIDAndPositionPairs.clear();
		UIDAndNodePanelPairs.clear();
		tbl.clear();
		//maxWidth = 0;
		
		/* If the update timer is currently active, cancel it and set it to null */
		if (updateTimer != null) {
			updateTimer.cancel();
			updateTimer = null;
		}
		
		this.viewMode = viewMode;
		fillLegendPanel();
		
		if (job != null) {
			fillTableWithTaskTree(job.getTaskTree(), 0, tbl);
	
			/* If the view mode is set to "submitted jobs", initialize the update timer */
			if (viewMode==JobViewMode.MODE_SUBMITTED && job.getExecutionState()==UIExecutionState.RUNNING) {
				updateTimer = new Timer() {
					@Override
					public void run() {
						AsyncCallback<JobUIElement> getJobCallback = new AsyncCallback<JobUIElement>() {

							public void onFailure(Throwable arg0) {
								Window.alert("Error while retrieving job status." + arg0);
							}

							public void onSuccess(JobUIElement arg0) {
								UIExecutionState oldExecState = visualizedJob.getExecutionState();

								/* If the visualized job is completed, cancel the update timer */
								visualizedJob = arg0;
								UIExecutionState execState = visualizedJob.getExecutionState();
								if (execState!=UIExecutionState.NOT_STARTED && execState!=UIExecutionState.RUNNING)
									cancel();

								/* Update the visualized job tree */
								LinkedList<ExecutionEntityUIElement> nodeList = new LinkedList<ExecutionEntityUIElement>();
								nodeList.add(arg0.getTaskTree());
								while (!nodeList.isEmpty()) {
									ExecutionEntityUIElement node = nodeList.poll();
									UIDAndNodePanelPairs.get(node.getUID()).setEntity(node, JobViewMode.MODE_SUBMITTED);
									if (node instanceof ExecutionTypeUIElement)
										nodeList.addAll(((ExecutionTypeUIElement) node).getTasks());
								}

								/* If the visualized job's state has changed since the previous update, invoke the
								 * registered state change listener.
								 */
								if (!execState.equals(oldExecState) && visualizedJobChangeListener!=null)
									visualizedJobChangeListener.onJobStatusChanged(visualizedJob, execState);
							}
						};
						IRBootstrapperPortletG.bootstrapperService.getSubmittedJobByUID(visualizedJob.getUID(), getJobCallback);
					}
				};
				updateTimer.scheduleRepeating(UPDATE_TIMER_INTERVAL);
			}
		}
	}
	
	/**
	 * Places the given task tree inside the given FlexTable.
	 * @param element the tree root element
	 * @param numChild the index of the given element inside the list of children of the parent node (zero-based)
	 * @param tbl the FlexTable to fill
	 */
	private void fillTableWithTaskTree(ExecutionEntityUIElement element, int numChild, FlexTable tbl) {
		int col = 0;
		int row = 0;
		boolean bHasParent = false;
		final String connectorPaddingVal = String.valueOf((NODE_WIDGET_WIDTH / 2) - (CONNECTOR_LINE_WIDTH / 2)) + "px";
		
		/* If the element is not the root element... */
		if (element.getParentUID() != null) {
			bHasParent = true;
			
			/* We must find the position on the table where this element should be put. If the parent
			 * element is located at (x,y), we start searching from (x+numChild, y+2). First we must
			 * check if there are any other nodes in the rest of the "x+numChild" column, moving downwards
			 * from "y+2" If there are, we must move to the next column. Repeat this until we find
			 * a column that suits our needs.
			 * NOTE: y is increased by 2 and not by 1 each time, because one table row is always used for
			 * a horizontal line connector
			 */
			EntityPositionInTable parentPos = UIDAndPositionPairs.get(element.getParentUID());
			col = parentPos.getColumn() + numChild;
			row = parentPos.getRow() + 2;
						
			boolean bFound = false;
			while (!bFound) {
				int checkRow = row;
				int rowCount = tbl.getRowCount();
				while (checkRow < rowCount) {
					if (tbl.isCellPresent(checkRow, col)) {
						if (tbl.getWidget(checkRow, col)==null)
							checkRow += 2;
						else break;
					}
					else checkRow += 2;
				}
				if (checkRow >= rowCount)
					bFound = true;
				else
					col++;
			}
			
		}
		
		/* Create a widget and put it in the calculated coordinates. Also update
		 * the UIDAndPositionPairs and UIDAndEntityPairs arrays.
		 * NOTE: if the element has a parent, place a vertical connector over its widget.
		 */
		VerticalPanel vp = new VerticalPanel();
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		if (bHasParent) {
			Panel vertLine = new Panel();
			vertLine.setWidth(CONNECTOR_LINE_WIDTH);
			vertLine.setHeight(CONNECTOR_HEIGHT / 2);
			vp.add(vertLine);
		}
		TaskTreeNodePanel panel = new TaskTreeNodePanel(element, viewMode, logWindow);
		panel.setEntity(element, viewMode);
		panel.setWidth(NODE_WIDGET_WIDTH);
		panel.setHeight(NODE_WIDGET_HEIGHT);
		panel.setAutoScroll(true);
		
		vp.add(panel);
		tbl.getFlexCellFormatter().setVerticalAlignment(row, col, HasVerticalAlignment.ALIGN_TOP);
		tbl.setWidget(row, col, vp);
		EntityPositionInTable pos = new EntityPositionInTable(element, row, col);
		UIDAndPositionPairs.put(element.getUID(), pos);
		UIDAndEntityPairs.put(element.getUID(), element);
		UIDAndNodePanelPairs.put(element.getUID(), panel);

		/* If the element has children, process them too, but first of all
		 * add a vertical connector under the widget of the current element. */
		if (element instanceof ExecutionTypeUIElement) {
			Panel vertLine = new Panel();
			vertLine.setWidth(CONNECTOR_LINE_WIDTH);
			vertLine.setHeight(CONNECTOR_HEIGHT / 2);
			vp.add(vertLine);
			
			List<ExecutionEntityUIElement> children = ((ExecutionTypeUIElement) element).getTasks();
			for (int i=0; i<children.size(); i++) {
				fillTableWithTaskTree(children.get(i), i, tbl);
			}
			
			/* If the current element has more than one children, create a horizontal connector line */
			if (children.size() > 1) {
				row++;
				EntityPositionInTable startPos = UIDAndPositionPairs.get(children.get(0).getUID());
				EntityPositionInTable endPos = UIDAndPositionPairs.get(children.get(children.size()-1).getUID());
				FlexCellFormatter formatter = tbl.getFlexCellFormatter();
				
				col = 0;
				int numCellsPassed = 0;
				while (numCellsPassed < startPos.getColumn()) {
					if (!tbl.isCellPresent(row, col))
						numCellsPassed++;
					else
						numCellsPassed += formatter.getColSpan(row, col);
					col++;
				}

				int colSpan = endPos.getColumn() - startPos.getColumn();
				Panel horzLine = new Panel();
				horzLine.setWidth(colSpan * (NODE_WIDGET_WIDTH + HORZ_CELL_PADDING) + CONNECTOR_LINE_WIDTH);
				horzLine.setHeight(CONNECTOR_LINE_WIDTH);
				tbl.setWidget(row, col, horzLine);
				formatter.setColSpan(row, col, colSpan + 1);
				Element cellElement = formatter.getElement(row, col);
				DOM.setStyleAttribute(cellElement, "paddingLeft", connectorPaddingVal);
			}

		}
	}
	
	/**
	 * Fills the legend panel with the appropriate icon explanations, depending on the current view mode
	 */
	private void fillLegendPanel() {
		if (this.visualizedJob!=null && viewMode==JobViewMode.MODE_SUBMITTED) {
			StringBuilder html = new StringBuilder();
		
			html.append("<table><tr>");
			html.append("<td style=\"padding-left: 10px;\"><div style=\"width: 16px; height: 16px; float: left;\" class='fulfilled-icon'> </div><span style=\"vertical-align: middle;\"> Fulfilled task, will not be executed</span></td>");
			html.append("<td style=\"padding-left: 10px;\"><div style=\"width: 16px; height: 16px; float: left;\" class='running-icon'> </div><span style=\"vertical-align: middle;\"> Running task</span></td>");
			html.append("<td style=\"padding-left: 10px;\"><div style=\"width: 16px; height: 16px; float: left;\" class='success-icon'> </div><span style=\"vertical-align: middle;\"> Completed succesfully</span></td>");
			html.append("<td style=\"padding-left: 10px;\"><div style=\"width: 16px; height: 16px; float: left;\" class='warning-icon'> </div><span style=\"vertical-align: middle;\"> Completed with warnings</span></td>");
			html.append("<td style=\"padding-left: 10px;\"><div style=\"width: 16px; height: 16px; float: left;\" class='error-icon'> </div><span style=\"vertical-align: middle;\"> Failed</span></td>");
			html.append("</tr></table>");			
			
			legendPanel.setHtml(html.toString());
			legendPanel.setVisible(true);
		}
		else
			legendPanel.setVisible(false);
	}
}
