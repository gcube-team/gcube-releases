package org.gcube.data.publishing.gCatfeeder.collectors;

import org.gcube.data.publishing.gCatFeeder.model.CatalogueInstanceDescriptor;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CatalogueInstanceNotFound;

public interface CatalogueRetriever {

	public CatalogueInstanceDescriptor getInstance() throws CatalogueInstanceNotFound;
	
}
