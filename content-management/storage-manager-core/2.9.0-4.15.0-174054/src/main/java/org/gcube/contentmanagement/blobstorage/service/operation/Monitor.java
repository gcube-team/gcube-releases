package org.gcube.contentmanagement.blobstorage.service.operation;

//import org.apache.log4j.Logger;
//import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.util.Vector; 
/**
 * A monitor class for the concurrent operations
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class Monitor {
	/**
	 * Logger for this class
	 */
//	private static final GCUBELog logger = new GCUBELog(Monitor.class); 
	final Logger logger=LoggerFactory.getLogger(Monitor.class); 
  // request queue 
  private Vector<MyFile> requestQueue = new Vector<MyFile>();   
  // fetch the first request in the queue
  public synchronized MyFile getRequest(){
		if (logger.isDebugEnabled()) {
			logger.debug("getRequest() - start");
		}
    while (requestQueue.size() == 0){ 
      try { 
        wait(10000); 
      } 
      catch (InterruptedException e){
				logger.error("getRequest()", e);
      } 
    } 
    MyFile myFile=requestQueue.remove(0);
    notifyAll();
	if (logger.isDebugEnabled()) {
		logger.debug("getRequest() - end");
	}
    return myFile;
  } 
  
  public synchronized MyFile getRequest(ChunkProducer producer){
		if (logger.isDebugEnabled()) {
			logger.debug("getRequest(ChunkProducer) - start");
		}
	    while (requestQueue.size() == 0){ 
	      try { 
	        wait(); 
	      } 
	      catch (InterruptedException e){
				logger.error("getRequest(ChunkProducer)", e);
	      } 
	    } 
	    MyFile myFile=requestQueue.remove(0);
	    notifyAll();
		if (logger.isDebugEnabled()) {
			logger.debug("getRequest(ChunkProducer) - end");
		}
	    return myFile;
	  } 

  // Accoda una nuova richiesta 
  public synchronized void putRequest(MyFile richiesta){
		if (logger.isDebugEnabled()) {
			logger.debug("putRequest(MyFile) - start");
			logger.debug("request in queue, queue size: "+requestQueue.size());  
		}
	
	while (requestQueue.size() > Costants.MAX_THREAD){ 
	      try { 
	        wait();
	      } 
	      catch (InterruptedException e){
				logger.error("putRequest(MyFile)", e);
 	      } 
	} 
	requestQueue.addElement(richiesta); 
    notifyAll(); 
	if (logger.isDebugEnabled()) {
		logger.debug("putRequest(MyFile) - end");
	}
  } 
}
