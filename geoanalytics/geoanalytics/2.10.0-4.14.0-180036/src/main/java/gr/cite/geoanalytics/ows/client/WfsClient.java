package gr.cite.geoanalytics.ows.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import gr.cite.geoanalytics.util.http.CustomException;

public class WfsClient extends OwsClient {

	public WfsClient() {
		super("wfs", "1.1.0");
	}

	public String getLayerCapabilities(String url, String workspace, String name) throws Exception {
		logger.debug("Requesting GetCapabilities");

		Map<String, String> parameters = new HashMap<>();
		parameters.put("service", service);
		parameters.put("version", version);
		parameters.put("request", "getCapabilities");

		url = validateGeoserverUrl(url);
		
		url += workspace + "/" + name + "/";

		String getCapabilitiesXml = getRequest(url, parameters);

		logger.debug("xml = " + getCapabilitiesXml);

		return getCapabilitiesXml;
	}

	public byte[] downloadShapefile(String url, String workspace, String name) throws CustomException {
		logger.debug("Downloading shapefile");
		
		String request = "GetFeature";
		String typeName =  workspace + ":" + name;
		String outputFormat =  "shape-zip";
		
		try {
			url = validateGeoserverUrl(url);
		} catch (CustomException e) {
			throw new CustomException(e.getStatusCode(), "Failed to download shapefile. " + e.getMessage(), e);
		}
		
		StringBuilder urlBuilder = new StringBuilder(url);
		urlBuilder.append("ows");
		urlBuilder.append("?service=" + service);
		urlBuilder.append("&version=" + version);
		urlBuilder.append("&request=" + request);
		urlBuilder.append("&typeName=" + typeName);
		urlBuilder.append("&outputFormat=" + outputFormat);
		url = urlBuilder.toString();
		
		byte[] data = null;
		InputStream is = null;
		
		try {
			is = new URL(url).openStream();
			data = IOUtils.toByteArray(is);
		} catch (IOException e) {
			logger.error("Could open connection to " + url);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.error("Could close downloaded shapefile inputstream of " + url);
			}
		}

		return data;
	}
}
