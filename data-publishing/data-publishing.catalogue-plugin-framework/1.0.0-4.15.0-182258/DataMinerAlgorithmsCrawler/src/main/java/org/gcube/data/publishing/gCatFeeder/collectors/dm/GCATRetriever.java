package org.gcube.data.publishing.gCatFeeder.collectors.dm;

import org.gcube.data.publishing.gCatFeeder.model.CatalogueInstanceDescriptor;
import org.gcube.data.publishing.gCatfeeder.collectors.CatalogueRetriever;

public class GCATRetriever implements CatalogueRetriever {

	private static GCATRetriever instance=null;
	
	static synchronized GCATRetriever get() {
		if(instance==null) instance =new GCATRetriever();
		return instance;
	}
	
	
	
	@Override
	public CatalogueInstanceDescriptor getInstance() {
//		throw new RuntimeException("Implement this");
//		GCoreEndpoint ep=ISUtils.queryForGCoreEndpoint("NO", "NO");
		return new CatalogueInstanceDescriptor();
	}
	
}
