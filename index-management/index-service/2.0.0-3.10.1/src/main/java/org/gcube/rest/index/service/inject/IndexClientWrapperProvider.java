package org.gcube.rest.index.service.inject;

import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.resources.IndexResource;
import org.gcube.rest.index.service.IndexClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class IndexClientWrapperProvider implements Provider<IndexClientWrapper> {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(IndexClientWrapperProvider.class);
	
	private final IndexDiscovererAPI<IndexResource> indexDiscoverer;
	
	@Inject
	public IndexClientWrapperProvider(IndexDiscovererAPI<IndexResource> indexDiscoverer) {
		this.indexDiscoverer = indexDiscoverer;
	}
	
	@Override
	public IndexClientWrapper get() {
		return new IndexClientWrapper(this.indexDiscoverer);
	}

	
}