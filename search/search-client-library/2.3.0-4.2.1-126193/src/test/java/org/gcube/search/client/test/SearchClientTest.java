package org.gcube.search.client.test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.search.SearchClient2;


public class SearchClientTest {

	public static void main(String[] args) throws Exception {
//		SearchClient client = new SearchClient("/gcube/devNext");
//		client.randomClient();
//		
//		System.out.println("initialized at : " + client.getEndpoint());
		
//		client.initializeClient("http://localhost:8080/searchsystem-rest");

		String query = "(((gDocCollectionID == 78e01c59-35b9-48ae-b20d-ebf6d403670e)) and (bdbb8301-c5e7-41e6-93f7-d0a63198cc86 = species)) project d91f3c47-e46e-4737-9496-a0f72361a397 3e3584f0-eed3-4089-99cd-86a7def1471e";
		Set<String> sids = null;
		Boolean names = true;
//		long starttime = System.currentTimeMillis();
//		List<Map<String, String>> response = client.queryAndRead(query, sids,
//				names);
//
//		long endtime = System.currentTimeMillis();
//
//		System.out.println(response);
//		System.out.println(response.size() + " hits. Total search time : "
//				+ (endtime - starttime) + " ms");
		
		
		SearchClient2 client2 = new SearchClient2.Builder()
		.scope("/gcube/devNext")
		//.endpoint("http://jazzman.di.uoa.gr:8080/searchsystemservice")
		.build();
		long starttime = System.currentTimeMillis();
		for (int i = 0 ; i < 1 ; i++){
		
			System.out.println("initialized at : " + client2.getEndpoint());
			
			
			System.out.println(new Date() + " started" );
			//System.out.println(i + " " + client2.getSearchableFields());
			System.out.println(i + " " + client2.getCollections());
			System.out.println(i + " " + client2.getCollectionsTypes());
			System.out.println(new Date() + " ended" );
		}
		
		
		long endtime = System.currentTimeMillis();

		System.out.println("Total search time : "
				+ (endtime - starttime) + " ms");
		
		List<Map<String, String>> response = client2.queryAndRead(query, sids,
				names);
		System.out.println(response);
		
		
//		String grs2 = client2.query(query, sids,
//				names);
//		
//		System.out.println("grs2 : " + grs2);
//		//Thread.sleep(10000);
//		List<Map<String, String>> response = ResultReader.resultSetToRecords(grs2);
//		
//
//		long endtime = System.currentTimeMillis();
//
//		System.out.println(response.get(0));
//		System.out.println(response.size() + " hits. Total search time : "
//				+ (endtime - starttime) + " ms");
		
		
	}
	
}
