package org.gcube.contentmanagement.blobstorage.service.impl;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;


/**
 * Unlike the RemoteResource class, return informations to the client like a ttl or a size  
 * This class is used for the operations on TTL
 * @author rcirillo
 *
 */
public class RemoteResourceInfo extends Resource{

	// parameters for GCube instance

	private String serviceName;
	private String ownerGcube;
	private String gcubeScope;
	private String gcubeAccessType;
	private String gcubeMemoryType;

	public RemoteResourceInfo(MyFile file, ServiceEngine engine) {
		super(file, engine);
	}

	/**
	 * identify a remote resource by path (a file or a directory) 
	 * @param path the remote path
	 * @return a long object to remote resource ex: the size of the resource
	 * @throws RemoteBackendException  if there are runtime exception from the remote backend
	 */

	public long RFile(String path) throws RemoteBackendException{
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
		String info= getRemoteObject(getMyFile(),engine.primaryBackend,engine.volatileBackend).toString();
		if(info!=null)
			return Long.parseLong(info);
		else
			return -1;
	}

	/**
	 * identify a remote resource by object id
	 * @param id identifies a remote file
	 * @return a long object to remote resource ex: the size of the resource
	 * @throws RemoteBackendException  if there are runtime exception from the remote backend
	 */
	public long RFileById(String id) throws RemoteBackendException{
		getMyFile().setRemoteResource(REMOTE_RESOURCE.ID);
		engine.service.setResource(getMyFile());
//		Object info=null;
//		try {
//			if(engine.getCurrentOperation().equalsIgnoreCase("download"))
//				info=engine.service.startOperation(getMyFile(), id, engine.owner, engine.primaryBackend, ServiceEngine.DEFAULT_CHUNK_OPTION, "", engine.isReplaceOption());
//			else
//				throw new IllegalArgumentException("Input Parameters incompatible");
//		} catch (Throwable t) {
//			logger.error("get()", t.getCause());
//			throw new RemoteBackendException(" Error in "+engine.currentOperation+" operation: "+t.getMessage(), t.getCause());
//		}
//		if(info!=null)
//			return Long.parseLong(info.toString());
//		else
//			return -1;
		String info= getRemoteObject(getMyFile(),engine.primaryBackend,engine.volatileBackend).toString();
		if(info!=null)
			return Long.parseLong(info);
		else
			return -1;
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

