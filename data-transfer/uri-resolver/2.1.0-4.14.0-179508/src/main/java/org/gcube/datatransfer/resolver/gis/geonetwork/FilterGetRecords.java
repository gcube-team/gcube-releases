/**
 *
 */
package org.gcube.datatransfer.resolver.gis.geonetwork;

import java.util.List;

import org.gcube.datatransfer.resolver.caches.LoadingGNPublicLayerIDsInstanceCache;
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

	private List<String> foundPublicLayerIds = null;

	private String geonetworkEndPoint;


	/**
	 * Instantiates a new filter get records.
	 *
	 * @param readBody the read body
	 * @param geonetworkEndPoint the geonetwork end point
	 */
	public FilterGetRecords(String readBody, String geonetworkEndPoint) {
		this.geonetworkEndPoint = geonetworkEndPoint;

		if(readBody!=null && !readBody.isEmpty() && readBody.contains(CSW_GET_RECORDS)){
			logger.info("The request is "+CSW_GET_RECORDS+" so getting GN public layer IDs");
			loadGNPublicLayers();
		}else
			logger.trace("Is not a"+CSW_GET_RECORDS+" request, skipping");
	}
	
	
	/**
	 * Gets the found public ids.
	 *
	 * @return the found public ids
	 */
	public List<String> getFoundPublicIds() {
		
		if(foundPublicLayerIds==null) {
			loadGNPublicLayers();
		}
		
		if(foundPublicLayerIds.isEmpty()) {
			return null;
		}
		
		return foundPublicLayerIds;
	}
	
	private void loadGNPublicLayers(){
		try {
			foundPublicLayerIds = LoadingGNPublicLayerIDsInstanceCache.get(geonetworkEndPoint);
			logger.info("For the GN {}, I found {} public ID layer/s",geonetworkEndPoint,foundPublicLayerIds.size());
		}catch (Exception e) {
			logger.error("Error occurred on loading cache of GN public IDs: ",e);
		}
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("FilterGetRecords [foundPublicIds=");
		builder.append(foundPublicLayerIds);
		builder.append("]");
		return builder.toString();
	}
}
