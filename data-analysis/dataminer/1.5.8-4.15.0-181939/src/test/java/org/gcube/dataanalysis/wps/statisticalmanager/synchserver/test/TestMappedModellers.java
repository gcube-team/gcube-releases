package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.test;


public class TestMappedModellers {

	/*
	@Test
	public void testFEED_FORWARD_ANN() throws Exception{
		FEED_FORWARD_ANN algorithm = new FEED_FORWARD_ANN();
		
//		algorithm.setScope("/gcube/devsec");
//		algorithm.setUserName("wps.synch");
		File f1 = new File("./datasets/locallinkshcaf1.txt");
		algorithm.setTrainingDataSet(new GenericFileData(f1, "UTF-8"));
		algorithm.setLayersNeurons(10+"|"+10);
		algorithm.setLearningThreshold(0.01);
		algorithm.setMaxIterations(100);
		algorithm.setModelName("wps_model_ann");
		algorithm.setReference("1");
		algorithm.setTargetColumn("depthmean");
		algorithm.setTrainingColumns("depthmin|depthmax");
		algorithm.run();
	}
	*/
	
	/*
	@Test
	public void testHSPEN() throws Exception{
		HSPEN algorithm = new HSPEN();
	
		algorithm.setScope("/gcube/devsec");
		
		File f1 = new File("./datasets/locallinkshcaf1.txt");
		algorithm.setCsquarecodesTable(new GenericFileData(f1, "UTF-8"));
		File f2 = new File("./datasets/locallinkshspen1.txt");
		algorithm.setEnvelopeTable(new GenericFileData(f2, "UTF-8"));
		File f3 = new File("./datasets/linkocccells.txt");
		algorithm.setOccurrenceCellsTable(new GenericFileData(f3, "UTF-8"));
		algorithm.setOuputEnvelopeTableLabel("wps_hspen_regenerated");
		
		algorithm.run();
	}
	*/
	
	/*
	@Test
	public void testAQUAMAPSNN() throws Exception{
		AQUAMAPSNN algorithm = new AQUAMAPSNN();
	
//		algorithm.setScope("/gcube/devsec");
//		algorithm.setUserName("wps.stat");
		File f1 = new File("./datasets/locallinkshcaf1.txt");
		algorithm.setAbsenceDataTable(new GenericFileData(f1, "UTF-8"));
		algorithm.setLayersNeurons("10|10");
		algorithm.setNeuralNetworkName("wps_aquamaps_nn");
		algorithm.setSpeciesName("Latimeria chalumnae");
		algorithm.setPresenceDataTable(new GenericFileData(f1, "UTF-8"));
		
		algorithm.run();
	}
	*/
}
