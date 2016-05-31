package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.test;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymLocalTransducer;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.capabilities.GetCapabilitiesChecker;
import org.slf4j.LoggerFactory;

public class MultiThreadingCalls {

	public static void main(String[] args) throws Exception{
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(ch.qos.logback.classic.Level.OFF);
		//final URL urlToCall = new URL("http://localhost:8080/wps/WebProcessingService?request=Execute&service=WPS&version=1.0.0&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.generators.FEED_FORWARD_A_N_N_DISTRIBUTION&DataInputs="+
		//URLEncoder.encode("scope=/gcube/devsec;user.name=test.user;FinalTableLabel=wps_fann;GroupingFactor= ;FeaturesColumnNames=depthmin|depthmax;FeaturesTable=https://dl.dropboxusercontent.com/u/12809149/hcaf_d_mini.csv@text/csv;ModelName=https://dl.dropboxusercontent.com/u/12809149/1430317177514.1_wpssynch.statistical_wps_model_ann","UTF-8"));
		//String host = "localhost";
		String host = "statistical-manager-new.d4science.org";
		new org.gcube.dataaccess.algorithms.drmalgorithms.SubmitQuery ();
//		http://statistical-manager-new.d4science.org:8080/wps/WebProcessingService?request=Execute&service=WPS&version=1.0.0&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIONYM_LOCAL&DataInputs=scope=/gcube/devsec/devVRE;user.name=tester;SpeciesAuthorName=Gadus%20morhua;Taxa_Authority_File=ASFIS;Parser_Name=SIMPLE;Activate_Preparsing_Processing=true;Use_Stemmed_Genus_and_Species=false;Accuracy_vs_Speed=MAX_ACCURACY;Matcher_1=GSAy;Threshold_1=0.6;MaxResults_1=10
			
//		final URL urlToCall = new URL("http://"+host+":8080/wps/WebProcessingService?request=Execute&service=WPS&version=1.0.0&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.modellers.FEED_FORWARD_ANN&DataInputs=" +
//				URLEncoder.encode("scope=/gcube/devsec;user.name=test.user;LayersNeurons=10|10;LearningThreshold=0.01;MaxIterations=100;ModelName=wps_ann;Reference=1;TargetColumn=depthmean;TrainingColumns=depthmin|depthmax;TrainingDataSet=http://goo.gl/juNsCK@MimeType=text/csv","UTF-8"));
			
			final URL urlToCall = new URL("http://"+host+":8080/wps/WebProcessingService?request=Execute&service=WPS&version=1.0.0&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIONYM_LOCAL&DataInputs=scope=/gcube/devsec/devVRE;user.name=tester.user;SpeciesAuthorName=Gadus%20morhua;Taxa_Authority_File=ASFIS;Parser_Name=SIMPLE;Activate_Preparsing_Processing=true;Use_Stemmed_Genus_and_Species=false;Accuracy_vs_Speed=MAX_ACCURACY;Matcher_1=GSAy;Threshold_1=0.6;MaxResults_1=10");

		int nthreads = 50;

		for (int i = 0; i < nthreads; i++) {
			final int index = i+1;
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						long t0= System.currentTimeMillis();
						System.out.println(new Date(System.currentTimeMillis())+": starting "+index);
						String page = GetCapabilitiesChecker.readPage(urlToCall);
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
