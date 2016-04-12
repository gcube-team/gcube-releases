package org.gcube.datatransfer.portlets.user.server.workers;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;

public class ConnectionFTP  {
	int autoId;
	int limitDepth;
	List<String> errors;
	FTPClient ftp;
	String host;
	String specificPath;
	String user;
	String pass;
	int maxDepth=0;
	String maxPath;

	public ConnectionFTP(String host, String specificPath, String user, String pass){
		errors=new ArrayList<String>();
		this.host=host;
		this.user=user;
		this.pass=pass;
		this.specificPath=specificPath;
		this.limitDepth=255;
	}
	
	public FolderDto browse(){
		return this.process(host);
	}
	
	public FolderDto process(String path){
		ftp = new FTPClient();
		FolderDto folder=null;
		try {
			//connect
			ftp.connect(path);			
			//login
			ftp.login(user, pass);
	//		System.out.println(ftp.getReplyString());
			int reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)){
				ftp.disconnect();
				errors.add("ConnectionFTP(process) - FTP Server refused connection");
				return null;
			}

			//listing the files of the rootFolder
			FTPFile[] ftpFiles = ftp.listFiles(specificPath);
			
			//for(FTPFile folder : ftpFiles){
			//	if(folder.getType()!=1)continue;
			//	if(folder.getName().startsWith("."))continue;
			//	System.out.println(folder.getName());
			//}
			
			//create all the files/folders objects by listing everything
			//folder = listFilesAndDirectories(ftpFiles, specificPath, 0);
			folder = listOnlyFirstLevel(ftpFiles, specificPath, 0);
			
		} catch (SocketException e) {
			errors.add("ConnectionFTP(process) - SocketException\n"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			errors.add("ConnectionFTP(process) - IOException\n"+e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			errors.add("ConnectionFTP(process) - Other Exception\n"+e.getMessage());
			e.printStackTrace();
		}	
	//	System.out.println("process - folder of first level has "+folder.getChildren().size()+" children (including empty file)");
		return folder;
	}

	public void disconnect(){
		//disconnect
		if(ftp.isConnected()){
			try{
				ftp.logout();
		//		System.out.println(ftp.getReplyString());
				ftp.disconnect();
			}catch(IOException e ){
				errors.add("ConnectionFTP(process) - IOException\n"+e.getMessage());
			}
		}
	}
	
	public FolderDto listOnlyFirstLevel(FTPFile[] ftpFiles, String pathFolder, int depth){
		FolderDto empty= makeFolder("");

		FolderDto folder = makeFolder(pathFolder);

		for(FTPFile file : ftpFiles){
			String type="";
			if(file.getName().startsWith("."))continue;  //hidden file
			else if(file.getName().startsWith("$"))continue;  
			else if(file.getName().startsWith("lock."))continue;  
			else if(file.getName().startsWith("~"))continue;  


			if(file.getType()==1){ // It's a folder !
				type="folder";
				//System.out.println(type+" : name="+file.getName()+" - size="+file.getSize());				
				FolderDto subFolder = makeFolder(pathFolder+file.getName()+"/");
				subFolder.addChild(empty);
				folder.addChild(subFolder);		
			}
			else if(file.getType()==0){ // It's a file !
				type="file";
				//System.out.println(type+" : name="+file.getName()+" - size="+file.getSize());				
				FolderDto subf = makeFolder(pathFolder+file.getName());
				folder.addChild(subf);		
			}

		}
		//System.out.println("path='"+pathFolder+"' - files="+numFiles+" - folders="+numFolders+" - id is now="+autoId);
	//	System.out.println("listOnlyFirstLevel - folder of first level has "+folder.getChildren().size()+" children");
		if(folder.getChildren().size()==0)folder.addChild(empty);		
		return folder;
	}
	
	// not used anymore - this one retrieves all the subdirectories, not only the first level
	public FolderDto listFilesAndDirectories(FTPFile[] ftpFiles, String pathFolder, int depth){
		//System.out.println("totalFiles="+ftpFiles.length);
		
		int numFiles=0;
		int numFolders=0;
		FolderDto folder = makeFolder(pathFolder);
		
		if(depth>limitDepth){
			maxDepth=depth-1;
			maxPath=pathFolder;
			return folder; //empty
		}
		if(depth>=maxDepth){
			maxDepth=depth;
			maxPath=pathFolder;
		}
		
		for(FTPFile file : ftpFiles){
			String type="";
			if(file.getName().startsWith("."))continue;  //hidden file
			else if(file.getName().startsWith("$"))continue;  
			else if(file.getName().startsWith("lock."))continue;  
			else if(file.getName().startsWith("~"))continue;  


			if(file.getType()==1){ // It's a folder !
				type="folder";
				//System.out.println(type+" : name="+file.getName()+" - size="+file.getSize());				
				++numFolders;
				FTPFile[] subFiles = null;
				try {
					subFiles = ftp.listFiles(pathFolder+file.getName()+"/");
				} catch (IOException e) {
					errors.add("listFilesAndDirectories - IOException\n"+e.getMessage());
				}
				if(subFiles!=null){
					if (subFiles.length>0){
						FolderDto childFolder = listFilesAndDirectories(subFiles, pathFolder+file.getName()+"/", depth+1);
						if(childFolder!=null)folder.addChild(childFolder);
					}
				}
			}
			else if(file.getType()==0){ // It's a file !
				type="file";
				//System.out.println(type+" : name="+file.getName()+" - size="+file.getSize());				
				++numFiles;
				FolderDto subf = makeFolder(pathFolder+file.getName());
				folder.addChild(subf);		
			}

		}
		//System.out.println("path='"+pathFolder+"' - files="+numFiles+" - folders="+numFolders+" - id is now="+autoId);
		if(folder.getChildren().size()==0){
			FolderDto emptyFile= makeFolder("");
			folder.addChild(emptyFile);
		}
		return folder;
	}
	
	public void printFolder(FolderDto folder, int indent){
		for(int i = 0; i < indent; i++) System.out.print("\t");
		System.out.println("fold : name="+folder.getName() +" - id="+folder.getId());

		List<FolderDto> tmpListOfChildren = folder.getChildren();
		if(tmpListOfChildren!=null){
			for(FolderDto tmp : tmpListOfChildren){ //first the files
				if(tmp.getChildren().size() <= 0){
					if((tmp.getName().compareTo("")==0))continue;
					for(int i = 0; i < indent; i++) System.out.print("\t");
					String type= "";
					if((tmp.getName().substring(tmp.getName().length()-1,tmp.getName().length())).compareTo("/")==0)type="fold";
					else type="file";
					System.out.println(type+" : name="+tmp.getName()+" - id="+tmp.getId());
				}
			}		    	
			for(FolderDto tmp : tmpListOfChildren){ //then the folders
				if(tmp.getChildren().size() > 0){
					printFolder(tmp,indent+1);
				}
			}
		}		    
	}
	
	public FolderDto makeFolder(String name) {
		FolderDto theReturn = new FolderDto(++autoId, name);
		theReturn.setChildren((List<FolderDto>) new ArrayList<FolderDto>());
		return theReturn;
	}
	
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public int getLimitDepth() {
		return limitDepth;
	}

	public void setLimitDepth(int limitDepth) {
		this.limitDepth = limitDepth;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public String getMaxPath() {
		return maxPath;
	}

	public void setMaxPath(String maxPath) {
		this.maxPath = maxPath;
	}

	public int getAutoId() {
		return autoId;
	}

	public void setAutoId(int autoId) {
		this.autoId = autoId;
	}
	
	
}


