/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SearchTypeSelectedEvent extends GwtEvent<SearchTypeSelectedEventHandler> {
	
	public static final GwtEvent.Type<SearchTypeSelectedEventHandler> TYPE = new Type<SearchTypeSelectedEventHandler>();
	private SearchType searchType;
	
	
	public SearchTypeSelectedEvent(SearchType searchType) {
		this.searchType = searchType;
	}

	@Override
	public Type<SearchTypeSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchTypeSelectedEventHandler handler) {
		handler.onSearchTypeSelected(this);	
	}

	/**
	 * @return
	 */
	public SearchType getType() {
		return searchType;
	}
	
}
