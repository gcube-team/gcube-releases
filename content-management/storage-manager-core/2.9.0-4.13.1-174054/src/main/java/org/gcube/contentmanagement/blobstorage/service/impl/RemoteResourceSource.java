package org.gcube.contentmanagement.blobstorage.service.impl;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.LOCAL_RESOURCE;

/**
 * 
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class RemoteResourceSource extends Resource {

	public RemoteResourceSource(MyFile file, ServiceEngine engine) {
		super(file, engine);
	}
	
	/**
	 * 
	 * @param remoteIdentifier: it can be an id or a remote path
	 * @return
	 */
	public RemoteResourceDestination from(String remoteIdentifier){
		if(getMyFile() != null){
			getMyFile().setLocalPath(remoteIdentifier);
		}else{
			setMyFile(setGenericProperties("", "", remoteIdentifier, "local"));
			getMyFile().setLocalPath(remoteIdentifier);
		}
		if(ObjectId.isValid(remoteIdentifier)){
			getMyFile().setLocalResource(LOCAL_RESOURCE.ID);
				getMyFile().setId(remoteIdentifier);
		}else{
			getMyFile().setLocalResource(LOCAL_RESOURCE.PATH);
		}
		return new RemoteResourceDestination(file, engine);
	}

}
