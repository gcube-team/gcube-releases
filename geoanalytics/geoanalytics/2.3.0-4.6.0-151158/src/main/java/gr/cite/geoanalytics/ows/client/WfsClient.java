package gr.cite.geoanalytics.ows.client;

import java.util.HashMap;
import java.util.Map;

public class WfsClient extends OwsClient {

	public WfsClient() {
		super("wfs", "1.1.0");
	}

	public void describeFeatureType(String url, String workspace, String name) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("service", service);
		parameters.put("version", version);
		parameters.put("request", "DescribeFeatureType");
		parameters.put("typeNames", workspace + ":" + name);
		String results = getRequest(url, parameters);

		System.out.println(results);
	}
}
