package org.gcube.rest.index.service.inject;

import org.gcube.rest.index.client.IndexClient;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.resources.IndexResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class IndexClientBuilderProvider implements Provider<IndexClient.Builder> {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(IndexClientBuilderProvider.class);
	
	private IndexDiscovererAPI<IndexResource> indexDiscoverer;
	
	@Inject
	public IndexClientBuilderProvider(IndexDiscovererAPI<IndexResource> indexDiscoverer) {
		this.indexDiscoverer = indexDiscoverer;
	}
	
	@Override
	public IndexClient.Builder get() {
		return new IndexClient.Builder(this.indexDiscoverer);
	}

	
}