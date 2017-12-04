/**
 *
 */
package org.gcube.portlets.user.geoexplorer.server.util;

import org.apache.log4j.Logger;
import org.gcube.spatial.data.geoutility.bean.WmsParameters;
import org.gcube.spatial.data.geoutility.wms.WmsUrlValidator;



/**
 * The Class CheckLayerNameFromWmsRequestUtils.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 25, 2016
 */
public class CheckLayerNametUtils {

	public static Logger logger = Logger.getLogger(CheckLayerNametUtils.class);

	/**
	 * Checks if layer name exists into input WMS request, otherwise the layer name passed is added and new WMS request returned.
	 *
	 * @param wmsRequest the wms request
	 * @param layerName the layer name
	 * @return the wms request with layer name passed
	 */
	public static String checkLayerNameIntoWMSRequest(String wmsRequest, String layerName){
		try {

			WmsUrlValidator validator;
			String checkedLayerName = WmsUrlValidator.getValueOfParameter(WmsParameters.LAYERS, layerName);
			String checkedParams;
			if(checkedLayerName==null){ //layer name is not found, Adding it..
				checkedParams = WmsUrlValidator.setValueOfParameter(WmsParameters.LAYERS, wmsRequest, layerName, true);
			}else
				checkedParams = wmsRequest;

			validator = new WmsUrlValidator(checkedParams);
			String checkedWmsRequest = validator.parseWmsRequest(true, true);
			logger.debug("Image preview request: "+checkedWmsRequest);
			return checkedWmsRequest;
		}catch (Exception e) {
			logger.error("Error in calculating wms request, returning input request: "+wmsRequest, e);
			return wmsRequest;
		}
	}
}
