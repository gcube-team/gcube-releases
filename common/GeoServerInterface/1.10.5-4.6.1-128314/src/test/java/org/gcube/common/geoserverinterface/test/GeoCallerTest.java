package org.gcube.common.geoserverinterface.test;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.URI;
import java.net.URISyntaxException;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.geoserverinterface.DataTransferUtl;
import org.gcube.common.geoserverinterface.GeoCaller;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverMethodResearch;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class GeoCallerTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	
	private static final String scope="/d4science.research-infrastructures.eu/EUBrazilOpenBio/SpeciesLab";
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("Scope : "+scope);
		
		ScopeProvider.instance.set(scope);
		
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		
		//Query For GN
		SimpleQuery gnQuery = queryFor(ServiceEndpoint.class);
		gnQuery.addCondition("$resource/Profile/Category/text() eq 'Gis'")
		.addCondition("$resource/Profile/Platform/Name/text() eq 'geonetwork'")				
         .setResult("$resource/Profile/AccessPoint");

		AccessPoint gnPoint=client.submit(gnQuery).get(0);
		
		//Query for GS
		SimpleQuery gsQuery = queryFor(ServiceEndpoint.class);
		gsQuery.addCondition("$resource/Profile/Category/text() eq 'Gis'")
		.addCondition("$resource/Profile/Platform/Name/text() eq 'GeoServer'")				
         .setResult("$resource/Profile/AccessPoint");

		AccessPoint gsPoint=client.submit(gsQuery).get(0);
		
		System.out.println("Instantiating caller with following parameters");
		System.out.println(gnPoint.address());
		System.out.println(gsPoint.address());
		
		GeoCaller caller=new GeoCaller(
				gnPoint.address(), 
				gnPoint.username(),
				decrypt(gnPoint.password()), 
				gsPoint.address(), 
				gnPoint.username(), 
				decrypt(gsPoint.password()), 
				GeoserverMethodResearch.MOSTUNLOAD);
		
		System.out.println("Caller's current wms "+caller.getCurrentWmsGeoserver());
		System.out.println("Agent params : "+getAgentParams(caller.getCurrentWmsGeoserver()));
		
		System.out.println("Found wms was "+gsPoint.address());
		System.out.println("Agent params : "+getAgentParams(gsPoint.address()));
		
		
		
	}

	private static final String getAgentParams(String wmsUrl) throws URISyntaxException{
		String host=(new URI(wmsUrl).getHost());		
		return "Host : "+host+", found port "+DataTransferUtl.getAgentPortOnHost(host);
	}
	
	
	private static final String decrypt(String toDecrypt) throws Exception{
		return StringEncrypter.getEncrypter().decrypt(toDecrypt);
	}
	
}
