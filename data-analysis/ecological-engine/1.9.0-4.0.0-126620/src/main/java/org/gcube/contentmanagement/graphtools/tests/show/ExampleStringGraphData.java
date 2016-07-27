package org.gcube.contentmanagement.graphtools.tests.show;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.plotting.graphs.GaussianDistributionGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.HistogramGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.LineGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.PieGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.RadarGraph;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;


public class ExampleStringGraphData {

	
	public static void main(String[] args) throws Exception{
		String table = "ts_3637f670_430c_11df_a0a2_909e7d074592";
		String xDimension = "field5";
		String yDimension = "field6";
		String groupDimension = "field1";
		String speciesColumn = "field3";
		String filter1 = "Crabs, sea-spiders";
		String filter2 = "Marine fishes not identified";
		StatisticsGenerator stg = new StatisticsGenerator();
		
		
		LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
		//database Parameters
		conf.setDatabaseUserName("root");
//		conf.setDatabasePassword("password");
		conf.setDatabaseDriver("com.mysql.jdbc.Driver");
		conf.setDatabaseURL("jdbc:mysql://localhost/timeseries");
		conf.setDatabaseDialect("org.hibernate.dialect.MySQLDialect");
		conf.setDatabaseAutomaticTestTable("connectiontesttable");
		conf.setDatabaseIdleConnectionTestPeriod("3600");
		
		
		stg.init("./cfg/",conf);
		
		stg.addColumnFilter("field4", "F","=");
		
		//String generation
		GraphGroups gg = stg.generateGraphs(100, table, xDimension, yDimension, groupDimension, speciesColumn, filter1, filter2);
		//graph plot
		RadarGraph series = new RadarGraph("");
		series.renderGraphGroup(gg);

		HistogramGraph series2 = new HistogramGraph("");
		series2.renderGraphGroup(gg);
		
		LineGraph series3 = new LineGraph("");
		series3.renderGraphGroup(gg);
		
		PieGraph series4 = new PieGraph("");
		series4.renderGraphGroup(gg);
		
		GaussianDistributionGraph series5 = new GaussianDistributionGraph("");
		series5.renderGraphGroup(gg);
	}
	
}
