package org.gcube.contentmanagement.blobstorage.service.operation;

import java.io.IOException;
import java.io.OutputStream;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadAndLock extends Operation {

	final Logger logger=LoggerFactory.getLogger(Download.class);
	private String localPath;
	private String remotePath;
	private OutputStream os;
/**
 * @deprecated
 * @param server
 * @param bucket
 * @param monitor
 * @param isChunk
 * 
 */
	public DownloadAndLock(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType) {
		// TODO Auto-generated constructor stub
			super(server, user, pwd, bucket, monitor, isChunk, backendType);
	}
	
	@Override
	public String doIt(MyFile myFile) throws RemoteBackendException {
		if (logger.isDebugEnabled()) {
			logger.debug(" DOWNLOAD " + myFile.getRemotePath()
					+ " in bucket: " + bucket);
		}
		try {
		//TODO add field for file lock
				get(myFile, true);
		} catch (Exception e) {
			TransportManagerFactory tmf=new TransportManagerFactory(server, user, password);
			TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType());
			tm.close();
			throw new RemoteBackendException(" Error in downloadAndLock operation ", e.getCause());
		}
		return null;
	}

	@Override
	public String initOperation(MyFile file, String RemotePath,
			String author, String[] server, String rootArea,
			boolean replaceOption) {
		this.localPath=file.getLocalPath();
		this.remotePath=remotePath;
		return getRemoteIdentifier(remotePath, rootArea);
	}

	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		// TODO Auto-generated method stub
		return null;
	}

}
