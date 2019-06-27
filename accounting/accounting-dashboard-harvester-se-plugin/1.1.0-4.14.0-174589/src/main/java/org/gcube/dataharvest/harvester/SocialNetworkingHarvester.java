package org.gcube.dataharvest.harvester;

import java.util.Date;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.dataharvest.utils.Utils;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.json.JSONObject;

public abstract class SocialNetworkingHarvester extends BasicHarvester {
	
	public SocialNetworkingHarvester(Date start, Date end) throws Exception {
		super(start, end);
	}

	public static String CLASS_FORMAT = "$resource/Profile/ServiceClass/text() eq '%1s'";
	public static String NAME_FORMAT = "$resource/Profile/ServiceName/text() eq '%1s'";
	public static String STATUS_FORMAT = "$resource/Profile/DeploymentData/Status/text() eq 'ready'";
	public static String CONTAINS_FORMAT = "$entry/@EntryName eq '%1s'";
	
	public static String SERVICE_CLASS = "Portal";
	public static String SERVICE_NAME = "SocialNetworking";
	public static String ENTRY_NAME = "jersey-servlet";
	
	protected SimpleQuery getGCoreEndpointQuery() {
		return ICFactory.queryFor(GCoreEndpoint.class)
				.addCondition(String.format(CLASS_FORMAT, SERVICE_CLASS))
				.addCondition(String.format(NAME_FORMAT, SERVICE_NAME))
				.addCondition(String.format(STATUS_FORMAT))
				.addVariable("$entry", "$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint")
				.addCondition(String.format(CONTAINS_FORMAT, ENTRY_NAME))
				.setResult("$entry/text()");
	}
	
	protected String getAddress() {
		SimpleQuery gCoreEndpointQuery = getGCoreEndpointQuery();
		List<String> addresses = ICFactory.client().submit(gCoreEndpointQuery);
		if(addresses.size()==0) {
			throw new DiscoveryException("No running Social Networking Service");
		}
		return addresses.get(0);
	}
	
	
	protected JSONObject getJSONObject(String path) throws Exception {
		String token = SecurityTokenProvider.instance.get();
		String baseAddress = getAddress();
		StringBuffer sb = new StringBuffer(baseAddress);
		sb.append(path);
		sb.append(token);
		return new JSONObject(Utils.getJson(sb.toString()));
	}
	
}
