package org.gcube.portlets.user.transectgenerator.examples;

import org.gcube.contentmanagement.graphtools.plotting.graphs.TransectLineGraph;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.transectgenerator.core.AquamapsProcessor;


public class ExampleDataBuiltGraph2 {
	
	public static void main(String[] args) throws Exception{
		
		String cfg = "./cfg/";
	
//		String x1="-23";
		String x1="-180";
		String y1="90";
		String x2="+180";
//		String y2="-20";
		String y2="-90";
		String SRID="4326";
		int maxelements = 1000;
		int minumumGap = -11;
		
//		String biodiversityfield = "probability";
		
		String biodiversityfield = "maxspeciescountinacell";
		
		//cannone!
//		String tablename = "biofede2010_09_02_18_11_37_986";
		String tablename = "default2010_06_03_19_03_24_582";
//		String tablename = "fis_1427002010_08_30_12_33_45_094";
		
		
		
		
		AquamapsProcessor ap = new AquamapsProcessor();
		ap.init(cfg,new LexicalEngineConfiguration(),new LexicalEngineConfiguration());
		GraphGroups gg = ap.calculateTransect(x1, y1, x2, y2, SRID, tablename, biodiversityfield, maxelements,minumumGap);
		
		TransectLineGraph series = new TransectLineGraph("");
		series.renderGraphGroup(gg);
	}	
}
