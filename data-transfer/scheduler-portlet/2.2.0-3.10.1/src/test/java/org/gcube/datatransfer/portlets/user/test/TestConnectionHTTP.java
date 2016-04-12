package org.gcube.datatransfer.portlets.user.test;

import java.util.List;

import org.gcube.datatransfer.portlets.user.server.workers.ConnectionHTTP;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;

public class TestConnectionHTTP {
	static public String usedURL;
	static public String username;
	static public String password;
	static public String specificPath;
	
	public static void main(String[] args) {
		//usedURL="http://www.di.uoa.gr/~std06009/nick_di_eam_askisi3/eam/images_files/";
		//usedURL="http://www.di.uoa.gr/~std06009/";
		//usedURL = "https://riouxsvn.com/svn/nickdi/";
		//username="nickdi";
		//password="666666";
		
		usedURL="https://svn.research-infrastructures.eu/public/d4science/gcube/trunk/";
		
		specificPath="./Common/Bitlet/";
		ConnectionHTTP connectionHTTP = new ConnectionHTTP(usedURL,specificPath, username, password);
		FolderDto folder = connectionHTTP.process();
		if(folder==null){
			System.out.println("TestConnectionHTTP - folder is null");
			return;
		}
		connectionHTTP.printFolder(folder,0);	
		connectionHTTP.disconnect();
	}
	
	

}
