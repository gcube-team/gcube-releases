package org.gcube.datatransformation.adaptors.common.db.tools;

import java.util.HashMap;

public class DBConstants {
	
//	public static HashMap<String, String> dbNameToDriverClass;
//	
//	static{
//		dbNameToDriverClass = new HashMap<String, String>();
//		dbNameToDriverClass.put("mysql", "com.mysql.jdbc.Driver");
//		dbNameToDriverClass.put("postgresql", "org.postgresql.Driver");
//		dbNameToDriverClass.put("sqlite", "org.sqlite.JDBC");
//	}
	
	private static String[] dbtypes = {"postgre","mysql","sqlite"};  
	
	/**
	 * 
	 * @param name e.g. "postgres" or "postgresql" for postgresql driver, "mysql" for mysql driver, "sqlite" for sqlite driver.
	 * @return the driver String ; e.g: "com.postgresql.Driver" for postgres
	 */
	public static String getDriverForName(String name){
		String driverName = "";
		if(name.toLowerCase().contains("postgre"))
			driverName = "org.postgresql.Driver";
		if(name.toLowerCase().contains("mysql"))
			driverName = "com.mysql.jdbc.Driver";
		if(name.toLowerCase().contains("sqlite"))
			driverName = "org.sqlite.JDBC";
		return driverName;
	}
	
	public static String getFixedFullNameOf(String dbType){
		String fullname = dbType;
		if(dbType.toLowerCase().contains("postgre"))
			fullname = "postgresql";
		if(dbType.toLowerCase().contains("mysql"))
			fullname = "mysql";
		if(dbType.toLowerCase().contains("sqlit"))
			fullname = "sqlite";
		return fullname;
	}
	
	public static String[] getDBTypes(){	
		return dbtypes;
	}
	
//	public static final String RUNNING_CATEGORY_NAME = "Database";
//	public static final String GENERIC_CATEGORY_NAME = "HarvesterProps";
	
	
}
