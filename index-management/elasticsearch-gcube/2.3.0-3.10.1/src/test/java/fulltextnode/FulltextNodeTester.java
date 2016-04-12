//package fulltextnode;
//import java.io.IOException;
//import java.util.Scanner;
//
//import org.gcube.elasticsearch.FullTextNode;
//import org.gcube.elasticsearch.FullTextNode.Builder;
//
//
//public class FulltextNodeTester {
//	static FullTextNode ftn = null;
//	final static String indexTypeName = "ft_2.0";
//	final static String scope = "/gcube/devNext";
//	final static String indexName = "test-index";
//	final static String hostname = "dl13.di.uoa.gr";
//
//	public static void main(String[] args) throws IOException {
////		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
////				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
////		root.setLevel(ch.qos.logback.classic.Level.WARN);
//		
//		Scanner keyboard = null;
//		try {
//
//			String clusterName = "es-cluster--gcube-devNext-default-cluster-name";
//			String indexName = "test-index";
//
//			ftn = new FullTextNode.Builder().hostname(indexName).clusterName(clusterName).build();
//			ftn.getIndicesOfCollection(collection)
////			ftn.createOrJoinCluster();
////			ftn.addMetaIndex();
////			//ftn.addIndexType(indexTypeName, new FullTextIndexTypeFT2(indexTypeName, scope));
////
////			// /ftn.getIndexTypesOfIndex();
////
////			// Thread.sleep(1000);
////
////			String rs = "grs2-proxy://jazzman.di.uoa.gr:39397?key=15d4ba51-7f90-4998-9b57-db3a2ea4d42d#TCP";
////			ftn.feedLocator(rs);
////			
////			Thread.sleep(1000);
////			
////			String rs2 = "grs2-proxy://jazzman.di.uoa.gr:55553?key=59dab0a4-c65f-46e8-a810-28217b7a56ae#TCP";
////			ftn.feedLocator(rs2);
//			// QueryParser.insertSimple(QueryParser.createJSONObjectTest(),
//			// ftn.indexClient, ftn.indexName, "ft_2.0", ftn.indexTypes);
//
//			// Thread.sleep(1000);
//
//			// ftn.commit();
//
//			// List<String> fields = Arrays.asList("f_longtext2");
//
//			// TODO: check query form from LookupService
//
//			// String query =
//			// "(((gDocCollectionID:\"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") AND (gDocCollectionLang:\"en\"))  AND (title:map OR coverage:map OR ObjectID:map OR identifier:map OR gDocCollectionID:map OR gDocCollectionLang:map OR allIndexes:map)) project title S";
//			//
//			// System.out.println("query1 : " + ftn.query(query));
//			// System.out.println("-----------------------------------------------------");
//			// System.out.println("query2 : " + ftn.queryStream(query));
//
//			// List<String> docIDs = Arrays
//			// .asList("5b268db0-9d63-11de-8d8f-a04a2d1ca936");
//			// ftn.deleteDocuments(docIDs);
//
//			System.out.println("Press enter to stop...");
//			keyboard = new Scanner(System.in);
//			keyboard.nextLine();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			// DeleteByQueryResponse dr =
//			// ftn.indexClient.prepareDeleteByQuery(ftn.indexName).setQuery(QueryBuilders.matchAllQuery()).setTypes("ft_2.0").execute().actionGet();
//			// System.out.println("Delete response : " + dr.toString());
//
//			ftn.close();
//			if (keyboard != null)
//				keyboard.close();
//		}
//	}
//}
