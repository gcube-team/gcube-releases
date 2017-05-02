package org.gcube.data.analysis.tabulardata.operation;

import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


public class StatementContainer{

	private static Map<String, Statement> threadStatementMapping = new HashMap<String, Statement>();
	
		
	public static Statement get(String identifier){
		return threadStatementMapping.get(identifier);
	}

	public static void set(Statement statement){
		threadStatementMapping.put(Thread.currentThread().getThreadGroup().getName(), statement);
	}
	
	public static void reset(){
		if (threadStatementMapping.containsKey(Thread.currentThread().getThreadGroup().getName()))
				threadStatementMapping.remove(Thread.currentThread().getThreadGroup().getName());
	}
}
