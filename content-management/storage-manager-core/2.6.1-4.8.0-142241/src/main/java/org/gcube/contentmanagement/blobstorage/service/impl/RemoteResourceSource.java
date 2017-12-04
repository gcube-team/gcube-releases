package org.gcube.contentmanagement.blobstorage.service.impl;

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
	
	public RemoteResourceDestination from(String path){
		if(getMyFile() != null){
			getMyFile().setLocalPath(path);
		}else{
			setMyFile(setGenericProperties("", "", path, "local"));
			getMyFile().setLocalPath(path);
		}
		getMyFile().setLocalResource(LOCAL_RESOURCE.PATH);
		return new RemoteResourceDestination(file, engine);
	}

}
