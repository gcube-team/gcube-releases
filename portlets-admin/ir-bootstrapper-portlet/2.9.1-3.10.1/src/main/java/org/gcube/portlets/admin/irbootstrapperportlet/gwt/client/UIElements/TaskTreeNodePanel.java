/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElements;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.AssignUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionEntityUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionTypeUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.TaskUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionEntityUIElement.UIExecutionState;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobViewMode;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.Function;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.ToolTip;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class TaskTreeNodePanel extends Panel {

	private ExecutionEntityUIElement associatedEntity;
	
	private Label descLabel = new Label();
	
	/**
	 * Class constructor
	 * @param entity
	 */
	public TaskTreeNodePanel(final ExecutionEntityUIElement entity, final JobViewMode viewMode, final ExecutionLogWindow logWindow) {
		descLabel.setWordWrap(true);
		this.add(descLabel);
		this.associatedEntity = null;

		this.setTitle("<br/>");
		this.descLabel.setText(entity.getName());
		if (entity instanceof ExecutionTypeUIElement)
			this.setBodyStyle("background-color:#aaccff");
		else if (entity instanceof AssignUIElement)
			this.setBodyStyle("background-color:#55ff55");
		else if (entity instanceof TaskUIElement)
			this.setBodyStyle("background-color:#ffbc66");
		
		String html = "<b>" + entity.getName() + "</b><br/>" +
			entity.getDescription();
		if (viewMode == JobViewMode.MODE_SUBMITTED) {
			html += "<br/><br/><b>Status:</b> ";
			UIExecutionState execState = entity.getExecutionState();
			if (execState == UIExecutionState.RUNNING)
				html += "running";
			else if (execState == UIExecutionState.COMPLETED_SUCCESS)
				html += "completed successfully";
			else if (execState == UIExecutionState.COMPLETED_FAILURE)
				html += "failed";
			else if (execState == UIExecutionState.COMPLETED_WARNINGS)
				html += "Completed with warnings";
			else if (execState == UIExecutionState.CANCELLED)
				html += "Cancelled";
		}
		ToolTip pp = new ToolTip(html);
		pp.setDismissDelay(0);
		pp.setShowDelay(0);
		pp.applyTo(this);
		
		if (viewMode == JobViewMode.MODE_SUBMITTED) {
			/* Add the "view log" tool to the panel */
			this.addTool(new Tool(Tool.PLUS, new Function() {  
				public void execute() {
					if (associatedEntity != null) {
						logWindow.setLogData(associatedEntity.getExecutionLog());
						logWindow.show();
					}
				}  
			}, "View execution log"));
		}
	}
	
	/**
	 * Sets the {@link ExecutionEntityUIElement} visualized by this {@link TaskTreeNodePanel}
	 * and updates the panel appearence with information from the given element
	 * @param entity the wrapped entity
	 * @param viewMode the current view mode
	 */
	public void setEntity(ExecutionEntityUIElement entity, JobViewMode viewMode) {
		String iconCls = "";
		this.associatedEntity = entity;
		
		if (viewMode == JobViewMode.MODE_SUBMITTED) {
			UIExecutionState execState = entity.getExecutionState();
			if (execState==UIExecutionState.NOT_STARTED)
				this.setDisabled(true);
			else {
				this.setDisabled(false);
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
			}
		}
		
		if (entity.isFulfilled())
			iconCls = "fulfilled-icon";

		this.setIconCls(iconCls);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.gwtext.client.widgets.Panel#setIconCls(java.lang.String)
	 */
	public void setIconCls(String iconCls) {
		String cls = this.getIconCls();
		if (cls==null || cls.length()==0)
			super.setIconCls(iconCls);
		else {
			Element el = this.getHeader().child("img[class~=\"x-panel-inline-icon\"]");
			el.setClassName("x-panel-inline-icon " + iconCls);
		}
	}
}
