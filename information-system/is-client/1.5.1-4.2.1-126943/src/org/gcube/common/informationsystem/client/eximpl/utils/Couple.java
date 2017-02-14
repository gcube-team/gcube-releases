package org.gcube.common.informationsystem.client.eximpl.utils;

public class Couple{
	private String toInsert;
	//is strange Behaviour is true if we are searching for RI , GHN or Service
	private boolean isStrangeBehaviour;
	public Couple(String toInsert, boolean isStrangeBehaviour){
		this.toInsert= toInsert;
		this.isStrangeBehaviour= isStrangeBehaviour;
	}
	
	public String getToInsert(){
		return this.toInsert;
	}
	
	public boolean isStrangeBehaviour(){
		return this.isStrangeBehaviour;
	}
}