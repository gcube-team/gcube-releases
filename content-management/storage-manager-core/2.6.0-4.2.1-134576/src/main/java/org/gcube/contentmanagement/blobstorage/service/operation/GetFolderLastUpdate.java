package org.gcube.contentmanagement.blobstorage.service.operation;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.DirectoryBucket;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetFolderLastUpdate extends Operation {

	/**
	 * Logger for this class
	 */
    final Logger logger=LoggerFactory.getLogger(GetSize.class);
	public String file_separator = ServiceEngine.FILE_SEPARATOR;//System.getProperty("file.separator");

	public GetFolderLastUpdate(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType);
	}
	
	public String doIt(MyFile myFile) throws RemoteBackendException{
	return null;
	}

	@Override
	public String initOperation(MyFile file, String remotePath,
		String author, String[] server, String rootArea, boolean replaceOption) {
		String[] dirs= remotePath.split(file_separator);
		if(logger.isDebugEnabled())
			logger.debug("remotePath: "+remotePath);
		String buck=null;
		BucketCoding bc=new BucketCoding();
		buck=bc.bucketFileCoding(remotePath, rootArea);
		if(!OperationManager.CLIENT_TYPE.equalsIgnoreCase("mongo")){
			buck=buck.replaceAll(file_separator, BucketCoding.SEPARATOR);
		//remove directory bucket		
			DirectoryBucket dirBuc=new DirectoryBucket(server,user, password, remotePath, author);
			dirBuc.removeKeysOnDirBucket(file, buck, rootArea, backendType);
			String bucketName=null;
		}
		return bucket=buck;
	}


	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		throw new IllegalArgumentException("Input/Output stream is not compatible with getSize operation");
	}


}
