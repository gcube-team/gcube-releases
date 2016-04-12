package org.gcube.portlets.user.databasesmanager.server.util;

public class DataExchangedThroughQueue {

	private String scope;
	private boolean loadTree = false;
	// private LinkedHashMap<String, String> Data;
	private String elementType;
	private String resource;
	private String database;
	private String schema;
	private String DBType;

	public DataExchangedThroughQueue(String scopeValue) {
		scope = scopeValue;
		loadTree = true;
	}

	public DataExchangedThroughQueue(String scopeValue, String elemType,
			String resourceName, String databaseName, String schemaName,
			String databaseType) {
		scope = scopeValue;
		loadTree = false;
		elementType = elemType;
		resource = resourceName;
		database = databaseName;
		schema = schemaName;
		DBType = databaseType;
	}

	public String getScope() {
		return scope;
	}

	public boolean treeToBeLoaded() {
		return loadTree;
	}

	public String elementType() {
		return elementType;
	}

	public String resource() {
		return resource;
	}

	public String database() {
		return database;
	}

	public String schema() {
		return schema;
	}

	public String DBType() {
		return DBType;
	}
}
