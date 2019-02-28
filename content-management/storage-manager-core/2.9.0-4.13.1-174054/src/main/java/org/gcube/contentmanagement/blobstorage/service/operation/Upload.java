package org.gcube.contentmanagement.blobstorage.service.operation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a upload operation from the cluster: upload a file object
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */

public abstract class Upload extends Operation {
	/**
	 * Logger for this class
	 */
//	private static final GCUBELog logger = new GCUBELog(Upload.class);
	final Logger logger=LoggerFactory.getLogger(Upload.class);
	protected InputStream is;
	private boolean replaceOption;
	protected String localPath;
	protected String remotePath;
	protected OutputStream os;
	protected MyFile resource;

	public Upload(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk, String bck, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, bck, dbs);
	}

	
	
	public String doIt(MyFile myFile) throws RemoteBackendException{
		if (logger.isDebugEnabled()) {
			logger.debug(" UPLOAD " + myFile.getLocalPath()
					+ " author: " + myFile.getOwner());
		}
		String objectId=null;
		try {
			objectId=put(this, myFile, isChunk(), false, replaceOption, false);
		} catch (Throwable e) {
			TransportManagerFactory tmf=new TransportManagerFactory(server, user, password);
			TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType(), dbNames, myFile.getWriteConcern(), myFile.getReadPreference());
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
		setResource(file);
//patch id: check if remotePath is not an id		
		if(remotePath.contains(Costants.FILE_SEPARATOR)){
			// the name of bucket is formed: path_____fileName_____author				
			String bucketName=new BucketCoding().bucketFileCoding(remotePath, rootArea);
			return bucket=bucketName;
		}else{
			return bucket=remotePath;
		}
	}



	@Override
	public String initOperation(MyFile resource, String remotePath,
			String author, String[] server, String rootArea) {
		// the name of bucket is formed: path_____fileName_____author				
		String bucketName=new BucketCoding().bucketFileCoding(remotePath, rootArea);
		setResource(resource);
		this.is=resource.getInputStream();
		return bucket=bucketName;
	}


	public abstract String execute(MongoIOManager mongoPrimaryInstance, MongoIOManager mongoSecondaryInstance, MyFile resource, String bucket, boolean replace) throws IOException;

	public InputStream getIs() {
		return is;
	}



	public void setIs(InputStream is) {
		this.is = is;
	}



	public boolean isReplaceOption() {
		return replaceOption;
	}



	public void setReplaceOption(boolean replaceOption) {
		this.replaceOption = replaceOption;
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



	public MyFile getResource() {
		return resource;
	}



	public void setResource(MyFile resource) {
		this.resource = resource;
	}
	
	
}
