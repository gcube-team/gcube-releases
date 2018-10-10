package org.gcube.data.spd.utils;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicList implements Iterator<String>{

	private static Logger logger = LoggerFactory.getLogger(DynamicList.class); 

	private final static long TIMEOUT_IN_MILLIS = 2000;
	private final static int RETRY = 20;

	private LinkedBlockingQueue<String> internalQueue = new LinkedBlockingQueue<String>(50);

	private boolean closed= false;

	private String nextElement;

	public boolean add(String element){
		if (this.closed) return false;
		return internalQueue.offer(element);
	}

	public boolean hasNext(){
		if (this.closed && internalQueue.isEmpty()){
			this.remove();
			return false;
		}
		int _retry = 0;
		String retrievedElement = null;
		while (_retry<RETRY && retrievedElement == null && (!this.closed || !internalQueue.isEmpty()))
			try{
				retrievedElement = internalQueue.poll(TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS);
				_retry++;
			} catch (InterruptedException e) {
				logger.warn("interrupd exception arrived", e);
				return false;
			}
		if(retrievedElement==null){
			this.close();
			this.remove();
			logger.trace("no more elements");
			return false;
		} else {
			nextElement = retrievedElement;
			return true;
		}
	}

	@Override
	public String next() {
		return nextElement;
	}

	public void close(){
		this.closed = true;
	}

	public void remove(){
		internalQueue = null;
	}


}
