package org.gcube.datatransfer.portlets.user.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface SchedulerServiceAsync {
	void listFiles(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void getTransfers(String scope,String resourceName, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void getObjectsFromIS(String type, String scope,String resourceName, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void schedule(String obj, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void monitor(String scope, String resourceName, String transferId, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void getOutcomes(String scope, String resourceName, String transferId, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void cancel(String scope, String resourceName, String transferId, boolean force, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void getFileListOfDSourceOrDStorage(String type,String dataSourceId,String specificPath,  String scope,String resourceName,AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void getWorkspaceFolder(String jsonWorkspace, String folderId,boolean needTheParent, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	void getUserAndScopeAndRole(AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	void getWorkspace(String username, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	void getAgentStatistics(String scope,AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	void getFileListOfMongoDB(String smServiceClassSource,String smServiceNameSource,String smOwnerSource,String smAccessTypeSource,String path,String scope,AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void getFileListOfAgent(String path,String agentHostname,String agentPort,String scope, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void createNewFolderInMongoDB(String smServiceClassSource,String smServiceNameSource,String smOwnerSource,String smAccessTypeSource,String path,String scope, AsyncCallback<Void> callback)
			throws IllegalArgumentException;	
	void deleteFolderInMongoDB(String smServiceClassSource,String smServiceNameSource,String smOwnerSource,String smAccessTypeSource,String path,String scope, AsyncCallback<Void> callback)
			throws IllegalArgumentException;
	void getTreeSources(String agentHostname,String agentPort, String scope,String type, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void createNewTreeSource(String agentHostname,String agentPort, String scope, String sourceId, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void deleteTreeSource(String agentHostname,String agentPort, String scope,  String sourceId, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	
}
