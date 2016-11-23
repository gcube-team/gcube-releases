package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure;

public class DatabaseInfo {
	
	public String username;
	public String password;
	public String url;
	public String driver  = "org.postgresql.Driver";
	public String dialect = "org.hibernate.dialect.PostgreSQLDialect";
	public String tablespace = "";
	
	
	public String toString(){
		return "DB Info: "+username+":"+password+" - "+url+" ("+driver+","+dialect+","+tablespace+")";
	}
}
