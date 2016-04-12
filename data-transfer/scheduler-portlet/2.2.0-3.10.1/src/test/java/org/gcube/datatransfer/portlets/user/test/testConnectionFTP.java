package org.gcube.datatransfer.portlets.user.test;
import org.gcube.datatransfer.portlets.user.server.workers.ConnectionFTP;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;

import com.google.gson.Gson;

public class testConnectionFTP {
	public static void main(String[] argv)throws Exception{
		//String rootString = "ftp://andrea:bilico1980@pcd4science3.cern.ch/";
		
		//assume these default values
		String host="pcd4science3.cern.ch";
		String specificPath="./temp/META-INF/";
		//String specificPath="./";

		String user="andrea";
		String pass="bilico1980";
		//--------------------------------------
		
		ConnectionFTP connectionFTP = null;
		connectionFTP = new ConnectionFTP(host,specificPath,user,pass);

		//connectionFTP.setLimitDepth(4);
		FolderDto rootFolder = connectionFTP.browse();
		if(rootFolder==null){
			System.out.println("GET LIST OF DATASOURCE: PROBLEM result =null\nERRORS:\n");
			for(String err : connectionFTP.getErrors())System.out.println(err);		
			return ;
		}

		connectionFTP.printFolder(rootFolder, 0);
		if(connectionFTP!=null && connectionFTP.getErrors().size()>0){
			for(String err : connectionFTP.getErrors()){
				System.out.println(err);		
			}
		}
		
		Gson gson = new Gson();
		String serializedObj = gson.toJson(rootFolder);	

		if(rootFolder!=null){
			System.out.println("returned folder!=null");			
			System.out.println("GET LIST OF DATASOURCE: gson folder length= "+serializedObj.length());
		}
		

		connectionFTP.disconnect();

		//FolderDto again = gson.fromJson(serializedObj, FolderDto.class);
		//connectionFTP.printFolder(again, 0);

	}

}
