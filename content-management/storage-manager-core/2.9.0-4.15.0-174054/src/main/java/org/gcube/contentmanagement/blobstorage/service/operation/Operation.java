package org.gcube.contentmanagement.blobstorage.service.operation;

import org.bson.types.ObjectId;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.BucketCoding;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Define the utilities function for the sub classes operations 
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */

public abstract class Operation {
	/**
	 * Logger for this class
	 */
	final Logger logger=LoggerFactory.getLogger(Operation.class);

	String[] server;
	String user;
	private String owner;
	String password;
	String bucket;
	String[] dbNames;
	private Monitor monitor;
	private boolean isChunk;
	String backendType;

	public Operation(String[] server, String user, String pwd,  String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs){
		this.server=server;
		this.user=user;
		this.password=pwd;
		this.bucket=bucket;
		this.monitor=monitor;
		this.isChunk=isChunk;
		this.backendType=backendType;
		this.dbNames=dbs;
	}
 	
	protected int numOfThread(int totChunks) {
		if((totChunks> Costants.MIN_THREAD) &&(totChunks < Costants.MAX_THREAD)){
			int returnint = totChunks - 1;
			return returnint;
		}else if(totChunks > Costants.MAX_THREAD){
			return Costants.MAX_THREAD;
		}else{
			return 1;
		}
	}

	
	protected int getLengthCurrentChunk(long len, int i, int dimChunk) {
		int lengthCurrentChunk=0;
		if(((i+1)*dimChunk) <= len){
			lengthCurrentChunk=dimChunk;
		}else{
			lengthCurrentChunk=(int) (len - (i*dimChunk));
		}
		return lengthCurrentChunk;
	}

	
	protected int getNumberOfChunks(long len, long dimChunk) {
		if(len< dimChunk)
			return 1;
		else if((len%dimChunk)>0){
			long returnint = (len / dimChunk) + 1;
			return (int)returnint;
		}else{
			long returnint = (len / dimChunk);
			return (int)returnint;
		}
	}
		
	/**
	 * Upload operation 
	 * @param resource object that contains the resource coordinates
	 * @param isChunk if the file is in chunk
	 * @param isBase64 if is in base64 coding
	 * @param replaceOption if the file will be replaced
	 * @param isLock if the file is lock
	 * @return a String that identifies a file
	 * @throws Exception
	 */
	public  String put(Upload upload, MyFile resource, boolean isChunk, boolean isBase64, boolean replaceOption, boolean isLock) throws Exception{
		if (logger.isDebugEnabled()) {
			logger.debug("put(MyFile, boolean, boolean) - start");
		}
		long len=1;
		if(resource.getLocalPath()!=null)
			len=new File(resource.getLocalPath()).length();
		if(logger.isDebugEnabled()){
			logger.debug("file size: "+len);
		}
		long dimensionChunk=0;
		if(logger.isDebugEnabled())
		logger.debug("PUT is chukn? "+isChunk);
		if(isChunk){
			ChunkOptimization chunkOptimization=new ChunkOptimization(len);
			dimensionChunk=chunkOptimization.chunkCalculation();
		}else{
			if(len==0){
				dimensionChunk=1;
				len=1;
			}else{
				dimensionChunk=len;
			}
			
		}
		if (logger.isDebugEnabled()) {
			logger.debug("put(MyFile, boolean, boolean) - encode length: "
					+ len);
		}
// number of chunks calculation
		int totChunks=1;
		if(logger.isDebugEnabled())
			logger.debug("len File: "+len+" len chunk: "+dimensionChunk);
		totChunks=getNumberOfChunks(len, dimensionChunk);
		if (logger.isDebugEnabled()) {
			logger.debug("put(MyFile, boolean, boolean) - number of chunks: "
					+ totChunks);
		}
		int nThreads=1;
		if(totChunks>1){
			nThreads=numOfThread(totChunks);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("put(MyFile, boolean, boolean) - number of thread: "
					+ nThreads);
		}
		if(logger.isDebugEnabled())
			logger.debug("consumer have a bucket name: "+bucket);
		if(totChunks>1){
			if(logger.isDebugEnabled())
				logger.debug("THREAD POOL USED");
		    ChunkConsumer consumer= new ChunkConsumer(monitor, 1, server, user, password, dbNames, isChunk,  bucket, replaceOption);
		    Thread producer=new Thread(new ChunkProducer(monitor, resource, dimensionChunk, totChunks, nThreads, bucket, consumer));
		    producer.start();
		    if (logger.isDebugEnabled()) {
				logger.debug("put(MyFile, boolean, boolean) - end");
			}
		    producer.join();
		    return null;
		}else{
			if(logger.isDebugEnabled())
				logger.debug("NO THREAD POOL USED");
			TransportManagerFactory tmf=new TransportManagerFactory(server, user, password);
			TransportManager tm=tmf.getTransport(backendType, resource.getGcubeMemoryType(), dbNames, resource.getWriteConcern(), resource.getReadPreference());
			String objectId=tm.uploadManager(upload, resource, bucket, bucket+"_1", replaceOption);
			return objectId;
		}
	}

