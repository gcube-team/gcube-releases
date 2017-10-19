package gr.cite.geoanalytics.ows.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import gr.cite.geoanalytics.util.http.CustomException;

public class WcsClient extends OwsClient {

	public WcsClient() {
		super("WCS", "2.0.1");			// WCS must be uppercase!
	}
	
	public byte[] downloadCoverage(String url, String workspace, String name) throws CustomException {
		logger.debug("Downloading coverage");
		
		String request = "GetCoverage";
		String typeName = workspace + ":" + name;
		String outputFormat = "geotiff";

		try {
			url = validateGeoserverUrl(url);
		} catch (CustomException e) {
			throw new CustomException(e.getStatusCode(), "Failed to download coverage. " + e.getMessage(), e);
		}
		
		StringBuilder urlBuilder = new StringBuilder(url);
		urlBuilder.append("wcs");
		urlBuilder.append("?service=" + service);
		urlBuilder.append("&version=" + version);
		urlBuilder.append("&request=" + request);
		urlBuilder.append("&coverageId=" + typeName);
		urlBuilder.append("&format=" + outputFormat);
		url = urlBuilder.toString();
		
		byte[] data = null;
		InputStream is = null;
		
		try {
			is = new URL(url).openStream();
			data = IOUtils.toByteArray(is);
		} catch (IOException e) {
			logger.error("Could open connection to " + url);
		} finally {
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					logger.error("Could close downloaded coverage inputstream of " + url);
				}
			}
		}

		return data;
	}
}
