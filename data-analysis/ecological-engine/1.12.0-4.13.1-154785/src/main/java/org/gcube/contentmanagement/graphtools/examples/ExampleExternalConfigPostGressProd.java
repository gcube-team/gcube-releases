package org.gcube.contentmanagement.graphtools.examples;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class ExampleExternalConfigPostGressProd {

	
	public static void main(String[] args) throws Exception{
		String table = "ts_c4bdfaa0_6c16_11e0_bb1f_fb760af5afc7";
		String xDimension = "field4"; // the dates
		String yDimension = "field6"; // values on Y
		String groupDimension = "field1"; // group names
		String speciesColumn = "field2"; // lines labels
		
		String filter1 = "ABW";
//		String filter2 = "Osteichthyes";
		StatisticsGenerator stg = new StatisticsGenerator();
		
		AnalysisLogger.setLogger("./cfg/ALog.properties");
		LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
		//database Parameters
		conf.setDatabaseUserName("gcube1");
		conf.setDatabasePassword("d4science");
		conf.setDatabaseDriver("org.postgresql.Driver");
		conf.setDatabaseURL("jdbc:postgresql://node28.p.d4science.research-infrastructures.eu/timeseries");
		
//		conf.setDatabaseDialect("org.hibernate.dialect.MySQLDialect");
		conf.setDatabaseDialect("org.hibernate.dialect.PostgreSQLDialect");
		conf.setDatabaseAutomaticTestTable("connectiontesttable");
		conf.setDatabaseIdleConnectionTestPeriod("3600");
		
		stg.init("./cfg/",conf);
		
//		stg.generateGraphs(200, table, xDimension, yDimension, groupDimension, speciesColumn, filter1, filter2);
		stg.generateGraphs(200, table, xDimension, yDimension, groupDimension, speciesColumn,filter1);
		
		System.out.println();
	}
	
}
