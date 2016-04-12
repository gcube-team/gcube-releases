package org.gcube.datatransfer.portlets.user.server;

import static org.gcube.datatransfer.agent.library.proxies.Proxies.transferAgent;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.objs.LocalSource;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;


public class AgentProxy {
	public int autoId;
	AgentLibrary agentLibrary = null;

	public AgentProxy(){
		autoId=0;
	}

	public void retrieveAgentLibrary(String hostname,String port,String scope){
		try{
			ScopeProvider.instance.set(scope);	
			int portInt = Integer.valueOf(port);
	//		System.out.println("retrieveAgentLibrary - host='"+hostname+"' - port='"+portInt+"'");
			agentLibrary=transferAgent().at(hostname, portInt).build();
		}catch(Exception e ){
			e.printStackTrace();
		}
	}

	public FolderDto getLocalAgentSources(String path){
		try{
			if(path==null){
				System.out.println("getLocalAgentSources path=null");
				return null;
			}
			if(path.startsWith("."))path=path.substring(1);
			if(!path.startsWith("/"))path="/"+path;
			if(path.endsWith("."))path=path.substring(0, path.length()-1);
			if(!path.endsWith("/"))path=path+"/";

			FolderDto empty= makeFolder("");
			FolderDto folder = makeFolder(path);

			ArrayList<LocalSource> sources = agentLibrary.getLocalSources(path);
			if(sources==null){
				System.out.println("local sources = null");
				return null;
			}

			for(LocalSource obj:sources){		
				if (!obj.isDirectory()){
					//System.out.println("File:"+obj.getName());
					FolderDto subf = makeFolder(obj.getPath().replaceFirst(obj.getVfsRoot(), ""));
					subf.setLink("file://"+obj.getPath());
					folder.addChild(subf);	
				}
				else {				
					FolderDto subFolder = makeFolder(obj.getPath().replaceFirst(obj.getVfsRoot(), "")+"/");
					subFolder.addChild(empty);
					folder.addChild(subFolder);
				}
			}
	//		System.out.println("getLocalAgentSources (listOnlyFirstLevel) - folder of first level has "+folder.getChildren().size()+" children");
			if(folder.getChildren().size()==0)folder.addChild(empty);
			return folder;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	public FolderDto getTreeSources(String type){
		try{
			FolderDto empty= makeFolder("");
			FolderDto folder = makeFolder("");

			ArrayList<String> treeSources = agentLibrary.getTreeSources(type);
			if(treeSources==null){
				System.out.println("tree sources = null");
				return null;
			}

			for(String treeSource:treeSources){		
				String[] parts = treeSource.split("--");
				if(parts==null || parts.length<3){
					System.out.println("getTreeSources - one of the treesources had no or less than expected info -- we skip it");
					continue;
				}
				String id=parts[0];
				String name=parts[1];
				String cardinality=parts[2];
				//			if (!obj.isDirectory()){
				//				System.out.println("File:"+obj.getName());
				FolderDto subf = makeFolder(name);
				subf.setLink(id+"--"+cardinality);
				folder.addChild(subf);	
				//			}
				//			else {				
				//				FolderDto subFolder = makeFolder(obj.getPath().replaceFirst(obj.getVfsRoot(), "")+"/");
				//				subFolder.addChild(empty);
				//				folder.addChild(subFolder);
				//			}
			}
	//		System.out.println("getTreeSources - folder of first level has "+folder.getChildren().size()+" children");
			if(folder.getChildren().size()==0)folder.addChild(empty);
			return folder;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String createNewTreeSource(String id){
		//no specific endpoint...
		return agentLibrary.createTreeSource(id, null, 0);
	}
	
	public String deleteTreeSource(String id){
		return agentLibrary.removeGenericResource(id);
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
}
