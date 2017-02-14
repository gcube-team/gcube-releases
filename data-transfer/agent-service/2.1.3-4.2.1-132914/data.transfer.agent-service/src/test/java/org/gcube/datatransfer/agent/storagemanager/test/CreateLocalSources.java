package org.gcube.datatransfer.agent.storagemanager.test;

import java.io.File;
import java.io.IOException;

public class CreateLocalSources {

	static String path="test/test2/emptyFile/";
	public static void main(String[] args) {
		//createFile(path);
		removeFileAndFolders(path);
	}
	
	public static void createFile(String path){
		if(path.endsWith("/"))path=path.substring(0, path.length()-1);
		if(path.startsWith("/"))path=path.substring(1);
		
		String rootPath = path.substring(0, path.lastIndexOf("/")+1);
		String emptyFilePath=path.substring(path.lastIndexOf("/")+1,path.length());
		//System.out.println("rootPath="+rootPath+ " - emptyFilePath="+emptyFilePath );
		
		File rootFolders=new File(rootPath);
		rootFolders.mkdirs();
		File emptyFile=new File(rootPath+emptyFilePath);
		try {
			emptyFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void removeFileAndFolders(String path){
		if(path.endsWith("/"))path=path.substring(0, path.length()-1);
		if(path.startsWith("/"))path=path.substring(1);
		
		String rootPath = path.substring(0, path.lastIndexOf("/")+1);
		String emptyFilePath=path.substring(path.lastIndexOf("/")+1,path.length());
		//System.out.println("rootPath="+rootPath+ " - emptyFilePath="+emptyFilePath );
		File rootFolders=new File(rootPath);
		File emptyFile=new File(rootPath+emptyFilePath);
		emptyFile.delete();
		rootFolders.delete();
		while(rootFolders.getParentFile()!=null){
			rootFolders=rootFolders.getParentFile();
			rootFolders.delete();
		}
	}
	

}
