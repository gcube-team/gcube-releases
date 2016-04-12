package org.gcube.portlets.admin.vredefinition.client.event;

import org.gcube.portlets.admin.vredefinition.client.model.WizardStepModel;

import com.google.gwt.event.shared.GwtEvent;


public class TreeNodeWizardMenuEvent extends GwtEvent<TreeNodeWizardMenuEventHandler>{

	public static Type<TreeNodeWizardMenuEventHandler> TYPE = new Type<TreeNodeWizardMenuEventHandler>();
	private WizardStepModel step;
	
	public TreeNodeWizardMenuEvent(WizardStepModel step) {
		this.step = step;
	}
	
	public WizardStepModel getStepModel() {
		return step;
	}
	
	@Override
	protected void dispatch(TreeNodeWizardMenuEventHandler handler) {
		handler.onClick(this);
	}

	@Override
	public Type<TreeNodeWizardMenuEventHandler> getAssociatedType() {
		return TYPE;
	}

}
