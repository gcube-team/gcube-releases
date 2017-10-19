package org.gcube.dataanalysis.ecoengine.connectors;

import java.util.HashMap;
import java.util.List;

public class RemoteHspecInputObject {
	
	public String userName;
	public int nWorkers;
	public String id;
	public String generativeModel;
	public String environment;
	public List<String> speciesList;
	public Table hspenTableName;
	public Table hcafTableName;
	public Table hspecDestinationTableName;
	public Table occurrenceCellsTable;
	public boolean is2050;
	public boolean isNativeGeneration;
	public HashMap<String,String> configuration;
	
	public RemoteHspecInputObject() {
		hspenTableName=new Table();
		hcafTableName=new Table();
		hspecDestinationTableName=new Table();
		occurrenceCellsTable=new Table();
	}
	public class Table{
		public String jdbcUrl;
		public String tableName;
	}
}
