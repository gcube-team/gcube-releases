package org.gcube.contentmanagement.blobstorage.service.operation;

import java.io.OutputStream;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a Renew TTL operation for a locked remote resource
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class RenewTTL extends Operation {

	final Logger logger=LoggerFactory.getLogger(Download.class);
	private String localPath;
	private String remotePath;
	private OutputStream os;

	public RenewTTL (String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		// TODO Auto-generated constructor stub
			super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
	}
	
	@Override
	public String doIt(MyFile myFile) throws RemoteBackendException {
		TransportManagerFactory tmf= new TransportManagerFactory(server, user, password);
		TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType(), dbNames, myFile.getWriteConcern(), myFile.getReadPreference());
		long ttl=-1;
		try {
			myFile.setRemotePath(bucket);
			ttl = tm.renewTTL(myFile);
		} catch (Throwable e) {
			tm.close();
			throw new RemoteBackendException(" Error in renew TTL operation ", e.getCause());		
		}
		return ttl+"";
	}

	@Override
	public String initOperation(MyFile file, String remotePath,
			String author, String[] server, String rootArea,
			boolean replaceOption) {
		this.localPath=file.getLocalPath();
		this.remotePath=remotePath;
		String bucketName = new BucketCoding().bucketFileCoding(remotePath, rootArea);
		return bucket=bucketName;

	}

	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		// TODO Auto-generated method stub
		return null;
	}

}
