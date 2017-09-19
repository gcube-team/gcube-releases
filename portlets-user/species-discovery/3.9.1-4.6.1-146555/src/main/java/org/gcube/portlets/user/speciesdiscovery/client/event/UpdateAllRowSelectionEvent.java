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
public class UpdateAllRowSelectionEvent extends GwtEvent<UpdateAllRowSelectionEventHandler> {
	
	public static final GwtEvent.Type<UpdateAllRowSelectionEventHandler> TYPE = new Type<UpdateAllRowSelectionEventHandler>();
	private boolean selectionValue;
	private SearchResultType searchType;


	@Override
	public Type<UpdateAllRowSelectionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateAllRowSelectionEventHandler handler) {
		handler.onUpdateAllRowSelection(this);	
	}
	
	public UpdateAllRowSelectionEvent(boolean selectionValue, SearchResultType type){
		this.selectionValue = selectionValue;
		this.searchType = type;
		
	}

	public boolean getSelectionValue() {
		return selectionValue;
	}

	public SearchResultType getSearchType() {
		return searchType;
	}
	
}
