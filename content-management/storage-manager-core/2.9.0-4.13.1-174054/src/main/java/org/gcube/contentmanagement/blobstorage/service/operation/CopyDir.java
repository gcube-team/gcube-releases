package org.gcube.contentmanagement.blobstorage.service.operation;

import java.net.UnknownHostException;
import java.util.List;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Implements the copy dir operation
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public abstract class CopyDir extends Operation{

	/**
	 * Logger for this class
	 */
	final Logger logger=LoggerFactory.getLogger(Download.class);
	private String sourcePath;
	private String destinationPath;
	private MyFile resource;
	
	public CopyDir(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
	}
	
	public String initOperation(MyFile file, String remotePath,
			String author, String[] server, String rootArea, boolean replaceOption) {
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
		List<String> ids=null;
		try {
//			ids=tm.copyDir(myFile, sourcePath, destinationPath);
			ids=tm.copyDir(this);
		} catch (UnknownHostException e) {
			tm.close();
			logger.error("Problem in copyDir from: "+sourcePath+" to: "+destinationPath+": "+e.getMessage());
			throw new RemoteBackendException(" Error in copyDir operation ", e.getCause());
		}
		return ids.toString();
	}
	

	@Override
	public String initOperation(MyFile resource, String remotePath,
			String author, String[] server, String rootArea) {
//		DirectoryBucket dirBuc=new DirectoryBucket(server, user, password,  remotePath, author);
// For terrastore, the name of bucket is formed: path_____fileName_____author				
//		String bucketName=new BucketCoding().bucketFileCoding(remotePath, rootArea);
		this.sourcePath=resource.getLocalPath();
		this.destinationPath=resource.getRemotePath();
		sourcePath = new BucketCoding().bucketFileCoding(resource.getLocalPath(), rootArea);
		destinationPath = new BucketCoding().bucketFileCoding(resource.getRemotePath(), rootArea);
		setResource(resource);
		return bucket=destinationPath;
	}
	
	public abstract List<String> execute(MongoIOManager mongoPrimaryInstance, MyFile resource, String sourcePath, String destinationPath) throws UnknownHostException;

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
