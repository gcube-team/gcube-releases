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
import com.mongodb.MongoException;
import java.net.UnknownHostException;

/**
 * Implements a remove operation from the cluster: remove a file object
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class Remove extends Operation{
	/**
	 * Logger for this class
	 */
	final Logger logger=LoggerFactory.getLogger(Remove.class);
	public String file_separator = ServiceEngine.FILE_SEPARATOR; //System.getProperty("file.separator");

	public Remove(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType) {
		super(server,user,pwd, bucket, monitor, isChunk, backendType);
	}
	
	public String doIt(MyFile myFile) throws RemoteBackendException{
		TransportManagerFactory tmf= new TransportManagerFactory(server, user, password);
		TransportManager tm=tmf.getTransport(backendType);
		removeBucket(tm, bucket, myFile);
		if (logger.isDebugEnabled()) {
			logger.debug(" REMOVE " + bucket);
		}
		return null;
	}

	@Override
	public String initOperation(MyFile file, String remotePath,
		String author, String[] server, String rootArea, boolean replaceOption) {
		String[] dirs= remotePath.split(file_separator);
		if(logger.isDebugEnabled())
			logger.debug("remotePath: "+remotePath);
		String buck=null;
// in this case the remote path is really  a remote path and not  a objectId		
		if((dirs != null) && ((dirs.length >1) || ((dirs.length==1) && (dirs[0].length()<23)))){
			BucketCoding bc=new BucketCoding();
			buck=bc.bucketFileCoding(remotePath, rootArea);
			if(!OperationManager.CLIENT_TYPE.equalsIgnoreCase("mongo")){
				buck=buck.replaceAll(file_separator, BucketCoding.SEPARATOR);
			//remove directory bucket		
				DirectoryBucket dirBuc=new DirectoryBucket(server, user, password,  remotePath, author);
				dirBuc.removeKeysOnDirBucket(file, buck, rootArea, backendType);
				String bucketName=null;
			}
		}else{
		// is an object id	
			buck=remotePath;
		}
	
//		bucketName=new BucketCoding().bucketFileCoding(remotePath, author, rootArea);
		return bucket=buck;
	}

	/**
	 *  Remove a remote directory identifies by bucketName
	 * @param bucketName indicates the remote directory to remove
	 * @throws RemoteBackendException 
	 */
		public void removeBucket(TransportManager tm, String bucketName, MyFile resource) throws RemoteBackendException {
			if(logger.isDebugEnabled())
			logger.debug("removing file bucket: "+bucketName);
			try {
				tm.removeRemoteFile(bucket, resource);
			} catch (Exception e) {
				tm.close();
				logger.error("Problem in remove: "+bucket+": "+e.getMessage());
				throw new RemoteBackendException(" Error in remove operation ", e.getCause());			
			}
		}

	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		throw new IllegalArgumentException("Input/Output stream is not compatible with remove operation");
	}

}

	


