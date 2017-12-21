package org.gcube.contentmanagement.graphtools.examples;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.plotting.graphs.RadarGraph;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;

public class ExamplePostGressLocalRadar {

	
	public static void main(String[] args) throws Exception{
		
		AnalysisLogger.setLogger("./cfg/ALog.properties");
		
		String table = "ts_3bdaf790_edbe_11e0_93e3_f6a9821baa29";
		String xDimension = "field2"; // the dates
		String yDimension = "field4"; // values on Y
		String groupDimension = "field0"; // group names
		String speciesColumn = "field3"; // lines labels
		String filter2 = "Perciformes";
		String filter1 = "Boregadus";
		StatisticsGenerator stg = new StatisticsGenerator();
		
		
		LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
		//database Parameters
		conf.setDatabaseUserName("gcube");
		conf.setDatabasePassword("d4science2");
		conf.setDatabaseDriver("org.postgresql.Driver");
		conf.setDatabaseURL("jdbc:postgresql://dbtest.next.research-infrastructures.eu/timeseries");
		conf.setDatabaseDialect("org.hibernate.dialect.PostgreSQLDialect");
		
		stg.init("./cfg/",conf);
		
		GraphGroups gg = stg.generateGraphs(200, table, xDimension, yDimension, groupDimension, speciesColumn, filter1);
		
		RadarGraph radar = new RadarGraph("");
		radar.renderGraphGroup(gg);
	}
	
}
