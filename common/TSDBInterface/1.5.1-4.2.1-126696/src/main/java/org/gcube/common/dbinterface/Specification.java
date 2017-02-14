package org.gcube.common.dbinterface;

public enum Specification{
	NOT_NULL("NOT NULL"),
	UNIQUE("UNIQUE"),
	PRIMARY_KEY("PRIMARY KEY"),
	AUTO_INCREMENT("AUTO INCREMENT");
	
	private String stringValue;
	
	Specification(String stringValue){this.stringValue= stringValue;}
	
	public String getValue(){
		return this.stringValue;
	}
}