package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class SearchTextEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 9, 2015
 */
public class SearchTextEvent extends GwtEvent<SearchTextEventHandler> {
	public static Type<SearchTextEventHandler> TYPE = new Type<SearchTextEventHandler>();

	private String textSearch = null;

	private String folderId;

	/**
	 * Instantiates a new search text event.
	 *
	 * @param text the text
	 * @param folderId the folder id
	 */
	public SearchTextEvent(String text, String folderId) {
		this.textSearch = text;
		this.folderId = folderId;
	}

	/**
	 * @return the folderId
	 */
	public String getFolderId() {
		return folderId;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<SearchTextEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(SearchTextEventHandler handler) {
		handler.onSearchText(this);

	}

	/**
	 * Gets the text search.
	 *
	 * @return the text search
	 */
	public String getTextSearch() {
		return textSearch;
	}

}