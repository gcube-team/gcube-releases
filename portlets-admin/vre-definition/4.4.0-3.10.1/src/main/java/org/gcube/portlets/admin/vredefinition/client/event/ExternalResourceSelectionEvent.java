package org.gcube.portlets.admin.vredefinition.client.event;

import java.util.List;

import org.gcube.portlets.admin.vredefinition.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredefinition.shared.ExternalResourceModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * @version 0.2 Sep 2012
 * 
 */
public class ExternalResourceSelectionEvent  extends GwtEvent<ExternalResourceSelectionEventHandler>{

	public static Type<ExternalResourceSelectionEventHandler> TYPE = new Type<ExternalResourceSelectionEventHandler>();
	private VREFunctionalityModel functionality;
	private List<ExternalResourceModel> associatedExternalResources;
	
	public ExternalResourceSelectionEvent(VREFunctionalityModel functionality, List<ExternalResourceModel> resources) {
		this.functionality = functionality;
		this.associatedExternalResources = resources;
	}
	
	public List<ExternalResourceModel> getAssociatedExternalResources() {
		return associatedExternalResources;
	}

	public VREFunctionalityModel getFunctionalityModel() {
		return functionality;
	}
	
	@Override
	protected void dispatch(ExternalResourceSelectionEventHandler handler) {
		handler.onSelectedExternalResources(this);
	}

	@Override
	public Type<ExternalResourceSelectionEventHandler> getAssociatedType() {
		return TYPE;
	}

	
}
