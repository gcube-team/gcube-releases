package org.gcube.data.analysis.tabulardata.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.data.analysis.tabulardata.model.table.TableId;

public class TableContainer{

	private static Map<String, Set<TableId>> threadTableMapping = new HashMap<String, Set<TableId>>();
	
		
	public static Set<TableId> get(String identifier){
		return threadTableMapping.get(identifier);
	}

	public static void add(TableId tableId){
		String threadGroupName = Thread.currentThread().getThreadGroup().getName();
		if (threadTableMapping.containsKey(threadGroupName))
			threadTableMapping.get(threadGroupName).add(tableId);
		else threadTableMapping.put(threadGroupName, new HashSet<TableId>(Collections.singleton(tableId)));
	}
	
	public static void reset(){
		String threadGroupName = Thread.currentThread().getThreadGroup().getName();
		if (threadTableMapping.containsKey(threadGroupName))
			threadTableMapping.remove(threadGroupName);
	}
}
