//import static org.junit.Assert.*;
//
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import javax.swing.text.StyledEditorKit.AlignmentAction;
//
//import org.gcube.rest.index.client.ClientFactory;
//import org.gcube.rest.index.client.cache.IndexClient;
//import org.gcube.rest.index.client.exceptions.BadCallException;
//import org.gcube.rest.index.client.exceptions.NoAvailableIndexServiceInstance;
//import org.gcube.rest.index.common.entities.CollectionInfo;
//import org.gcube.rest.index.common.entities.configuration.DatasourceType;
//import org.gcube.rest.index.common.entities.fields.Field;
//import org.gcube.rest.index.common.entities.fields.config.FacetType;
//import org.gcube.rest.index.common.entities.fields.config.FieldConfig;
//import org.gcube.rest.index.common.search.Query;
//import org.gcube.rest.index.common.search.Search_Response;
//import org.gcube.rest.index.common.search.facets.Facet;
//import org.junit.BeforeClass;
//import org.junit.runners.MethodSorters;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.common.collect.Sets;
//
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class Tests {
//	
//	private static final Logger logger = LoggerFactory.getLogger(Tests.class);
//	
//	private static final String SCOPE = "/gcube/devsec";
//	private static final String COLLECTION_DOMAIN = "/gcube/devsec/devVRE";
//	private static final long SLEEPTIME = 1000L;
//	private static final String COLLECTION2CHECK = "257ac5fff4d048c7c660287b3dd35250f750da27705d4b9e65fc5c2949cb82df";
//	private static final String COLLECTIONTOPLAYWITH = "test-collection";
//	
//	private static IndexClient client;
//	
//	@BeforeClass
//	public static void setup(){
//		client = ClientFactory.getMeAnIndexClient(SCOPE);
//		cleanUp();
//	}
//
//	
//	public static void cleanUp(){
////		try {
////			client.dropCollection(COLLECTIONTOPLAYWITH);
////		} catch (NoAvailableIndexServiceInstance e) {
////			e.printStackTrace();
////		}
//	}
//	
//	
////    @Test
////    public void b1_testGeneric() throws NoAvailableIndexServiceInstance, BadCallException, InterruptedException {
////    }
//    
//	
//	
//    @Test
//    public void c1_testIfAvailable() {
//    	int epNum = client.getEndpointProvider().endpointsNumber();
//    	System.out.println("Endpoints number: "+epNum);
//    	logger.debug("Endpoints number: "+epNum);
//    	assertTrue(epNum > 0);
//    }
//    
//    @Test
//    public void c2_testInsertNewCollectionInfo() throws NoAvailableIndexServiceInstance, BadCallException {
//		CollectionInfo collection = new CollectionInfo(COLLECTION2CHECK, COLLECTION_DOMAIN, DatasourceType.ELASTIC, "DummyCollection", "A dummy collection for testing...", new Date());
//		collection.setCollectionFields(Sets.newHashSet(new Field("dc:title"), new Field("dc:creator")));
//		Map<String,String> alias = new HashMap<String,String>();
//		alias.put("dc:title", "Title");	alias.put("dc:creator", "Author");
//		collection.setCollectionFieldsAliases(alias);
//		boolean result = client.setCompleteCollectionInfo(collection);
//		assertEquals(result, true);
//    }
//    
//    @Test
//    public void c3_testAllGetters() throws NoAvailableIndexServiceInstance{
//    	try {
//    		Thread.sleep(SLEEPTIME);
//    	} catch (InterruptedException e) {e.printStackTrace();}
//    	
//    	List<CollectionInfo> collInfo = client.getCompleteCollectionInfo(COLLECTION2CHECK, COLLECTION_DOMAIN);
//    	assertTrue(collInfo.size() > 0);
//    	List<CollectionInfo> collsInfo = client.getCompleteCollectionInfo("", COLLECTION_DOMAIN);
//    	assertTrue(collsInfo.size() > 0);
//    	List<String> colls = client.getCollections(COLLECTION_DOMAIN);
//		assertTrue(colls.size() > 0);
//		System.out.println(client.getCollectionFieldsAlias(COLLECTION2CHECK, true, COLLECTION_DOMAIN));
//		Map<String, List<Field>> collFields = client.getAllCollectionFields(COLLECTION_DOMAIN);
////		assertTrue(!(collFields.get(COLLECTION2CHECK).stream().filter(field->{return field.getName().equals("Title");}).collect(Collectors.toList()).isEmpty()));
//		assertTrue(collFields.keySet().size() > 0);
//		for(List<Field> lst : collFields.values())
//			assertNotNull(lst);
//		Map<String, Map<String, String>> aliasForward = client.getCollectionFieldsAlias(COLLECTION2CHECK, true, COLLECTION_DOMAIN);
//		assertTrue(aliasForward.get(COLLECTION2CHECK).get("dc:title").equals("Title"));
//		
//		Map<String, Map<String, String>> aliasInverse = client.getCollectionFieldsAlias(COLLECTION2CHECK, false, COLLECTION_DOMAIN);
//		assertTrue(aliasInverse.get(COLLECTION2CHECK).get("Title").equals("dc:title"));
//		
//    	
//    }
//
//    
//    @Test
//    public void c4_testAllSetters() throws NoAvailableIndexServiceInstance, InterruptedException{
//    	Map<String,String> alias = new HashMap<String,String>();
//		alias.put("dc:title", "MyTitle");	alias.put("dc:creator", "MyAuthor");
//		assertTrue(client.setCollectionFieldsAlias(COLLECTION2CHECK, alias));
//		try {Thread.sleep(SLEEPTIME);} catch (InterruptedException e) {e.printStackTrace();}
//		Map<String, Map<String, String>> aliasForward = client.getCollectionFieldsAlias(COLLECTION2CHECK, true, COLLECTION_DOMAIN);
//		System.out.println(aliasForward);
//		System.out.println(client.getAllCollectionFields(COLLECTION_DOMAIN));
//		assertTrue(aliasForward.get(COLLECTION2CHECK).get("dc:title").equals("MyTitle"));
//    }
//    
//    
//    @Test
//    public void c5_testInsertionReindexingAndDelete() throws NoAvailableIndexServiceInstance{
//    	try { Thread.sleep(SLEEPTIME);} catch (InterruptedException e) {e.printStackTrace();}
//    	boolean status;
//    	//test insertion
//    	String recordJSON = "{\"menu\":{\"id\":\"the little red riding hood\",\"value\":\"ενα το και αν αεροπλανο for the if new to be airplane abandoned for and\",\"popup\":{\"menuitem\":[{\"value\":\"New\",\"onclick\":\"CreateNewDoc()\"},{\"value\":\"Open\",\"onclick\":\"OpenDoc()\"},{\"value\":\"Close\",\"onclick\":\"CloseDoc()\"}]}}}";
//    	status = client.insertJson(COLLECTION_DOMAIN, COLLECTIONTOPLAYWITH, "1", recordJSON);
//    	assertTrue(status);
//    	try { Thread.sleep(SLEEPTIME);} catch (InterruptedException e) {e.printStackTrace();}
//    	//evaluate insertion by searching
//    	Query query = new Query();
//    	query.add_SearchTerm(COLLECTIONTOPLAYWITH, "menu.value", "abandoned");
//    	Search_Response response = client.search(query, COLLECTION_DOMAIN);
//    	assertEquals(1, response.getTotalHits());
//    	//test reindexing with FacetType.NORMAL
//    	List<CollectionInfo> collectionInfos = client.getCompleteCollectionInfo(COLLECTIONTOPLAYWITH, COLLECTION_DOMAIN);
//    	assertEquals(1, collectionInfos.size());
//    	CollectionInfo colInfo = collectionInfos.get(0);
//    	Map<String, FieldConfig> fieldConfigs = new HashMap<String, FieldConfig>();
//    	fieldConfigs.put("menu.value", new FieldConfig(FacetType.NORMAL, true, null));
//    	colInfo.setCollectionFieldsConfigs(fieldConfigs);
//    	status = client.reIndex(colInfo);
//    	assertTrue(status);
//    	//test if queries work as should
//    	//first one
//    	query = new Query();
//    	query.add_SearchTerm(COLLECTIONTOPLAYWITH, "menu.value.raw_normal", "abandoned");
////    	query.addFacetField("menu.value.raw_normal", 10);
//    	response = client.search(query, COLLECTION_DOMAIN);
//    	assertEquals(1, response.getTotalHits());
//    	//also check facets
//    	System.out.println(response.getFacets().getFacets());
//    	//second one
//    	query = new Query();
//    	query.add_SearchTerm(COLLECTIONTOPLAYWITH, "menu.value.raw_normal", "to");
//    	response = client.search(query , COLLECTION_DOMAIN);
//    	assertEquals(0, response.getTotalHits());
//    	//now test reindexing with FacetType.NON_TOKENIZED
//    	collectionInfos = client.getCompleteCollectionInfo(COLLECTIONTOPLAYWITH, COLLECTION_DOMAIN);
//    	assertEquals(1, collectionInfos.size());
//    	colInfo = collectionInfos.get(0);
//    	fieldConfigs = new HashMap<String, FieldConfig>();
//    	fieldConfigs.put("menu.id", new FieldConfig(FacetType.NON_TOKENIZED, true, null));
//    	colInfo.setCollectionFieldsConfigs(fieldConfigs);
//    	System.out.println("TESTING REINDEXING");
//    	status = client.reIndex(colInfo);
//    	assertTrue(status);
//    	System.out.println("TESTED REINDEXING");
//    	//test if queries work as should
//    	//first one
//    	query = new Query();
//    	query.add_SearchTerm(COLLECTIONTOPLAYWITH, "menu.id.raw_non_tokenized", "*");
////    	query.addFacetField("menu.id.raw_non_tokenized", 10);
//    	response = client.search(query, COLLECTION_DOMAIN);
//    	assertEquals(1, response.getTotalHits());
//    	System.out.println(response.getFacets().getFacets());
//    	//second one
//    	query = new Query();
//    	query.add_SearchTerm(COLLECTIONTOPLAYWITH, "menu.id.raw_non_tokenized", "little");
//    	response = client.search(query, COLLECTION_DOMAIN);
//    	assertEquals(0, response.getTotalHits());
//    	
//    	//test deletion of a record
//    	status = client.delete(COLLECTIONTOPLAYWITH, "1");
//    	assertTrue(status);
//    	try { Thread.sleep(SLEEPTIME);} catch (InterruptedException e) {e.printStackTrace();}
//    	query = new Query();
//    	query.add_SearchTerm(COLLECTIONTOPLAYWITH, "menu.value", "notifications");
//    	response = client.search(query, COLLECTION_DOMAIN);
//    	assertEquals(0, response.getTotalHits());
//    	   	
//    	status = client.dropCollection(COLLECTIONTOPLAYWITH);
//    	assertTrue(status);
//    	try { Thread.sleep(SLEEPTIME);} catch (InterruptedException e) {e.printStackTrace();}
//    	status = client.getCollections(COLLECTION_DOMAIN).contains(COLLECTIONTOPLAYWITH);
//    	assertFalse(status);
//    	boolean found = false;
//    	for(CollectionInfo ci : client.getCompleteCollectionInfo(COLLECTIONTOPLAYWITH, COLLECTION_DOMAIN))
//    		if(ci.getId().equals(COLLECTIONTOPLAYWITH))
//    			found = true;
//    	assertFalse(found);
//
//    }
//    
//    
//    
//    @Test
//    public void c6_testAllDeletes() throws NoAvailableIndexServiceInstance{
//    	try {
//			Thread.sleep(SLEEPTIME);
//		} catch (InterruptedException e) {e.printStackTrace();}
//    	boolean result = client.deleteCompleteCollectionInfo(COLLECTION2CHECK);
//    	assertEquals(result, true);
//    	try {Thread.sleep(SLEEPTIME);} catch (InterruptedException e) {e.printStackTrace();}
//    	assertTrue(client.getCompleteCollectionInfo(COLLECTION2CHECK, COLLECTION_DOMAIN).isEmpty());
//    	
//    }
//    
//    
//    
//}