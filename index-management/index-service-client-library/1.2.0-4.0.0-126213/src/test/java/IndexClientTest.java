import java.util.List;
import java.util.Set;

import org.gcube.rest.index.client.IndexClient;
import org.gcube.rest.index.client.factory.IndexFactoryClient;
import org.gcube.rest.index.client.inject.IndexClientModule;
import org.gcube.rest.index.common.entities.ClusterResponse;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;


public class IndexClientTest {

	public static void main(String[] args) throws Exception {
		final String scope = "/gcube/devNext";
		Injector injector = Guice.createInjector(new IndexClientModule());
//		IndexFactoryClient fclient = injector.getInstance(IndexFactoryClient.class);
		
//		fclient.randomFactory();
//		fclient.createResource(null, scope);
		
		
		
		IndexClient.Builder indexClientBuilder = injector.getInstance(IndexClient.Builder.class);
		
		IndexFactoryClient.Builder indexFactoryClientBuilder = injector.getInstance(IndexFactoryClient.Builder.class);
		
//		IndexClient cl = indexClientBuilder.scope(scope).build();
		
		IndexFactoryClient fcl = new IndexFactoryClient.Builder().scope(scope).build();
		IndexFactoryClient fcl2 = indexFactoryClientBuilder.scope(scope).build();
		
		System.out.println("fcl : "  + fcl.getEndpoint());
		System.out.println("fcl2 : "  + fcl2.getEndpoint());
		
		IndexClient cl = new IndexClient.Builder().scope(scope).build();
		
		System.out.println("cl : "  + cl.getEndpoint());
		
		IndexClient cl2 = indexClientBuilder.scope(scope).build();
		
		System.out.println("cl2 : "  + cl2.getEndpoint());
		
		final String queryString = "((title = genus) and (gDocCollectionID == \"c9076f3f-be8d-43e2-9f02-de35e6d8f72c\")) project title S";
		
		System.out.println(cl.frequentTerms(queryString, null));

//		List<ClusterResponse> clustering = cl.clustering(queryString, "genus", null, 10, "ObjectID", Lists.newArrayList("title"), Lists.newArrayList("description"), Lists.newArrayList("gDocCollectionLang"), "lingo", 1000);
//
//		for (ClusterResponse cr : clustering){
//			System.out.println(cr.getClusterName());
//			System.out.println(cr.getDocs().size());
//		}
		
		
		
		
		//final String resultSetLocation = "grs2-proxy://meteora.di.uoa.gr:4000?key=fb147a11-45f0-47c3-bcec-d1667e9246b5#TCP";

		
//		String indexName = "zookeys";
//		String endpoint = "http://localhost:8080/index-service";
////		fclient.setScope(scope);
//		fclient.initializeFactory(endpoint);
//		//fclient.randomFactory();
//
//		IndexClient client1 = new IndexClient.Builder()
//			.scope(scope)
//			.build();
//		client.randomClient();
//		System.out.println("endpoint   : " + client1.getEndpoint());
//		System.out.println("resourceID : " + client1.getResourceID());
//		
		
//		IndexClient client2 = indexClientBuilder
//			.scope(scope)
//			//.endpoint("http://dl050.madgik.di.uoa.gr:8080/index-service-1.0.0-SNAPSHOT/")
//			.build();
//		
////		client.initializeEndpointClient("http://dl050.madgik.di.uoa.gr:8080/index-service-1.0.0-SNAPSHOT/");
//		System.out.println("endpoint   : " + client2.getEndpoint());
//		System.out.println("resourceID : " + client2.getResourceID());
//		
		Set<String> indices = cl.indicesOfCollection("c9076f3f-be8d-43e2-9f02-de35e6d8f72c");
//		System.out.println("indices : " + indices);
//		cl.deleteIndex(indices.iterator().next());
		
//		IndexClient client3 = new IndexClient.Builder()
//			.scope(scope)
//			.resourceID("24b34767-2600-4ce3-844a-f82f16fef16f")
//			.build();
//		
//		System.out.println("endpoint   : " + client3.getEndpoint());
//		System.out.println("resourceID : " + client3.getResourceID());
		
		
//		client.initializeClient(endpoint, resourceID);
		
		Set<String> sids = null;

		// client.feedLocator(resultSetLocation, indexName, true, sids);
		// Thread.sleep(60 * 1000);
		 
		 
		 
		// ////
//		 String collectionID = "5b268db0-9d63-11de-8d8f-a04a2d1ca936";
////		// ////
//		 System.out.println("collectionCount : " +
//		 client.collectionCount(collectionID));
//		 System.out.println("collectionsOfIndex : " +
//		 client.collectionsOfIndex(indexName));
//		 System.out.println("indicesOfCollection : " +
//		 client.indicesOfCollection(collectionID));
////		// //
////		// //////
////		// Thread.sleep(30*1000);
////		// //////
//		long starttime, endtime;
//		 starttime = System.currentTimeMillis();
//		 System.out.println(client.query(queryString, sids));
//		 endtime = System.currentTimeMillis();
//		 System.out.println("query time : " + (endtime - starttime) / 1000.0 +
//		 " secs");
////
//		for (int i = 0; i < 10; ++i) {
//			starttime = System.currentTimeMillis();
//			System.out
//					.println(client.queryAndReadClientSide(queryString, sids));
//			endtime = System.currentTimeMillis();
//			System.out.println("query time : " + (endtime - starttime) / 1000.0
//					+ " secs");
//		}

		//
		// System.out.println("deleteIndex : " + client.deleteIndex(indexName));
		//
		// client.destroyNode();
		// client.destroy();

	}
}
