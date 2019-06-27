package org.gcube.contentmanagement.blobstorage.service.operation;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;

/**
 *  Implements a download operation from the cluster: download a file object
 * 
 *@author Roberto Cirillo (ISTI - CNR)
 */

public abstract class Download extends Operation{
	/**
	 * Logger for this class
	 */
//	private static final GCUBELog logger = new GCUBELog(Download.class);
	final Logger logger=LoggerFactory.getLogger(Download.class);
	protected String localPath;
	protected String remotePath;
	protected OutputStream os;
	protected MyFile resource;
	
	public Download(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
	}
	
	public String initOperation(MyFile file, String remotePath,
			String author, String[] server, String rootArea, boolean replaceOption) {
		this.localPath=file.getLocalPath();
		this.remotePath=remotePath;
		setResource(file);
		return getRemoteIdentifier(remotePath, rootArea);
	}
	
	public String doIt(MyFile myFile) throws RemoteBackendException{
		String id=null;
		if (logger.isDebugEnabled()) {
			logger.debug(" DOWNLOAD " + myFile.getRemotePath()
					+ " in bucket: " + getBucket());
		}
		try {
			id=get(this, myFile, false);

		} catch (Throwable e) {
			TransportManagerFactory tmf=new TransportManagerFactory(getServer(), getUser(), getPassword());
			TransportManager tm=tmf.getTransport(getBackendType(), myFile.getGcubeMemoryType(), getDbNames(), myFile.getWriteConcern(), myFile.getReadPreference());
			tm.close();
			logger.error("Problem in download from: "+myFile.getRemotePath()+": "+e.getMessage());
//			e.printStackTrace();
			throw new RemoteBackendException(" Problem in download operation ", e.getCause());
		}
		return id;
	}
	

	@Override
	public String initOperation(MyFile resource, String remotePath,
			String author, String[] server, String rootArea) {
//		DirectoryBucket dirBuc=new DirectoryBucket(server, getUser(), getPassword(),  remotePath, author);
// For terrastore, the name of bucket is formed: path_____fileName_____author				
		String bucketName=new BucketCoding().bucketFileCoding(remotePath, rootArea);
//		DirectoryEntity dirObject=null;
		this.os=resource.getOutputStream();
		setBucket(bucketName);
		return bucketName;
	}

	public abstract ObjectId execute(MongoIOManager mongoPrimaryInstance, MongoIOManager mongoSecondaryInstance) throws IOException;

	public MyFile getResource() {
		return resource;
	}

	public void setResource(MyFile resource) {
		this.resource = resource;
	}

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
	
}
