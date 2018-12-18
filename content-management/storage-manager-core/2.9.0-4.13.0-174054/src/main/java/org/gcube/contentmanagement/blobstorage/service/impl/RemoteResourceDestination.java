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
	
	/**
	 * 
	 * @param remoteDestination it can be a remote path or an id
	 * @return
	 * @throws RemoteBackendException
	 */
	public String to(String remoteDestination) throws RemoteBackendException{
		logger.info("file gCube parameter before: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
		file = setGenericProperties(engine.getContext(), engine.owner, remoteDestination, "remote");
		file.setRemotePath(remoteDestination);
		file.setOwner(engine.owner);
		setMyFile(file);
		if((remoteDestination != null) &&(ObjectId.isValid(remoteDestination))){
			getMyFile().setRemoteResource(REMOTE_RESOURCE.ID);
				getMyFile().setId2(remoteDestination);
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
