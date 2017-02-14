/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.service;

import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;

import javax.servlet.http.HttpSession;

import org.gcube.portlets.user.geoexplorer.server.datafetcher.converter.CloseableIterator;
import org.gcube.portlets.user.geoexplorer.server.datafetcher.converter.ConversionIterator;
import org.gcube.portlets.user.geoexplorer.server.datafetcher.converter.GNMetadataConverter;
import org.gcube.portlets.user.geoexplorer.shared.FetchingElement;
import org.gcube.portlets.user.geoexplorer.shared.GeonetworkMetadata;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class IteratorChainBuilder {
	
	@SuppressWarnings("unchecked")
	public static <O extends FetchingElement> CloseableIterator<O> buildChain(CloseableIterator<GNMetadata> input,HttpSession session)
	{
		return (CloseableIterator<O>) buildMetadataChain(input, session);

	}
	
	protected static CloseableIterator<GeonetworkMetadata> buildMetadataChain(CloseableIterator<GNMetadata> input, HttpSession session)
	{
		//from GNMetadata to GeonetworkMetadata
		GNMetadataConverter converter = new GNMetadataConverter();
		ConversionIterator<GNMetadata, GeonetworkMetadata> inputConverter = new ConversionIterator<GNMetadata, GeonetworkMetadata>(input, converter);
		
		return inputConverter;
	}
	
}
