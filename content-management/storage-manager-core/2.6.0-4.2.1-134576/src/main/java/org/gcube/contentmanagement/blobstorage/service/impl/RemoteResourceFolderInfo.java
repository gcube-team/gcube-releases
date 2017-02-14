package org.gcube.contentmanagement.blobstorage.service.impl;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;


/**
 * Manage folder operation result of String type
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class RemoteResourceFolderInfo extends Resource {

	private String serviceName;
	private String ownerGcube;
	private String gcubeScope;
	private String gcubeAccessType;
	private String gcubeMemoryType;

	public RemoteResourceFolderInfo(MyFile file, ServiceEngine engine) {
		super(file, engine);
	}

	/**
	 * identify a remote resource by path (a file or a directory) 
	 * @param path the remote path
	 * @return a long object to remote resource ex: the size of the resource
	 * @throws RemoteBackendException  if there are runtime exception from the remote backend
	 */

	public String RDir(String path) throws RemoteBackendException{
		file = setGenericProperties(engine.getContext(), engine.owner, path, "remote");
		file.setRemotePath(path);
		file.setOwner(engine.owner);
		getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
		setMyFile(file);
		engine.service.setResource(getMyFile());
//		Object info=null;
//		try {
//			if(((file.getInputStream() != null) || (file.getOutputStream()!=null)) || ((file.getLocalPath() != null) || (file.getRemotePath() != null)))
//				info=(String)engine.service.startOperation(file,file.getRemotePath(), engine.owner, engine.primaryBackend, ServiceEngine.DEFAULT_CHUNK_OPTION, engine.getContext(), engine.isReplaceOption());
//			else{
//				logger.error("parameters incompatible ");
//			}
//
//		} catch (Throwable t) {
//			logger.error("get()", t.getCause());
//			throw new RemoteBackendException(" Error in "+engine.currentOperation+" operation: "+t.getMessage(), t.getCause());
//		}
//		if(info!=null)
//			return info.toString();
//		else
//			return null;
		return getRemoteObject(getMyFile(),engine.primaryBackend,engine.volatileBackend).toString();
	}

	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getOwnerGcube() {
		return ownerGcube;
	}

	public void setOwnerGcube(String ownerGcube) {
		this.ownerGcube = ownerGcube;
	}

	public String getGcubeScope() {
		return gcubeScope;
	}

	public void setGcubeScope(String gcubeScope) {
		this.gcubeScope = gcubeScope;
	}

	public String getGcubeAccessType() {
		return gcubeAccessType;
	}

	public void setGcubeAccessType(String gcubeAccessType) {
		this.gcubeAccessType = gcubeAccessType;
	}

	
	public String getGcubeMemoryType() {
		return gcubeMemoryType;
	}

	public void setGcubeMemoryType(String gcubeMemoryType) {
		this.gcubeMemoryType = gcubeMemoryType;
	}

}
