/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ReLoadListJobEvent extends GwtEvent<ReLoadListJobEventHandler> {
	
	public static final GwtEvent.Type<ReLoadListJobEventHandler> TYPE = new Type<ReLoadListJobEventHandler>();

	private SearchResultType loadType;
	
	@Override
	public Type<ReLoadListJobEventHandler> getAssociatedType() {
		return TYPE;
	}

	public ReLoadListJobEvent(SearchResultType loadType){
		this.loadType = loadType;
	}

	public SearchResultType getLoadType() {
		return loadType;
	}

	@Override
	protected void dispatch(ReLoadListJobEventHandler handler) {
		handler.onLoadJobList(this);
		
	}
	
}
