package org.gcube.datatransfer.portlets.user.test;

public class TestStrSplit {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String path="///test//Fooolder////";
		String path="name";
		path=path.replaceAll("(/)\\1+", "$1");
		System.out.println("path="+path);
		String[] parts=path.split("/");
		System.out.println("parts.length="+parts.length+"\n");
		for(String tmp:parts)System.out.println("'"+tmp+"'");
	}

}
