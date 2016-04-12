package org.gcube.spatial.data.gis.is;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfrastructureCrawler {

	final static Logger logger= LoggerFactory.getLogger(InfrastructureCrawler.class);
	
	public static List<GeoServerDescriptor> queryforGeoServer(){
		List<GeoServerDescriptor> toReturn=new ArrayList<GeoServerDescriptor>();
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		 
		query.addCondition("$resource/Profile/Category/text() eq 'Gis'")
				.addCondition("$resource/Profile/Platform/Name/text() eq 'GeoServer'")
		         .setResult("$resource/Profile/AccessPoint");
		 
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		 
		List<AccessPoint> accesspoints = client.submit(query);
//		if(accesspoints.size()==0) throw new Exception("No Resource found under current scope "+ScopeProvider.instance.get()); 
		for (AccessPoint point : accesspoints) {
			try{
			toReturn.add(new GeoServerDescriptor(point.address(),point.username(),StringEncrypter.getEncrypter().decrypt(point.password()),0l));
			}catch(Exception e){
				logger.warn("Unable to decript password for "+point.username()+" in access point "+point.address()+", access to modify methods may fail"); 
			}
//			url=point.address();
//			user=point.username();
//			pwd=point.password();
		}
		return toReturn; 
	}
	
}
