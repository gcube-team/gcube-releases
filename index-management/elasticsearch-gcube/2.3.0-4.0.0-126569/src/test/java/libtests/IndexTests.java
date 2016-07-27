package libtests;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import helpers.FullTextIndexTypeFT2;
import helpers.FullTextIndexTypeFile;
import helpers.RS2Feed;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import static org.junit.Assert.*;

import org.gcube.elasticsearch.FullTextNode;
import org.gcube.elasticsearch.helpers.ElasticSearchHelper;
import org.gcube.elasticsearch.helpers.QueryParser;
import org.gcube.indexmanagement.common.IndexException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.BaseEncoding;
import com.google.common.io.Files;



public class IndexTests {

	static FullTextNode ftn = null;
	final static String indexTypeName1 = "files";
	//final static String indexTypeName2 = "ft_test";
	final static String scope = "/gcube/devNext";
	final static String indexName = "test-index";
	final static String mycoll1 = "workspace";
	final static String mycoll2 = "mycoll2";
	final static String fileNamePath = "/home/alex/Desktop/proviso.pdf";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		URL url = ElasticSearchHelper.class.getClassLoader().getResource(ElasticSearchHelper.STOPWORDS_FILENAME);
		System.out.println(url.toString());
		
		String clusterName = "test-elastic-search-cluster-memory";
		String indexName = "test-index-memory";
		
		ftn = new FullTextNode.Builder()
				.hostname("jazzman.di.uoa.gr")
				.clusterName(clusterName)
				.indexName(indexName)
				.scope(scope)
				.useResourceRegistry(false)
				.build();
		ftn.createOrJoinCluster();
		
		FullTextIndexTypeFile indexType = new FullTextIndexTypeFile(indexTypeName1, scope);
		ftn.addIndexType(indexTypeName1, indexType, mycoll1);
		
