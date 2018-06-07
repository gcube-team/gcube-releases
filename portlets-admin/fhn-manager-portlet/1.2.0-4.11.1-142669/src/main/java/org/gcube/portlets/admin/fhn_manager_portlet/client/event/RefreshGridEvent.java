package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import java.util.Map;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.google.gwt.event.shared.GwtEvent;

public class RefreshGridEvent extends GwtEvent<RefreshGridEventHandler> implements CascadedEvent, FutureEvent{

	public static class RefreshGridOptions{
		private ObjectType type;
		private Map<String,String> filters;
		public RefreshGridOptions(ObjectType type, Map<String, String> filters) {
			super();
			this.type = type;
			this.filters = filters;
		}
		/**
		 * @return the type
		 */
		public ObjectType getType() {
			return type;
		}
		/**
		 * @return the filters
		 */
		public Map<String, String> getFilters() {
			return filters;
		}
		
		
	}
	
	public static Type<RefreshGridEventHandler> TYPE= new Type<RefreshGridEventHandler>();
	
	private RefreshGridOptions options;
	
	private FutureEvent cascadeEvent=null;
	
	
	public RefreshGridEvent(RefreshGridOptions options,FutureEvent cascade) {
		this.options=options;		
		this.cascadeEvent=cascade;
	}
	
	@Override
	public Type<RefreshGridEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RefreshGridEventHandler handler) {
		handler.onRefreshGrid(this);
	}
	
	public RefreshGridOptions getOptions() {
		return options;
	}
	
	
	@Override
	public FutureEvent getCascade() {
		return cascadeEvent;
	}
	
	@Override
	public void setCascade(FutureEvent theEvent) {
		cascadeEvent=theEvent;
	}

	@Override
	public void setResult(Object result) {
		
	}
}
