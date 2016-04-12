package org.gcube.datatransfer.portlets.user.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("scheduler")
public interface SchedulerService extends RemoteService {
	String listFiles(String name) throws IllegalArgumentException;
	String getTransfers(String scope, String resourceName) throws IllegalArgumentException;
	String getObjectsFromIS(String type, String scope, String resourceName) throws IllegalArgumentException;

	String schedule(String obj) throws IllegalArgumentException;
	String monitor(String scope, String resourceName, String transferId) throws IllegalArgumentException;
	String getOutcomes(String scope, String resourceName, String transferId) throws IllegalArgumentException;
	String cancel(String scope, String resourceName, String transferId, boolean force) throws IllegalArgumentException;
	String getFileListOfDSourceOrDStorage(String type,String dataSourceId, String specificPath, String scope,String resourceName) throws IllegalArgumentException;
	String getWorkspaceFolder(String jsonWorkspace, String folderId, boolean needTheParent) throws IllegalArgumentException;
	String getUserAndScopeAndRole() throws IllegalArgumentException;
	String getWorkspace(String username) throws IllegalArgumentException;
	String getAgentStatistics(String scope) throws IllegalArgumentException;
	String getFileListOfMongoDB(String smServiceClassSource,String smServiceNameSource,String smOwnerSource,String smAccessTypeSource,String path,String scope)throws IllegalArgumentException;
	String getFileListOfAgent(String path,String agentHostname,String agentPort, String scope)throws IllegalArgumentException;
	void createNewFolderInMongoDB(String smServiceClassSource,String smServiceNameSource,String smOwnerSource,String smAccessTypeSource,String path,String scope)throws IllegalArgumentException;
	void deleteFolderInMongoDB(String smServiceClassSource,String smServiceNameSource,String smOwnerSource,String smAccessTypeSource,String path,String scope)throws IllegalArgumentException;
	String getTreeSources(String agentHostname,String agentPort, String scope,String type)throws IllegalArgumentException;
	String createNewTreeSource(String agentHostname,String agentPort, String scope, String sourceId)throws IllegalArgumentException;
	String deleteTreeSource(String agentHostname,String agentPort, String scope,  String sourceId)throws IllegalArgumentException;
	
}
