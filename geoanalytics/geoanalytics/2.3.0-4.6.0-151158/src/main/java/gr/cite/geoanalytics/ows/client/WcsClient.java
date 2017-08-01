package gr.cite.geoanalytics.ows.client;

import java.util.HashMap;
import java.util.Map;

public class WcsClient extends OwsClient {

	public WcsClient() {
		super("wcs", "1.1.0");
	}

	public void wcsRequest() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("service", service);
		parameters.put("version", version);
		parameters.put("request", "DescribeFeatureType");
		//parameters.put("typeNames", externalLayer.getWorkspace() + ":" + externalLayer.getName());		
	}
}
