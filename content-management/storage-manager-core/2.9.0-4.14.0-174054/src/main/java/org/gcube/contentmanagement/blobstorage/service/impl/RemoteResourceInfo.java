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
		getMyFile().setRemoteResource(REMOTE_RESOURCE.PATH);
		String info= executeOperation(path).toString();
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
		String info=executeOperation(id).toString();
		if(info!=null)
			return Long.parseLong(info);
		else
			return -1;
	}

}

