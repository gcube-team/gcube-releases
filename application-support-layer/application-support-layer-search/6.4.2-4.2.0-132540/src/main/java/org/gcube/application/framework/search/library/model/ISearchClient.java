package org.gcube.application.framework.search.library.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ISearchClient {
	public void setScope(String scope);
	public void initializeClient(String uri);
	public String query(String query, Set<String> sids, Boolean names) throws SearchASLException;
	public List<Map<String, String>> queryAndRead(String query,
			Set<String> sids, Boolean names) throws SearchASLException;
}
