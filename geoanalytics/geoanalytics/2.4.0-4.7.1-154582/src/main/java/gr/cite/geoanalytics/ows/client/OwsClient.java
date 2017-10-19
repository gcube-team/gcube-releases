package gr.cite.geoanalytics.ows.client;

import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import gr.cite.geoanalytics.util.http.CustomException;

public class OwsClient {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected String service;
	protected String version;

	protected OwsClient(String service, String version) {
		this.service = service;
		this.version = version;
	}

	public String getRequest(String url, Map<String, String> parameters) {
		Client client = Client.create();
		WebResource webResource = null;

		url += service + "?";

		MultivaluedMap<String, String> nameValuePairs = new MultivaluedMapImpl();
		for (Map.Entry<String, String> params : parameters.entrySet()) {
			if (!params.getKey().trim().isEmpty() && params.getValue() != null && !params.getValue().trim().isEmpty()) {
				nameValuePairs.add(params.getKey(), params.getValue());
			}
		}

		webResource = client.resource(url).queryParams(nameValuePairs);

		ClientResponse response = webResource.get(ClientResponse.class);

		if (response.getStatus() == 201 || response.getStatus() == 200) {
			return response.getEntity(String.class);
		} else {
			return "";
		}
	}

	protected String validateGeoserverUrl(String url) throws CustomException {
		Assert.isTrue(url != null && url.length() > 0, "URL cannot be empty");

		if (url.endsWith("geoserver")) {
			url += "/";
		} else if (!url.endsWith("geoserver/")) {
			throw new CustomException("Invalid Geoserver URL");
		}

		return url;
	}

	public boolean layerIsVector(String url, String workspace, String name) throws Exception {
		logger.debug("Get external layer type (vector or raster)");

		String getCapabilitiesXml = new WfsClient().getLayerCapabilities(url, workspace, name);
				
		XPathUtils xPathUtils = new XPathUtils(getCapabilitiesXml);

		Object layerInfo = xPathUtils.evaluateNode("//FeatureTypeList/FeatureType[Name='" + workspace + ":" + name + "']");
		
		return layerInfo != null ? true : false;
	}
}
