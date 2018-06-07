package org.gcube.contentmanagement.graphtools.tests;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.plotting.graphs.GaussianDistributionGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.HistogramGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.LineGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.PieGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.RadarGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.ScatterGraphGeneric;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;


public class RegressionTestAllGraphs {

	
	public static void main(String[] args) throws Exception{
		
		String table = "rdm7d053300d89e11e087918065b36ddd05";
		String xDimension = "field3";
		String yDimension = "field5";
		String groupDimension = "field2";
		String linesColumn = "field4";
		String filter1 = "Perciformes";
		String filter2 = "Osteichthyes";
		StatisticsGenerator stg = new StatisticsGenerator();
		
		
		LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
		
		//database Parameters
		conf.setDatabaseURL("jdbc:postgresql://localhost/testdb");
		conf.setDatabaseUserName("gcube");
		conf.setDatabasePassword("d4science2");
		conf.setDatabaseDriver("org.postgresql.Driver");
		conf.setDatabaseDialect("org.hibernate.dialect.PostgreSQLDialect");
		
		stg.init("./cfg/",conf);
		
//		stg.addColumnFilter("field4", "F","=");
		
		//String generation
		GraphGroups gg = stg.generateGraphs(100, table, xDimension, yDimension, groupDimension, linesColumn, filter1, filter2);
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
		
		ScatterGraphGeneric series6 = new ScatterGraphGeneric("");
		series6.renderGraphGroup(gg);
	}
	
}
