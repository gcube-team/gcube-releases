package org.gcube.datatransfer.portlets.user.test;
/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestStrMoveToParentPath {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String currentPath="/test/jkfhsdjkfhsjk/jkhdfjksdf/";
		String parentPath=getParentPath(currentPath);
		System.out.println("parentPath="+parentPath);
	}

	public static String getParentPath(String currentPath){
		if(currentPath.endsWith("/"))currentPath=currentPath.substring(0,currentPath.length()-1);
		
		int pos = currentPath.lastIndexOf("/");
		if(pos!=0)return currentPath.substring(0,pos);
		else return currentPath;
	}
}
