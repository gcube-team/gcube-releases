package org.acme;

import static java.lang.String.*;
import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceInstance;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.junit.BeforeClass;
import org.junit.Test;

public class APIClient {

	@BeforeClass
	public static void setup() {
		
		ScopeProvider.instance.set("/gcube/devNext/devVre");
	}
	

	@Test
	public void allServiceEndpoints() {
		
		
		//pick a predefined query
		Query query = queryFor(ServiceEndpoint.class);
		
		//pick a client that knows how to parse those resources
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		
		//execute query in current scope
		List<ServiceEndpoint> resources = client.submit(query);
		
		System.out.println(resources);
				
	}
	
	@Test
	public void someServiceEndpoints() {
		
		
		XQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq 'Database'");
		query.setResult("$resource/Profile/AccessPoint/Interface/Endpoint/text()");
		
		DiscoveryClient<String> client = client();
		
		List<String> resources = client.submit(query);
		
		System.out.println(resources);
				
	}
	
	@Test
	public void someServiceEndpointAddresses() {
		
		
		XQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq 'Database'");
		query.setResult("$resource/Profile/AccessPoint/Interface/Endpoint/text()");
		
		DiscoveryClient<String> client = client();
		
		List<String> addresses = client.submit(query);
		
		System.out.println(addresses);
				
	}
	
	@Test
	public void someGCoreServiceEndpointAddresses() {
		
		
		XQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq 'DataAccess'");
		query.addCondition("$resource/Profile/ServiceName/text() eq 'tree-manager-service'");
		query.addVariable("$entry","$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint");
		query.addCondition("$entry/@EntryName/string() eq 'gcube/data/tm/binder'");
		query.setResult("$entry/text()");
		
		DiscoveryClient<String> client = client();
		
		List<String> addresses = client.submit(query);
		
		System.out.println(addresses);
				
	}
	
	@Test
	public void someCustomServiceEndpointResults() {
		
		
		XQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq 'Database'");
		query.setResult("<perfect>" +
								"<id>{$resource/ID/text()}</id>" +
								"{$resource/Profile/AccessPoint}" +
						"</perfect>");
		
		DiscoveryClient<PerfectResult> client = clientFor(PerfectResult.class);
		
		List<PerfectResult> results = client.submit(query);
		
		for (PerfectResult result : results) {
			System.out.println(result.id+":"+result.ap);
		}
				
	}
	
	@XmlRootElement(name="perfect")
	private static class PerfectResult {
		
		@XmlElement(name="id")
		String id;
		
		@XmlElementRef
		AccessPoint ap;
	}
	
	
	@Test
	public void someServiceEndpointsAccessData() {
		
		
		XQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq 'Database'")
			 .setResult("$resource/Profile/AccessPoint");
		
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		
		List<AccessPoint> accesspoints = client.submit(query);
		
		for (AccessPoint point : accesspoints) {
			System.out.println(point.name()+":"+point.address());
		}
				
	}

	@Test
	public void someServiceInstances() {
		
		
		
		XQuery query = queryFor(ServiceInstance.class);
		query.addCondition("$resource/Data/gcube:ServiceClass/text() eq 'DataAccess'").
              addCondition("$resource/Data/gcube:ServiceName/text() eq 'tree-manager-service'");
		
		DiscoveryClient<ServiceInstance> client = clientFor(ServiceInstance.class);
		
		List<ServiceInstance> props = client.submit(query);
			
		System.out.println(props);
	}
	
	@Test
	public void someServiceInstanceReferences() {
		
		
		XQuery query = queryFor(ServiceInstance.class);
		query.addCondition("$resource/Data/gcube:ServiceClass/text() eq 'DataAccess'").
              addCondition("$resource/Data/gcube:ServiceName/text() eq 'tree-manager-service'")
              .addCondition(format("$resource/Source/text()[ends-with(.,'%1s')]","gcube/data/tm/binder"));
              	
		DiscoveryClient<ServiceInstance> client = clientFor(ServiceInstance.class);
		
		List<ServiceInstance> refs = client.submit(query);
		
		System.out.println(refs);
				
	}
	
	@Test
	public void someServiceInstanceProperties() {
		
		
		XQuery query = queryFor(ServiceInstance.class);
		query.addNamespace("tm",URI.create("http://gcube-system.org/namespaces/data/tm")).
			  addCondition("$resource/Data/tm:Plugin/name/text() eq 'species-tree-plugin'");
		
		DiscoveryClient<ServiceInstance> client = clientFor(ServiceInstance.class);
		
		List<ServiceInstance> props = client.submit(query);
		
		System.out.println(props);
				
	}
	
	@Test
	public void someServiceEndpointsProperties() {
		
		
		XQuery query = queryFor(ServiceEndpoint.class);
		query.addVariable("$prop", "$resource/Profile/AccessPoint/Properties/Property").
			  addCondition("$prop/Name/text() eq 'dbname'").
			  addCondition("$prop/Value/text() eq 'timeseries'").
			  setResult("$resource/Profile/AccessPoint/Interface/Endpoint/text()");
		
		DiscoveryClient<String> client = client();
		
		List<String> props = client.submit(query);
		
		System.out.println(props);
				
	}

}