		Thread.sleep(3 * 1000);
		
//		ftn.addIndexType(indexTypeName2, new FullTextIndexTypeFTTest(indexTypeName2, scope), mycoll2);
	}

	//@AfterClass
	public static void tearDownAfterClass() throws Exception {
		try {
			try {
				ftn.clearIndex(indexTypeName1);
				boolean deleteIndex = ftn.deleteIndex(mycoll1);
//				boolean deleteCollection = ftn.deleteCollection("5b268db0-9d63-11de-8d8f-a04a2d1ca936");
//				ftn.commit();
				System.out.println("deleteIndex : " + deleteIndex);
//				System.out.println("deleteCollection : " + deleteCollection);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			System.out.println("Deleted index");
			Thread.sleep(60*1000);
//			Thread.sleep(60*1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ftn.close();
	}


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	
	@Test
	public void feedFiles(){
		Boolean cfiResult = ftn.createFileIndex();
		
		assertEquals(cfiResult, true);
		
		File pdfFile = new File(fileNamePath);
		String base64 = null;
		try {
			base64 = BaseEncoding.base64().encode(Files.toByteArray(pdfFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ftn.addFile(base64);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("file added");
		
	}
	
	
	//@Test
	public void feedAndQueryTest() throws InterruptedException, GRS2WriterException, IndexException {
		TCPConnectionManager.Init(new TCPConnectionManagerConfig("localhost", new ArrayList<PortRange>(), true));
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
//		
		final RS2Feed rs2feed = new RS2Feed();
		final String rsLocator = rs2feed.getOutput();
		final Set<String> sids = new HashSet<String>();
		sids.add("sid1");
		sids.add("sid2");
		
		Thread rsFeedThread = new Thread(new Runnable() {
			public void run() {
				try {
					//rs2feed.feedIndex("src/test/resources/ftRowsets_ft2.xml");
					rs2feed.feedIndex("src/test/resources/file_rowset.xml");
					Thread.sleep(5*1000);
					rs2feed.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		Thread indexFeedThread = new Thread(new Runnable() {
			public void run() {
				try {
					setSuc(ftn.feedLocator(rsLocator, mycoll1,sids));
				} catch (GRS2RecordDefinitionException e) {
					e.printStackTrace();
				} catch (GRS2ReaderException e) {
					e.printStackTrace();
				} catch (GRS2BufferException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		
		rsFeedThread.start();
		indexFeedThread.start();
		
		rsFeedThread.join();
		indexFeedThread.join();
		
		ftn.activateIndex(mycoll1);
		
		System.out.println("Feed ok");
		
		Assert.assertEquals(true, success);
		
		System.out.println("collection documents  : " + ftn.getCollectionDocumentsCount(mycoll1));
		System.out.println("collections of index  : " + ftn.getCollectionsOfIndex(mycoll1));
		System.out.println("indices of collection : " + ftn.getIndicesOfCollection("5b268db0-9d63-11de-8d8f-a04a2d1ca936"));
		
		Thread.sleep(10 * 60*1000);
//		final RS2Feed rs2feed2 = new RS2Feed();
//		final String rsLocator2 = rs2feed2.getOutput();
//		
//		Thread rsFeedThread2 = new Thread(new Runnable() {
//			public void run() {
//				try {
//					rs2feed2.feedIndex("/home/alex/clustering/abstracts_rowsets.xml");
//					Thread.sleep(5*1000);
//					rs2feed2.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		
//		Thread indexFeedThread2 = new Thread(new Runnable() {
//			public void run() {
//				try {
//					setSuc(ftn.feedLocator(rsLocator2, mycoll2));
//				} catch (GRS2RecordDefinitionException e) {
//					e.printStackTrace();
//				} catch (GRS2ReaderException e) {
//					e.printStackTrace();
//				} catch (GRS2BufferException e) {
//					e.printStackTrace();
//				} catch (URISyntaxException e) {
//					e.printStackTrace();
//				}
//			}
//		});
		
//		rsFeedThread2.start();
//		indexFeedThread2.start();
//		
//		rsFeedThread2.join();
//		indexFeedThread2.join();
		
//		ftn.activateIndex(mycoll2);
		
//		Assert.assertEquals(true, success);
//		Thread.sleep(100*1000);
		
//		String locator = ftn.query(query);
//		System.out.println("locator : " + locator);
//		Assert.assertNotNull(locator);
		
		
		//ftn.query(query, sids);
		
		String query = "((title = map) and ((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (number <= 2))) project title";
		ftn.query(query, sids);
		query = "((title = map) and ((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (number > 3))) project title";
		ftn.query(query, sids);
		query = "((title == \"*\") and ((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (number > 2))) project title S";
		ftn.query(query, sids);

//		String collectionID = "5b268db0-9d63-11de-8d8f-a04a2d1ca936";
//		Long cnt = ftn.getCollectionDocuments(collectionID);
//		System.out.println(collectionID + " count : " +  cnt);
//		collectionID = "wikipedia_col";
//		cnt = ftn.getCollectionDocuments(collectionID);
//		System.out.println(collectionID + " count : " +  cnt);
		
		//Thread.sleep(20*1000);
		
//		ftn.listAlgorithms();
//		ftn.clusteringTest("soccer");
		
	}
	boolean success = false;
	public void setSuc(boolean suc) {
		success  = suc;
	}

	@Test
	public void listAlgs() {
//		ftn.clusteringTest("soccer");
	}
	
	
	@Test
	public void testQuery() throws IndexException, GRS2WriterException{
//		String locator = ftn.query(query);
//		System.out.println("locator : " + locator);
//		Assert.assertNotNull(locator);
	}
	@Test
	public void getCollectionsIDFromQueryTest() {
		String query = "((title = map) and ((gDocCollectionID == \"5b268db0-9d63-11de-8d8f-a04a2d1ca936\") and (number < 2))) project title";
		List<String> qIDs = QueryParser.getCollectionsIDFromQuery(query);
		Assert.assertEquals(qIDs.get(0), "5b268db0-9d63-11de-8d8f-a04a2d1ca936");
	}
	
	@Test
	public void getLuceneQueryFromQueryStringTest() {
//		String luceneQuery = QueryParser.getLuceneQueryFromQueryString(query);
//		Assert.assertEquals(luceneQuery, "(((gDocCollectionID:5b268db0-9d63-11de-8d8f-a04a2d1ca936) AND (gDocCollectionLang:en))  AND (title:map OR coverage:map OR ObjectID:map OR identifier:map OR gDocCollectionID:map OR gDocCollectionLang:map OR allIndexes:map))");
	}
	
	@Test
	public void getProjectionsQueryFromQueryStringTest() {
//		List<String> projections = QueryParser.getProjectionsQueryFromQueryString(query);
//		Assert.assertEquals(projections.get(0), "title");
//		Assert.assertEquals(projections.get(1), "S");
	}
	
	
//	public static String createJSONObjectTest() throws IOException {
//	Map<String, Object> t = new HashMap<String, Object>();
//
//	String[] vals = { "str1", "str2", "str3" };
//	List<String> mylist = new ArrayList<String>();
//	mylist.add("str1");
//	mylist.add("str2");
//	mylist.add("str3");
//
//	Set<String> myset = new HashSet<String>();
//	myset.add("str1");
//	myset.add("str2");
//	myset.add("str3");
//
//	Map<String, String> mymap = new HashMap<String, String>();
//	// mymap.put("_store", "yes");
//	// mymap.put("_index", "analyzed");
//	// mymap.put("_boost", "1.0");
//
//	t.put("f_longtext1",
//			"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut quis diam augue. Pellentesque ut dui in ligula malesuada feugiat. Maecenas ipsum velit, faucibus a dignissim et, elementum ut risus. Nunc neque dui, condimentum ac venenatis non, luctus quis neque. In non aliquet enim. Nunc euismod enim ante. Aenean at libero dui. Nunc vulputate consectetur erat sed sagittis. Integer enim tellus, pellentesque eu sollicitudin non, pellentesque consectetur massa.");
//	t.put("f_longtext2",
//			"Cristiano Ronaldo dos Santos Aveiro,[2] OIH, (born 5 February 1985),[3] commonly known as Cristiano Ronaldo, is a Portuguese footballer who plays as a forward for Spanish La Liga club Real Madrid and is the captain of the Portuguese national team. Ronaldo became the most expensive footballer in history after moving from Manchester United to Real Madrid in a transfer worth £80 million (€93.9 million/$131.6 million). In addition, his contract with Real Madrid, in which he is paid €12 million per year, makes him one of the highest-paid footballers in the world,[4] and his buyout clause is valued at €1 billion as per his contract..");
//	t.put("f_longtext3", "Cristiano Ronaldo....");
//	t.put("f_longtext4",
//			"Cristiano Ronaldo Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut quis diam augue. Pellentesque ut dui in ligula malesuada feugiat. Maecenas ipsum velit, faucibus a dignissim et, elementum ut risus. Nunc neque dui, condimentum ac venenatis non, luctus quis neque. In non aliquet enim. Nunc euismod enim ante. Aenean at libero dui. Nunc vulputate consectetur erat sed sagittis. Integer enim tellus, pellentesque eu sollicitudin non, pellentesque consectetur massa Cristiano Ronaldo.");
//	//
//	// t.put("f_int", 1);
//	// t.put("f_Int", Integer.valueOf(1));
//	//
//	// t.put("f_Double", Double.valueOf(3.14));
//	// t.put("f_double", (double) 3.14);
//	//
//	// t.put("f_Short", Short.valueOf((short) 2));
//	// t.put("f_short", (short) 2);
//	//
//	// t.put("f_long", (long) 5);
//	// t.put("f_Long", Long.valueOf(5));
//	//
//	// t.put("f_byte", (byte) 5);
//	// t.put("f_Byte", Byte.valueOf((byte) 5));
//	//
//	// t.put("f_char", 'a');
//	// t.put("f_Char", Character.valueOf('a'));
//	//
//	// t.put("f_String", "string");
//	// // t.put("f_obj", new Object());
//	// t.put("f_float", 3.14);
//	// t.put("f_Float", Float.valueOf("3.14"));
//	//
//	// t.put("f_arr", vals);
//	// t.put("f_lst", mylist);
//	// t.put("f_set", myset);
//
//	// Map<String, Object> fieldMap = new HashMap<String, Object>();
//
//	// fieldMap.put("_value", "val1");
//	// fieldMap.put("_store", "yes");
//	// fieldMap.put("properties", mymap);
//	// t.put("multi_field", "ppp");
//
//	XContentBuilder xcb = null;
//	try {
//		xcb = createJSONObject(t);
//		System.out.println("xcd : " + xcb.string());
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
//	return xcb.string();
//}
	
}
