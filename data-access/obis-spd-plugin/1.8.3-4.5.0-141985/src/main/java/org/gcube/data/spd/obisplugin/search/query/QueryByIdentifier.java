package org.gcube.data.spd.obisplugin.search.query;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
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
		log.trace("query by dentifier is "+query.toString());
		return query.toString();
	}
	
	
}
