//package fulltextnode;
//import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
//
//import java.io.IOException;
//import java.util.Scanner;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import org.gcube.elasticsearch.FullTextNode;
//import org.gcube.indexmanagement.common.IndexException;
//
//
//public class FulltextNodeTesterQuery {
//	//FullTextNode ftn = null;
//	final static String indexTypeName = "ft_2.0";
//	final static String scope = "/gcube/devNext";
//	final static String indexName = "test-index";
//	
//	
//
//	public static void main(String[] args) throws IOException {
////		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
////				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
////		root.setLevel(ch.qos.logback.classic.Level.WARN);
//
//		Scanner keyboard = null;
//		try {
//
//			final String clusterName = "test-elastic-search-cluster";
//			final String indexName = "test-index";
//			final FullTextNode ftn = new FullTextNode(clusterName, indexName, scope);
//			ftn.createOrJoinCluster();
//			//ftn.addIndexType(indexTypeName, new FullTextIndexTypeFT2(indexTypeName, scope));
//			
//			Runnable run = new Runnable() {
//					//@Override
//					public void run() {
//						 String query =
//						 "(((gDocCollectionID:abd6e8cc-5f2f-11e2-be7a-001e0ba1d295) AND (gDocCollectionLang:en))  AND (title:dressingtable)) project title";
//						//
//						 try {
//							System.out.println("[" + Thread.currentThread() +  "] query : " + ftn.query(query));
//							
//						} catch (GRS2WriterException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (IndexException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				};
//			
//			int numberOfThreads = 1;
//			ExecutorService exec = Executors.newFixedThreadPool(numberOfThreads);
//			long startTime = System.currentTimeMillis();
//			for (int i = 0; i < numberOfThreads; i++) {
//				exec.execute(run);
//			}
//			exec.shutdown();
//			exec.awaitTermination(100, TimeUnit.SECONDS);
//			long endtime = System.currentTimeMillis();
//
//			System.out.println("Total time for " + numberOfThreads + " queries : " + (endtime - startTime) / 1000.0 + " secs"); 
//			
//			
//			ftn.close();
//			 
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
//			if (keyboard != null)
//				keyboard.close();
//			
//		}
//	}
//}
