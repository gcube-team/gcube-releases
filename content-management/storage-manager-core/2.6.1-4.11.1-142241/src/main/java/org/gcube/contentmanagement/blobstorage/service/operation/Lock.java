package org.gcube.contentmanagement.blobstorage.service.operation;

import java.io.IOException;
import java.io.OutputStream;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a lock operation relative to a remote resource
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class Lock extends Operation {

	final Logger logger=LoggerFactory.getLogger(Download.class);
	private String localPath;
	private String remotePath;
	private OutputStream os;

	public Lock(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType) {
		// TODO Auto-generated constructor stub
			super(server, user,pwd, bucket, monitor, isChunk, backendType);
	}
	
	@Override
	public String doIt(MyFile myFile) throws RemoteBackendException {
		if (logger.isDebugEnabled()) {
			logger.debug(" DOWNLOAD " + myFile.getRemotePath()
					+ " in bucket: " + bucket);
		}
		String unlockKey=null;
		try {
		//aggiungere field per il lock del file	
			unlockKey=get(myFile, true);
		} catch (Exception e) {
			TransportManagerFactory tmf=new TransportManagerFactory(server, user, password);
			TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType());
			tm.close();
			throw new RemoteBackendException(" Error in lock operation ", e.getCause());
		}
		return unlockKey;
	}

	@Override
	public String initOperation(MyFile file, String remotePath,
			String author, String[] server, String rootArea,
			boolean replaceOption) {
		String bucketName=null;
		// create the directory bucket		
		if((remotePath.length()<23) || (remotePath.contains(ServiceEngine.FILE_SEPARATOR))){
			this.localPath=file.getLocalPath();
			this.remotePath=remotePath;
			bucketName = new BucketCoding().bucketFileCoding(remotePath, rootArea);
		}else{
			bucketName=remotePath;
		}
		return bucket=bucketName;

	}

	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		// TODO Auto-generated method stub
		return null;
	}

}
