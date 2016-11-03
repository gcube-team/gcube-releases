package org.gcube.rest.opensearch.client;

import java.util.List;
import java.util.Map;

import org.gcube.rest.opensearch.client.exception.OpenSearchClientException;
import org.gcube.rest.opensearch.client.factory.OpenSearchFactoryClient;
import org.gcube.rest.opensearch.client.inject.OpenSearchClientModule;
import org.gcube.rest.opensearch.common.entities.Provider;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class OpenSearchClientTest {

	public static void main(String[] args) throws OpenSearchClientException {
		
		final String scope = "/gcube/devNext";
		String endpoint = "http://dl08.di.uoa.gr:8080/opensearchdatasource-2.0.0-SNAPSHOT/";

		OpenSearchFactoryClient factory = new OpenSearchFactoryClient.Builder()
											.endpoint(endpoint)
											.scope(scope)
											.build();
		
		System.out.println("f : " + factory.getEndpoint());
		
		Injector injector = Guice.createInjector(new OpenSearchClientModule());
		
		OpenSearchFactoryClient factory2 = injector.getInstance(OpenSearchFactoryClient.Builder.class)
			.endpoint(endpoint)
			.scope(scope)
			.build();

		System.out.println("f2 : " + factory2.getEndpoint());
		
		OpenSearchClient client = new OpenSearchClient.Builder()
			.endpoint(endpoint)
			.scope(scope)
			.build();
		
		System.out.println("cl : " + client.getEndpoint());
		
		OpenSearchClient client2 = injector.getInstance(OpenSearchClient.Builder.class)
			.endpoint(endpoint)
			.scope(scope)
			.build();
	
		System.out.println("cl : " + client2.getEndpoint());
		
	//	factory.createResource(scope);
//		testCreateScenario(factory, scope);
		
//		OpenSearchClient client = injector.getInstance(OpenSearchClient.Builder.class)
//				.scope(scope)
//				.build();
		
//		testGetOpenSearchGenericResources();
		
		
		String queryString = "((((gDocCollectionID == \"c050ec70-302c-11e3-aee1-ef80f04700ac\") and (gDocCollectionLang == \"en\"))) and (bdbb8301-c5e7-41e6-93f7-d0a63198cc86 = greece)) project d91f3c47-e46e-4737-9496-a0f72361a397 3e3584f0-eed3-4089-99cd-86a7def1471e";
//		List<Map<String, String>> grs2 = client2.queryAndRead(queryString, true);
		List<Map<String, String>> grs2 = client2.queryAndReadClientSide(queryString, true);
		System.out.println(grs2);
		
		
	}
	
	
	static void testCreateScenario(OpenSearchFactoryClient factory, String scope) throws OpenSearchClientException{
		List<String> fieldParameters = Lists.newArrayList("en:s:allIndexes", "en:p:title", "en:p:link", "en:p:description", "en:p:pubDate", "en:p:S");
		List<String> fixedParameters = Lists.newArrayList(
				"http%3A%2F%2Fa9.com%2F-%2Fspec%2Fopensearch%2F1.1%2F:count=\"50\"", 
				"config:numOfResults=\"200\"");
		String collectionID = "c050ec70-302c-11e3-aee1-ef80f04700ac";
		String openSearchResourceID = "118646f0-7b7c-11e2-b8a4-e3f7b403b9a5";
		
		testCreate(factory, fieldParameters, fixedParameters, collectionID, openSearchResourceID, scope);
		
	}
	
	
	static void testCreate(OpenSearchFactoryClient factory, List<String> fieldParameters, List<String> fixedParameters, String collectionID, String openSearchResourceID, String scope) throws OpenSearchClientException {
		List<String> fieldParams = Lists.newArrayList();
		
		for (int i=0; i<fieldParameters.size(); i++) {
			fieldParams.add(collectionID + ":" + fieldParameters.get(i));
			System.out.println("Field parameter: " + (i+1) + " " + fieldParams.get(i));
		}
		
		Provider p = new Provider();
		p.setCollectionID(collectionID);
		p.setOpenSearchResourceID(openSearchResourceID);
		p.setFixedParameters(fixedParameters);
		
		List<Provider> providers = Lists.newArrayList();
		providers.add(p);
		
		factory.createResource(fieldParams, providers, scope);
	}
}
