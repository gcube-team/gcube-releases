package org.gcube.contentmanagement.blobstorage.service.operation;



import java.io.File;
import java.io.InputStream;

//import org.apache.log4j.Logger;
//import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.DirectoryBucket;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.DirectoryEntity;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a upload operation from the cluster: upload a file object
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */

public class Upload extends Operation {
	/**
	 * Logger for this class
	 */
//	private static final GCUBELog logger = new GCUBELog(Upload.class);
	final Logger logger=LoggerFactory.getLogger(Upload.class);
	private InputStream is;
	private boolean replaceOption;

	public Upload(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk, String bck) {
		super(server, user, pwd, bucket, monitor, isChunk, bck);
	}

	
	
	public String doIt(MyFile myFile) throws RemoteBackendException{
		if (logger.isDebugEnabled()) {
			logger.debug(" UPLOAD " + myFile.getLocalPath()
					+ " author: " + myFile.getOwner());
		}
		String objectId=null;
		try {
			objectId=put(myFile, isChunk, false, replaceOption, false);
		} catch (Throwable e) {
			TransportManagerFactory tmf=new TransportManagerFactory(server, user, password);
			TransportManager tm=tmf.getTransport(backendType);
			tm.close();
			logger.error("Problem in upload from: "+myFile.getLocalPath()+": "+e.getMessage());
			throw new RemoteBackendException(" Error in upload operation ", e.getCause());
		}
		return objectId;
	}
	



	@Override
 public String initOperation(MyFile file, String remotePath, String author, String[] server, String rootArea, boolean replaceOption) {
		// set replace option
		this.replaceOption=replaceOption;
//patch id: check if remotePath is not an id		
		if(remotePath.contains(ServiceEngine.FILE_SEPARATOR)){
			// create the directory bucket		
			DirectoryBucket dirBuc=new DirectoryBucket(server, user, password,  remotePath, author);
			// the name of bucket is formed: path_____fileName_____author				
			String bucketName=new BucketCoding().bucketFileCoding(remotePath, rootArea);
			DirectoryEntity dirObject=null;
			return bucket=bucketName;
		}else{
			return bucket=remotePath;
		}
	}



	@Override
	public String initOperation(MyFile resource, String remotePath,
			String author, String[] server, String rootArea) {
		// create the directory bucket		
		DirectoryBucket dirBuc=new DirectoryBucket(server, user, password,  remotePath, author);
		// the name of bucket is formed: path_____fileName_____author				
		String bucketName=new BucketCoding().bucketFileCoding(remotePath, rootArea);
		DirectoryEntity dirObject=null;
		this.is=resource.getInputStream();
		return bucket=bucketName;
	}
}
