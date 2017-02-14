package org.gcube.datatransformation.adaptors.common.db.xmlobjects;

import java.io.Serializable;


/**
 * This is the class which holds the DB information stored on the IS.
 * Design should be kept simple.
 *
 */
public class DBSource implements Serializable{

	private static final long serialVersionUID = -7797176263943017719L;
	
	private String sourcename;
	private String username;
	private String password;
	private String dbType;
	private int versionMajor;
	private int versionMinor;
	private String hostname;
	private String connectionStr;
	
	
	public DBSource(){
		
	}
	
	public void setSourceName(String sourcename){
		this.sourcename = sourcename;
	}
	
	public String getSourceName(){
		return sourcename;
	}
	
	public void setUserName(String username){
		this.username = username;
	}
	
	public String getUserName(){
		return username;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setDBType(String dbType){
		this.dbType = dbType;
	}
	
	public String getDBType(){
		return dbType;
	}
	
	public void setVersionMajor(int versionMajor){
		this.versionMajor = versionMajor;
	}
	
	public int getVersionMajor(){
		return versionMajor;
	}
	
	public void setVersionMinor(int versionMinor){
		this.versionMinor = versionMinor;
	}
	
	public int getVersionMinor(){
		return versionMinor;
	}
	
	public void setHostName(String hostname){
		this.hostname = hostname;
	}
	
	public String getHostName(){
		return hostname;
	}
	
	public void setConnectionString(String connectionString){
		this.connectionStr = connectionString;
	}
	
	public String getConnectionString(){
		return connectionStr;
	}
	
	
}

