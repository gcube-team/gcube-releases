package org.gcube.data.spd.gbifplugin.search.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;


public @AllArgsConstructor enum  QueryType{
	Dataset("dataset"),
	Occurrence("occurrence"),
	Taxon("species"),
	Organization("organization");
	
	@Getter
	private @NonNull String queryEntry;

}
