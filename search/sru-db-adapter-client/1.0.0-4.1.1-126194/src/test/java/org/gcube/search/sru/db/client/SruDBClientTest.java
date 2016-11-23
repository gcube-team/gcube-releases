package org.gcube.search.sru.db.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.gcube.search.sru.db.client.exception.SruDBClientException;
import org.gcube.search.sru.db.client.factory.SruDBFactoryClient;
import org.gcube.search.sru.db.common.resources.ExplainInfo;
import org.gcube.search.sru.db.common.resources.SruDBResource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SruDBClientTest {

	public static void main(String[] args) throws SruDBClientException, JAXBException {
		final String scope = "/gcube/devNext";
		final String endpoint = "http://jazzman.di.uoa.gr:8080/sru-db-adapter-service";
		
		
		SruDBFactoryClient factory = new SruDBFactoryClient.Builder()
			.endpoint(endpoint)
			.scope(scope)
			.skipInitialize(true)
			.build();
//
//		SRUDatabaseResource resource = new SRUDatabaseResource();
//		ExplainInfo explainInfo = new ExplainInfo();
//		
//		//table and fields
//		
//		explainInfo.setIndexInfo(ImmutableMap.<String, List<String>>builder()
//				.put("books", Lists.newArrayList("author", "title"))
//				.build()
//				);
//		
//		explainInfo.setIndexSets(ImmutableMap.<String, String>builder()
//				.put("cql", "info:srw/cql-context-set/1/cql-v1.1")
//				.put("books", "info:srw/cql-context-set/1/db-v1.1")
//				.build()
//				);
//		
//		explainInfo.setSchemaID("http://www.loc.gov/mods");
//		explainInfo.setSchemaName("rss");
//		explainInfo.setRecordSchema("http://explain.z3950.org/dtd/2.0/");
//		explainInfo.setRecordPacking("xml");
//		
//		resource.setDbName("test");
//		resource.setDbType("mysql");
//		resource.setHostname("jazzman.di.uoa.gr");
//		resource.setPort(3306);
//		resource.setUsername("alex");
//		resource.setPassword("alexis87");
//		resource.setExplainInfo(explainInfo);
//		
//		System.out.println(resource.toXML());
//		System.out.println(resource.toJSON());
//		
//		String resourceID = factory.createResource(resource, scope);
		
		
		Map<String, ArrayList<String>> tables = Maps.newHashMap();
		tables.put("books", Lists.newArrayList("author", "title"));
		
		Map<String, String> fieldsMapping = Maps.newHashMap();
		fieldsMapping.put("creator", "author");
//		
		SruDBResource resource = new SruDBFactoryClient.ResourceBuilder()
			.databaseTitle("db for testing")
			.databaseDescription("dummy db for testing")
			.databaseName("test")
			.databaseType("mysql")
			.serverHost("localhost")
			.serverPort(3306)
			.databaseUsername("root")
			.databasePassword("alexis87")
			.schemaName("rss")
			.recordPacking("xml")
			.tables(tables)
			.defaultTable("books")
			.fieldsMapping(fieldsMapping)
			.build();
		
		
		System.out.println(resource.toXML());
		
		
		String resourceID = factory.createResource(resource, scope);
		
		System.out.println("Resource created with ID : " + resourceID);
		
		
		
		
		//String resourceID = "3fe6c0a3-f266-4855-bb86-fc4a8d0fb72a";
		
		
		
		SruDBClient client = new SruDBClient.Builder()
			.endpoint(endpoint)
			.resourceID(resourceID)
			.scope(scope)
			.skipInitialize(true)
			.build();
		
		
		
		String query = client.searchRetrieve((float) 1.1, null, "books.author =\"myauthor\"", 3, null);
		System.out.println("1.-----------------");
		System.out.println(query);
		System.out.println("1.-----------------");
		
		String query2 = client.searchRetrieve((float) 1.1, null, "oai_dc.author =\"myauthor\"", 3, null);
		System.out.println("2.-----------------");
		System.out.println(query2);
		System.out.println("2.-----------------");

		
		String explain = client.explain();
		System.out.println("explain : " + explain);
		
//		String call = client.searchRetrieve(1.1f, "", "books.author = \"myauthor\"", 4, "rss");
//		System.out.println("call : " + call);
		
		
	}
	
	
	
	void createResource(Map<String, ArrayList<String>> tables, String recordPacking, String schemaName) throws SruDBClientException{
		
		//
		SruDBResource resource = new SruDBResource();
		ExplainInfo explainInfo = new ExplainInfo();
		
		//table and fields
		
		explainInfo.setIndexInfo(tables);
		
		Map<String, String> indexSets = Maps.newHashMap();
		indexSets.put("cql", "info:srw/cql-context-set/1/cql-v1.1");
		
		for (String table : tables.keySet()){
			indexSets.put(table, "info:srw/cql-context-set/1/db-v1.1");
		}
		
		explainInfo.setIndexSets(indexSets);
		
		
		explainInfo.setSchemaID("http://www.loc.gov/mods");
		explainInfo.setSchemaName(schemaName);
		explainInfo.setRecordSchema("http://explain.z3950.org/dtd/2.0/");
		explainInfo.setRecordPacking(recordPacking);
		
		resource.setDbName("test");
		resource.setDbType("mysql");
		resource.setHostname("jazzman.di.uoa.gr");
		resource.setPort(3306);
		resource.setUsername("alex");
		resource.setPassword("alexis87");
		resource.setExplainInfo(explainInfo);
	}
	
	
}
