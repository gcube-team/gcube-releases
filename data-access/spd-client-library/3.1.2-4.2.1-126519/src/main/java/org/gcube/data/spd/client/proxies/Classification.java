package org.gcube.data.spd.client.proxies;


import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.stubs.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedPluginException;
import org.gcube.data.streams.Stream;

public interface Classification {

	public Stream<TaxonomyItem> getTaxonChildrenById(final String id) throws UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException;
	
	public Stream<TaxonomyItem> getTaxaByIds(final Stream<String> ids);
		
	public Stream<TaxonomyItem> getTaxonTreeById(final String id) throws UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException; 

	public Stream<TaxonomyItem> getSynonymsById(String id) throws UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException;
	
}
