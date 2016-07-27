package org.gcube.portlets.admin.fhn_manager_portlet.client.event;

import java.util.Map;

import org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data.DataContainer;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.google.gwt.event.shared.GwtEvent;

public class RefreshGridEvent extends GwtEvent<RefreshGridEventHandler>{

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
	private DataContainer theDataContainer;
	
	public RefreshGridEvent(RefreshGridOptions options,DataContainer dataDestination) {
		this.options=options;
		this.theDataContainer=dataDestination;		
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
	
	public DataContainer getTheDataContainer() {
		return theDataContainer;
	}
}
