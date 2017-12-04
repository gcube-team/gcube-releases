package org.gcube.contentmanagement.graphtools.examples.graphsTypes;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.data.conversions.GraphConverter2D;
import org.gcube.contentmanagement.graphtools.plotting.graphs.TransectLineGraph;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;



public class ExampleDerivative {

	
	public static void main(String[] args) throws Exception{
		
		String table = "ts_a904da30_b4fc_11df_800d_bcef80d51986";
		String xDimension = "field1";
		String yDimension = "field4";
		String groupDimension = "field2";
		String speciesColumn = "field3";
		String filter1 = "Toluene";
//		String filter2 = "Osteichthyes";
		StatisticsGenerator stg = new StatisticsGenerator();
		
		stg.init("./cfg/");
		
		GraphGroups gg = stg.generateGraphs(200, table, xDimension, yDimension, speciesColumn, speciesColumn, filter1);
		
		/*
		for (String key:gg.getGraphs().keySet()){
			GraphData graph = gg.getGraphs().get(key);
			
			//for each series
			int trends = graph.getData().size();
			int yvalues = graph.getData().get(0).getEntries().size();
			System.out.println("yvalues "+yvalues);
			System.out.println("trends "+trends);
			for (int i=0;i<trends;i++){
				double [] points = MathFunctions.points2Double(graph.getData(), i, yvalues);			
				double [] derivative = MathFunctions.derivative(points);
				boolean [] spikes = MathFunctions.findSpikes(derivative);
				for (int k=0;k<yvalues;k++){
					if (spikes[k]){
						String label = graph.getData().get(i).getEntries().get(k).getLabel();
						String newLabel = label+";spike";
						graph.getData().get(i).getEntries().get(k).setLabel(newLabel);
					}
				}
			}
		}
		*/
		GraphConverter2D.anotateStationaryPoints(gg);
		
		TransectLineGraph series = new TransectLineGraph("");
		series.renderGraphGroup(gg);
		
		
//		series.renderGraphGroup(gg);
	
	}
	
	
		
}
