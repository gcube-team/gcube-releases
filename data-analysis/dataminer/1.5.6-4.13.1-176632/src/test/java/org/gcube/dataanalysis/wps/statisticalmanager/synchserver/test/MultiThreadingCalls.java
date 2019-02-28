package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.test;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

public class MultiThreadingCalls {

	

	
	public static void main(String[] args) throws Exception{
		//final URL urlToCall = new URL("http://localhost:8080/wps/WebProcessingService?request=Execute&service=WPS&version=1.0.0&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.generators.FEED_FORWARD_A_N_N_DISTRIBUTION&DataInputs="+
		//URLEncoder.encode("scope=/gcube/devsec;user.name=test.user;FinalTableLabel=wps_fann;GroupingFactor= ;FeaturesColumnNames=depthmin|depthmax;FeaturesTable=https://dl.dropboxusercontent.com/u/12809149/hcaf_d_mini.csv@text/csv;ModelName=https://dl.dropboxusercontent.com/u/12809149/1430317177514.1_wpssynch.statistical_wps_model_ann","UTF-8"));
		//String host = "localhost";
//		String host = "dataminer1-d-d4s.d4science.org";
		String host = "dataminer1-devnext.d4science.org";
		
//		http://statistical-manager-new.d4science.org:8080/wps/WebProcessingService?request=Execute&service=WPS&version=1.0.0&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIONYM_LOCAL&DataInputs=scope=/gcube/devsec/devVRE;user.name=tester;SpeciesAuthorName=Gadus%20morhua;Taxa_Authority_File=ASFIS;Parser_Name=SIMPLE;Activate_Preparsing_Processing=true;Use_Stemmed_Genus_and_Species=false;Accuracy_vs_Speed=MAX_ACCURACY;Matcher_1=GSAy;Threshold_1=0.6;MaxResults_1=10
			
//		final URL urlToCall = new URL("http://"+host+":8080/wps/WebProcessingService?request=Execute&service=WPS&version=1.0.0&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.modellers.FEED_FORWARD_ANN&DataInputs=" +
//				URLEncoder.encode("scope=/gcube/devsec;user.name=test.user;LayersNeurons=10|10;LearningThreshold=0.01;MaxIterations=100;ModelName=wps_ann;Reference=1;TargetColumn=depthmean;TrainingColumns=depthmin|depthmax;TrainingDataSet=http://goo.gl/juNsCK@MimeType=text/csv","UTF-8"));
			
/*
		final URL urlToCall = new URL("http://"+host+"/wps/WebProcessingService?request=Execute&service=WPS&Version=1.0.0&gcube-token=4ccc2c35-60c9-4c9b-9800-616538d5d48b&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.clusterers.XMEANS&DataInputs=" +
				URLEncoder.encode("OccurrencePointsClusterLabel=OccClustersTest;min_points=1;maxIterations=100;minClusters=1;maxClusters=3;OccurrencePointsTable=http://goo.gl/VDzpch;FeaturesColumnNames=depthmean|sstmnmax|salinitymean;","UTF-8"));
*/
		final URL urlToCall = new URL("http://dataminer1-devnext.d4science.org/wps/WebProcessingService?request=Execute&service=WPS&Version=1.0.0&gcube-token=f9d49d76-cd60-48ed-9f8e-036bcc1fc045-98187548&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.SUBMITQUERY&DataInputs=" +
				URLEncoder.encode("DatabaseName=fishbase;Query=select * from food limit 100;Apply Smart Correction=false;Language=POSTGRES;ResourceName=FishBase;Read-Only Query=true;","UTF-8"));
		
		
		//		final URL urlToCall = new URL("http://"+host+"/wps/WebProcessingService?Request=GetCapabilities&Service=WPS&gcube-token=4ccc2c35-60c9-4c9b-9800-616538d5d48b");
		
		int nthreads = 100;
		
		for (int i = 0; i < nthreads; i++) {
			final int index = i+1;
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						long t0= System.currentTimeMillis();
						System.out.println(new Date(System.currentTimeMillis())+": starting "+index);
						String page = "";//GetCapabilitiesChecker.readPage(urlToCall);
						long t1= System.currentTimeMillis();
						System.out.println(new Date(System.currentTimeMillis())+": finished "+index +" elapsed "+(t1-t0));
//						System.out.println(new Date(System.currentTimeMillis())+":\n"+page);
						if (page.contains("ows:ExceptionText")){
							System.out.println(new Date(System.currentTimeMillis())+":\n"+page);
							System.exit(0);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
	}

}
