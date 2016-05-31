package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.forms;

import org.gcube.portlets.admin.fhn_manager_portlet.client.event.CascadedEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.CreateElementEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.GenericParameterEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data.AdvancedGrid;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;

public class WizardConfiguration {

	public static WizardConfiguration CREATE_REMOTE_NODE=null;
	public static WizardConfiguration CREATE_SERVICE_PROFILE=null;
	public static WizardConfiguration CREATE_VM_PROVIDER=null;
	public static WizardConfiguration CREATE_VM_TEMPLATE=null;
	
	static{
		// Create Remote Node
		CREATE_REMOTE_NODE=new WizardConfiguration(
		new StepDefinition[]{
			new ResourceSelectionStep<ServiceProfile>(ObjectType.SERVICE_PROFILE),
			new ResourceSelectionStep<ServiceProfile>(ObjectType.VM_TEMPLATES),
		}, "Create Remote Node",new CreateElementEvent(ObjectType.REMOTE_NODE, null));		
		((CascadedEvent)CREATE_REMOTE_NODE.theEvent).setCascade(AdvancedGrid.getCentralGrid(ObjectType.REMOTE_NODE).getRefreshEvent());
		
	}
	
	
	
	
	public WizardConfiguration(StepDefinition[] steps, String title,
			GenericParameterEvent theEvent) {
		super();
		this.steps = steps;
		this.title = title;
		this.theEvent = theEvent;
	}

	private StepDefinition[] steps;
	
	private String title;
	
	private GenericParameterEvent theEvent;

	/**
	 * @return the steps
	 */
	public StepDefinition[] getSteps() {
		return steps;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	public GenericParameterEvent getTheEvent() {
		return theEvent;
	}
	
	
}
