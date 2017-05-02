package org.gcube.contentmanagement.graphtools.examples;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;

public class ExampleExternalConfigPostGress {

	
	public static void main(String[] args) throws Exception{
		String table = "ts_7ab1d700_18d9_11e0_b703_c9d7e969ced7";
		String xDimension = "field3"; // the dates
		String yDimension = "field5"; // values on Y
		String groupDimension = "field2"; // group names
		String speciesColumn = "field4"; // lines labels
		String filter1 = "Perciformes";
		String filter2 = "Osteichthyes";
		StatisticsGenerator stg = new StatisticsGenerator();
		
		
		LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
		//database Parameters
		conf.setDatabaseUserName("lucio");
		conf.setDatabasePassword("d4science");
		conf.setDatabaseDriver("org.postgresql.Driver");
		conf.setDatabaseURL("jdbc:postgresql://dlib29.isti.cnr.it/timeseries");
		conf.setDatabaseDialect("org.hibernate.dialect.PostgreSQLDialect");
		conf.setDatabaseAutomaticTestTable("connectiontesttable");
		conf.setDatabaseIdleConnectionTestPeriod("3600");
		
		stg.init("./cfg/",conf);
		
		stg.generateGraphs(200, table, xDimension, yDimension, groupDimension, speciesColumn, filter1, filter2);
	}
	
}
