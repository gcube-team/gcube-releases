package org.gcube.contentmanagement.blobstorage.service.impl;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.LOCAL_RESOURCE;
/**
 * This class is used from methods that can have both a RemoteResource or a LocalResource
 * 
 * @author Roberto Cirillo	(ISTI-CNR)
 *
 */
public class AmbiguousResource extends RemoteResource {
	
	public AmbiguousResource(MyFile file, ServiceEngine engine) {
		super(file, engine);
	}
	
	/**
	 * define local resource
	 * @param path : local absolute path of resource
	 * @return remoteResource object
	 */
	public RemoteResource LFile(String path){
		if(getMyFile() != null){
			getMyFile().setLocalPath(path);
		}else{
			setMyFile(setGenericProperties("", "", path, "local"));
			getMyFile().setLocalPath(path);
		}
		getMyFile().setLocalResource(LOCAL_RESOURCE.PATH);
		return new RemoteResource(getMyFile(), getEngine());
	}

}
