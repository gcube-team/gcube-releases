/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.service.operation;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public abstract class SoftCopy extends Operation {

	/**
	 * Logger for this class
	 */
    final Logger logger=LoggerFactory.getLogger(SoftCopy.class);
	private String sourcePath;
	private String destinationPath;
	private MyFile resource;
	
	
	public SoftCopy(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
	}
	
	public String initOperation(MyFile file, String remotePath,	String author, String[] server, String rootArea, boolean replaceOption) {
//			if(remotePath != null){
//				boolean isId=ObjectId.isValid(remotePath);
//				setResource(file);
//				if(!isId){
////					String[] dirs= remotePath.split(file_separator);
//					if(logger.isDebugEnabled())
//						logger.debug("remotePath: "+remotePath);
//					String buck=null;
//					buck = new BucketCoding().bucketFileCoding(remotePath, rootArea);
//					return bucket=buck;
//				}else{
//					return bucket=remotePath;
//				}
//			}return bucket=null;//else throw new RemoteBackendException("argument cannot be null");

		this.sourcePath=file.getLocalPath();
		this.destinationPath=remotePath;
		sourcePath = new BucketCoding().bucketFileCoding(file.getLocalPath(), rootArea);
		destinationPath = new BucketCoding().bucketFileCoding(remotePath, rootArea);
		setResource(file);
		return bucket=destinationPath;
		
	}
	
	public String doIt(MyFile myFile) throws RemoteBackendException{
		TransportManagerFactory tmf= new TransportManagerFactory(server, user, password);
		TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType(), dbNames, myFile.getWriteConcern(), myFile.getReadPreference());
		String id=null;
		try {
			id=tm.softCopy(this);
		} catch (UnknownHostException e) {
			tm.close();
			logger.error("Problem in copy from: "+sourcePath+" to: "+destinationPath+": "+e.getMessage());
			throw new RemoteBackendException(" Error in copy operation ", e.getCause());
		}
		return id;
	}
	

	@Override
	public String initOperation(MyFile resource, String remotePath, String author, String[] server, String rootArea) {
// For terrastore, the name of bucket is formed: path_____fileName_____author				
		this.sourcePath=resource.getLocalPath();
		this.destinationPath=resource.getRemotePath();
		sourcePath = new BucketCoding().bucketFileCoding(resource.getLocalPath(), rootArea);
		destinationPath = new BucketCoding().bucketFileCoding(resource.getRemotePath(), rootArea);
		setResource(resource);
		return bucket=destinationPath;
//		if(remotePath != null){
//			boolean isId=ObjectId.isValid(remotePath);
//			setResource(resource);
//			if(!isId){
////				String[] dirs= remotePath.split(file_separator);
//				if(logger.isDebugEnabled())
//					logger.debug("remotePath: "+remotePath);
//				String buck=null;
//				buck = new BucketCoding().bucketFileCoding(remotePath, rootArea);
//				return bucket=buck;
//			}else{
//				return bucket=remotePath;
//			}
//		}return bucket=null;//else throw new RemoteBackendException("argument cannot be null");
	}
	
	public abstract String execute(MongoIOManager mongoPrimaryInstance, MyFile resource, String sourcePath, String destinationPath) throws UnknownHostException;

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public MyFile getResource() {
		return resource;
	}

	public void setResource(MyFile resource) {
		this.resource = resource;
	}
	

	
}
