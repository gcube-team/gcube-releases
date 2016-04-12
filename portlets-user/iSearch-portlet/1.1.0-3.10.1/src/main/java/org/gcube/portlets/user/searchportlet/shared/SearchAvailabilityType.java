package org.gcube.portlets.user.searchportlet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum SearchAvailabilityType implements IsSerializable{
	NO_COLLECTION_SELECTED,
	MANY_COLLECTIONS_SELECTED,
	SEARCH_UNAVAILABLE,
	SEARCH_AVAILABLE,
	FTS_NOGEO_AVAILABLE,
	GEO_NOFTS_AVAILABLE,
	FTS_GEO_AVAILABLE,
	NOFTS_NOGEO_AVAILABLE
}

