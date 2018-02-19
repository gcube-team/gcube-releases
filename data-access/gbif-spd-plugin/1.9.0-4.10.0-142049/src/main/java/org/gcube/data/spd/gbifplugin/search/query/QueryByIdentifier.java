package org.gcube.data.spd.gbifplugin.search.query;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueryByIdentifier {

	private @NonNull String baseUri;
	
	private @NonNull String key; 	
	
	private @NonNull QueryType type;
	
	private List<String> paths = new ArrayList<String>();
	
	public void addPath(String path){
		paths.add(path);
	}
	
	public String build(){
		StringBuilder query = new StringBuilder(baseUri);
		if (!baseUri.endsWith("/")) query.append("/");
		query.append(this.type.getQueryEntry()).append("/");
		query.append(key);
		for (String path : paths)
			query.append("/").append(path);
		return query.toString();
	}
	
	
}
