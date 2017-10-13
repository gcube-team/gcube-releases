package org.gcube.contentmanagement.graphtools.examples;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.plotting.graphs.HistogramGraph;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;

public class ExampleFilters2 {

	
	public static void main(String[] args) throws Exception{
		String table = "ts_161efa00_2c32_11df_b8b3_aa10916debe6";
		String xDimension = "field5";
		String yDimension = "field6";
		String groupDimension = "field1";
		String speciesColumn = "field3";
		String filter1 = "Brown seaweeds";
		String filter2 = "River eels";
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
		
//		stg.addColumnFilter("field4", "F");
		
//		stg.addColumnFilter("field5", "2005","<");
		
		//stg.addXRangeFilter("2004", "2005");
		
		stg.addYRangeFilter("5600000", "7000000");
		
		/*
		stg.addColumnFilter("field1", "Africa - Inland waters");
		stg.addColumnFilter("field1", "Atlantic, Northeast");
		*/
		GraphGroups gg = stg.generateGraphs(3, table, xDimension, yDimension, groupDimension, speciesColumn, filter1);
		
		HistogramGraph series = new HistogramGraph("");
		series.renderGraphGroup(gg);
	}
	
}
