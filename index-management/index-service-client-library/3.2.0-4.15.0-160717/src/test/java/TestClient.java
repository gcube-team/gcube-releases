import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.gcube.rest.index.client.ClientFactory;
import org.gcube.rest.index.client.cache.IndexClient;
import org.gcube.rest.index.client.exceptions.BadCallException;
import org.gcube.rest.index.client.exceptions.NoAvailableIndexServiceInstance;
import org.gcube.rest.index.common.search.Query;
import org.gcube.rest.index.common.search.Query.SearchMode;
import org.gcube.rest.index.common.search.SearchResult;
import org.gcube.rest.index.common.search.Search_Response;
import org.springframework.util.FileCopyUtils;
import org.gcube.rest.index.common.discover.exceptions.IndexDiscoverException;
import org.gcube.rest.index.common.entities.CollectionInfo;
import org.gcube.rest.index.common.entities.configuration.CollectionStatus;
import org.gcube.rest.index.common.entities.configuration.DatasourceType;
import org.gcube.rest.index.common.entities.fields.Field;
import org.gcube.rest.index.common.entities.fields.config.FacetType;
import org.gcube.rest.index.common.entities.fields.config.FieldConfig;
import org.gcube.rest.index.common.entities.fields.config.FieldType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestClient {

	private static Gson prettygson = new GsonBuilder().setPrettyPrinting().create();
	
//	public static String DOMAIN = "/d4science.research-infrastructures.eu/gCubeApps/iSearch";
	public static String DOMAIN = "/gcube/devsec/devVRE";
//	public static String DOMAIN = "/gcube/devNext/NextNext";
	
	
	public static void main (String [] args) throws IOException {
		
		IndexClient client = ClientFactory.getMeAnIndexClient(DOMAIN);
		try{
			test(client);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			ClientFactory.closeAllClients();
		}
		
	}
	
	
	
	
	
	
	
	public static void test (IndexClient client) throws NoAvailableIndexServiceInstance, InterruptedException, BadCallException, IndexDiscoverException, IOException {
		
		System.out.println(client.getEndpointProvider().endpointsNumber());
		System.out.println(client.getEndpointProvider().getAnEndpoint());
		
//		System.out.println(client.getAllCollectionFields(DOMAIN));
		
		List<String> collectionIDs = client.getCollections(DOMAIN);
		
		System.out.println(collectionIDs);
		
		System.out.println(client.getCompleteCollectionInfo("3ba5d6c8e71d484a7d8320bdd9cdc9f484f334de3d706dcb1d57fe7933edb288", ""));
		
		
//		Query query = new Query();
////		query.setPosition_paging(0);
//		query.setSize_paging(10);
//		
//		query.setSearchMode(SearchMode.SCROLL);
//		
//		Search_Response sr = client.search(query, DOMAIN);
//		System.out.println("Found: "+sr.getTotalHits());
//		
//		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(
//				sr.getSearchResultList().stream().map(result -> result.getRecordId()).collect(Collectors.toList())
//				));
////		System.out.println(sr.getScrollId());
//		
//		while(sr.getScrollId()!=null && !sr.getScrollId().isEmpty() && sr.getSearchResultList().size()>0){
//			
//			query.setScrollId(sr.getScrollId());
//			sr = client.search(query, DOMAIN);
//			
//			System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(
//				sr.getSearchResultList().stream().map(result -> result.getRecordId()).collect(Collectors.toList())
//			));
////			System.out.println(sr.getScrollId());
//		}
		
		
//		System.out.println(ClientFactory.getMeAnIndexClient("/gcube/devsec/devVRE").getCompleteCollectionInfo("3759b55ef5f6de23111422072c36aa4cd9c7354d80163873e5efba737f90eb39", "/gcube/devsec/devVRE"));
		
//		CollectionInfo sampleCollInfo = ClientFactory.getMeAnIndexClient("/gcube/devsec/devVRE").getCompleteCollectionInfo("3759b55ef5f6de23111422072c36aa4cd9c7354d80163873e5efba737f90eb39", "/gcube/devsec/devVRE").get(0);
//		sampleCollInfo.setCollectionDomain(DOMAIN);
//		System.out.println(client.setCompleteCollectionInfo(sampleCollInfo));
		
//		Iterator<CollectionInfo> iter1 = client.getCompleteCollectionInfo("", DOMAIN).iterator();
//		while(iter1.hasNext()){
//			CollectionInfo ci = iter1.next();
//			ci.setStatus(CollectionStatus.OK);
//			client.setCompleteCollectionInfo(ci);
//		}
		
//		System.out.println(client.getCollectionDocCounts(DOMAIN));
		
//		System.out.println(client.getCollections(DOMAIN));
		
//		System.out.println(client.setCompleteCollectionInfo(coll));
		
		
//		System.out.println(sampleCollInfo);
		
//		System.out.println(client.setCompleteCollectionInfo(sampleCollInfo));
		
//		System.out.println(client.deleteCompleteCollectionInfo("111111111"));
		
//		System.out.println(client.getCompleteCollectionInfo("", DOMAIN));
		
		
		
//		Map<String, FieldConfig> configs = ci.getCollectionFieldsConfigs();
//		configs.get("dc:title").setFacetType(FacetType.NON_TOKENIZED);
//		configs.get("dc:description").setFacetType(FacetType.NORMAL);
//		
//		System.out.println(ci);
//		client.setCompleteCollectionInfo(ci);
//		
//		client.reIndex(ci);
		
		
		
		
//		String colID = "af697225988147c8cd3116ae789f182b1e1e43487242ae48ebe7aa17996a8d32";
//		List<CollectionInfo> previous = client.getCompleteCollectionInfo(colID, DOMAIN);
//		if(previous!=null && !previous.isEmpty() && previous.get(0).getId().equals(colID)) //if a collection info for this id exists, skip preconfiguring
//			System.out.println("ok");
		
//		System.out.println(client.getCompleteCollectionInfo("", DOMAIN));
		
//		client.deleteCompleteCollectionInfo("9e4e6a7e36d879176adff5064b78f494e567eea8c10a5d04c0a161b77d8ac444");
		
//		CollectionInfo ci = new CollectionInfo("9e4e6a7e36d879176adff5064b78f494e567eea8c10a5d04c0a161b77d8ac444", "/gcube/devsec/devVRE", DatasourceType.ELASTIC, "sdfasdfsdf", "rtyuertyhdfb", new Date());
//		System.out.println(client.setCompleteCollectionInfo(ci));
		
//		System.out.println(client.getCollections("/gcube/devsec/devVRE"));
//		for(int i=0;i<1000;i++)
//			System.out.println(client.getCompleteCollectionInfo("3121eccdcbf9c6e982dcc5a2164b8b4029f51d8b1c448ddaf46316178d755c6f", "/gcube/devsec/devVRE"));
//		System.out.println(client.getAllCollectionFields("temp-coll"));

//		CollectionInfo collection = new CollectionInfo("257ac5fff4d048c7c660287b3dd35250f750da27705d4b9e65fc5c2949cb82df", "/gcube/devsec/devVRE", DatasourceType.ELASTIC, "DummyCollection", "A dummy collection for testing...", new Date());
//		collection.setCollectionFields(Arrays.asList("dc:title", "dc:creator"));
//		Map<String,String> alias = new HashMap<String,String>();
//		alias.put("dc:title", "Title");	alias.put("dc:creator", "Author");
//		collection.setCollectionFieldAlias(alias);
//		collection.setJsonTransformer("{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"value3\"}");
//		boolean result = client.setCompleteCollectionInfo(collection);
//		System.out.println(result);
		
//		System.out.println(client.getAllCollectionFields("/gcube/devsec/devVRE"));
		
//		System.out.println(client.createIndex("twitteralt", "/gcube/devsec/devVRE"));
//		System.out.println(client.getAllCollectionFields(""));
		
//		System.out.println(client.deleteCompleteCollectionInfo("twitter"));

//		System.out.println(client.getCollections("/gcube/devsec/devVRE").size());
		
//		System.out.println(client.getCompleteCollectionInfo("257ac5fff4d048c7c660287b3dd35250f750da27705d4b9e65fc5c2949cb82df", "/gcube/devsec/devVRE"));
//		System.out.println(client.getCompleteCollectionInfo("", "/gcube/devsec/devVRE"));
//		System.out.println(client.getCollections("/gcube/devsec/devVRE"));
//		System.out.println(client.getAllCollectionFields("/gcube/devsec/devVRE"));
//		System.out.println(client.getCollectionFieldsAlias("257ac5fff4d048c7c660287b3dd35250f750da27705d4b9e65fc5c2949cb82df", true, "/gcube/devsec/devVRE"));
//		System.out.println(client.getJSONTransformer("257ac5fff4d048c7c660287b3dd35250f750da27705d4b9e65fc5c2949cb82df", "/gcube/devsec/devVRE"));
		
		
		
		
//		long b4 = System.currentTimeMillis();
//		for(int i=0;i<1000;i++)
//			client.getCompleteCollectionInfo("bebc670f-dd10-40ec-8439-2555d586d426");
//		System.out.println("Took: "+(double)(System.currentTimeMillis()-b4)/1000);
		
//		System.out.println(client.createIndex("abc"));
//		client.insertJson("abc","{\"description\":\"the a of the or in nikolas\"}");

//		System.out.println(client.getCollectionFieldsAlias("80041b5236ee79763e95b621dfe8802410a4e6f3e293fa4afeedf87acc1d04fb", true));
		
//		System.out.println(client.getAllCollectionFieldsAliases(true));
		
//		System.out.println(client.getAllCollectionFields());
		
//		System.out.println(client.getCollectionFieldsAlias("", true));
		
		
//		System.out.println(client.getCompleteCollectionInfo("fc075650d6d041f99b7544693ba06cfda0c8a6945db1c72a1f611510c62e6539", DOMAIN));
		
//		Query query = new Query();
//		query.setPosition_paging(0);
//		query.setSize_paging(10);
//		query.add_SearchTerm("_all", "1");
//		Search_Response sr = client.search(query, DOMAIN);
//		System.out.println(sr.getFacets());
//		System.out.println(client.getCollections());
		
		
//		for (String s: client.getCollections()) // <-- pairnw ta resources
//			System.out.println(s);
		
//		boolean status = client.insertJson("plato-academy", "1", "{\"id\": \"0001\",\"type\": \"donut\",\"name\": \"Cake\",\"ppu\": 0.55,\"batters\":{\"batter\":[{ \"id\": \"1001\", \"type\": \"Regular\" },{ \"id\": \"1002\", \"type\": \"Chocolate\" },{ \"id\": \"1003\", \"type\": \"Blueberry\" },{ \"id\": \"1004\", \"type\": \"Devil's Food\" }]},\"topping\":[{ \"id\": \"5001\", \"type\": \"None\" },{ \"id\": \"5002\", \"type\": \"Glazed\" },{ \"id\": \"5005\", \"type\": \"Sugar\" },{ \"id\": \"5007\", \"type\": \"Powdered Sugar\" },{ \"id\": \"5006\", \"type\": \"Chocolate with Sprinkles\" },{ \"id\": \"5003\", \"type\": \"Chocolate\" },{ \"id\": \"5004\", \"type\": \"Maple\" }]}");
//		boolean status = 
//				client.insertJson("arxiv", "1", "{\"datestamp\":\"2008-12-13\",\"dc:creator\":[\"Streinu,Ileana\",\"Theran,Louis\"],\"dc:date\":[\"2007-03-30\",\"2008-12-13\"],\"dc:description\":[\"Wedescribeanewalgorithm,thepebblegamewithcolors,anduseitobtainacharacterizationofthefamilyofsparsegraphsandalgorithmicsolutionstoafamilyofproblemsconcerningtreedecompositionsofgraphs.Specialinstancesofsparsegraphsappearinrigiditytheoryandhavereceivedincreasedattentioninrecentyears.Inparticular,ourcoloredpebblesgeneralizeandstrengthenthepreviousresultsofLeeandStreinuandgiveanewproofoftheTutte-Nash-Williamscharacterizationofarboricity.Wealsopresentanewdecompositionthatcertifiessparsitybasedonthepebblegamewithcolors.OurworkalsoexposesconnectionsbetweenpebblegamealgorithmsandprevioussparsegraphalgorithmsbyGabow,GabowandWestermannandHendrickson.\",\"Comment:ToappearinGraphsandCombinatorics\"],\"dc:identifier\":\"http://arxiv.org/abs/0704.0002\",\"dc:subject\":[\"Mathematics-Combinatorics\",\"ComputerScience-ComputationalGeometry\",\"05C85\",\"05C70\",\"68R10\",\"05B35\"],\"dc:title\":\"Sparsity-certifyingGraphDecompositions\",\"dc:type\":\"text\",\"identifier\":\"oai:arXiv.org:0704.0002\",\"setSpec\":[\"cs\",\"math\"]}");
//		boolean status = client.delete("wewe", "6");
//		boolean status = client.dropCollection(Constants.TRANSFORMERS_COLLECTION_NAME);
		
//		boolean status = client.dropCollection("oai-pmh-test-collection");
//		boolean status = client.dropCollection("gcube-collection-transformers");
//		boolean status = client.dropCollection("argolida");
//		client.dropCollection("ccc");
		
//		System.out.println(client.createIndex("mydummyindex"));
		
//		boolean status = client.insertJson("argolida3", "{\"datestamp\":\"2008-12-13\"}");
//		System.out.println(status);
		
//		System.out.println(client.getEndpointProvider().getAnEndpoint());
		
//		System.out.println(client.getCollections());
//		System.out.println(client.getCollections());
//		System.out.println(client.getCollections());
//		System.out.println(client.getCollections());
//		System.out.println(client.getCollections());
//		System.out.println(client.getAllCollectionFields());
//		System.out.println(client.getAllCollectionFields());
//		System.out.println(client.getAllCollectionFields());
//		System.out.println(client.getAllCollectionFields());
		
//		System.out.println(client.getAllCollectionsInfo());
		
//		client.getCollectionInfo("");
		
		
//		Map<String,String> fieldAlias = new HashMap<String, String>();
//		fieldAlias.put("dc:contributor", "Contributor");
//		fieldAlias.put("dc:coverage", "Coverage");
//		fieldAlias.put("dc:creator", "Creator");
//		fieldAlias.put("dc:date", "Date");
//		fieldAlias.put("dc:description", "Description");
//		fieldAlias.put("dc:format", "Format");
//		fieldAlias.put("dc:identifier", "Identifier");
//		fieldAlias.put("dc:language", "Language");
//		fieldAlias.put("dc:publisher", "Publisher");
//		fieldAlias.put("dc:relation", "Relation");
//		fieldAlias.put("dc:rights", "Rights");
//		fieldAlias.put("dc:source", "Source");
//		fieldAlias.put("dc:subject", "Subject");
//		fieldAlias.put("dc:title", "Title");
//		fieldAlias.put("dc:type", "Type");
//		
//		
//		CollectionInfo collInfo;
//		String id;
//		String domain = "/gcube/devsec/devVRE";
//		DatasourceType type = DatasourceType.ELASTIC;
//		String title;
//		String description;
//		Date date = new Date();
//		
//		id = "0000000000000000000000000000000000000000000000000000000000000000";
////		id = "6bcae4683bd7a61b4c4a689403855d670b555ad7a634153762fd64a9a3045c82";
//		title = "DiVA.org datasource";
//		description = "A datasource which contains... blah blah ... and another blah blah...";
//		collInfo = new CollectionInfo(id, domain, DatasourceType.ELASTIC, title, description, date);
//		collInfo.setCollectionFieldsAliases(fieldAlias);
//		client.setCompleteCollectionInfo(collInfo);
		
		
		
//		System.out.println(client.deleteCollectionFieldsAlias("sample-anemi"));
//		System.out.println(client.getCollectionFieldsAlias("sample-anemi", true));
		
		
		
//		System.out.println(client.getAllCollectionFields());
		
		
//		FileInputStream fis = null;
//		try {
//			fis = new FileInputStream("/home/nikolas/w");
//			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//			CollectionInfo collInfo = new CollectionInfo(br.readLine(), br.readLine(), br.readLine(), "oai-pmh");
//			client.setCollectionInfo("anemi4", collInfo);
//			br.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		CollectionInfo collInfo = new CollectionInfo("anemi", "ANEMH Ψηφιακό Αποθετήριο", "Μια συλλογή από... μπλα μπλα...", "oai-pmh");
//		client.setCollectionInfo("anemi", collInfo);
		
//		client.deleteCollectionInfo("anemi1");
		
//		long st = System.currentTimeMillis();
//		System.out.println(client.getCollectionInfo(""));
//		for(int i=0;i<1000000;i++)
//			client.getCollectionInfo("anemi1");
//		System.out.println("Took: "+((double)(System.currentTimeMillis()-st))/1000);
		
//		System.out.println(client.getCompleteCollectionInfo("", DOMAIN).size());
		
//		System.out.println(client.getJSONTransformer(""));
		
//		System.out.println(client.getCollections(DOMAIN));
		
//		Query query = new Query();
//		query.setPosition_paging(0);
//		query.setSize_paging(2);
//		query.add_SearchTerm("testindex3", "text", "*");
//		Search_Response sr = client.search(query, DOMAIN);
//		System.out.println(sr.getTotalHits());
//		System.out.println(sr.getSearchResultList().get(0).getSource());
		
		
		/*
		
		Query query = new Query();
		
		query.setPosition_paging(0);
		query.setSize_paging(2);
//		query.add_SearchTerm("ctaegypt", "dc:rights", "*2015*");
//		query.add_SearchTerm("ctaegypt", "dc:creator", "*Mohamed*");
		query.add_SearchTerm("anemi", "Συνταχθέν");
//		query.add_SearchTerm("argolida", "dc:title", "*Περίαπτο*");
//		query.add_SearchTerm("argolida", "dc:date", "*(*)*");
		
		System.out.println(query.get_SearchTerms());
		
		System.out.println(client.search(query, DOMAIN));
		
		//---------------------------------------------------------------------
		System.out.println("---------------------------------------------------------------");
		
		query.setPosition_paging(2);
		query.setSize_paging(2);
//		query.add_SearchTerm("ctaegypt", "dc:rights", "*2015*");
//		query.add_SearchTerm("ctaegypt", "dc:creator", "*Mohamed*");
//		query.add_SearchTerm("ctaegypt", "*mohamed*");
//		query.add_SearchTerm("anemi", "Συνταχθέν");
		
//		query.add_SearchTerm("argolida", "dc:title", "*Περίαπτο*");
//		query.add_SearchTerm("argolida", "dc:date", "*(*)*");
		
			
		System.out.println(query.get_SearchTerms());
		
		System.out.println(client.search(query, DOMAIN));
		
		*/
		
//		System.out.println(client.getAllCollectionFieldsAliases(true));
//		client.deleteCollectionFieldsAlias("bf7039fe8998e8d4f628448273dc25c3297957a2fd9eae7b4bdac0015441b8f6");
//		client.deleteCollectionFieldsAlias("bf7039fe8998e8d4f628448273dc25c3297957a2fd9eae7b4bdac0015441b8f6");
//		client.deleteCollectionFieldsAlias("d6cddc6b92f5e46e1468a754a85b6ae1e2ff1c2af5bfe2d2b51be1145a34eafc");
//		client.deleteCollectionFieldsAlias("6adf175fb1c72f068b09fe3243100322463391033d61356a8835f66c03e77d10");
//		client.deleteCollectionFieldsAlias("a6a23950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3");
//		client.deleteCollectionFieldsAlias("7aa1a9a72a2947def6a635fcb1081d3eddfa3a2d0a4fc12a79dfb07c5d50cf25");
		
		/*
		Query query = new Query();
		
		query.setPosition_paging(0);
		query.setSize_paging(2);
		
		for(String collectionid : client.getAllCollectionFields().keySet())
			query.add_SearchTerm(collectionid, "d");
		
//		query.addFacetField("record.metadata.oai_dc:dc.dc:title", 5);
//		query.addFacetField("dc:description", 5);
//		query.addFacetField("record.metadata.oai_dc:dc.dc:description", 5);
		query.addFacetField("Description", 5);
//		query.addFacetField("dc:title", 5);
		query.addFacetField("Title", 5);
		
		Search_Response sr = client.search(query);
		
		List<SearchResult> results = sr.getSearchResultList();
		for(SearchResult res : results){
			System.out.println(prettygson.toJson(res.getSource()));
		}
		
		System.out.println(client.search(query));
		*/
		
//		boolean status = client.insertJson("ccc", "{\"anonymousField\":\"aaaa\"}");
//		System.out.println(status);dc:description
		
//		boolean status = client.dropCollection("eee");
//		client.dropCollection("oai-pmh-test-collection");
//		System.out.println(status);
		
//		Map<String, String> fieldAlias = new HashMap<String,String>();
//		fieldAlias.put("author", "creator");
//		fieldAlias.put("ISBN", "description");
//		boolean status = client.setCollectionFieldsAlias("gutenberg1", fieldAlias);
//		System.out.println(status);
		
//		Map<String,Map<String,String>> cfac = client.getAllCollectionFieldsAlias();
//		System.out.println(cfa);
		
//		boolean status = client.deleteCollectionFieldsAlias("gutenberg1");
		
//		Map<String,List<String>> collFields = client.getAllCollectionFields();
//		System.out.println(collFields);
		
//		Map<String,String> results = client.fieldSearch("gutenberg", "author", "Someone");
//		System.out.println(results);
		
//		boolean status = client.setJSONTransformer("plato-academy1", "[{\"operation\":\"remove\",\"spec\":{\"record\":{\"metadata\":{\"oai_dc:dc\":{\"xsi:*\":\"\"}}}}},{\"operation\":\"remove\",\"spec\":{\"record\":{\"metadata\":{\"oai_dc:dc\":{\"xmlns:*\":\"\"}}}}},{\"operation\":\"shift\",\"spec\":{\"*\":{\"*\":\"\"}}},{\"operation\":\"shift\",\"spec\":{\"*\":{\"datestamp\":\"datestamp\",\"identifier\":\"identifier\",\"setSpec\":\"setSpec\",\"oai_dc:dc\":\"\"}}}]");
		
		//INSERT TRANSFORMERS
//		String transformerJSON = "[{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"xsi:*\":\"\"}}}},{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"xmln*\":\"\"}}}},{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"dc:*\":{\"xml:lang\":\"\"}}}}},{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"dc:*\":{\"*\":{\"xml*\":\"\"}}}}}},{\"operation\":\"shift\",\"spec\":{\"metadata\":{\"oai_dc:dc\":\"\"}}},{\"operation\":\"shift\",\"spec\":{\"*\":{\"content\":\"&1\",\"*\":{\"content\":\"&2\"}},\"dc:format\":\"&\",\"dc:identifier\":\"&\",\"dc:language\":\"&\",\"dc:publisher\":\"&\"}}]";
//		boolean status = client.setJSONTransformer("argolida", transformerJSON);
//		System.out.println(status);
//		transformerJSON = "[{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"xsi:*\":\"\"}}}},{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"xmlns:*\":\"\"}}}},{\"operation\":\"shift\",\"spec\":{\"*\":{\"datestamp\":\"datestamp\",\"identifier\":\"identifier\",\"setSpec\":\"setSpec\",\"oai_dc:dc\":\"\"}}}]";
//		status = client.setJSONTransformer("anemi", transformerJSON);
//		System.out.println(status);
//		transformerJSON = "[{\"operation\":\"remove\",\"spec\":{\"record\":{\"metadata\":{\"oai_dc:dc\":{\"xsi:*\":\"\"}}}}},{\"operation\":\"remove\",\"spec\":{\"record\":{\"metadata\":{\"oai_dc:dc\":{\"xmlns:*\":\"\"}}}}},{\"operation\":\"shift\",\"spec\":{\"*\":{\"*\":\"\"}}},{\"operation\":\"shift\",\"spec\":{\"*\":{\"datestamp\":\"datestamp\",\"identifier\":\"identifier\",\"setSpec\":\"setSpec\",\"oai_dc:dc\":\"\"}}}]";
//		status = client.setJSONTransformer("arxiv", transformerJSON);
//		System.out.println(status);
//		String transformerJSON = "[{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"xsi:*\":\"\",\"xmlns:*\":\"\",\"dc:*\":{\"*\":{\"xml:lang\":\"\"}}}}}},{\"operation\":\"shift\",\"spec\":{\"*\":{\"datestamp\":\"datestamp\",\"identifier\":\"identifier\",\"setSpec\":\"setSpec\",\"oai_dc:dc\":\"\"}}},{\"operation\":\"remove\",\"spec\":{\"dc:*\":{\"xml:lang\":\"\"}}},{\"operation\":\"shift\",\"spec\":{\"dc:identifier\":\"&\",\"dc:format\":\"&\",\"dc:contributor\":\"&\",\"dc:creator\":\"&\",\"dc:date\":\"&\",\"dc:language\":\"&\",\"dc:relation\":\"&\",\"dc:*\":{\"content\":\"&1\",\"*\":{\"content\":\"&2\"}},\"datestamp\":\"datestamp\",\"identifier\":\"identifier\",\"setSpec\":\"setSpec\"}}]";
//		boolean status = client.setJSONTransformer("behaviorandlawjournal", transformerJSON);
//		System.out.println(status);
//		transformerJSON = "[{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"xsi:*\":\"\",\"xmlns:*\":\"\",\"dc:*\":{\"*\":{\"xml:lang\":\"\"}}}}}},{\"operation\":\"shift\",\"spec\":{\"*\":{\"datestamp\":\"datestamp\",\"identifier\":\"identifier\",\"setSpec\":\"setSpec\",\"oai_dc:dc\":\"\"}}},{\"operation\":\"remove\",\"spec\":{\"dc:*\":{\"xml:lang\":\"\"}}},{\"operation\":\"shift\",\"spec\":{\"dc:identifier\":\"&\",\"dc:format\":\"&\",\"dc:contributor\":\"&\",\"dc:creator\":\"&\",\"dc:date\":\"&\",\"dc:language\":\"&\",\"dc:relation\":\"&\",\"dc:*\":{\"content\":\"&1\",\"*\":{\"content\":\"&2\"}},\"datestamp\":\"datestamp\",\"identifier\":\"identifier\",\"setSpec\":\"setSpec\"}}]";
//		status = client.setJSONTransformer("ctaegypt", transformerJSON);
//		System.out.println(status);
		
		
//		Map<String,String> transformers = client.getJSONTransformer("argolida");
//		System.out.println(transformers.get("argolida"));
		
//		String argolidaRecord = "{\"metadata\": {\"oai_dc:dc\": {\"xmlns:dcmitype\": \"http://purl.org/dc/dcmitype/\",\"xmlns:dcterms\": \"http://purl.org/dc/terms/\",\"dc:description\": [\" http://www.argolisculture.gr/el/sylloges/antikeimena/2471_el/\",{\"xml:lang\": \"el\",\"content\": \"Βαθύ ημισφαιρικό σχήμα, το πάνω σώμα κλείνει ελαφρά προς τα μέσα, χείλος έξω νεύον, επικλινές. Οι δύο οριζόντιες, κυκλικής διατομής λαβές είναι τοποθετημένες ψηλά στο σώμα, βάση δακτυλιόσχημη (FS, 281). Πηλός καστανοερυθρός, βερνίκι ερυθρωπό, εσωτερικά φέρει πλατιά ταινία στο ύψος των λαβών και στον πυθμένα. Το χείλος είναι ολόβαφο. Η ζώνη που φέρει την γραπτή διακόσμηση οριοθετείται από πλατιές ταινίες, μία ακριβώς κάτω από το χείλος και τρεις στο κάτω σώμα. Η διακόσμηση αποτελείται από ένα κεντρικό τρίγλυφο (FM 75) εκατέρωθεν του οποίου υπάρχουν δύο πλευρικά. Το πρώτο αποτελείται από δύο κάθετες γραμμές με κάθετη τεθλασμένη (FM 61) μεταξύ τους και το δεύτερο από τρεις κάθετες γραμμές, μεταξύ των οποίων υπάρχουν οριζόντιες κυματοειδείς (FM 53) γραμμές. Οι λαβές φέρουν τρεις πινελιές ερυθρού χρώματος. Η άνω επιφάνεια της βάσης είναι ολόβαφη.\"},{\"xml:lang\": \"el\",\"content\": \"Inventory number 197\"},{\"xml:lang\": \"el\",\"content\": \"Δ’ Εφορεία Προϊστορικών & Κλασικών Αρχαιοτήτων\"}],\"dc:language\": \"zxx\",\"dc:subject\": {\"xml:lang\": \"el\",\"content\": \"Πήλινο αγγείο\"},\"dc:coverage\": {\"xml:lang\": \"el\",\"content\": \"Νοτιοδυτική Σοινικία, Υπόγεια Δωμάτια Ζ1-Ζ2\"},\"dc:creator\": {\"xml:lang\": \"el\",\"content\": \"Άγνωστος\"},\"xmlns:xsi\": \"http://www.w3.org/2001/XMLSchema-instance\",\"dc:format\": \"0.29 μ.\",\"dc:rights\": [{\"xml:lang\": \"el\",\"content\": \"Αναφορά Δημιουργού-Μη Εμπορική Χρήση-Όχι Παράγωγα Έργα 4.0 Διεθνές (CC BY-NC-ND 4.0): Δ’ Εφορεία Προϊστορικών & Κλασικών Αρχαιοτήτων\"},{\"xml:lang\": \"el\",\"content\": \"Creative Commons Αναφορά Μη Εμπορική Χρήση Όχι Παράγωγα Έργα 4.0\"},{\"xml:lang\": \"el\",\"content\": \"https://creativecommons.org/licenses/by-nc-nd/4.0/deed.el\"}],\"xmlns:oai_dc\": \"http://www.openarchives.org/OAI/2.0/oai_dc/\",\"xmlns\": \"info:fedora/fedora-system:def/foxml#\",\"dc:type\": {\"xml:lang\": \"el\",\"content\": \"Αγγείο\"},\"dc:title\": {\"xml:lang\": \"el\",\"content\": \"Κρατηράς σκυφοειδής\"},\"dc:date\": {\"xml:lang\": \"el\",\"content\": \"ΥΕ ΙΙΙ Β2 (1250-1180 π.Χ.)\"},\"dc:identifier\": \"http://hdl.handle.net/11525/DOI-28523\",\"xmlns:dc\": \"http://purl.org/dc/elements/1.1/\",\"dc:source\": {\"xml:lang\": \"el\",\"content\": \"Δ’ Εφορεία Προϊστορικών & Κλασικών Αρχαιοτήτων\"},\"dc:publisher\": \"Δ’ Εφορεία Προϊστορικών & Κλασικών Αρχαιοτήτων\"}}}";
//		boolean status = client.insertJson("argolida", Toolbox.toUnicode(argolidaRecord));
//		System.out.println(status);
		
		
//		System.out.println(client.getJSONTransformer("arxiv"));
//		System.out.println(client.getAllJSONTransformers());
		
//		boolean status = client.deleteJSONTransformer("ctaegypt");
//		System.out.println(status);
		
		
//		String json = "{  \"dc:coverage\" : \"Παλιομάνδρι, Τάφος Δ\",  \"dc:creator\" : \"Άγνωστος\",  \"dc:date\" : \"ΥΕ ΙΙΙΒ (1300-1180 π.Χ.)\",  \"dc:description\" : [ \"Inventory number 1940\", \"Δ’ Εφορεία Προϊστορικών & Κλασικών Αρχαιοτήτων\" ],  \"dc:format\" : \"0.77 μ.\",  \"dc:identifier\" : \"http://hdl.handle.net/11525/DOI-28561\",  \"dc:language\" : \"zxx\",  \"dc:publisher\" : \"Δ’ Εφορεία Προϊστορικών & Κλασικών Αρχαιοτήτων\",  \"dc:rights\" : [ \"Αναφορά Δημιουργού-Μη Εμπορική Χρήση-Όχι Παράγωγα Έργα 4.0 Διεθνές (CC BY-NC-ND 4.0): Δ’ Εφορεία Προϊστορικών & Κλασικών Αρχαιοτήτων\", \"Creative Commons Αναφορά Μη Εμπορική Χρήση Όχι Παράγωγα Έργα 4.0\", \"https://creativecommons.org/licenses/by-nc-nd/4.0/deed.el\" ],  \"dc:source\" : \"Δ’ Εφορεία Προϊστορικών & Κλασικών Αρχαιοτήτων\",  \"dc:subject\" : \"Πήλινο αγγείο\",  \"dc:title\" : \"Κύαθος με ανυψωμένη λαβή\",  \"dc:type\" : \"Αγγείο\"}";
//		String json = "{\"dc:description\":\"Το βιβλίο του Νικόλα. Δοκιμή\"}";
//		System.out.println(json);
//		boolean status = client.insertJson("argolida2", "3", json);
//		System.out.println(status);
		
//		Settings settings = Settings.settingsBuilder()
//			    .put("client.transport.sniff", true)
//			    .put("cluster.name", "gCubeIndex")
//			    .build();
//		TransportClient client = TransportClient.builder().settings(settings).build()
//			    .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("localhost", 9300)));
//		List<String> collections = Lists.newArrayList(client.admin().cluster()
//			    .prepareState().execute()
//			    .actionGet().getState()
//			    .getMetaData().concreteAllIndices());
//		System.out.println(collections);
//		client.close();
		
		
		
//		Query query = new Query();
//		for(String coll : client.getAllCollectionFields().keySet())
//			query.add_SearchTerm(coll, "*");
//		query.setPosition_paging(0);
//		query.setSize_paging(10);
//		
//		Search_Response sr = client.search(query);
//		
//		System.out.println("Found: "+sr.getTotalHits());
		
		
		
//		IndexClient client2 = ClientFactory.getMeAnIndexClient("/gcube");
//		System.out.println(client2.getAllCollectionFields());
//		System.out.println(client2.getAllCollectionFields());
		
//		ClientFactory.closeClient("/gcube/devNext");
		
//		System.out.println("================================================================");
//		System.out.println(client.getAllCollectionFields());
		
//		List<String> aaa = new ArrayList<String>();
//		aaa.add("0ae2b659be107214177cb36cf40d6d110f290a05d8ee2dd3b569abb81b485973");
//		aaa.add("00003950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3");
//		aaa.add("bf7039fe8998e8d4f628448273dc25c3297957a2fd9eae7b4bdac0015441b8f6");
//		for(String a : aaa){
//			client.dropCollection(a);
//			client.deleteJSONTransformer(a);
//		}
		
//		System.out.println(client.getAllCollectionFieldsAliases(true));
//		System.out.println(client.getAllCollectionFields());
		
//		int i = Integer.parseInt("-10");
//		while(i<0)
//			System.out.println(client.getAllCollectionFields());
		
//		System.out.println(client.dropCollection("mydummyindex"));
//		System.out.println(client.dropCollection("dummycollection111111"));
//		System.out.println(client.dropCollection("dummycollection111"));
		
//		System.out.println(client.deleteJSONTransformer("10003950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3"));
//		System.out.println(client.deleteJSONTransformer("00003950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3"));
//		System.out.println(client.deleteJSONTransformer("5dd67780441f57408e4205f46c5f332b998e20202134843ed07c9ca90b60aacc"));
		
//		System.out.println(client.setJSONTransformer("10003950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3", 
//				"[{\"operation\":\"shift\",\"spec\":{\"*\":{\"*\":{\"oai_dc:dc\": \"\"}}}}]"));
		
//		System.out.println(client.createIndex("00003950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3"));
//		System.out.println(client.insertJson("10003950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3", 
//				"{\"record\":{\"metadata\":{\"oai_dc:dc\":{\"dc:description\":\"This is a sample description\",\"dc:language\":\"en\",\"dc:coverage\":\"Origin of publication: Italy\",\"dc:relation\":\"http://www.bioline.org.br/ac\",\"dc:creator\":[\"V. Gesheva\",\"R. Rachev\"],\"dc:rights\":\"Copyright 2000 C.E.T.A., The International Centre for Theoretical and Applied Ecology, Gorizia\",\"dc:format\":\"html\",\"dc:type\":\"AA\",\"dc:date\":\"2000-12-31\",\"dc:title\":\"Fatty Acid Composition of Streptomyces hygroscopicus   Populations\",\"dc:identifier\":\"http://www.bioline.org.br/abstract?id=ac00001\",\"dc:publisher\":\"University of Udine, Mycology Department\",\"dc:source\":\"Actinomycetes (ISSN: 0732-0574) Vol 10 Num 1\"}},\"header\":{\"identifier\":\"cria:bioline:ac:ac00001\",\"datestamp\":\"2000-12-31\",\"setSpec\":\"ac\"}}}"));
		
		
	}
	
	
	
	
}


class WorkerThread implements Runnable {
	
	public static String DOMAIN = "/d4science.research-infrastructures.eu/gCubeApps/iSearch";
	
    private String queryTerm;
    private long startAfter;
    
    public WorkerThread(String queryTerm, long startAfter){
        this.queryTerm = queryTerm;
        this.startAfter = startAfter;
    }

    @Override
    public void run() {
    	
    	try {
    		Thread.sleep(startAfter);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    	
    	System.out.println("Executing thread: "+Thread.currentThread().getId());
    	
    	Query query = new Query();
		query.setPosition_paging(0);
		query.setSize_paging(10);
		query.add_SearchTerm("_all", queryTerm);

		try{
			Search_Response sr = getIndexClient(DOMAIN).search(query, DOMAIN);
			System.out.println("Thread "+Thread.currentThread().getId()+" found: "+sr.getTotalHits());
		}catch(NoAvailableIndexServiceInstance e){
			System.out.println("Exception on thread");
		}
		
        
    }
    
	private IndexClient getIndexClient(String scope){
		System.out.println("Calling index client on scope: " + scope);
		return ClientFactory.getMeAnIndexClient(scope);
	}

}
