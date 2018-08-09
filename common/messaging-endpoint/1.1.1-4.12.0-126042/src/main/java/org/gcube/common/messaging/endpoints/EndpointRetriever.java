package org.gcube.common.messaging.endpoints;


import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Andrea
 *
 */
public class EndpointRetriever implements Runnable{

	Logger logger = LoggerFactory.getLogger(EndpointRetriever.class);

	private  ArrayList<String> brokerEndpoints  = new ArrayList<String>();
	
	String scope = null;

	public EndpointRetriever(String scope){
		this.scope= scope;
	}

	public List<AccessPoint> retrieveMessageBrokerEndpoints () throws Exception{
		
		ScopeProvider.instance.set(scope);

		XQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Name/text() eq 'MessageBroker'")
		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);

		List<AccessPoint> endpoints = client.submit(query);

		if (endpoints.size() == 0){
			throw new Exception("No MessageBroker Endpoints are available");
		}
		return endpoints;

	}

	public synchronized ArrayList<String>  getEndpoints(){
		return brokerEndpoints;			
	}


	@Override
	public void run() {
		ArrayList<String> endpoints  = new ArrayList<String>();

		try {
			for (AccessPoint point :this.retrieveMessageBrokerEndpoints()){
				logger.debug(point.address());
				endpoints.add(point.address());
			}
		} catch (Exception e) {
			logger.error("Error retrieving Message Broker Endpoints",e);
			return;
		}
		if (endpoints.size()!=0)
			synchronized (brokerEndpoints){
				brokerEndpoints.clear();
				for(String endpoint: endpoints)
					brokerEndpoints.add(endpoint);
			}
	}

}	
