package org.gcube.portlets.user.workspace.client.gridevent;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ScopeChangeEvent extends GwtEvent<ScopeChangeEventHandler> {
  public static Type<ScopeChangeEventHandler> TYPE = new Type<ScopeChangeEventHandler>();
  
  private String scopeId;


	public ScopeChangeEvent(String scopeId) {
		this.scopeId = scopeId;
	}

	@Override
	public Type<ScopeChangeEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
	
	@Override
	protected void dispatch(ScopeChangeEventHandler handler) {
		handler.onLoadScope(this);
		
	}

	public String getScopeId() {
		return scopeId;
	}

}