/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.datafetcher.converter;

import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.shared.GeonetworkMetadata;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GNMetadataConverter implements Converter<GNSearchResponse.GNMetadata, GeonetworkMetadata>{
	

	protected Logger logger = Logger.getLogger(GNMetadataConverter.class);
	public static final String NOT_FOUND = Constants.NOT_FOUND;
	
	@Override
	public GeonetworkMetadata convert(GNMetadata metadata) throws Exception {
	
//		logger.trace("convert metadata: "+metadata.getUUID());
		return new GeonetworkMetadata(metadata.getUUID(), metadata.getSchema(), metadata.getId());
	}
}
