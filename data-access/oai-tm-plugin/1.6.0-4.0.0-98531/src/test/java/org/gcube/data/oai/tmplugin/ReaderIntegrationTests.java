package org.gcube.data.oai.tmplugin;

import static org.junit.Assert.assertNotNull;
import static org.gcube.data.trees.io.Bindings.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.gcube.data.oai.tmplugin.repository.BaseRepository;
import org.gcube.data.oai.tmplugin.repository.Repository;
import org.gcube.data.oai.tmplugin.repository.Summary;
import org.gcube.data.oai.tmplugin.requests.Request;
import org.gcube.data.oai.tmplugin.requests.WrapRepositoryRequest;
import org.gcube.data.oai.tmplugin.requests.WrapSetsRequest;
import org.gcube.data.oai.tmplugin.utils.Utils;
import org.gcube.data.streams.Stream;
import org.gcube.data.trees.data.Tree;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fabio Simeoni
 *
 */
public class ReaderIntegrationTests {

	private static final Logger log = LoggerFactory.getLogger("test");

	public static final String DCMI_URL = "http://drs.nio.org/oai/request";
	//	public static final String DCMI_URL1 = "http://dspace.mit.edu/oai/request";
	public static final String TN_SET = "ijoat:TN";
	public static final String EA_SET = "ijoat:EA";
	public static final String OTHR_SET = "ijoat:OTHR";
	public static final String SAMPLEID_IN_TN_SET = "oai:ojs.ijict.org:article/217";
	public static final String SAMPLEID_IN_EA_SET = "oai:ojs.ijict.org:article/156";
	public static final String SAMPLEID_IN_OTHR_SET = "oai:ojs.ijict.org:article/377";

	public  static final String AQUACOMM_URL = "http://aquacomm.fcla.edu/cgi/oai2";

	public static final String AQUACOMM_SET = "7375626A656374733D48";
	public static final String ID_IN_AQUACOMM_SET = "oai:generic.eprints.org:2198";

	//	@Test
	//	public void trees() throws Exception {
	//		System.out.println("WrapRepositoryRequest " + Utils.toSchema(WrapRepositoryRequest.class));		
	//		System.out.println("/n");
	//		System.out.println("WrapSetsRequest " + Utils.toSchema(WrapSetsRequest.class));
	//	}



//		@Test
//		public void test() throws Exception{
//			Stream<Record> records = null;
//	
//			final Harvester connection = new Harvester("http://ws.pangaea.de/oai/");
//	
//				records = new RepositoryIterator() {
//					@Override 
//					protected RecordIterator fetchRecords() throws FileNotFoundException, Exception{
//						log.info("RepositoryIterator, no set ");
//						log.info("fetchRecords sets.isEmpty");
//						return connection.listRecords("oai_dc");
//					}
//	
//	
//					@Override
//					public boolean isClosed() {
//						// TODO Auto-generated method stub
//						return false;
//					}			
//				};
//	
//				int i = 0;
//				
//				while(records.hasNext()) {
//					Record rec = records.next();
//					System.out.println( i++ + " - " + rec.toString());
//				}
//				System.out.println("cardinality " + i);
//		
//				
//		}


