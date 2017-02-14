package org.gcube.data.analysis.tabulardata.operation.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Worker<T extends Result> extends Observable implements Runnable {

	private boolean aborted = false;
	
	List<AbortListener> abortListeners = new ArrayList<>();
	
	private static final Logger log = LoggerFactory.getLogger(Worker.class);

	private float progress = 0;

	private String humanReadableStatus="";
	
	private WorkerException exception = null;

	private T result = null;

	private WorkerStatus status = WorkerStatus.INITIALIZING;

	private OperationInvocation sourceInvocation;

	public Worker(OperationInvocation sourceInvocation) {
		this.sourceInvocation = sourceInvocation;
	}

	protected void updateProgress(float progress, String humanReadableStatus) {
		if (progress <= 0f || progress >= 1f)
			throw new IllegalArgumentException(
					"When updating progress, progress value must be between 0 and 1 (excluded)");
		this.progress = progress;
		this.humanReadableStatus = humanReadableStatus;
		status = WorkerStatus.IN_PROGRESS;
		setChanged();
		notifyObservers(this);
	}
		
	protected abstract T execute() throws WorkerException, OperationAbortedException;

	public void abort(){
		aborted = true;
		for (AbortListener listener : abortListeners)
			if (listener!=null) 
				listener.onAbort();
	}
	
	protected final void checkAborted() throws OperationAbortedException{
		if (aborted) throw new OperationAbortedException();
	}
	
	public void run() {
		try {
			log.debug("Starting worker: " + this.getClass().getSimpleName());
			long start = System.currentTimeMillis();
			T result = execute();
			succeded(result);
			log.debug("Worker ended execution succesfully: " + this.getClass().getSimpleName()+" in "+(System.currentTimeMillis()-start)+" millis ");
		} catch (WorkerException e) {
			log.debug("Worker failed execution: " + this.getClass().getSimpleName());
			failed(e);
		}catch(OperationAbortedException oae){
			log.debug("execution aborted: "+ this.getClass().getSimpleName());
			aborted();
		} catch (Exception e) {
			log.debug("Worker failed execution: " + this.getClass().getSimpleName());
			failed(new WorkerException("Internal operation error", e));
		}
	}

	private void succeded(T result) {
		this.progress = 1f;
		this.result = result;
		this.status = WorkerStatus.SUCCEDED;
		this.humanReadableStatus ="Operation succeeded"; 
		setChanged();
		notifyObservers(this);
	}

	private void failed(WorkerException e) {
		this.status = WorkerStatus.FAILED;
		this.exception = e;
		this.humanReadableStatus ="Operation failed"; 
		setChanged();
		notifyObservers(this);
	}
	
	private void aborted() {
		this.status = WorkerStatus.ABORTED;
		this.humanReadableStatus ="Operation aborted"; 
		setChanged();
		notifyObservers(this);
	}

	public float getProgress() {
		return progress;
	}

	public String getHumanReadableStatus() {
		return humanReadableStatus;
	}

	public WorkerException getException() {
		return exception;
	}

	public T getResult() {
		return result;
	}

	public WorkerStatus getStatus() {
		return status;
	}

	public OperationInvocation getSourceInvocation() {
		return sourceInvocation;
	}
	
	public <R extends Result, K extends Worker<R>> WorkerWrapper<K, R> createWorkerWrapper(WorkerFactory<K> wrapped){
		WorkerWrapper<K,R> wrapper = new WorkerWrapper<>(wrapped);	
		this.abortListeners.add(wrapper);
		return wrapper;
	}
}
