package org.gcube.rest.index.common.discover;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.rest.index.common.discover.exceptions.IndexDiscoverException;

public interface IndexDiscovererAPI {

	public Set<String> discoverFulltextIndexNodes(String scope);

	public Set<String> discoverFulltextIndexNodesOfThisAndAllOtherVres(String vreScope);
	

}
