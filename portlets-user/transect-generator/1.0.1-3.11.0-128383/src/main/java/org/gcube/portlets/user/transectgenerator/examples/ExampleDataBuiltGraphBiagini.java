package org.gcube.portlets.user.transectgenerator.examples;

import org.gcube.contentmanagement.graphtools.plotting.graphs.TransectLineGraph;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.transectgenerator.core.AquamapsProcessor;


public class ExampleDataBuiltGraphBiagini {
	
	public static void main(String[] args) throws Exception{
		
		String cfg = "./cfg/";
	
		String x1="-131.1328125";
//		String x1="-180";
		String y1="10.1953125";
		String x2="-88.2421875";
		String y2="-22.1484375";
//		String y2="-30";
		String SRID="4326";
		int maxelements = 1000;
		int minumumGap = -11;
		
		
//		String biodiversityfield = "probability";
		
		String biodiversityfield = "\"DepthMean\"";
		
		//cannone!
		String tablename = "\"depth\"";
		
//		String tablename = "allspecies2011_01_18_19_10_59_636";
//		String tablename = "fis_1427002010_08_30_12_33_45_094";
		
		
//		String tablename  = "gadidae22010_09_02_18_11_37_100";
		
		AquamapsProcessor ap = new AquamapsProcessor();
		ap.init(cfg,new LexicalEngineConfiguration(),new LexicalEngineConfiguration());
		GraphGroups gg = ap.calculateTransect(x1, y1, x2, y2, SRID, tablename, biodiversityfield, maxelements,minumumGap);
		
		TransectLineGraph series = new TransectLineGraph("");
		series.renderGraphGroup(gg);
	}
	
	
		
}
