/**
 *
 */
package org.gcube.spatial.data.geoutility;



/**
 * The Class GeoWmsServiceUtility.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 25, 2016
 */
public class GeoWmsServiceUtility {

	public static final String OWS = "ows";
	public static final String SERVICE_WMS = "service=wms";


	/**
	 * Instantiates a new geo service wms utility.
	 */
	public GeoWmsServiceUtility() {
	}


	/**
	 * Checks if is WMS service.
	 *
	 * @param wmsRequest the wms request
	 * @return true, if is WMS service
	 * @throws Exception the exception
	 */
	public static boolean isWMSService(String wmsRequest) throws Exception{
		validateWmsRequest(wmsRequest);
		return wmsRequest.toLowerCase().lastIndexOf(SERVICE_WMS)>-1;
	}


	/**
	 * Validate wms request.
	 *
	 * @param wmsRequest the wms request
	 * @throws Exception the exception
	 */
	private static void validateWmsRequest(String wmsRequest) throws Exception{
		if(wmsRequest==null || wmsRequest.isEmpty())
			throw new Exception("Wms Request is null or empty!");
	}


	/**
	 * Checks if is OWS serice.
	 *
	 * @param wmsRequest the wms request
	 * @return true, if is OWS serice
	 * @throws Exception the exception
	 */
	public static boolean isOWSSerice(String wmsRequest) throws Exception{
		validateWmsRequest(wmsRequest);
		return wmsRequest.toLowerCase().contains(OWS);
	}
}
