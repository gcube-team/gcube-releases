package org.gcube.contentmanagement.graphtools.tests.old;


import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.data.BigSamplesTable;
import org.gcube.contentmanagement.graphtools.plotting.graphs.TimeSeriesGraph;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;

import com.rapidminer.RapidMiner;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.preprocessing.sampling.AbsoluteSampling;
import com.rapidminer.tools.OperatorService;
import com.rapidminer.tools.math.MathFunctions;

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
		
		GraphGroups gg = stg.generateGraphs(200, table, xDimension, yDimension, groupDimension, speciesColumn, filter1);
		
		TimeSeriesGraph series = new TimeSeriesGraph("");
		series.renderGraphGroup(gg);
	
	}
	
	
		
}
