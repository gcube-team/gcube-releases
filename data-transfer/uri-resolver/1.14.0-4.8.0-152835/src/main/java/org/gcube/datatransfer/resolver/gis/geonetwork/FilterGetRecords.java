/**
 *
 */
package org.gcube.datatransfer.resolver.gis.geonetwork;

import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class FilterGetRecords.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 15, 2016
 */
public class FilterGetRecords {

	/**
	 *
	 */
	private static final String CSW_GET_RECORDS = "csw:GetRecords";

	private static final Logger logger = LoggerFactory.getLogger(FilterGetRecords.class);

	private List<String> foundPublicIds = null;


	/**
	 * Instantiates a new filter get records.
	 *
	 * @param readBody the read body
	 */
	public FilterGetRecords(String readBody) {

		if(readBody!=null && !readBody.isEmpty() && readBody.contains(CSW_GET_RECORDS)){
			logger.info("Is "+CSW_GET_RECORDS+" request, getting public ids");
			GeoNetworkReader reader;
			try {
				reader = GeoNetwork.get();
				final GNSearchRequest req=new GNSearchRequest();
				req.addParam(GNSearchRequest.Param.any,"");
				GNSearchResponse resp=reader.query(req);

				foundPublicIds = new ArrayList<String>();
				Iterator<GNMetadata> iterator=resp.iterator();
				while(iterator.hasNext()){
					foundPublicIds.add(iterator.next().getUUID());
				}
				logger.info("Public Metadata ids are: "+foundPublicIds.size());
			}catch (Exception e) {
				logger.error("Error during sending GNSearchRequest: ",e);
			}
		}else
			logger.trace("Is not a"+CSW_GET_RECORDS+" request, skipping");
	}



	/**
	 * Gets the public file identifiers.
	 *
	 * @return the public file identifiers
	 */
	public List<String> getPublicFileIdentifiers(){

		logger.info("Performing query to retrieve the public file identifiers");
		GeoNetworkReader reader;
		try {
			reader = GeoNetwork.get();
			final GNSearchRequest req=new GNSearchRequest();
			req.addParam(GNSearchRequest.Param.any,"");
			GNSearchResponse resp=reader.query(req);

			foundPublicIds = new ArrayList<String>();
			Iterator<GNMetadata> iterator=resp.iterator();
			while(iterator.hasNext()){
				foundPublicIds.add(iterator.next().getUUID());
			}
			logger.info("Public Metadata ids are: "+foundPublicIds.size());

		}catch (Exception e) {
			logger.error("Error during performing "+CSW_GET_RECORDS+": ",e);
		}

		return foundPublicIds;
	}


	/**
	 * Gets the found public ids.
	 *
	 * @return the foundPublicIds
	 */
	public List<String> getFoundPublicIds() {

		return foundPublicIds==null || foundPublicIds.isEmpty()? null: foundPublicIds;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("FilterGetRecords [foundPublicIds=");
		builder.append(foundPublicIds);
		builder.append("]");
		return builder.toString();
	}
}
