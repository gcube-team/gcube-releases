package org.gcube.data.spd.utils;

public class VOID {

	private static VOID singleton = new VOID();
	
	public static VOID instance(){
		return singleton;
	} 
	
	private VOID(){}
	
}
