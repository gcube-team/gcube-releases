package org.gcube.portlets.user.transectgenerator.examples;

import org.gcube.contentmanagement.graphtools.plotting.graphs.TransectLineGraph;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.transectgenerator.core.AquamapsProcessor;


public class ExampleDataBuiltProdProbability {
	
	public static void main(String[] args) throws Exception{
		
		String cfg = "./cfg/";
	
//		String x1="-23";
		String x1="-162";
		String y1="20";
		String x2="+15";
//		String y2="-20";
		String y2="-10";
		String SRID="4326";
		int maxelements = 1500;
		
		String biodiversityfield = "probability";
		String tablename = "fis_1128222010_06_25_18_14_27_510";

//		String biodiversityfield = "maxspeciescountinacell";
//		String tablename = "allspecies2011_01_18_19_10_59_636";
		
		
		
		
		
		AquamapsProcessor ap = new AquamapsProcessor();
		ap.init(cfg,new LexicalEngineConfiguration(),new LexicalEngineConfiguration());
		
		long t0 = System.currentTimeMillis();
		GraphGroups gg = ap.calculateTransect(x1, y1, x2, y2, SRID, tablename, biodiversityfield, maxelements);
		
		TransectLineGraph series = new TransectLineGraph("");
		series.renderGraphGroup(gg);
		long t1 = System.currentTimeMillis();
		AnalysisLogger.getLogger().debug("ELAPSED TIME : "+(t1-t0));
	}
	
	
		
}
