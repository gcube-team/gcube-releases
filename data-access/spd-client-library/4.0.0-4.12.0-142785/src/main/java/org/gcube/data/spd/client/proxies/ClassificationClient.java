package org.gcube.data.spd.client.proxies;


import java.util.List;

import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.service.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedPluginException;
import org.gcube.data.streams.Stream;

public interface ClassificationClient {

	public Stream<TaxonomyItem> getTaxonChildrenById(String id) throws UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException;
	
	public Stream<TaxonomyItem> getTaxaByIds(List<String> ids);
		
	public Stream<TaxonomyItem> getTaxonTreeById(String id) throws UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException; 

	public Stream<TaxonomyItem> getSynonymsById(String id) throws UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException;
	
}
