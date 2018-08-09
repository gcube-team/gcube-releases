/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.service.impl;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.OperationDefinition.REMOTE_RESOURCE;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class RemoteResourceBoolean extends Resource{
	
	/**
	 * @param file
	 * @param engine
	 */
	public RemoteResourceBoolean(MyFile file, ServiceEngine engine) {
		super(file, engine);
		logger.info("file gCube parameter costructor: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
	}


	/**
	 * identify a remote resource by path (a file or a directory) 
	 * @param path the remote path
	 * @return remote resource id
	 * @throws RemoteBackendException if there are runtime exception from the remote backend
	 */
	public boolean RFile(String path) throws RemoteBackendException{
//		getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
//		logger.info("file gCube parameter before: "+file.getGcubeAccessType()+" "+file.getGcubeScope());
//		file = setGenericProperties(engine.getContext(), engine.owner, path, "remote");
//		file.setRemotePath(path);
//		file.setOwner(engine.owner);
//		setMyFile(file);
//		engine.service.setResource(getMyFile());
//		Object obj=getRemoteObject(getMyFile(),engine.primaryBackend,engine.volatileBackend);
//		Boolean value= new Boolean(obj.toString());
//		return value;
		return RFile(path, false);
	}

	
	/**
	 * identify a remote resource by path (a file or a directory) 
	 * @param path the remote path
	 * @return remote resource id
	 * @throws RemoteBackendException if there are runtime exception from the remote backend
	 */
		public boolean RFile(String path, boolean backendTypeReturned) throws RemoteBackendException{
			getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
			Object obj = executeOperation(path);
			Boolean value= new Boolean(obj.toString());
			return value;
		}



		
	/**
	 * identify a remote resource by object id
	 * @param id that identifies a remote resource
	 * @return remote resource id
	 * @throws RemoteBackendException if there are runtime exception from the remote backend
	 * @deprecated this method could be replace with RFile method 
	 */
		public boolean RFileById(String id) throws RemoteBackendException{
			getMyFile().setRemoteResource(REMOTE_RESOURCE.ID);
			Object obj = executeOperation(id);
			Boolean value= new Boolean(obj.toString());
			return value;
//			getMyFile().setOwner(engine.owner);
//			engine.service.setResource(getMyFile());
//			String idReturned=null;
//			getMyFile().setRemotePath(id);
//			Object obj=getRemoteObject(getMyFile(),engine.primaryBackend,engine.volatileBackend);
//			if(obj!=null)
//				idReturned=obj.toString();
//			return idReturned;
		}

		
	
}
