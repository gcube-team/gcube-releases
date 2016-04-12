package org.gcube.datatransfer.portlets.user.test;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;

public class testConnectionFTP3 {
	static int autoId;
	static List<String> errors;
	static FTPClient ftp;
	static String host;
	static String specificPath;
	static String user;
	static String pass;


	public static void main(String[] args) {
		errors=new ArrayList<String>();
		host="ftp.d4science.org";
		specificPath="./";
		user="d4science";
		pass="fourD_314";
		
		process(host);
	}
	
	public static FolderDto process(String path){
		ftp = new FTPClient();
		FolderDto folder=null;
		try {
			//connect
			ftp.connect(path);			
			//login
			ftp.login(user, pass);
			System.out.println(ftp.getReplyString());
			int reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)){
				ftp.disconnect();
				System.out.println("process - FTP Server refused connection");
				return null;
			}
			
			//listing the files of the rootFolder
			FTPFile[] ftpFiles = ftp.listFiles(specificPath);
			
			for(FTPFile tmp : ftpFiles){
				//ftp.deleteFile(specificPath+tmp.getName());
				if(tmp.getType()!=0)continue;
				if(tmp.getName().startsWith("."))continue;
				System.out.println(tmp.getName());
			}
			
			//create all the files/folders objects by listing everything
			//folder = listFilesAndDirectories(ftpFiles, specificPath, 0);

		} catch (SocketException e) {
			System.out.println("process - SocketException"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("process - IOException\n"+e.getMessage());
			e.printStackTrace();

		} catch (Exception e) {
			System.out.println("process - Other Exception\n"+e.getMessage());
			e.printStackTrace();

		}	
		return folder;
	}

	public static void disconnect(){
		//disconnect
		if(ftp.isConnected()){
			try{
				ftp.logout();
				System.out.println(ftp.getReplyString());
				ftp.disconnect();
			}catch(IOException e ){
				System.out.println("process - IOException\n"+e.getMessage());
				e.printStackTrace();

			}
		}
	}
	



}
