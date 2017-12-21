package org.gcube.contentmanagement.blobstorage.service.operation;

//import org.apache.log4j.Logger;
//import org.gcube.common.core.utils.logging.GCUBELog;
import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.DirectoryBucket;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.DirectoryEntity;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.OutputStream;

/**
 *  Implements a download operation from the cluster: download a file object
 * 
 *@author Roberto Cirillo (ISTI - CNR)
 */

public class Download extends Operation{
	/**
	 * Logger for this class
	 */
//	private static final GCUBELog logger = new GCUBELog(Download.class);
	final Logger logger=LoggerFactory.getLogger(Download.class);
	private String localPath;
	private String remotePath;
	private OutputStream os;
	public Download(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType);
	}
	
	public String initOperation(MyFile file, String remotePath,
			String author, String[] server, String rootArea, boolean replaceOption) {
		this.localPath=file.getLocalPath();
		this.remotePath=remotePath;
		return getRemoteIdentifier(remotePath, rootArea);
	}
	
	public String doIt(MyFile myFile) throws RemoteBackendException{
		String id=null;
		if (logger.isDebugEnabled()) {
			logger.debug(" DOWNLOAD " + myFile.getRemotePath()
					+ " in bucket: " + bucket);
		}
		try {
			id=get(myFile, false);
		} catch (Throwable e) {
			TransportManagerFactory tmf=new TransportManagerFactory(server, user, password);
			TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType());
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
		DirectoryBucket dirBuc=new DirectoryBucket(server, user, password,  remotePath, author);
// For terrastore, the name of bucket is formed: path_____fileName_____author				
		String bucketName=new BucketCoding().bucketFileCoding(remotePath, rootArea);
		DirectoryEntity dirObject=null;
		this.os=resource.getOutputStream();
		return bucket=bucketName;
	}


}
