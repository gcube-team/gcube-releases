package org.gcube.textextractor.helpers;

import org.gcube.rest.index.client.IndexClient;
import org.gcube.rest.index.client.exceptions.IndexException;
import org.gcube.rest.index.client.factory.IndexFactoryClient;

public class IndexHelper {
	public static void main(String[] args) throws IndexException {
		String scope = "/gcube/devsec";
		
		createCluster(scope, 1);
	}
	
	public static void createCluster(String scope, Integer nodes) throws IndexException  {
		IndexFactoryClient fclient = new IndexFactoryClient.Builder()
			.scope(scope)
			.endpoint("http://dl050.madgik.di.uoa.gr:8080/index-service-1.1.0-3.2.0")
			.build();
		for (int i = 0 ; i != nodes ; ++i){
			String endpoint = fclient.createResource("smartfish-cluster-id", scope);
			System.out.println("created resource at : " + endpoint);
		}
		
	}
	
	public static void feedIndex(String scope, String locator) throws IndexException  {
		IndexClient client = new IndexClient.Builder()
			.scope(scope)
			.endpoint("http://dl050.madgik.di.uoa.gr:8080/index-service-1.1.0-3.2.0")
			.build();
		client.feedLocator(locator, "smartfish-index", true, null);
		
	}
	
	
	public static Boolean destroy(String scope) throws IndexException  {
		IndexClient client = new IndexClient.Builder()
		.scope(scope)
		.endpoint("http://dl050.madgik.di.uoa.gr:8080/index-service-1.1.0-3.2.0")
		.build();
		return client.destroyCluster();
		
	}
	
	public static boolean delete(String scope) throws IndexException  {
		IndexClient client = new IndexClient.Builder()
			.scope(scope)
			.endpoint("http://dl050.madgik.di.uoa.gr:8080/index-service-1.1.0-3.2.0")
			.build();
		return client.deleteIndex("smartfish-index");
		
	}
	
}
