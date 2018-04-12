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

/**
 * Implements a getTTL operation for a resource locked in the remote system: return the TTL left
 * @author Roberto Cirillo (ISTI - CNR)
 */

public class GetTTL extends Operation {

	final Logger logger=LoggerFactory.getLogger(Download.class);
	private String localPath;
	private String remotePath;
	private OutputStream os;

	public GetTTL(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType) {
		// TODO Auto-generated constructor stub
			super(server, user, pwd, bucket, monitor, isChunk, backendType);
	}
	
	@Override
	public String doIt(MyFile myFile) throws RemoteBackendException {
		if (logger.isDebugEnabled()) {
			logger.debug(" DOWNLOAD " + myFile.getRemotePath()
					+ " in bucket: " + bucket);
		}
		long currentTTL=-1;
		TransportManager tm=null;
		try {
		//aggiungere field per il lock del file	
			TransportManagerFactory tmf=new TransportManagerFactory(server, user, password);
			tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType());
			currentTTL=tm.getTTL(bucket);
		} catch (Exception e) {
			tm.close();
			throw new RemoteBackendException(" Error in getTTL operation ", e.getCause());
		}
		return currentTTL+"";
	}

	@Override
	public String initOperation(MyFile file, String remotePath,
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

