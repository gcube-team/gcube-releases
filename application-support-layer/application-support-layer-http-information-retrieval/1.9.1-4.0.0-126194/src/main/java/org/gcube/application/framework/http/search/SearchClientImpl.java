package org.gcube.application.framework.http.search;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.framework.search.library.model.ISearchClient;
import org.gcube.application.framework.search.library.model.SearchASLException;
import org.gcube.search.SearchClient;
import org.gcube.search.exceptions.SearchException;

public class SearchClientImpl implements ISearchClient {

	private SearchClient client = new SearchClient();
	
	@Override
	public void setScope(String scope) {
		client.setScope(scope);
	}

	@Override
	public void initializeClient(String uri) {
		client.initializeClient(uri);
	}

	@Override
	public String query(String query, Set<String> sids, Boolean names)
			throws SearchASLException {
		
		try {
			return client.query(query, sids, names);
		} catch (SearchException e) {
			throw new SearchASLException(e);
		}
		
	}

	@Override
	public List<Map<String, String>> queryAndRead(String query,
			Set<String> sids, Boolean names) throws SearchASLException {
		try {
			return client.queryAndRead(query, sids, names);
		} catch (SearchException e) {
			throw new SearchASLException(e);
		}
	}

}