	@Test
	public void trees() throws Exception {

		//			WrapRepositoryRequest request = new WrapRepositoryRequest("oai:generic.eprints.org:16","http://aquacomm.fcla.edu/cgi/oai2");
		//			request.setMetadataFormat("oai_dc");
		//			request.setName("aquacomm");
		//			request.setMetadataFormat("oai_dc");
		//			request.setDescription("oai test collection");
		//			request.setContentXPath("//*[local-name()='identifier' and contains(.,'://')]");
		//			request.setTitleXPath("//*[local-name()='title']");
		//			request.addAlternativesXPath("//*[local-name()='relation' and contains(.,'://')]");	
		System.out.println("trees()");
		WrapRepositoryRequest request = new WrapRepositoryRequest();

//		request.setId("openaire-geocode1");
//		request.setRepositoryUrl("http://www.biodiversitylibrary.org/oai");
		request.setRepositoryUrl("http://www.nature.com/oai/request");
//		request.addSets("geocode1");
		request.setMetadataFormat("oai_dc");
		request.setName("nature");
		request.setDescription("A collection resulting from nature");
		request.setContentXPath("//*[local-name()='identifier' and contains(.,'://')]");
		request.setTitleXPath("//*[local-name()='title']");
		request.addAlternativesXPath("//*[local-name()='relation' and contains(.,'://')]");	
		
		
//		WrapSetsRequest request = new WrapSetsRequest("http://ijict.org/index.php/ijoat/oai");
//		request.setMetadataFormat("oai_dc");
//		request.addSets("ijoat:TN");
//		request.addSets("ijoat:EA");
//		request.setName("ijictxx");
//		request.setDescription("ijict");
//		request.setContentXPath("//*[local-name()='identifier' and contains(.,'://')]");
//		request.setTitleXPath("//*[local-name()='title']");
//		request.addAlternativesXPath("//*[local-name()='relation' and contains(.,'://')]");	
		Repository repository = new BaseRepository(request);


		//			WrapRepositoryRequest request = new WrapRepositoryRequest();
		//			request.setId("arxivxx");
		//			request.setRepositoryUrl("http://export.arxiv.org/oai2");
		//			request.setMetadataFormat("oai_dc");
		//			request.setName("arxiv");
		//			request.setDescription("A collection resulting from Bioline International (http://www.bioline.org.br/) Bioline International is a not-for-profit scholarly publishing cooperative committed to providing open access to quality research journals published in developing countries. BI's goal of reducing the South to North knowledge gap is crucial to a global understanding of health (tropical medicine, infectious diseases, epidemiology, emerging new diseases), biodiversity, the environment, conservation and international development. By providing a platform for the distribution of peer-reviewed journals (currently from Bangladesh, Brazil, Chile, China, Colombia, Egypt, Ghana, India, Iran, Kenya, Malaysia, Nigeria, Tanzania, Turkey, Uganda and Venezuela), BI helps to reduce the global knowledge divide by making bioscience information generated in these countries available to the international research community world-wide.");
		//			request.setContentXPath("//*[local-name()='identifier' and contains(.,'://')]");
		//			request.setTitleXPath("//*[local-name()='title']");
		//			request.addAlternativesXPath("//*[local-name()='relation' and contains(.,'://')]");	
		//			Repository repository = new BaseRepository(request);

		//		List<Set> sets = repository.getSetsWith(Arrays.asList("ijoat:TN"));
		//		
		//			Tree tree = repository.get("oai:generic.eprints.org:2705", new ArrayList<Set>());


		//			*********************
		List<org.gcube.data.oai.tmplugin.repository.Set> sets = new ArrayList<org.gcube.data.oai.tmplugin.repository.Set>();

//					sets.add(new OAISet("ijoat:EA","ijoat:EA","ijoat:EA"));
//					sets.add(new OAISet("ijoat:TN","ijoat:TN","ijoat:TN"));

		Stream<Tree> trees = repository.getAllIn(sets);
		int i = 0;
		while(trees.hasNext()) {
			i++;
			Tree tree = trees.next();
//			System.out.println(i + " - " + toText(tree));
//							System.out.println("id: " + tree.id());
//							System.out.println("idDecoder: " + Utils.idDecoder(tree.id()));
//							System.out.println("single get: "+repository.get(Utils.idDecoder(tree.id()), new ArrayList<OAISet>()));

			//				log.info(trees.next().toString());
						
		}
		System.out.println("cardinality " + i);

		//			*********************
		//			System.out.println("***************************************************************");
		//			 Summary summary = repository.summary(new ArrayList());
		//			 System.out.println("summary.cardinality():  " + summary.cardinality() + " -  summary.lastUpdate(): " + summary.lastUpdate());
		//			 System.out.println("***************************************************************");

	}
	
	//	
	//	
	//	@Test
	//	public void treesInSet() throws Exception {
	//
	//		Request request = new WrapSetsRequest("http://aquaticcommons.org/cgi/oai2");
	//		request.setMetadataFormat("oai_dc");
	//		request.addSets("7374617475733D756E707562");
	//		request.setName("aquacomm");
	//		request.setMetadataFormat("oai_dc");
	//		request.setDescription("oai test collection");
	//		request.setContentXPath("//*[local-name()='identifier' and contains(.,'://')]");
	//		request.setTitleXPath("//*[local-name()='title']");
	//		request.addAlternativesXPath("//*[local-name()='relation' and contains(.,'://')]");	
	//		BaseRepository repository = new BaseRepository(request);
	//
	//		List<Set> sets = repository.getSetsWith(Arrays.asList("7374617475733D756E707562"));
	//		Stream<Tree> trees = repository.getAllIn(sets);
	//		//		Stream<Tree> trees = repository.getAllIn(new ArrayList<Set>());
	//		//		
	//		////		assertNotNull(trees);
	//
	//				int i=0;
	//		while(trees.hasNext()  ) {
	//			Tree tree = trees.next();
	////			System.out.println(tree.id());
	//						System.out.println(toText(tree));
	////						log.info(trees.next().toString());
	//						i++;
	//		}
	//
	//	}


	@Test
	public void getTreeById() throws Exception {
		System.out.println("getTreeById");
		Request request = new WrapSetsRequest("http://aquaticcommons.org/cgi/oai2");
		request.setMetadataFormat("oai_dc");
		request.addSets("7374617475733D756E707562");
		request.setName("aquacomm");
		request.setMetadataFormat("oai_dc");
		request.setDescription("oai test collection");
		request.setContentXPath("//*[local-name()='identifier' and contains(.,'://')]");
		request.setTitleXPath("//*[local-name()='title']");
		request.addAlternativesXPath("//*[local-name()='relation' and contains(.,'://')]");	
		BaseRepository repository = new BaseRepository(request);

		Tree tree_test = repository.get("af133001-c284-4be3-a7f0-f603ca83bc6d", new ArrayList<org.gcube.data.oai.tmplugin.repository.Set>());
		System.out.println(toText(tree_test));


		List<org.gcube.data.oai.tmplugin.repository.Set> sets = repository.getSetsWith(Arrays.asList("7374617475733D756E707562"));
		Stream<Tree> trees = repository.getAllIn(sets);


		//		Tree tree = repository.get("oai:generic.eprints.org:359", sets);
		//		
		//		System.out.println(toText(tree));
		//				Stream<Tree> trees = repository.getAllIn(new ArrayList<Set>());

		while(trees.hasNext()  ) {
			Tree tree = trees.next();
			System.out.println("single get: "+repository.get(tree.id(), new ArrayList<org.gcube.data.oai.tmplugin.repository.Set>()));
			System.out.println(toText(tree));
			System.out.println("************");
		}


	}

