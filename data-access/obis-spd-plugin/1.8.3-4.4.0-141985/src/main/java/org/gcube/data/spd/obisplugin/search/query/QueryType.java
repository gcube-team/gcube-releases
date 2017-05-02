package org.gcube.data.spd.obisplugin.search.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;


public @AllArgsConstructor enum  QueryType{
	Occurrence("occurrence"),
	Taxon("taxon"),
	Dataset("resource");
	
	@Getter
	private @NonNull String queryEntry;

}
