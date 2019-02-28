package org.gcube.contentmanagement.blobstorage.service.operation;

import java.io.OutputStream;
import java.net.UnknownHostException;

import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.DirectoryBucket;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.MongoIOManager;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Move  extends Operation{

	/**
	 * Logger for this class
	 */
//	private static final GCUBELog logger = new GCUBELog(Download.class);
	final Logger logger=LoggerFactory.getLogger(Download.class);
	protected String sourcePath;
	protected String destinationPath;
	protected MyFile resource;
	public Move(String[] server,  String user, String pwd, String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
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
		String id=null;
		try {
//			id=tm.move(myFile, sourcePath, destinationPath);
			id=tm.move(this);
		} catch (UnknownHostException e) {
			tm.close();
			logger.error("Problem in move from: "+sourcePath+" to: "+destinationPath+": "+e.getMessage());
			throw new RemoteBackendException(" Error in move operation ", e.getCause());
		}
		return id;
	}
	

	@Override
	public String initOperation(MyFile resource, String remotePath,
			String author, String[] server, String rootArea) {
		this.sourcePath=resource.getLocalPath();
		this.destinationPath=resource.getRemotePath();
		sourcePath = new BucketCoding().bucketFileCoding(resource.getLocalPath(), rootArea);
		destinationPath = new BucketCoding().bucketFileCoding(resource.getRemotePath(), rootArea);
		
		return bucket=destinationPath;
	}

	public abstract String execute(MongoIOManager mongoPrimaryInstance,  MemoryType memoryType, MyFile resource, String sourcePath, String destinationPath) throws UnknownHostException;

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
