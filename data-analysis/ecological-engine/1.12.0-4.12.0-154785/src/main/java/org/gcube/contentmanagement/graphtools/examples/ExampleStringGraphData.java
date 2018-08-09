package org.gcube.contentmanagement.graphtools.examples;

import org.gcube.contentmanagement.graphtools.core.StatisticsGenerator;
import org.gcube.contentmanagement.graphtools.plotting.graphs.HistogramGraph;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ExampleStringGraphData {

	
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
		
		
		//String generation
		String ggs = stg.generateStringGraphs(100, table, xDimension, yDimension, groupDimension, speciesColumn, filter1, filter2);
		System.out.println(ggs);
		//String rebuilding
		XStream xStream = new XStream(new DomDriver());
		GraphGroups gg = (GraphGroups) xStream.fromXML(ggs);
		
		//graph plot
		HistogramGraph series = new HistogramGraph("");
		series.renderGraphGroup(gg);
	}
	
}
