package org.gcube.contentmanagement.blobstorage.service.operation;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.report.Report;
import org.gcube.contentmanagement.blobstorage.report.ReportConfig;
import org.gcube.contentmanagement.blobstorage.report.ReportFactory;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
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
//	private static final GCUBELog logger = new GCUBELog(OperationManager.class);
	final Logger logger=LoggerFactory.getLogger(OperationManager.class);
	private String[] server;
	private int dimension;
	private String operation;
	private MyFile resource;
	private boolean isChunk;
	private String bucketName;
	private String fileDest;
	private String backendType;
	private boolean isBase64;
//	private TerrastoreClient client;
	private String keyUnlock;
	private String user;
	private String password;
//COSTANT CLIENT FACTORY CLIENT	
	public static final String CLIENT_TYPE="mongo";
// COSTANTS  FOR THREAD MANAGEMENT	
	public static final int MIN_THREAD=1;
	public static final int MAX_THREAD=10;
// COSTANTS FOR CHUNK  MANAGEMENT	
	public static final int sogliaNumeroMassimo=400;
	public static final int sogliaNumeroMinimo=4;
// dimension is express in byte
	public static final int sogliaDimensioneMinima=1024*1024;
// dimension is express in byte
	public static final int sogliaDimensioneMassima= 4*1024*1024;
	
	
	public OperationManager(String[] server, String user, String password, String operation, MyFile myFile, String backendType){
		this.setServer(server);
		this.setUser(user);
		this.setPassword(password);
		this.setTypeOperation(operation);
		this.setResource(myFile);
		this.setTypeOperation(operation);
		this.backendType=backendType;
	}
	
	public Object startOperation(MyFile file, String remotePath, String author, String[] server, boolean chunkOpt, String rootArea, boolean replaceOption) throws Exception{
//		setUser(author);
		if (logger.isDebugEnabled()) {
			logger.debug("connection(boolean) - start");
		}
		logger.info("startOpertion getResource..getGcubeAccessType()= "+getResource().getGcubeAccessType()+" file..getGcubeAccessType() "+file.getGcubeAccessType());
		// creo il monitor 
	    Monitor monitor = new Monitor();
	    OperationFactory of=new OperationFactory(server, getUser(), getPassword(), getBucketName(), monitor, chunkOpt, getBackendType());
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
}
