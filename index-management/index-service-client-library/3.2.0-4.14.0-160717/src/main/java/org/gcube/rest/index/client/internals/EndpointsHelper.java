package org.gcube.rest.index.client.internals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.gcube.rest.index.client.cache.CacheConfig;
import org.gcube.rest.index.common.discover.IndexDiscoverer;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.springframework.cache.annotation.Cacheable;

public class EndpointsHelper {


	
//	@Cacheable(CacheConfig.ENDPOINTS)
	public static Set<String> getEndpointsOfScope(String scope){
		
		IndexDiscovererAPI discoverer = new IndexDiscoverer();
		Set<String> endpoints = discoverer.discoverFulltextIndexNodesOfThisAndAllOtherVres(scope);
		return endpoints;

	}
	
	
	
}