	//	@Test
	//	public void testAquacomm() throws Exception {
	//
	//		Request  req = new WrapRepositoryRequest("","http://aquacomm.fcla.edu/cgi/oai2" );
	//		req.setMetadataFormat("oai_dc");
	//		req.setName("aquacomm");
	//		req.setDescription("oai test collection");
	//		req.setContentXPath("//*[local-name()='identifier' and contains(.,'://')]");
	//		req.setTitleXPath("//*[local-name()='title']");
	//		req.addAlternativesXPath("//*[local-name()='relation' and contains(.,'://')]");	
	//
	//		log.info("req.getMetadataFormat() " + req.getMetadataFormat());
	//
	//		BaseRepository repository = new BaseRepository(req);	
	//		List<Set> sets = repository.getSetsWith(Arrays.asList(AQUACOMM_SET));
	//		Tree tree = repository.get(ID_IN_AQUACOMM_SET, sets);
	//
	//		System.out.println(toText(tree));
	//
	//	}

	//	@Test
	//	public void treeInRepo() throws Exception {
	//		
	//		Request  req = new WrapSetsRequest(DCMI_URL);
	//		req.setMetadataFormat("oai_dc");
	//		
	//		log.info("req.getMetadataFormat() " + req.getMetadataFormat());
	//	
	//		BaseRepository repository = new BaseRepository(req);
	//		
	//		List<Set> allsets = repository.getSetsWith(new ArrayList<String>());
	//		
	//		try {
	//			repository.get("badId",allsets);
	//			fail();
	//		}
	//		catch(UnknownTreeException e) {}
	//		
	//		Tree tree = repository.get(SAMPLEID_IN_OTHR_SET,allsets);
	//		System.out.println(toText(tree));
	//		assertNotNull(tree);
	//		log.info(tree.toString());
	//	}
	//	
	//	@Test
	//	public void treesInRepo() throws Exception {
	//		log.info("treesInRepo()");
	//		Request  req = new WrapSetsRequest(DCMI_URL);
	//		req.setMetadataFormat("oai_dc");
	//		Repository repository = new BaseRepository(req);
	//		
	//		List<Set> allsets = repository.getSetsWith(new ArrayList<String>());
	//		
	//		Stream<Tree> trees = repository.getAllIn(allsets);
	//		
	//		assertNotNull(trees);
	//		
	//		int i=0;
	//		while(trees.hasNext() && i<20) {
	//			log.info(trees.next().toString());
	//			i++;
	//		}
	//	}
	//	
	//	@Test
	//	public void treesInaSet() throws Exception {
	//		log.info("treesInSet()");
	//		Request  req = new WrapSetsRequest(DCMI_URL);
	//		req.setMetadataFormat("oai_dc");
	//		Repository repository = new BaseRepository(req);
	//		
	//		List<Set> sets = repository.getSetsWith(Arrays.asList(OTHR_SET,EA_SET));
	//		
	//		Stream<Tree> trees = repository.getAllIn(sets);
	//		
	//		assertNotNull(trees);
	//		
	//		int i=0;
	//		while(trees.hasNext() && i<20) {
	//			log.info(trees.next().toString());
	//			i++;
	//		}
	//	}
	//	

//	@Test
//	public void repoSummary() throws Exception {
//
//		Request  req = new WrapSetsRequest(DCMI_URL);
//		req.setMetadataFormat("oai_dc");
//		Repository repository = new BaseRepository(req);
//
//		List<org.gcube.data.oai.tmplugin.repository.Set> allsets = repository.getSetsWith(new ArrayList<String>());
//
//		Summary summary = repository.summary(allsets);
//
//		assertNotNull(summary);
//
//		log.info("summary.toString() " + summary.toString());
//
//		assertNotNull(summary.cardinality());
//		assertNotNull(summary.lastUpdate());
//
//
//	}
//
//	@Test
//	public void someSetsSummary() throws Exception {
//
//		Request  req = new WrapSetsRequest(DCMI_URL);
//		req.setMetadataFormat("oai_dc");
//		Repository repository = new BaseRepository(req);
//
//		List<org.gcube.data.oai.tmplugin.repository.Set> set = repository.getSetsWith(Collections.singletonList(TN_SET));
//
//		Summary summary = repository.summary(set);
//
//		assertNotNull(summary);
//
//		log.info("summary.toString() " + summary.toString());
//
//		assertNotNull(summary.cardinality());
//		assertNotNull(summary.lastUpdate());
//
//
//	}
}

