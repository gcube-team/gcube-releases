package org.gcube.search.sru.search.adapter.client;

import org.gcube.search.sru.search.adapter.client.exception.SruSearchAdapterClientException;
import org.gcube.search.sru.search.adapter.client.factory.SruSearchAdapterFactoryClient;
import org.gcube.search.sru.search.adapter.commons.resources.SruSearchAdapterResource;

public class SruSearchAdapterClientTest {

	public static void main(String[] args) throws SruSearchAdapterClientException {
		final String scope = "/gcube/devNext";
		final String endpoint = "http://jazzman.di.uoa.gr:8080/sru-search-adapter-service";
		
		

//		SruSearchAdapterFactoryClient factory = new SruSearchAdapterFactoryClient.Builder()
//				.endpoint(endpoint)
//				.scope(scope)
//				.build();
//		
//		
//		SruSearchAdapterResource resource = new SruSearchAdapterResource();
//		resource.setHostname("jazzman.di.uoa.gr");
//		resource.setPort(8080);
//		
//		resource.setSearchSystemEndpoint("http://jazzman.di.uoa.gr:8080/searchsystemservice");
//		
//		factory.createResource(resource, scope);
		
//		SruSearchAdapterClient client = new SruSearchAdapterClient.Builder()
//				.endpoint(endpoint)
//				.scope(scope)
//				.resourceID("f38f369a-dc1d-4537-add9-df09f4b8b7a3")
//				.build();
		
		
		SruSearchAdapterStatelessClient client2 = new SruSearchAdapterStatelessClient.Builder()
		.endpoint(endpoint)
		.scope(scope)
		.build();
		
		
		
		String call = client2.searchRetrieve(1.1f, "", "((gDocCollectionID == c9076f3f-be8d-43e2-9f02-de35e6d8f72c) and (allIndexes = species))", 4, "rss");
		System.out.println("call : " + call);
	}
}
