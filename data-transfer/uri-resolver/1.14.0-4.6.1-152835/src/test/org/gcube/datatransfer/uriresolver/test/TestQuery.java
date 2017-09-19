package org.gcube.datatransfer.uriresolver.test;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.junit.Test;

public class TestQuery {
	
	@Test
	public void TestQueryResolver() throws Exception{
		
		ScopeProvider.instance.set("/gcube");

		XQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Name/text() eq 'HTTP-URI-Resolver'")
		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);

		List<AccessPoint> endpoints = client.submit(query);

		if (endpoints.size() == 0)
			throw new Exception("No Resolver available");
		
		
		System.out.println(endpoints.get(0).address());
		System.out.println(endpoints.get(0).propertyMap().get("parameter").value());
		
	
	
	}

}
