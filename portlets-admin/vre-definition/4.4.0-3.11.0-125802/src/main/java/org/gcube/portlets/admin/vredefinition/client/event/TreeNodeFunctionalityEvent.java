package org.gcube.portlets.admin.vredefinition.client.event;

import org.gcube.portlets.admin.vredefinition.client.model.VREFunctionalityModel;

import com.google.gwt.event.shared.GwtEvent;


public class TreeNodeFunctionalityEvent  extends GwtEvent<TreeNodeFunctionalityEventHandler>{

	public static Type<TreeNodeFunctionalityEventHandler> TYPE = new Type<TreeNodeFunctionalityEventHandler>();
	private VREFunctionalityModel functionality;
	
	public TreeNodeFunctionalityEvent(VREFunctionalityModel functionality) {
		this.functionality = functionality;
	}
	
	public VREFunctionalityModel getFunctionalityModel() {
		return functionality;
	}
	
	@Override
	protected void dispatch(TreeNodeFunctionalityEventHandler handler) {
		handler.onClick(this);
	}

	@Override
	public Type<TreeNodeFunctionalityEventHandler> getAssociatedType() {
		return TYPE;
	}

	
}
