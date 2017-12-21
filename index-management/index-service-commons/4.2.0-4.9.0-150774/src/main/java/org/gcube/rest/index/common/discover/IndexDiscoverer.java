package org.gcube.rest.index.common.discover;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.rest.index.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IndexDiscoverer implements IndexDiscovererAPI {

	private static final Logger logger = LoggerFactory.getLogger(IndexDiscoverer.class);
	
	
	@Override
	public Set<String> discoverFulltextIndexNodesOfThisAndAllOtherVres(String vreScope){
		//TODO: ask parners if there's a way (library) to do this more efficiently
		String voScope = getVO(vreScope);
		return discoverFulltextIndexNodes(voScope);
	}
	
	
	@Override
	public Set<String> discoverFulltextIndexNodes(String scope){
		
		ScopeProvider.instance.set(scope);
		
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		
		query.addCondition("$resource/Profile/ServiceClass/text() eq '"+Constants.SERVICE_CLASS+"'")
		     .addCondition("$resource/Profile/ServiceName/text() eq '"+Constants.SERVICE_NAME+"'");
		
		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		
		List<GCoreEndpoint> eprs = client.submit(query);
		
		Set<String> clusterHosts = new HashSet<String>();
		for(GCoreEndpoint epr : eprs){
//			if(!epr.scopes().contains(scope))
//				continue;
			if(!"ready".equals(epr.profile().deploymentData().status().toLowerCase()))
				continue;
			for(Endpoint e : epr.profile().endpointMap().values().toArray(new Endpoint[epr.profile().endpointMap().values().size()]))
				if(!e.uri().toString().endsWith("/gcube/resource"))
					clusterHosts.add(e.uri().toString());
		}
		
		//TODO: this was added in order to avoid the problematic trash values (ghost instances) due to buggy IS discovery
//		Iterator<String> iter = clusterHosts.iterator();
//		while(iter.hasNext()){
//			String next = iter.next();
//			if(next.toLowerCase().contains("dl050.madgik.di.uoa.gr")||next.toLowerCase().contains("dl007.madgik.di.uoa.gr")||next.toLowerCase().contains("dionysus.di.uoa.gr"))
//				iter.remove();
//		}
		
		
		logger.info("Discovered on scope: "+scope+" the index cluster nodes: " + Arrays.toString(clusterHosts.toArray()));
		return clusterHosts;
		
	}
	
	/*
	 * This is a function to get a virtual organization (VO) from a given Virtual environment (VRE)
	 * Please, create 
	 */
	public static String getVO(String vre){
		if(vre.split("/+").length<=3)
			return vre;
		vre = vre.substring(1); //remove the beginning '/'
		String [] splits = vre.split("/+"); //split by one or multiple consecutive '/' (just in case)
		String vo = "";
		for(int i=0; i<splits.length-1; i++) 
			vo += "/"+splits[i];
		if(vo.isEmpty())
			vo = "/";
		return vo;
	}
	
	
	public static void main (String [] args){
		Set<String> endpoints = new IndexDiscoverer().discoverFulltextIndexNodes("/gcube/devsec/devVRE");
		System.out.println(Arrays.toString(endpoints.toArray()));
	}
	
}
