package org.gcube.contentmanagement.blobstorage.service.impl;

import org.bson.types.ObjectId;
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
		setMyFile(file);
		if((path != null) &&(ObjectId.isValid(path))){
			getMyFile().setRemoteResource(REMOTE_RESOURCE.ID);
				getMyFile().setId2(path);
		}else{
			getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
		}
//		setMyFile(file);
		engine.service.setResource(getMyFile());
		String bucketName=null;
		logger.info("file gCube parameter after: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
		bucketName=getRemoteObject(getMyFile(),engine.primaryBackend,engine.volatileBackend).toString();
		return bucketName;
	}

}
