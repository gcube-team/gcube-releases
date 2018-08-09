package org.gcube.contentmanagement.blobstorage.service.operation;

import java.io.OutputStream;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.transport.backend.operation.DownloadOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a lock operation relative to a remote resource
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public abstract class Lock extends Operation {

	final Logger logger=LoggerFactory.getLogger(Download.class);
	protected String localPath;
	protected String remotePath;
	protected OutputStream os;
	protected MyFile resource;
	protected Download download;

	public Lock(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		// TODO Auto-generated constructor stub
			super(server, user,pwd, bucket, monitor, isChunk, backendType, dbs);
	}
	
	@Override
	public String doIt(MyFile myFile) throws RemoteBackendException {
		if (logger.isDebugEnabled()) {
			logger.debug(" DOWNLOAD " + myFile.getRemotePath()
					+ " in bucket: " + getBucket());
		}
		String unlockKey=null;
		try {
		//aggiungere field per il lock del file	
			Download download = new DownloadOperator(getServer(), getUser(), getPassword(), getBucket(), getMonitor(), isChunk(), getBackendType(), getDbNames());
			unlockKey=get(download, myFile, true);
		} catch (Exception e) {
			TransportManagerFactory tmf=new TransportManagerFactory(getServer(), getUser(), getPassword());
			TransportManager tm=tmf.getTransport(getBackendType(), myFile.getGcubeMemoryType(), getDbNames(), myFile.getWriteConcern(), myFile.getReadPreference());
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
		setResource(file);
		// create the directory bucket		
		if((remotePath.length()<23) || (remotePath.contains(ServiceEngine.FILE_SEPARATOR))){
			this.localPath=file.getLocalPath();
			this.remotePath=remotePath;
			bucketName = new BucketCoding().bucketFileCoding(remotePath, rootArea);
		}else{
			bucketName=remotePath;
		}
		setBucket(bucketName);
		return bucketName;

	}

	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract String execute(MongoIOManager mongoPrimaryInstance, MongoIOManager mongoSecondaryInstance, MyFile resource, String serverLocation) throws Exception;

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public OutputStream getOs() {
		return os;
	}

	public void setOs(OutputStream os) {
		this.os = os;
	}

	public MyFile getResource() {
		return resource;
	}

	public void setResource(MyFile resource) {
		this.resource = resource;
	}

	public Download getDownload() {
		return download;
	}

	public void setDownload(Download download) {
		this.download = download;
	}  
	
	
}
