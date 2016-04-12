package org.gcube.datatransfer.portlets.user.server.workers;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;


public class ConnectionSMP {
	public int autoId;
	public String serviceClass;
	public String serviceName;
	public String owner;
	public String accessType;
	public String scope;
	public IClient client;
	public String rootPath;

	public URLConnection connection = null;
	public BufferedReader rd  = null;
	public StringBuilder sb = null;
	List<String> errors;

	public ConnectionSMP(String smServiceClassSource,String smServiceNameSource,String smOwnerSource,String smAccessTypeSource,String scope,String path){
		errors=new ArrayList<String>();

		this.serviceClass=smServiceClassSource;
		this.serviceName=smServiceNameSource;
		this.owner=smOwnerSource;
		this.accessType=smAccessTypeSource;
		this.scope=scope;
		this.rootPath=path;
		this.autoId=0;
	}

	public FolderDto browse(){
		ScopeProvider.instance.set(scope.toString());
		try {
	//		client=new StorageClient(serviceClass, serviceName, owner, AccessType.valueOf(accessType.toUpperCase()), scope.toString()).getClient();
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.valueOf(accessType.toUpperCase())).getClient();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return process();
	}
	public FolderDto process() {
		if(rootPath==null){
			errors.add("ConnectionSMP(process) - rootPath is null");
			return null;
		}
		if(rootPath.startsWith("."))rootPath=rootPath.substring(1);
		if(!rootPath.startsWith("/"))rootPath="/"+rootPath;
		if(rootPath.endsWith("."))rootPath=rootPath.substring(0, rootPath.length()-1);
		if(!rootPath.endsWith("/"))rootPath=rootPath+"/";

		FolderDto empty= makeFolder("");
		FolderDto folder = makeFolder(rootPath);

		List<StorageObject> result = client.showDir().RDir(rootPath);
		for(StorageObject obj:result){		
			if (!obj.isDirectory()){
				//System.out.println("File:"+obj.getName());
				FolderDto subf = makeFolder(rootPath+obj.getName());
				subf.setLink(client.getUrl().RFile(rootPath+obj.getName()));
				folder.addChild(subf);	
			}
			else {				
				FolderDto subFolder = makeFolder(rootPath+obj.getName()+"/");
				subFolder.addChild(empty);
				folder.addChild(subFolder);
			}
		}
	//	System.out.println("listOnlyFirstLevel - folder of first level has "+folder.getChildren().size()+" children");
		if(folder.getChildren().size()==0)folder.addChild(empty);
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


	public void storeNewFolder(String pathForNewFolder){
		try {
			ScopeProvider.instance.set(scope.toString());
		//	client=new StorageClient(serviceClass, serviceName, owner, AccessType.valueOf(accessType.toUpperCase()), scope.toString()).getClient();
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.valueOf(accessType.toUpperCase())).getClient();

			System.out.println("ConnectionSMP (storeNewFolder) - pathForNewFolder="+pathForNewFolder);
			
			String localPath=new String(pathForNewFolder);
			if(localPath.startsWith("/"))localPath=localPath.substring(1);
			
			CreateLocalSources.createFile(localPath+"empty");

			//workaround to store the folder - upload a file and then remove it, in this way SM keeps the folder structure
			client.put(true).LFile(localPath+"empty").RFile(pathForNewFolder+"empty");
			client.remove().RFile(pathForNewFolder+"empty");
			
			//in case of not being deleted in the first place
			List<StorageObject> result = client.showDir().RDir(pathForNewFolder);
			for(StorageObject obj:result){	
				if(obj.getName().compareTo("empty")==0){
					client.remove().RFile(pathForNewFolder+"empty");
				}
			}
			
			CreateLocalSources.removeFileAndFolders(localPath+"empty");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void deleteFolder(String pathToDelete){
		try {
			ScopeProvider.instance.set(scope.toString());
		//  client=new StorageClient(serviceClass, serviceName, owner, AccessType.valueOf(accessType.toUpperCase()), scope.toString()).getClient();
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.valueOf(accessType.toUpperCase())).getClient();

			System.out.println("ConnectionSMP (storeNewFolder) - pathToDelete="+pathToDelete);
			
			if(!pathToDelete.startsWith("/"))pathToDelete="/"+pathToDelete;
			client.removeDir().RDir(pathToDelete);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sleepFiveSec(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("\nCheckDBForTransfersThread (sleepFiveSec)-- InterruptedException-Unable to sleep");
			e.printStackTrace();
		}
	}
}
