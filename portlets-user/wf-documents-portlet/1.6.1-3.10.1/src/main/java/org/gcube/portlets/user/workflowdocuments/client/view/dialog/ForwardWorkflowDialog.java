package org.gcube.portlets.user.workflowdocuments.client.view.dialog;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.ForwardAction;
import org.gcube.portlets.admin.wfdocslibrary.shared.ForwardActionWithDest;
import org.gcube.portlets.admin.wfdocslibrary.shared.Step;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.user.workflowdocuments.client.event.ForwardEvent;
import org.gcube.portlets.user.workflowdocuments.shared.WorkflowDocument;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.shared.HandlerManager;

public class ForwardWorkflowDialog extends Dialog {

	public ForwardWorkflowDialog(final HandlerManager eventBus, final WorkflowDocument wfDoc, WfTemplate workflow) {
		super.setSize(550, 300);
		setHeading("Workflow Forward Operation");

		VerticalPanel vpanel = new VerticalPanel();
		vpanel.setSpacing(5);
		
		Html wfName = new Html("Forward " + wfDoc.getName() + " from step "+ wfDoc.getStatus());
		Html yourRole = new Html("Your role on this step is: " + wfDoc.getCurRole());
		vpanel.add(wfName);
		vpanel.add(yourRole);
		vpanel.setSpacing(10);
		Step toLookFor = new Step(wfDoc.getStatus(), null);
		ArrayList<ForwardActionWithDest> fwa = getForwardActionsWithDestination(toLookFor, workflow.getGraph());

		Html yourEntitledTo = new Html();
		vpanel.add(yourEntitledTo);		
		
		boolean atLeastOne = false;
		for (ForwardActionWithDest fa : fwa) {
			for (WfRole role : fa.getFwAction().getActions().keySet()) {
				if ( role.getRolename().equals(wfDoc.getCurRole()) ) {
					Button toAdd = new Button(fa.getToStepLabel());
					final String toStepLabel = fa.getToStepLabel();
					toAdd.addSelectionListener( new SelectionListener<ButtonEvent>() {  
						public void componentSelected(ButtonEvent ce) {  
							eventBus.fireEvent(new ForwardEvent(wfDoc, toStepLabel));
							hide();
						}  
					});  
					toAdd.setWidth(100);	
					vpanel.add(new Html("&nbsp;"));	
					HorizontalPanel buttonspanel = new HorizontalPanel();
					buttonspanel.setHorizontalAlign(HorizontalAlignment.CENTER);
					buttonspanel.setWidth("100%");
					buttonspanel.setSpacing(10);
					buttonspanel.add(toAdd);
					buttonspanel.add(new Html(" (" + getStatusDescription(workflow.getGraph(), toStepLabel)+ ")") );
					vpanel.add(buttonspanel);	
					atLeastOne = true;
				}
			}
		}		
		yourEntitledTo.setHtml((atLeastOne) ? "Based on your role you can forward to the following:" :
			"<b>Sorry, you don't have any forward permission on this step</b><br />if you think this is a mistake contact the author, <br /> author username: " + workflow.getAuthor());
			add(vpanel);
		setButtons(Dialog.CLOSE);
	}

	private String getStatusDescription(WfGraph graph, String statusToLookForDesc) {
		for (int i = 0; i < graph.getSteps().length; i++) {
			Step step = graph.getSteps()[i];
			if (step.getDescription() != null) {
				if (step.getLabel().trim().compareTo(statusToLookForDesc.trim()) == 0) {
					return step.getDescription();
				}
			}			
		}
		return "";
	}
	/**
	 * return the forward actions associated to a given source step
	 * @param source
	 * @return
	 */
	private ArrayList<ForwardActionWithDest> getForwardActionsWithDestination(Step source, WfGraph graph) {
		ArrayList<ForwardActionWithDest> fwActions = new ArrayList<ForwardActionWithDest>();
		ForwardAction[][] matrix = graph.getMatrix();
		Step[] steps = graph.getSteps();
		int i = graph.indexOf(source);
		if (i < 0) {
			throw new AssertionError("The source step doesn not belong to this graph");
		}
		for (int j = 0; j < steps.length; j++) {
			if (matrix[i][j] != null) 
				fwActions.add(new ForwardActionWithDest(matrix[i][j], steps[j].getLabel()));
		}
		return fwActions;
	}
}
