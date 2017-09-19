package org.gcube.contentmanagement.blobstorage.service.impl;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

/**
 * Manage operation results of String type 
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class RemoteResourceDestination extends Resource{

	public RemoteResourceDestination(MyFile file, ServiceEngine engine) {
		super(file, engine);
	}
	
	public String to(String path) throws RemoteBackendException{
		logger.info("file gCube parameter before: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
		file = setGenericProperties(engine.getContext(), engine.owner, path, "remote");
		file.setRemotePath(path);
		file.setOwner(engine.owner);
		getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
		setMyFile(file);
		engine.service.setResource(getMyFile());
		String bucketName=null;
		logger.info("file gCube parameter after: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
//		try {
//			if((file.getLocalPath() != null) && ((file.getRemotePath() != null))){
//				bucketName=(String)engine.service.startOperation(file, file.getRemotePath(), engine.owner, engine.primaryBackend, ServiceEngine.DEFAULT_CHUNK_OPTION, engine.getContext(), engine.isReplaceOption());
//			}else{
//				logger.error("parameters incompatible ");
//			}
//		} catch (Exception t) {
//			logger.error("get()", t.getCause());
//			t.printStackTrace();
//			throw new RemoteBackendException(" Error in "+engine.currentOperation+" operation: "+t.getMessage(), t.getCause());
//			
//		}
		bucketName=getRemoteObject(getMyFile(),engine.primaryBackend,engine.volatileBackend).toString();
		return bucketName;
	}

}
