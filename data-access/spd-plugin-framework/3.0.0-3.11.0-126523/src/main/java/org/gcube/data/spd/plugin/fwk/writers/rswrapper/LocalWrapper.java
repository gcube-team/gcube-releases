package org.gcube.data.spd.plugin.fwk.writers.rswrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.gcube.data.spd.model.exceptions.InvalidRecordException;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.exceptions.WrapperAlreadyDisposedException;

import java.util.Collections;


public class LocalWrapper<T> extends AbstractLocalWrapper<T> {

	private String locator;
		
	private ArrayBlockingQueue<T> queue;
	
	private List<StreamException> errorList = new ArrayList<StreamException>();
	
	private boolean forceOpen = false;
	
	private int timeoutTimeInMinutes =1;
	
	public LocalWrapper() {
		super();
		this.locator = UUID.randomUUID().toString();
		this.queue = new ArrayBlockingQueue<T>(100);
	}

	public LocalWrapper(int queueSize) {
		super();
		this.locator = UUID.randomUUID().toString();
		this.queue = new ArrayBlockingQueue<T>(queueSize);
	}
	
	
	@Override
	public String getLocator() throws Exception {
		return this.locator;
	}

	@Override
	public synchronized boolean add(T input) throws InvalidRecordException, WrapperAlreadyDisposedException {
		if (this.closed) new WrapperAlreadyDisposedException("the local wrapper has been disposed");
		try{
			return this.queue.offer(input, timeoutTimeInMinutes,TimeUnit.MINUTES);
		}catch (InterruptedException e) {
			this.close();
			this.queue= null;
			throw new WrapperAlreadyDisposedException("the local wrapper has been disposed");
		}
	}

	@Override
	public void close(){
		if (!isForceOpen()){
			this.closed= true;
		}
		else logger.warn("cannot close the Wrapper, forceOpen enabled");
	}

	@Override
	public ArrayBlockingQueue<T> getQueue(){
		return queue;
	}

	
	
	public void setTimeoutTimeInMinutes(int timeoutTimeInMinutes) {
		this.timeoutTimeInMinutes = timeoutTimeInMinutes;
	}

	
	public boolean isForceOpen() {
		return forceOpen;
	}

	public void forceOpen() {
		this.forceOpen = true;
	}
	
	public void disableForceOpen() {
		this.forceOpen = false;
	}
	
	public void disableForceOpenAndClose() {
		this.forceOpen = false;
		this.close();
	}

	@Override
	public void disposeBuffer() {
		queue = null;	
	}

	@Override
	public boolean add(StreamException result) throws InvalidRecordException,
			WrapperAlreadyDisposedException {
		errorList.add(result);
		return true;
	}
	
	public List<StreamException> getErrors(){
		return Collections.unmodifiableList(this.errorList);
	}
}
