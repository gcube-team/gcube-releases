package org.gcube.content.storage.rest.bean;

import java.util.List;

import org.gcube.content.storage.rest.utils.Utils;

import com.mongodb.ServerAddress;
/**
 * 
 * @author Roberto Cirillo (ISTI-CNR) 2017
 *
 */
public class Credentials {
	
	String token;
	String db;
	String collection;
	String user;
	String pwd;
	List<ServerAddress> servers;
	
	public Credentials(String token, String db, String collection){
		setToken(token);
		setDb(db);
		setCollection(collection);
	}

	public Credentials(List<ServerAddress> server, String db, String collection, String user, String pwd) {
		setServers(server);
		setDb(db);
		setCollection(collection);
		setUser(user);
		setPwd(pwd);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		Utils.notNullNotEmpty(token);
		this.token = token;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		Utils.notNullNotEmpty(db);
		this.db = db;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		Utils.notNullNotEmpty(collection);
		this.collection = collection;
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
		if(servers != null)
		Utils.notNullNotEmpty(servers.get(0).toString());
		this.servers = servers;
	}
	
	
	

}
