package org.gcube.datatransfer.portlets.user.server;

import javax.servlet.ServletException;

import org.gcube.datatransfer.portlets.user.server.workers.ConnectionFTP;
import org.gcube.datatransfer.portlets.user.server.workers.ConnectionHTTP;
import org.gcube.datatransfer.portlets.user.server.workers.ConnectionSMP;
import org.gcube.datatransfer.portlets.user.server.workers.ListFiles;
import org.gcube.datatransfer.portlets.user.server.workers.MappingWorker;
import org.gcube.datatransfer.portlets.user.server.workers.WorkspaceWorker;
import org.gcube.datatransfer.portlets.user.shared.SchedulerService;
import org.gcube.datatransfer.portlets.user.shared.obj.CallingManagementResult;
import org.gcube.datatransfer.portlets.user.shared.obj.CallingSchedulerResult;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;
import org.gcube.datatransfer.portlets.user.shared.obj.SchedulerObj;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;

import com.google.gson.Gson;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


@SuppressWarnings("serial")
public class SchedulerServiceImpl extends RemoteServiceServlet implements
SchedulerService {


	@Override
	public void init() throws ServletException {
		super.init();
	}


	public String listFiles(String input) throws IllegalArgumentException {
		try{
			String rootPath = input;
			ListFiles listFiles = new ListFiles(rootPath);
			FolderDto folder = listFiles.process();
			Gson gson = new Gson();
			if(folder!=null){
				System.out.println("returned folder!=null");			
				//System.out.println("gson folder: "+gson.toJson(folder));
				return null;
			}
			return gson.toJson(folder);			
		}catch(Exception e){e.printStackTrace();return null;}
	}

	public String getTransfers(String scope, String resourceName ) throws IllegalArgumentException {

		try{CallingManagementResult callingManagementResult;
		SchedulerProxy schedulerProxy = new SchedulerProxy();

		MappingWorker mappingWorker=new MappingWorker();
		callingManagementResult = mappingWorker.mappedcallingManagementResult (schedulerProxy.getTransfers(resourceName, scope));
		if(callingManagementResult==null){
			System.out.println("GET TRANSFERS: callingManagementResult=null");
			return null;
		}

		Gson gson = new Gson();
		//	System.out.println("GET TRANSFERS: gson callingManagementResult: "+gson.toJson(callingManagementResult));
		return gson.toJson(callingManagementResult);
		}catch(Exception e){e.printStackTrace();return null;}
	}

	public String schedule(String Obj) throws IllegalArgumentException {
		try{
			//System.out.println("SCHEDULE: gson obj: "+Obj);
			SchedulerObj schedulerObj = new SchedulerObj();
			SchedulerProxy schedulerProxy = new SchedulerProxy();
			Gson gson = new Gson();
			schedulerObj= gson.fromJson(Obj, SchedulerObj.class);

			schedulerProxy.retrieveSchedulerLibrary(schedulerObj.getScope(), schedulerObj.getSubmitter());
			MappingWorker mappingWorker=new MappingWorker();
			String transferId = schedulerProxy.schedule(mappingWorker.mappedSchedulerObj (schedulerObj), schedulerObj.getScope(), schedulerObj.getSubmitter(), schedulerObj.getPass(),schedulerObj.getSourceType(), schedulerObj.getDestinationFolder());
			return transferId;

		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String monitor(String scope, String resourceName, String transferId) throws IllegalArgumentException {
		try{
			CallingSchedulerResult callingSchedulerResult = new CallingSchedulerResult();
			SchedulerProxy schedulerProxy = new SchedulerProxy();

			schedulerProxy.retrieveSchedulerLibrary(scope, resourceName);
			MappingWorker mappingWorker=new MappingWorker();
			callingSchedulerResult = mappingWorker.mappedCallingSchedulerResult(schedulerProxy.monitor(transferId));
			Gson gson = new Gson();
			//System.out.println("MONITOR: gson callingSchedulerResult: "+gson.toJson(callingSchedulerResult));

			return gson.toJson(callingSchedulerResult);
		}catch(Exception e){e.printStackTrace();return null;}
	}

	public String getOutcomes(String scope, String resourceName, String transferId) throws IllegalArgumentException {
		try{
			CallingSchedulerResult callingSchedulerResult = new CallingSchedulerResult();
			SchedulerProxy schedulerProxy = new SchedulerProxy();

			schedulerProxy.retrieveSchedulerLibrary(scope, resourceName);
			MappingWorker mappingWorker=new MappingWorker();
			callingSchedulerResult = mappingWorker.mappedCallingSchedulerResult(schedulerProxy.getOutcomes(transferId));
			Gson gson = new Gson();
			//System.out.println("GET OUTCOMES: gson callingSchedulerResult: "+gson.toJson(callingSchedulerResult));

			return gson.toJson(callingSchedulerResult);
		}catch(Exception e){e.printStackTrace();return null;}
	}
	public String cancel(String scope, String resourceName ,String transferId, boolean force) throws IllegalArgumentException {
		try{
			CallingSchedulerResult callingSchedulerResult = new CallingSchedulerResult();
			SchedulerProxy schedulerProxy = new SchedulerProxy();

			schedulerProxy.retrieveSchedulerLibrary(scope, resourceName);
			MappingWorker mappingWorker=new MappingWorker();
			callingSchedulerResult = mappingWorker.mappedCallingSchedulerResult(schedulerProxy.cancel(transferId,force));
			Gson gson = new Gson();
			//	System.out.println("CANCEL: gson callingSchedulerResult: "+gson.toJson(callingSchedulerResult));

			return gson.toJson(callingSchedulerResult);
		}catch(Exception e){e.printStackTrace();return null;}
	}

	public String getObjectsFromIS(String type, String scope, String resourceName) throws IllegalArgumentException {
		try{
			SchedulerProxy schedulerProxy = new SchedulerProxy();
			String result=schedulerProxy.getObjectsFromIS(type, resourceName,scope);
			if(result==null){
				System.out.println("GET OBJECTS FROM IS: PROBLEM result from service=null");
			}
			else {
				//System.out.println("GET OBJECTS FROM IS: result from service="+result);
			}

			return result;
		}catch(Exception e){e.printStackTrace();return null;}
	}

	public String getFileListOfAgent(String path,String agentHostname,String agentPort, String scope){
		try{
			if(agentPort==null){System.out.println("GET FILE LIST OF AGENT: port is null");return null;}
			else if(agentHostname==null){System.out.println("GET FILE LIST OF AGENT: agentHostname is null");return null;}
			else if(scope==null){System.out.println("GET FILE LIST OF AGENT: scope is null");return null;}
			
			AgentProxy agentProxy=new AgentProxy();
			agentProxy.retrieveAgentLibrary(agentHostname,agentPort,scope);

			FolderDto rootFolder = agentProxy.getLocalAgentSources(path);
			if(rootFolder==null){
				System.out.println("GET FILE LIST OF AGENT: returned folder==null");			
				return null;
			}
			Gson gson = new Gson();
			String jsonString = gson.toJson(rootFolder);
		//	System.out.println("GET FILE LIST OF AGENT: gson folder length= "+jsonString.length());

			return jsonString;	
		}catch(Exception e){e.printStackTrace();return null;}
	}

	public String getTreeSources(String agentHostname,String agentPort, String scope,String type){
		try{
			if(agentPort==null){System.out.println("GET TREE SOURCES: port is null");return null;}
			else if(agentHostname==null){System.out.println("GET TREE SOURCES: agentHostname is null");return null;}
			else if(scope==null){System.out.println("GET TREE SOURCES: scope is null");return null;}
			
			
			AgentProxy agentProxy=new AgentProxy();
			agentProxy.retrieveAgentLibrary(agentHostname,agentPort,scope);

			FolderDto rootFolder = agentProxy.getTreeSources(type);
			if(rootFolder==null){
				System.out.println("GET TREE SOURCES: returned folder==null");			
				return null;
			}
			Gson gson = new Gson();
			String jsonString = gson.toJson(rootFolder);
		//	System.out.println("GET TREE SOURCES: gson folder length= "+jsonString.length());

			return jsonString;	
		}catch(Exception e){e.printStackTrace();return null;}
	}

	public String createNewTreeSource(String agentHostname, String agentPort,
			String scope, String sourceId) throws IllegalArgumentException {
		try{
			if(agentPort==null){System.out.println("CREATE NEW TREE SOURCE: port is null");return null;}
			else if(agentHostname==null){System.out.println("CREATE NEW TREE SOURCE: agentHostname is null");return null;}
			else if(scope==null){System.out.println("CREATE NEW TREE SOURCE: scope is null");return null;}
			
			AgentProxy agentProxy=new AgentProxy();
			agentProxy.retrieveAgentLibrary(agentHostname,agentPort,scope);

			String result = agentProxy.createNewTreeSource(sourceId);
			System.out.println("CREATE NEW TREE SOURCE: returned result=="+result);			

			return result;	
		}catch(Exception e){e.printStackTrace();return null;}
	}

	public String deleteTreeSource(String agentHostname, String agentPort,
			String scope, String sourceId) throws IllegalArgumentException {
		try{
			if(agentPort==null){System.out.println("DELETE TREE SOURCE: port is null");return null;}
			else if(agentHostname==null){System.out.println("DELETE TREE SOURCE: agentHostname is null");return null;}
			else if(scope==null){System.out.println("DELETE TREE SOURCE: scope is null");return null;}
			
			AgentProxy agentProxy=new AgentProxy();
			agentProxy.retrieveAgentLibrary(agentHostname,agentPort,scope);

			String result = agentProxy.deleteTreeSource(sourceId);
			System.out.println("DELETE TREE SOURCE: returned result=="+result);			

			return result;	
		}catch(Exception e){e.printStackTrace();return null;}
	}	

	public String getFileListOfDSourceOrDStorage(String type,String id,String specificPath, String scope, String resourceName){
		try{
			SchedulerProxy schedulerProxy = new SchedulerProxy();

			String typeOfObj;
			if(type.compareToIgnoreCase("DataSource")==0)typeOfObj="DataSource";
			else if (type.compareToIgnoreCase("DataStorage")==0)typeOfObj="DataStorage";
			else {
				System.out.println("GET FILE LIST OF DSOURCE OR DSTORAGE - given type != 'DataSource','DataStorage' --- given type="+type);
				return null;
			}
			String name=null;
			String description =null;
			String host=null;
			String user=null;
			String pass=null;
			String specificFolder=null;


			String result=schedulerProxy.getObjectsFromIS(typeOfObj, resourceName,scope);
			if (result==null) {
				System.out.println("GET FILE LIST OF DSOURCE OR DSTORAGE - getObjectsFromIS("+typeOfObj+") result=null");
				return null;
			}
			String[] sourcesArray=result.split("\n");
			for(String tmp:sourcesArray){
				//tmp contains: resultIdOfIS--name--description--endpoint--username--password--folder
				String[] partsOfInfo=tmp.split("--");
				if(partsOfInfo[0].compareTo(id)==0){
					name=partsOfInfo[1];
					description=partsOfInfo[2];			
					host=partsOfInfo[3];
					user=partsOfInfo[4];
					pass=partsOfInfo[5];
					if(specificPath==null || specificPath.compareTo("")==0){
						specificFolder=partsOfInfo[6];
					}
					else specificFolder=specificPath;

					if(specificFolder.compareTo(".")==0)specificFolder="./";
					else {
						if(!specificFolder.startsWith("./"))specificFolder="./"+specificFolder;
						if(!specificFolder.endsWith("/"))specificFolder=specificFolder.concat("/");		
						specificFolder = specificFolder.replaceAll("/{1,}", "/");				
					}
					break;
				}
			}
			if(host==null || name ==null){
				System.out.println("GET FILE LIST OF DSOURCE OR DSTORAGE - cannot find the "+typeOfObj+" in IS");
				return null;
			}

			FolderDto rootFolder = null;
			//FTP data source or data storage
			if(name.startsWith("FTP")){
				if(user==null || pass ==null){
					System.out.println("GET FILE LIST OF DSOURCE OR DSTORAGE - FTP "+typeOfObj+" does not have 'username' or/and 'password' in IS");
					return null;
				}
				String[] partsOfEndpoint=host.split("//"); 
				host=partsOfEndpoint[1]; //because in ftpConnector we do need the protocol

				ConnectionFTP connectionFTP = null;
				connectionFTP = new ConnectionFTP(host,specificFolder,user,pass);
				//String rootString = "ftp://USER:PASS@pcd4science3.cern.ch/";
				rootFolder = connectionFTP.browse();

				if(rootFolder==null){
					System.out.println("GET FILE LIST OF DSOURCE OR DSTORAGE: PROBLEM result(from FTP) =null\nERRORS:\n");
					for(String err : connectionFTP.getErrors())System.out.println(err);		
							connectionFTP.disconnect();
							return null;
				}

			//	connectionFTP.printFolder(rootFolder, 0);
				if(connectionFTP.getErrors().size()>0){
					System.out.println("GET FILE LIST OF DSOURCE OR DSTORAGE: ERRORS in connectionFTP:\n");
					for(String err : connectionFTP.getErrors()){
						System.out.println(err);		
					}
				}
				connectionFTP.disconnect();
			}
			else if(name.startsWith("HTTP") || name.startsWith("HTTPS")){
				ConnectionHTTP connectionHTTP = new ConnectionHTTP(host, specificFolder, user, pass);
				rootFolder = connectionHTTP.process();
				if(rootFolder==null){
					System.out.println("GET FILE LIST OF DSOURCE OR DSTORAGE: PROBLEM result(from HTTP/HTTPS) =null\nERRORS:\n");
					for(String err : connectionHTTP.getErrors())System.out.println(err);		
							connectionHTTP.disconnect();
							return null;
				}

		//		connectionHTTP.printFolder(rootFolder,0);	
				if(connectionHTTP!=null && connectionHTTP.getErrors().size()>0){
					System.out.println("GET FILE LIST OF DSOURCE OR DSTORAGE: ERRORS in connectionHTTP:\n");
					for(String err : connectionHTTP.getErrors()){
						System.out.println(err);
					}
				}
				connectionHTTP.disconnect();
			}
			else {
				// Other Protocols .... 
			}

			if(rootFolder==null){
				System.out.println("GET FILE LIST OF DSOURCE OR DSTORAGE: returned folder==null");			
				return null;
			}
			Gson gson = new Gson();
			String jsonString = gson.toJson(rootFolder);
		//	System.out.println("GET FILE LIST OF DSOURCE OR DSTORAGE: gson folder length= "+jsonString.length());

			return jsonString;	
		}catch(Exception e){e.printStackTrace();return null;}
	}

	public String getFileListOfMongoDB(String smServiceClassSource,String smServiceNameSource,String smOwnerSource,String smAccessTypeSource,String path,String scope){
		try{
			FolderDto rootFolder=null;

			ConnectionSMP connectionSMP = null;
			connectionSMP = new ConnectionSMP(smServiceClassSource,smServiceNameSource,smOwnerSource,smAccessTypeSource,scope,path);

			rootFolder = connectionSMP.browse();

			if(rootFolder==null){
				System.out.println("GET LIST OF MONGODBSOURCE: returned folder==null");			
				return null;
			}
			Gson gson = new Gson();
			String jsonString = gson.toJson(rootFolder);
		//	System.out.println("GET LIST OF MONGODBSOURCE: gson folder length= "+jsonString.length());

			return jsonString;
		}catch(Exception e){e.printStackTrace();return null;}
	}

	public void createNewFolderInMongoDB(String smServiceClassSource,String smServiceNameSource,String smOwnerSource,String smAccessTypeSource,String path,String scope){
		try{
			ConnectionSMP connectionSMP = null;
			connectionSMP = new ConnectionSMP(smServiceClassSource,smServiceNameSource,smOwnerSource,smAccessTypeSource,scope,"/");
			connectionSMP.storeNewFolder(path);
		}catch(Exception e){e.printStackTrace();return ;}
	}
	public void deleteFolderInMongoDB(String smServiceClassSource,String smServiceNameSource,String smOwnerSource,String smAccessTypeSource,String path,String scope){
		try{
			ConnectionSMP connectionSMP = null;
			connectionSMP = new ConnectionSMP(smServiceClassSource,smServiceNameSource,smOwnerSource,smAccessTypeSource,scope,"/");
			connectionSMP.deleteFolder(path);
		}catch(Exception e){e.printStackTrace();return ;}
	}
	/*
	 * getUserAndScope
	 * input: Nothing
	 * returns: String with the user name and the scope 
	 */
	public String getUserAndScopeAndRole(){
		try{
			WorkspaceWorker workspWorker = new WorkspaceWorker();	
			String returnedValue=workspWorker.getUserAndScopeAndRole(this.getThreadLocalRequest());

			//returning value .. example: nick--/gcube/devsec
			if(returnedValue==null)System.out.println("GET USER AND SCOPE AND ROLE: returnedValue==null");
			return returnedValue;
		}catch(Exception e){e.printStackTrace();return null;}
	}

	/*
	 * getWorkspace
	 * input: String with the user 
	 * returns: Json String with the Workspace object 
	 */
	public String getWorkspace(String username){
		try{
			if(username==null){	System.out.println("GET WORKSPACE: username==null");return null;}

			WorkspaceWorker workspWorker = new WorkspaceWorker();
			String serializedObject=workspWorker.getWorkspace(this.getThreadLocalRequest());

			if(serializedObject!=null)return serializedObject;
			else {
			//	System.out.println("GET WORKSPACE: serializedObject==null");
				return null;
			}
		}catch(Exception e){e.printStackTrace();return null;}
	}

	/*
	 * getWorkspaceFolder
	 * input: Json String with the Workspace obj
	 * input: String with the id of the folder if exist, in other case we take the root folder
	 * returns: Json String with the tree starting from the folder
	 */
	public String getWorkspaceFolder(String serializedWorkspaceInfo, String folderId, boolean needTheParent) throws IllegalArgumentException {
		try{
			Workspace workspace=null;
			WorkspaceWorker workerWS=new WorkspaceWorker();

			//CHANGED - we do not use serialized workspace anymore 
			//		if(serializedWorkspaceInfo==null){
			workspace = workerWS.getWorkspaceWithoutSerialization(this.getThreadLocalRequest());
			//		}
			//		else if(serializedWorkspaceInfo.compareTo("")==0){
			//			workspace = workerWS.getWorkspaceWithoutSerialization(this.getThreadLocalRequest());
			//		}
			//		else {
			//			XStream xstream = new XStream();
			//			WorkspaceInitializeInfo workspaceInfo=(WorkspaceInitializeInfo)xstream.fromXML(serializedWorkspaceInfo);
			//
			//			if(workspaceInfo==null){System.out.println("GET WORKSPACE FOLDER: workspaceInfo= null");return null;}
			//			workspace = workspaceInfo.getWorkspace();
			//		}

			if(workspace==null){System.out.println("GET WORKSPACE FOLDER: workspace= null");return null;}

			String workspaceWebDavLink=null;
			try {
				workspaceWebDavLink=workspace.getUrlWebDav();
			} catch (InternalErrorException e1) {
				e1.printStackTrace();
			}
			if(workspaceWebDavLink==null){System.out.println("GET WORKSPACE FOLDER: workspaceWebDavLink= null");return null;}


			WorkspaceFolder root=null;
			//String rootParent=null;
			if(folderId==null){
				root = workspace.getRoot();
			}
			else {
				try {
					root = (WorkspaceFolder) workspace.getItem(folderId);
					if(needTheParent)root=root.getParent(); // take the parent instead

				} catch (ItemNotFoundException e) {
					e.printStackTrace();
				}catch (InternalErrorException e) {
					e.printStackTrace();
				}
			}

			if(root==null){System.out.println("GET WORKSPACE FOLDER: root= null");return null;}

			WorkspaceWorker wsWorker = new WorkspaceWorker();
			FolderDto folder = null;
			try {
				folder = wsWorker.createTree(root, workspaceWebDavLink);
			} catch (InternalErrorException e) {
				e.printStackTrace();
			}		
			if(folder==null){System.out.println("GET WORKSPACE FOLDER: folder= null");return null;}
			else {
				//folder.setParentIdInWorkspace(rootParent);
			//	wsWorker.printFolder(folder,0);
				Gson gson2 = new Gson();
				String jsonString = gson2.toJson(folder);
		//		System.out.println("GET WORKSPACE FOLDER: gson folder length= "+jsonString.length());

				return jsonString;
			}
		}catch(Exception e){e.printStackTrace();return null;}
	}

	public String getAgentStatistics(String scope){
		try{
			SchedulerProxy schedulerProxy=new SchedulerProxy();
			String result = schedulerProxy.getAgentStatistics(scope);
			if(result==null)System.out.println("GET AGENT STATISTICS: result= null");
			//structure: agentIdOfIS--ongoing--failed--succeeded--canceled--total\n
			return result;
		}catch(Exception e){e.printStackTrace();return null;}
	}

}
