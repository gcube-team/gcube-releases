package org.gcube.contentmanagement.blobstorage.service.operation;

//import org.apache.log4j.Logger;
//import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A thread that write the chunk in a output stream specified
 *
 *@author Roberto Cirillo (ISTI - CNR)
 *
 */

public class FileWriter extends Thread{
	/**
	 * Logger for this class
	 */
//	private static final GCUBELog logger = new GCUBELog(FileWriter.class); 
	 final Logger logger=LoggerFactory.getLogger(FileWriter.class);   
	  private Monitor monitor; 
	  private int id; 
//	  private MyFile myFile;
//	  private byte[] encode;
//	  private int offset;
//	  private static int len=0;
	  private OutputStream out;
//	  private String path;
	  private byte[] full;
	  
	  
	  public synchronized void run(){
		if (logger.isDebugEnabled()) {
			logger.debug("run() - start");
		}
	      MyFile request = monitor.getRequest();
	      synchronized (FileWriter.class) {
	    	  if(logger.isDebugEnabled()){
		    	  logger.debug("recover request: "+request.getKey()+" length: "+request.getContent().length);  
		      }
			try {
				decodeByte2File(request.getContent());
				out.flush();
			} catch (Exception e) {
				logger.error("run()", e);
			}
		 }
		if (logger.isDebugEnabled()) {
			logger.debug("run() - end");
		}
	  } 

	  public FileWriter(Monitor monitor, OutputStream out, byte[] fullEncode){
		  this.monitor=monitor;
		  this.out=out;  
		  this.full=fullEncode;
	  }
	  
	  public FileWriter(Monitor monitor, OutputStream out){ 
	    this.monitor = monitor; 
	    this.out=out;   
	  } 
	  
	  public FileWriter(Monitor monitor, int id){ 
		    this.monitor = monitor; 
		    this.id = id; 
		  } 
	  
	  public void decodeByte2File(byte[] encode, int offset, int len){
			try {
				out.write(encode, offset, len);
				if(logger.isDebugEnabled())
					logger.debug("write from pos:"+offset+" to pos: "+len);
			} catch (IOException e) {
				logger.error("decodeByte2File(byte[], int, int)", e);
			}
			if(logger.isDebugEnabled())
				logger.debug("New file created!");
		}
	  
    public void decodeByte2File(byte[] encode){
		if (logger.isDebugEnabled()) {
			logger.debug("decodeByte2File(byte[]) - start");
			logger.debug("encode.length: "+encode.length);
		}
		try {
			out.write(encode);
		} catch (Exception e) {
			logger.error("scrittura chunk non riuscita!!");
			logger.error("decodeByte2File(byte[])", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("decodeByte2File(byte[]) - end");
		}
	}
}