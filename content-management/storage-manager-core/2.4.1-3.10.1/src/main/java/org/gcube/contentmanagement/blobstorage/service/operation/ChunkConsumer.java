package org.gcube.contentmanagement.blobstorage.service.operation;

import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import terrastore.client.KeyOperation;

/**
 * Upload the chunks in a concurrent safe mode is used only for terrastore
 * @author rcirillo
 *
 */
public class ChunkConsumer implements Runnable {
	/**
	 * Logger for this class
	 */
	  final Logger logger = LoggerFactory.getLogger(ChunkConsumer.class);
	  private Monitor monitor; 
	  private int id; 
	  private String[] server;
	  private static String bucketName;
	  public static ThreadLocal<TransportManager> client=new ThreadLocal<TransportManager>();
	  public static ThreadLocal<MyFile> resource=new ThreadLocal<MyFile>();
	  private boolean replaceOpt;
	  Thread producer;

	  public  void run(){
		if (logger.isDebugEnabled()) {
			logger.debug("run() - start");
		}
	    MyFile request = null;
	    synchronized (ChunkConsumer.class) {
	    	request=monitor.getRequest();
	    	resource.set(request);
		}
	    // ... actions for manage the requests ... 
		connection(resource.get());
		if (logger.isDebugEnabled()) {
			logger.debug("run() - end");
		}
	  }

	private void connection(MyFile richiesta) {
		if (logger.isDebugEnabled()) {
			logger.debug("connection(MyFile) - start");
		}
		try{
			if (logger.isDebugEnabled()) {
				logger.debug("connection(MyFile) - request fetched: "
						+ resource.get().getKey()
						+ " current Thread: "
						+ Thread.currentThread());
			}
			putInTerrastore(resource.get());
		}catch(Exception e){
			logger.warn("connection(MyFile)- upload"+ e.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug("connection(MyFile) - retry PUT");
			}
			connection(resource.get());
		}
		if (logger.isDebugEnabled()) {
			logger.debug("connection(MyFile) - end");
		}
	} 
	  
	  public ChunkConsumer(Monitor monitor, int id, String[] server, String bucket, boolean replaceOption){ 
	    this.monitor = monitor; 
	    this.id = id; 
	    this.server=server;
	    bucketName=bucket;
	    this.replaceOpt=replaceOption;
	  }

	private String[] randomizeServer(String[] server) {
		int len=server.length;
		if(logger.isDebugEnabled())
			logger.debug("array server length: "+len);
	    int n = (int)(Math.random()*10);
	    if(logger.isDebugEnabled())
	    	logger.debug("random number: "+n);
	    int start=0;
	    if(n>0){
	    	start=len%n;
	    	if(start>0)
    		start--;
	    	if(logger.isDebugEnabled())
	    		logger.debug("start index: "+start);
	    	String temp=server[0];
	    	server[0]=server[start];
	    	server[start]=temp;
	    }
	    if(logger.isDebugEnabled())
	    	logger.debug("Server 0: "+server[0]);
	    return server;
	} 
	  
	private void putInTerrastore(MyFile myFile) {
		if (logger.isDebugEnabled()) {
			logger.debug("putInTerrastore(MyFile) - start");
		}
		long start=0;
		if(client.get()==null){
			start=System.currentTimeMillis();
			synchronized(ChunkConsumer.class){
				String [] randomServer=randomizeServer(server);
				TransportManagerFactory tmf=new TransportManagerFactory(randomServer, null, null);
				client.set(tmf.getTransport(OperationManager.CLIENT_TYPE));
			}
			if(logger.isDebugEnabled()){
				logger.debug("waiting time for upload: "
						+ (System.currentTimeMillis() - start) + " su: "
						+ resource.get().getKey());
			}
		}
		start=System.currentTimeMillis();
		try{
			client.get().put(resource.get(), bucketName, resource.get().getKey(), replaceOpt);
		}catch(Exception e){
			logger.error("ERROR IN CLUSTER CONNECTION ", e);
			monitor.putRequest(resource.get());
		}
		if(logger.isDebugEnabled()){
			logger.debug("Time for upload: "
					+ (System.currentTimeMillis() - start) + " on: "
					+ resource.get().getKey());
		}		
		if (logger.isDebugEnabled()) {
			logger.debug("putInTerrastore(MyFile) - end");
		}
	}	
} 
