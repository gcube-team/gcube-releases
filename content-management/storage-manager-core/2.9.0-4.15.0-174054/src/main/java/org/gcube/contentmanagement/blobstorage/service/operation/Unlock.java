package org.gcube.contentmanagement.blobstorage.service.operation;

import java.io.OutputStream;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.transport.backend.operation.UploadOperator;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;


/**
 * Implements the unlock operation for a locked remote resource 
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public abstract class Unlock extends Operation {

	private String keyUnlock;
	protected String localPath;
	protected String remotePath;
	protected OutputStream os;
	protected MyFile resource;
	protected Upload upload;
	
	public Unlock(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		// TODO Auto-generated constructor stub
			super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
		}
	
	@Override
	public String doIt(MyFile myFile) throws RemoteBackendException {
		if (logger.isDebugEnabled()) {
			logger.debug(" UPLOAD " + myFile.getLocalPath()
					+ " author: " + myFile.getOwner());
		}
		String objectId=null;
		try {
			Upload upload= new UploadOperator(getServer(), getUser(), getPassword(), getBucket(), getMonitor(), isChunk(), getBackendType(), getDbNames());
			//inserire parametro per il lock 
			objectId=put(upload, myFile, isChunk(), false, false, true);
		} catch (Exception e) {
			TransportManagerFactory tmf=new TransportManagerFactory(server, user, password);
			TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType(), dbNames, myFile.getWriteConcern(), myFile.getReadPreference());
			tm.close();
			throw new RemoteBackendException(" Error in unlock operation ", e.getCause());
		}
		return objectId;

	}

	@Override
	public String initOperation(MyFile file, String remotePath,
			String author, String[] server, String rootArea,
			boolean replaceOption) {
		String bucketName=null;
		// create the directory bucket		
		if((remotePath.length()<23) || (remotePath.contains(Costants.FILE_SEPARATOR))){
			// the name of bucket is formed: path_____fileName_____author				
			bucketName=new BucketCoding().bucketFileCoding(remotePath, rootArea);
		}else{
		//is an ObjectId	
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
	
	public abstract String execute(MongoIOManager mongoPrimaryInstance, MongoIOManager mongoSecondaryInstance, MyFile resource, String bucket, String key4unlock) throws Exception;

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

	public Upload getUpload() {
		return upload;
	}

	public void setUpload(Upload upload) {
		this.upload = upload;
	}

	public String getKeyUnlock() {
		return keyUnlock;
	}

	public void setKeyUnlock(String keyUnlock) {
		this.keyUnlock = keyUnlock;
	}
	
}
