package org.gcube.datatransfer.agent.test;
/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestStr {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String address = "http://pcitgt1012.cern.ch:8081/";
		String sourceEndpoint=address;
		//we keep only the host name and the port
		String[] parts = address.split("/");
		if(parts.length>=3){
			System.out.println("changed...");
			sourceEndpoint = parts[0]+"//"+parts[2];
		}
		
		System.out.println(sourceEndpoint+"\n\n\n");
		
		String stringDate="07.06.13-12.44.02";
		String[] p=stringDate.split("\\.");
		System.out.println(p.length);
		
	}

}
