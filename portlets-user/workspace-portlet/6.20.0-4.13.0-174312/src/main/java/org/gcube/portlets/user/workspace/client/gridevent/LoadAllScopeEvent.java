package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class LoadAllScopeEvent extends GwtEvent<LoadAllScopeEventHandler> {
  public static Type<LoadAllScopeEventHandler> TYPE = new Type<LoadAllScopeEventHandler>();
  
	public LoadAllScopeEvent() {
	}

	@Override
	public Type<LoadAllScopeEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
	
	@Override
	protected void dispatch(LoadAllScopeEventHandler handler) {
		handler.onLoadScopes(this);
		
	}

}