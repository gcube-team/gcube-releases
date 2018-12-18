package org.gcube.contentmanagement.blobstorage.service.operation;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import terrastore.client.TerrastoreClient;

/**
 * This is the manager of the operation on file-object.
 * The number of threads in upload and the chunk threshold is determined in this class 
 * (TODO) build and send accounting report
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class OperationManager {
	/**
	 * Logger for this class
	 */
	final Logger logger=LoggerFactory.getLogger(OperationManager.class);
	private String[] server;
//	private int dimension;
	private String operation;
	private MyFile resource;
	private boolean isChunk;
	private String bucketName;
	private String fileDest;
	private String backendType;
	private boolean isBase64;
	private String user;
	private String password;
	private String[] dbNames;
	
	
	public OperationManager(String[] server, String user, String password, String operation, MyFile myFile, String backendType, String[] dbs){
		this.setServer(server);
		this.setUser(user);
		this.setPassword(password);
		this.setTypeOperation(operation);
		this.setResource(myFile);
		this.setTypeOperation(operation);
		this.setDbNames(dbs);
		this.backendType=backendType;
	}
	
	public Object startOperation(MyFile file, String remotePath, String author, String[] server, boolean chunkOpt, String rootArea, boolean replaceOption) throws RemoteBackendException{
//		setUser(author);
		if (logger.isDebugEnabled()) {
			logger.debug("connection(boolean) - start");
		}
		logger.info("startOpertion getResource..getGcubeAccessType()= "+getResource().getGcubeAccessType()+" file..getGcubeAccessType() "+file.getGcubeAccessType());
		// creo il monitor 
	    Monitor monitor = new Monitor();
	    OperationFactory of=new OperationFactory(server, getUser(), getPassword(), getBucketName(), monitor, chunkOpt, getBackendType(), getDbNames());
	    Operation op=of.getOperation(getTypeOperation());
	//start specific operation    
	    setBucketName(op.initOperation(file, remotePath, author, server, rootArea, replaceOption));
	    Object object=op.doIt(getResource());
 	    return object;
	}

	
	private String getBackendType() {
		return backendType;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getFileDest() {
		return fileDest;
	}

	public void setFileDest(String fileDest) {
		this.fileDest = fileDest;
	}
	
	public boolean isChunk() {
		return isChunk;
	}

	public void setChunk(boolean isChunk) {
		this.isChunk = isChunk;
	}
	
	public String[] getServer() {
		return server;
	}

	public void setServer(String[] server) {
		this.server = server;
	}
	
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String pwd) {
		this.password = pwd;
	}
	
	public String getTypeOperation() {
		return operation;
	}

	public void setTypeOperation(String operation) {
		this.operation = operation;
	}

	public MyFile getResource() {
		return resource;
	}

	public void setResource(MyFile resource) {
		this.resource = resource;
	}

	public boolean isBase64() {
		return isBase64;
	}

	public void setBase64(boolean isBase64) {
		this.isBase64 = isBase64;
	}

	public String[] getDbNames() {
		return dbNames;
	}

	public void setDbNames(String[] dbNames) {
		this.dbNames = dbNames;
	}
	
	
}