	/**
	 *  Download operation
	 * @param myFile object that contains the resource coordinates
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String get(Download download, MyFile myFile, boolean isLock) throws IOException, InterruptedException, Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("get(String) - start");
		}
		String unlocKey=null;
		TransportManagerFactory tmf=null;
//		if(server.length >1)
			tmf=new TransportManagerFactory(server, user, password);
//		else
//			tmf=new TransportManagerFactory(server, null, null);
		TransportManager tm=tmf.getTransport(backendType, myFile.getGcubeMemoryType(), dbNames, myFile.getWriteConcern(), myFile.getReadPreference());
		long start=System.currentTimeMillis();
		String path=myFile.getLocalPath();
		if(!Costants.CLIENT_TYPE.equalsIgnoreCase("mongo")){
			startPThreadChunk(download, myFile, tm, path);

		}else{
			unlocKey=tm.downloadManager(download, myFile, bucket, MyFile.class);
		}
		
			if((path!=null) && (new File(path).length()>0)){
				if (logger.isDebugEnabled()) {
					logger.debug("*** Time for downloading: "
							+ (System.currentTimeMillis() - start) + " ms "+"\n\n");
				}
			}
		return unlocKey;
    }

	/**
	 * @param myFile
	 * @param tm
	 * @param path
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	protected void startPThreadChunk(Download download,MyFile myFile, TransportManager tm,
			String path) throws FileNotFoundException, InterruptedException,
			IOException {
		ExecutorService executor = Executors.newFixedThreadPool (2);
		int j=0;
		MyFile value=null;
		
		if(logger.isInfoEnabled())
			logger.info("localPath: "+path+" bucket: "+bucket);
		OutputStream out =null;
		if((path !=null) && (!path.isEmpty()))
			out = new FileOutputStream(new File(path));
		do{
			value=null;
//		  	String currentKey=bucket+j;
			if (logger.isDebugEnabled()) {
				logger.debug("get(String) -");
			}
		  	try{
		  		value=(MyFile) tm.get(download);
		  	}catch(Exception e){
				if (logger.isDebugEnabled()) {
					logger.debug("get(String) - \n Trovate " + (j) + " key");
				}
		  		value=null;
		  	}
		  	if(value!=null){
				if (logger.isDebugEnabled()) {
					logger.debug("get(String) - write chunk , author: "
							+ value.getOwner());
				}
		  		monitor.putRequest(value);
		  		System.gc();
		  	  	executor.submit (new FileWriter(monitor, out));
		  	}
		  	j++;
		}while(value!=null);
		executor.shutdown ();
		executor.awaitTermination (Long.MAX_VALUE, TimeUnit.SECONDS);
		out.flush();
		out.close();
	}
	
	protected String getRemoteIdentifier(String remotePath, String rootArea) {
		String buck=null;
		boolean isId=ObjectId.isValid(remotePath);
		if(!isId){
			buck = new BucketCoding().bucketFileCoding(remotePath, rootArea);
			return bucket=buck;
		}else{
			return bucket=remotePath;
		}
	}
	
	protected String appendFileSeparator(String source) {
		if(source.lastIndexOf(Costants.FILE_SEPARATOR) != (source.length()-1))
			source=source+Costants.FILE_SEPARATOR;
		return source;
	}
	
	protected String extractParent(String source) {
		source=source.substring(0, source.length()-1);
		String parent=source.substring(source.lastIndexOf(Costants.FILE_SEPARATOR)+1);
		logger.debug("parent folder extracted: "+parent);
		return parent;
	}

	/**
	 * Do a operation
	 * @param myFile object that contains the resource coordinates
	 * @return a generic object that contains operation results
	 * @throws IllegalAccessException
	 */
	public abstract Object doIt(MyFile myFile) throws RemoteBackendException;
	
	/**
	 * init a operation
	 * @param file object that contains the resource coordinates
	 * @param remoteIdentifier remote path of the resource
	 * @param author file owner
	 * @param server server list
	 * @param rootArea remote root path
	 * @param replaceOption if true the file will be replaced
	 * @return a string that identifies the operation
	 */
	public abstract String initOperation(MyFile file, String remoteIdentifier, String author, String[] server, String rootArea, boolean replaceOption);
	
	
	/**
	 * init a operation
	 * @param resource object that contains the resource coordinates
	 * @param remoteIdentifier remote path of the resource
	 * @param author file owner
	 * @param server server list
	 * @param rootArea remote root path
	 * @return a string that identifies the operation
	 */
	public abstract String initOperation(MyFile resource, String remoteIdentifier, String author, String[] server, String rootArea);

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String[] getDbNames() {
		return dbNames;
	}

	public void setDbNames(String[] dbNames) {
		this.dbNames = dbNames;
	}

	public Monitor getMonitor() {
		return monitor;
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	public boolean isChunk() {
		return isChunk;
	}

	public void setChunk(boolean isChunk) {
		this.isChunk = isChunk;
	}

	public String getBackendType() {
		return backendType;
	}

	public void setBackendType(String backendType) {
		this.backendType = backendType;
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

	public void setUser(String user) {
		this.user = user;
	}
	
	
	
}