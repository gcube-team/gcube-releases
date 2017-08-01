package org.gcube.contentmanagement.graphtools.tests;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class testConnections {

	
	public static void main(String args[]) throws Exception{
		
		LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
		
		conf.setDatabaseURL("jdbc:postgresql://localhost/testdb");
		conf.setDatabaseUserName("gcube");
		conf.setDatabasePassword("d4science2");
		conf.setDatabaseDriver("org.postgresql.Driver");
		conf.setDatabaseDialect("org.hibernate.dialect.PostgreSQLDialect");
		String hibernateDefaultFile = "hibernate.cfg.xml";
		String loggerDefaultFile = "ALog.properties";
		
		String configPath = "./cfg/";
		
		AnalysisLogger.setLogger(configPath+loggerDefaultFile);
		
		SessionFactory  session = DatabaseFactory.initDBConnection(configPath+hibernateDefaultFile,conf);
		List<Object> resultSet = DatabaseFactory.executeSQLQuery("select * from hcaf_s limit 10", session);
		
		for (Object result:resultSet){
			Object [] row = (Object[]) result;
			for (int i=0;i<row.length;i++){
				System.out.print(""+row[i]+" ");
			}
			System.out.println();
		}
		
//		DatabaseFactory.executeSQLUpdate("set ... ", session);
		
		session.close();
	}
}
