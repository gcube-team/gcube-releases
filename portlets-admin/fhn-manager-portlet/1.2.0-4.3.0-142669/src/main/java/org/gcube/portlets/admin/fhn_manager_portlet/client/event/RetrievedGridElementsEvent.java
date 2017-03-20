package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import java.util.Set;

import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data.DataContainer;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;

import com.google.gwt.event.shared.GwtEvent;

public class RetrievedGridElementsEvent  extends GwtEvent<RetrievedGridElementsEventHandler> implements FutureEvent{

	public static Type<RetrievedGridElementsEventHandler> TYPE= new Type<RetrievedGridElementsEventHandler>();
	
	
	private DataContainer theDataContainer;
	
	private Object theResult;
	private ObjectType type;
	
	public RetrievedGridElementsEvent(DataContainer theDataContainer,ObjectType type) {
		super();
		this.theDataContainer = theDataContainer;
		this.type=type;
	}

	public ObjectType getStorableType() {
		return type;
	}
	
	@Override
	protected void dispatch(RetrievedGridElementsEventHandler handler) {
		handler.onRetrievedElements(this);
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RetrievedGridElementsEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public DataContainer getTheDataContainer() {
		return theDataContainer;
	}

	
	
	@Override
	public void setResult(Object result) {
		theResult=result;
	}
	
	public Object getTheResult() {
		return theResult;
	}
}
