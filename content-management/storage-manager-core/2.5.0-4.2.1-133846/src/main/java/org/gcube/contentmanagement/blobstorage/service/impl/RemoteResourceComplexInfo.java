package org.gcube.contentmanagement.blobstorage.service.impl;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

/**
 * Manage operations that return a structured object
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class RemoteResourceComplexInfo  extends Resource{


	public RemoteResourceComplexInfo(MyFile file, ServiceEngine engine) {
		super(file, engine);
	}
	
	/**
	 * identify a remote resource by path (a file or a directory) 
	 * @param path the remote path
	 * @return a long object to remote resource ex: the size of the resource
	 * @throws RemoteBackendException  if there are runtime exception from the remote backend
	 */

	public MyFile  RFile(String path) throws RemoteBackendException{
		setMyFile(setGenericProperties(engine.getContext(), engine.owner, path, "remote"));
		getMyFile().setRemotePath(path);
		getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
		engine.service.setResource(getMyFile());
//		try {
//			if(((file.getInputStream() != null) || (file.getOutputStream()!=null)) || ((file.getLocalPath() != null) || (file.getRemotePath() != null)))
//				engine.service.startOperation(file,file.getRemotePath(), engine.owner, engine.primaryBackend, ServiceEngine.DEFAULT_CHUNK_OPTION, engine.getContext(), engine.isReplaceOption());
//			else{
//				logger.error("parameters incompatible ");
//			}
//
//		} catch (Throwable t) {
//			logger.error("get()", t.getCause());
//			throw new RemoteBackendException(" Error in "+engine.currentOperation+" operation: "+t.getMessage(), t.getCause());
//		}
		getRemoteObject(getMyFile(),engine.primaryBackend,engine.volatileBackend);
		return getMyFile();
	}

}
