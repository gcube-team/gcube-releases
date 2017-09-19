package org.gcube.contentmanagement.graphtools.examples;

import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.plotting.graphs.GaussianDistributionGraph;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.jfree.data.function.NormalDistributionFunction2D;

public class ExampleMeanVariance {

	
	public static void main(String[] args) throws Exception{
		String table = "ts_161efa00_2c32_11df_b8b3_aa10916debe6";
		String xDimension = "field5";
		String yDimension = "field6";
		String groupDimension = "field1";
		String speciesColumn = "field3";
		String filter1 = "Brown seaweeds";
		String filter2 = "River eels";
//		String filter2 = "Osteichthyes";
		StatisticsGenerator stg = new StatisticsGenerator();
		
		stg.init("./cfg/");
		
		GraphGroups gg = stg.generateGraphs(200, table, xDimension, yDimension, groupDimension, speciesColumn, filter1, filter2);
		
		Map<String,List<NormalDistributionFunction2D>> normalsmap = GaussianDistributionGraph.graphs2Normals(gg);
		
		//show normals
		for (String key:normalsmap.keySet()){
			List<NormalDistributionFunction2D> normals = normalsmap.get(key);
			System.out.println("Means and Variances for distribution named: "+key);
			for (NormalDistributionFunction2D gaussian:normals){
				System.out.print("("+gaussian.getMean()+" ; "+gaussian.getStandardDeviation()+") ");
			}
			System.out.println();
		}
		
//		AnalysisLogger.getLogger().debug("Generated! "+normalsmap);
		
	}
	
}
