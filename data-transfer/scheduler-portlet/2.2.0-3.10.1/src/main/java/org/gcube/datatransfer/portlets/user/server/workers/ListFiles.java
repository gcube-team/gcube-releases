package org.gcube.datatransfer.portlets.user.server.workers;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gcube.datatransfer.portlets.user.shared.obj.BaseDto;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;


public class ListFiles   {
	int autoId;
	static int indentlvl;
	String rootPath;
	
	public ListFiles(String rootPath) {
		indentlvl = 0;
		this.autoId=0;
		this.rootPath=rootPath;
	}

	public FolderDto process(){
		File dir = new File(this.rootPath);
		FolderDto folderToBeReturned=  outFirstLevel(dir,indentlvl);
		//this.printFolder(folderToBeReturned, 0);
		return folderToBeReturned;
	}
	
	private FolderDto outFirstLevel(File dir, int indentlvl) {
		FolderDto empty= makeFolder("");		
		FolderDto folder = makeFolder(dir.getAbsolutePath()+"/");
		
		if(dir.listFiles()==null){
			System.out.println("empty folder: "+dir);
			folder.addChild(empty);
			return folder;
		}
		for(File f : dir.listFiles()) { //printing all the files
			if(!f.isDirectory()) {		
				FolderDto file = makeFolder(f.getAbsolutePath());
				folder.addChild(file);
			}
		}
		for(File f : dir.listFiles()) { //going to sub directory
			if(f.isDirectory()) {
				FolderDto childFolder = makeFolder(f.getAbsolutePath()+"/");
				childFolder.addChild(empty);
				folder.addChild(childFolder);
			}
		}
		if(folder.getChildren().size()==0)folder.addChild(empty);
		return folder;
	}
	
	// not used anymore - this one retrieves all the subdirectories, not only the first level
	private FolderDto out(File dir, int indentlvl) {
	//	for(int i = 0; i < indentlvl; i++) System.out.print("\t");

	//	System.out.println(dir.getAbsolutePath()+"  :");
		FolderDto folder = makeFolder(dir.getAbsolutePath()+"/");
		
		if(dir.listFiles()==null){
			System.out.println("empty folder: "+dir);
			FolderDto emptyFile= makeFolder("");
			folder.addChild(emptyFile);
			return folder;
		}
		for(File f : dir.listFiles()) { //printing all the files
			if(!f.isDirectory()) {
			//	for(int i = 0; i < indentlvl; i++) System.out.print("\t");
			//	System.out.println(f.getName());				
				FolderDto file = makeFolder(f.getAbsolutePath());
				//BaseDto file=new BaseDto(++this.autoId,f.getAbsolutePath());
				folder.addChild(file);
			}
		}
		for(File f : dir.listFiles()) { //going to sub directory
			if(f.isDirectory()) {
				FolderDto childFolder = out(f, indentlvl+1); //Recursive Call
				folder.addChild(childFolder);
			}
		}
		if(folder.getChildren().size()==0){
			FolderDto emptyFile= makeFolder("");
			folder.addChild(emptyFile);
		}
		return folder;
	}
	
	
	  private FolderDto makeFolder(String name) {
			FolderDto theReturn = new FolderDto(++this.autoId, name);
		    theReturn.setChildren((List<FolderDto>) new ArrayList<FolderDto>());
		    return theReturn;
		   
	  }
	  
	  public void printFolder(FolderDto folder, int indent){
			for(int i = 0; i < indent; i++) System.out.print("\t");
	    	System.out.println("fold: name="+folder.getName() +" - id="+folder.getId());
	    	
		    List<FolderDto> tmpListOfChildren = folder.getChildren();
		    if(tmpListOfChildren!=null){		    	
				for(FolderDto tmp : tmpListOfChildren){ //first the files
		    		if(tmp.getChildren().size() <= 0){
						if(tmp.getName().compareTo("")==0)continue;
						for(int i = 0; i < indent; i++) System.out.print("\t");
						String type= "";
						if((tmp.getName().substring(tmp.getName().length()-1,tmp.getName().length())).compareTo("/")==0)type="fold";
						else {
							type="file";
						}
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
}