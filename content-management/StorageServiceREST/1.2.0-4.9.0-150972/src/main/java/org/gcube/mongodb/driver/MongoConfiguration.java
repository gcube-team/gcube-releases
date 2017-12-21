package org.gcube.mongodb.driver;

import java.util.List;

import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

public class MongoConfiguration {
	
	private String user;
	private String pwd;
	private String db;
	private List<ServerAddress> servers;
	private MongoClientOptions options;
	
	public MongoConfiguration(String user, String pwd, String dbName, List<ServerAddress> servers, MongoClientOptions options){
		setUser(user);
		setPwd(pwd);
		setDb(dbName);
		setOptions(options);
		setServers(servers);
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPwd() {
		return pwd;
	}
	
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public List<ServerAddress> getServers() {
		return servers;
	}
	
	public void setServers(List<ServerAddress> servers) {
		this.servers = servers;
	}
	
	public MongoClientOptions getOptions() {
		return options;
	}
	
	public void setOptions(MongoClientOptions options) {
		this.options = options;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}
	
	

}
