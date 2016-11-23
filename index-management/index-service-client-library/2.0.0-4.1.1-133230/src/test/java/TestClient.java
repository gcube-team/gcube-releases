import java.util.ArrayList;
import java.util.List;

import org.gcube.rest.index.client.ClientFactory;
import org.gcube.rest.index.client.cache.IndexClient;
import org.gcube.rest.index.client.exceptions.NoAvailableIndexServiceInstance;

public class TestClient {

	public static void main (String [] args) throws NoAvailableIndexServiceInstance, InterruptedException {
			
		IndexClient client = ClientFactory.getMeAnIndexClient("/gcube/devNext");
		
//		IndexClient client1 = ClientFactory.getMeAnIndexClient("/gcube/devsec");
//		System.out.println(client1.getCollections());
		
		
//		for (String s: client.getCollections()) // <-- pairnw ta resources
//			System.out.println(s);
		
//		boolean status = client.insertJson("plato-academy", "1", "{\"id\": \"0001\",\"type\": \"donut\",\"name\": \"Cake\",\"ppu\": 0.55,\"batters\":{\"batter\":[{ \"id\": \"1001\", \"type\": \"Regular\" },{ \"id\": \"1002\", \"type\": \"Chocolate\" },{ \"id\": \"1003\", \"type\": \"Blueberry\" },{ \"id\": \"1004\", \"type\": \"Devil's Food\" }]},\"topping\":[{ \"id\": \"5001\", \"type\": \"None\" },{ \"id\": \"5002\", \"type\": \"Glazed\" },{ \"id\": \"5005\", \"type\": \"Sugar\" },{ \"id\": \"5007\", \"type\": \"Powdered Sugar\" },{ \"id\": \"5006\", \"type\": \"Chocolate with Sprinkles\" },{ \"id\": \"5003\", \"type\": \"Chocolate\" },{ \"id\": \"5004\", \"type\": \"Maple\" }]}");
//		boolean status = 
//				client.insertJson("arxiv", "2", "{\"datestamp\":\"2008-12-13\",\"dc:creator\":[\"Streinu,Ileana\",\"Theran,Louis\"],\"dc:date\":[\"2007-03-30\",\"2008-12-13\"],\"dc:description\":[\"Wedescribeanewalgorithm,thepebblegamewithcolors,anduseitobtainacharacterizationofthefamilyofsparsegraphsandalgorithmicsolutionstoafamilyofproblemsconcerningtreedecompositionsofgraphs.Specialinstancesofsparsegraphsappearinrigiditytheoryandhavereceivedincreasedattentioninrecentyears.Inparticular,ourcoloredpebblesgeneralizeandstrengthenthepreviousresultsofLeeandStreinuandgiveanewproofoftheTutte-Nash-Williamscharacterizationofarboricity.Wealsopresentanewdecompositionthatcertifiessparsitybasedonthepebblegamewithcolors.OurworkalsoexposesconnectionsbetweenpebblegamealgorithmsandprevioussparsegraphalgorithmsbyGabow,GabowandWestermannandHendrickson.\",\"Comment:ToappearinGraphsandCombinatorics\"],\"dc:identifier\":\"http://arxiv.org/abs/0704.0002\",\"dc:subject\":[\"Mathematics-Combinatorics\",\"ComputerScience-ComputationalGeometry\",\"05C85\",\"05C70\",\"68R10\",\"05B35\"],\"dc:title\":\"Sparsity-certifyingGraphDecompositions\",\"dc:type\":\"text\",\"identifier\":\"oai:arXiv.org:0704.0002\",\"setSpec\":[\"cs\",\"math\"]}");
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
		
		System.out.println(client.getCollections());
//		System.out.println(client.getCollections());
//		System.out.println(client.getCollections());
//		System.out.println(client.getCollections());
//		System.out.println(client.getCollections());
//		System.out.println(client.getAllCollectionFields());
//		System.out.println(client.getAllCollectionFields());
//		System.out.println(client.getAllCollectionFields());
//		System.out.println(client.getAllCollectionFields());
		
		System.out.println(client.getAllCollectionsInfo());
		
//		client.getCollectionInfo("");
		
//		Map<String,String> fieldAlias = new HashMap<String, String>();
//		fieldAlias.put("dc: contributor", "Contributor");
//		fieldAlias.put("dc: coverage", "Coverage");
//		fieldAlias.put("dc: creator", "Creator");
//		fieldAlias.put("dc: date", "Date");
//		fieldAlias.put("dc: description", "Description");
//		fieldAlias.put("dc: format", "Format");
//		fieldAlias.put("dc: identifier", "Identifier");
//		fieldAlias.put("dc: language", "Language");
//		fieldAlias.put("dc: publisher", "Publisher");
//		fieldAlias.put("dc: relation", "Relation");
//		fieldAlias.put("dc: rights", "Rights");
//		fieldAlias.put("dc: source", "Source");
//		fieldAlias.put("dc: subject", "Subject");Search_Response
//		fieldAlias.put("dc: title", "Title");
//		fieldAlias.put("dc: type", "Type");
//		
//		client.setCollectionFieldsAlias("argolida", fieldAlias);
//		client.setCollectionFieldsAlias("ctaegypt", fieldAlias);
		
//		System.out.println(client.getCollectionFieldsAlias("", true));
		
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
		
		
		
//		System.out.println(client.getJSONTransformer(""));
		
		
		/*
		Query query = new Query();
		
		query.setPosition_paging(0);
		query.setSize_paging(2);
//		query.add_SearchTerm("ctaegypt", "dc:rights", "*2015*");
//		query.add_SearchTerm("ctaegypt", "dc:creator", "*Mohamed*");
		query.add_SearchTerm("ctaegypt", "*mohamed*");
//		query.add_SearchTerm("argolida", "dc:title", "*Περίαπτο*");
//		query.add_SearchTerm("argolida", "dc:date", "*(*)*");
		
		query.addFacetField("dc:creator", 10);
		query.addFacetField("dc:language", 3);
		
		System.out.println(query.get_SearchTerms());
		
		System.out.println(client.search(query));
		
		//---------------------------------------------------------------------
		System.out.println("---------------------------------------------------------------");
		
		query.setPosition_paging(2);
		query.setSize_paging(2);
//		query.add_SearchTerm("ctaegypt", "dc:rights", "*2015*");
//		query.add_SearchTerm("ctaegypt", "dc:creator", "*Mohamed*");
		query.add_SearchTerm("ctaegypt", "*mohamed*");
//		query.add_SearchTerm("argolida", "dc:title", "*Περίαπτο*");
//		query.add_SearchTerm("argolida", "dc:date", "*(*)*");
		
		query.addFacetField("dc:creator", 10);
		query.addFacetField("dc:language", 3);
		
		
		System.out.println(query.get_SearchTerms());
		
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
		
//		Map<String,Map<String,String>> cfa = client.getAllCollectionFieldsAlias();
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
		
		
		client.dropCollection("11113950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3");
		client.dropCollection("48338c84a223b1a67404f2982b65f0d7141e9ae81d770e5b7b42871724a65cd8");
		client.dropCollection("08e0f8059a4289fd2052768da7ae3ef805aee6f398116260becbe250ba22b7e9");
		
		
//		System.out.println(client.deleteJSONTransformer("10003950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3"));
//		System.out.println(client.deleteJSONTransformer("00003950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3"));
//		System.out.println(client.deleteJSONTransformer("5dd67780441f57408e4205f46c5f332b998e20202134843ed07c9ca90b60aacc"));
		
//		System.out.println(client.setJSONTransformer("10003950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3", 
//				"[{\"operation\":\"shift\",\"spec\":{\"*\":{\"*\":{\"oai_dc:dc\": \"\"}}}}]"));
		
//		System.out.println(client.createIndex("00003950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3"));
//		System.out.println(client.insertJson("10003950211fb5e72513e93e0c026b06ac1c270f757bc22c3c5b290b691d95e3", 
//				"{\"record\":{\"metadata\":{\"oai_dc:dc\":{\"dc:description\":\"This is a sample description\",\"dc:language\":\"en\",\"dc:coverage\":\"Origin of publication: Italy\",\"dc:relation\":\"http://www.bioline.org.br/ac\",\"dc:creator\":[\"V. Gesheva\",\"R. Rachev\"],\"dc:rights\":\"Copyright 2000 C.E.T.A., The International Centre for Theoretical and Applied Ecology, Gorizia\",\"dc:format\":\"html\",\"dc:type\":\"AA\",\"dc:date\":\"2000-12-31\",\"dc:title\":\"Fatty Acid Composition of Streptomyces hygroscopicus   Populations\",\"dc:identifier\":\"http://www.bioline.org.br/abstract?id=ac00001\",\"dc:publisher\":\"University of Udine, Mycology Department\",\"dc:source\":\"Actinomycetes (ISSN: 0732-0574) Vol 10 Num 1\"}},\"header\":{\"identifier\":\"cria:bioline:ac:ac00001\",\"datestamp\":\"2000-12-31\",\"setSpec\":\"ac\"}}}"));
		
		ClientFactory.closeAllClients();
	}
	
}
