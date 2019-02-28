package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.test;


public class TestMappedGenerators {

	/*
	@Test
	public void testFFANN() throws Exception{
		FEED_FORWARD_A_N_N_DISTRIBUTION algorithm = new FEED_FORWARD_A_N_N_DISTRIBUTION();
		
//		algorithm.setScope("/gcube/devsec");
		File f1 = new File("./datasets/locallinkshcaf1.txt");
		algorithm.setFinalTableLabel("wps_ffann");
		algorithm.setGroupingFactor("");
		algorithm.setFeaturesColumnNames("depthmin|depthmax");
		algorithm.setFeaturesTable(new GenericFileData(f1, "UTF-8"));
		
//		algorithm.setScope("/gcube/devsec");
		File f2 = new File("./datasets/localfffile.txt");
		algorithm.setModelName(new GenericFileData(f2, "UTF-8"));
		
		algorithm.run();
	}
	*/
	/*
	@Test
	public void testBIONYM() throws Exception{
		BIONYM algorithm = new BIONYM();
		
		algorithm.setAccuracy_vs_Speed("MAX_ACCURACY");
		File f1 = new File("./datasets/taxanameslink.txt");
		algorithm.setRawTaxaNamesTable(new GenericFileData(f1, "UTF-8"));
		algorithm.setRawNamesColumn("species");
//		algorithm.setScope("/gcube/devsec");
//		algorithm.setUserName("wpssynch.statistical");
		algorithm.setTaxa_Authority_File("FISHBASE");
		algorithm.setParser_Name("SIMPLE");
		algorithm.setActivate_Preparsing_Processing(false);
		algorithm.setUse_Stemmed_Genus_and_Species(false);
		algorithm.setMatcher_1("LEVENSHTEIN");
		algorithm.setThreshold_1(0.6);
		algorithm.setMaxResults_1(3);
		algorithm.run();
	}
	*/
}
