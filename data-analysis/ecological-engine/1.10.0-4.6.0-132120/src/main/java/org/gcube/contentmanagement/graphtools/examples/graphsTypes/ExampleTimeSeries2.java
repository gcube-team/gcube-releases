package org.gcube.contentmanagement.graphtools.examples.graphsTypes;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.plotting.graphs.TimeSeriesGraph;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;

public class ExampleTimeSeries2 {

	
	public static void main(String[] args) throws Exception{
		String table = "ts_a904da30_b4fc_11df_800d_bcef80d51986";
		String xDimension = "field1";
		String yDimension = "field4";
		String groupDimension = "field2";
		String speciesColumn = "field3";
		String filter1 = "Toluene";
		String filter2 = "River eels";
//		String filter2 = "Osteichthyes";
		StatisticsGenerator stg = new StatisticsGenerator();
		
		
		
		
		stg.init("./cfg/");
		
		GraphGroups gg = stg.generateGraphs(200, table, xDimension, yDimension, groupDimension, speciesColumn, filter1);
		
		TimeSeriesGraph series = new TimeSeriesGraph("");
		series.renderGraphGroup(gg);
	}
	
}
