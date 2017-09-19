package org.gcube.contentmanagement.graphtools.examples;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.plotting.graphs.RadarGraph;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;

public class ExampleStaticConfig {

	public static void main(String[] args) throws Exception{
		String table = "ts_161efa00_2c32_11df_b8b3_aa10916debe6";
		String xDimension = "field5";
		String yDimension = "field6";
		String groupDimension = "field1";
		String speciesColumn = "field3";
		String filter1 = "Brown seaweeds";
		String filter2 = "River eels";
		StatisticsGenerator stg = new StatisticsGenerator();
		
		stg.init("./cfg/");

		
		GraphGroups gg = stg.generateGraphs(3, table, xDimension, yDimension, groupDimension, speciesColumn, filter1, filter2);
		
		RadarGraph radar = new RadarGraph("");
		radar.renderGraphGroup(gg);
	}
	
	
}
