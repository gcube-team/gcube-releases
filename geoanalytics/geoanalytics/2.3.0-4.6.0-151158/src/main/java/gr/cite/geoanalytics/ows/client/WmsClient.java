package gr.cite.geoanalytics.ows.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerBounds;
import gr.cite.geoanalytics.util.http.CustomException;

public class WmsClient extends OwsClient {

	public WmsClient() {
		super("wms", "1.1.0");
	}

	public String getCapabilities(String url) throws Exception {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("service", service);
		parameters.put("version", version);
		parameters.put("request", "getCapabilities");

		url = validateUrl(url);

		String xml = getRequest(url, parameters);
		 
		logger.info("xml = " + xml);

		return xml;
	}

	public String getLayerCapabilities(String url, String workspace, String name) throws Exception {
		logger.debug("Requesting GetCapabilities");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("service", service);
		parameters.put("version", version);
		parameters.put("request", "getCapabilities");

		url = validateUrl(url);

		url += workspace + "/" + name + "/";
		
		String xml = getRequest(url, parameters);

		logger.debug("xml = " + xml);

		return xml;
	}

	public WmsLayer parseLayerGetCapabilities(String getCapabilitiesXml, String geoserverUrl, String workspace, String name) throws Exception {
		logger.debug("Parsing layer GetCapabilities");

		WmsLayer wmsLayer = new WmsLayer();

		try {
			XPathUtils xPathUtils = new XPathUtils(getCapabilitiesXml);

			Object layerInfo = xPathUtils.evaluateNode("//Layer/Layer[Name='" + name + "']");

			if (layerInfo != null) {
				String title = xPathUtils.evaluateStringOf(layerInfo, "Title");
				String description = xPathUtils.evaluateStringOf(layerInfo, "Abstract");
				String style = xPathUtils.evaluateStringOf(layerInfo, "Style/Name");
				
				double minX = xPathUtils.evaluateNumberOf(layerInfo, "LatLonBoundingBox/@minx");
				double minY = xPathUtils.evaluateNumberOf(layerInfo, "LatLonBoundingBox/@miny");
				double maxX = xPathUtils.evaluateNumberOf(layerInfo, "LatLonBoundingBox/@maxx");
				double maxY = xPathUtils.evaluateNumberOf(layerInfo, "LatLonBoundingBox/@maxy");
				
				LayerBounds latLongBoundingBox = new LayerBounds(minX, minY, maxX, maxY);	
				
				wmsLayer.setName(name);
				wmsLayer.setWorkspace(workspace);
				wmsLayer.setGeoserverUrl(geoserverUrl);
				wmsLayer.setTitle(title);
				wmsLayer.setDescription(description);
				wmsLayer.setStyle(style);
				wmsLayer.setLatLongBoundingBox(latLongBoundingBox);	
			} else {
				throw new CustomException(HttpStatus.NOT_FOUND, "Layer not found in external Geoserver");
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new CustomException(HttpStatus.UNPROCESSABLE_ENTITY, "Layer is not well configured in external Geoserver", e);
		}
		
		logger.debug(wmsLayer.toString());

		return wmsLayer;
	}
}
