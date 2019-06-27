package org.gcube.data.publishing.gCatfeeder.collectors;

import java.util.Collection;
import java.util.Set;

import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatfeeder.collectors.model.CustomData;

public interface DataTransformer<T extends CatalogueFormatData,E extends CustomData> {

		
	public Set<T> transform(Collection<E> collectedData); 
}
