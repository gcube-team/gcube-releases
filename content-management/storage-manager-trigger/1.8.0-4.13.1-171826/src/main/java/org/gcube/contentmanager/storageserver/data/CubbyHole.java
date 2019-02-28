package org.gcube.contentmanager.storageserver.data;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBObject;



public class CubbyHole {
	
	private Vector<DBObject> requestQueue = new Vector<DBObject>();   
	final static Logger logger=LoggerFactory.getLogger(CubbyHole.class);
	private boolean available;

	public synchronized DBObject get() {
		while (requestQueue.size() == 0){ 
		      try { 
		    	logger.debug("waiting in get");  
		        wait(); 
		      } 
		      catch (InterruptedException e){
					logger.error("getRequest()", e);
		      } 
		} 
		DBObject value=requestQueue.remove(0);
		logger.debug("get element from queue: "+value);
		available = false;
		notifyAll();
		return value;
	}

	public synchronized void put(DBObject value) {
		while (available == true) {
			try {
				logger.debug("waiting in put"); 
				wait();
			} catch (InterruptedException e) {
			}
		}
		logger.debug("put element to queue: "+value);
		requestQueue.addElement(value); 
		available = true;
		notifyAll();
	}
}
