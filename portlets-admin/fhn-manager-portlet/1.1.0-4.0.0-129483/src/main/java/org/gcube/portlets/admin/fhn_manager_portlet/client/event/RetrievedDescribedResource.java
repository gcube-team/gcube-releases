package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.DescribedResource;

import com.google.gwt.event.shared.GwtEvent;

public class RetrievedDescribedResource extends GwtEvent<RetrievedDescribedResourceEventHandler> implements FutureEvent{
	private static Logger logger = Logger.getLogger(RetrievedDescribedResource.class+"");
	
	public static final Type<RetrievedDescribedResourceEventHandler> TYPE=new Type<RetrievedDescribedResourceEventHandler>();


	@Override
	public Type<RetrievedDescribedResourceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RetrievedDescribedResourceEventHandler handler) {
		handler.onRetrievedDescribedResource(this);

	}

	private DescribedResource describedResource;


	public RetrievedDescribedResource(DescribedResource describedResource) {
		super();
		this.describedResource = describedResource;
	}


	public DescribedResource getDescribedResource() {
		return describedResource;
	}

	@Override
	public void setResult(Object result) {
		try{
			describedResource=(DescribedResource) result;
		}catch(ClassCastException e){
			logger.log(Level.SEVERE,"Result "+result.getClass()+" is not a valid described resource");
		}
	}
}
