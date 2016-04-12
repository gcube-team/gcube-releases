package org.gcube.datatransfer.portlets.user.test;

public class TestStr {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String testText = "HH:mm:ss  EEE, d MMM yyyy -- Submitter: 'nick'";
		
		System.out.println(replaceSubmitterInHeader("testName",testText));
	
	}
	
	public static String replaceSubmitterInHeader(String name,String header){
		int start = header.indexOf("Submitter: '");
		int end=header.length()-1;
		
		return header.substring(0, start)+"Submitter: '"+name+header.substring(end);
	}

}
