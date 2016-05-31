package org.gcube.application.framework.http.oaipmh.Data;

public class Pair {
	
	private String id;
	private String name;
	
	public Pair(String id, String name){
		this.id = id;
		this.name = name;
	}
	
	public String getID(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
}
