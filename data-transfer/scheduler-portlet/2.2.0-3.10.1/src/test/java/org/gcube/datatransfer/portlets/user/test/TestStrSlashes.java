package org.gcube.datatransfer.portlets.user.test;


/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestStrSlashes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String newPath="/";
		
		String rootPath="http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-custom-webapp-2.4.1/repository/default/Home/nikolaos.drakopoulos/Workspace/";
		String currentPath=rootPath+"";
		String relCurrentPath=currentPath.replaceFirst(rootPath, "/");
		System.out.println("rel path="+relCurrentPath);
		
		String[] parts=relCurrentPath.split("/");
		System.out.println(parts.length+"\n");
		for(String tmp:parts)System.out.println("'"+tmp+"'");
		
		if(parts.length>1)newPath="";
		for(int i=0;i<parts.length-1;i++)newPath=newPath+parts[i]+"/";	
		
		System.out.println("\nnew rel path="+newPath);
	}

}
