package org.gcube.dataanalysis.executor.plugin;

public class QueueWatcher {
	
	long t;
	long maxLifeTime;
	
	public QueueWatcher(long maxLifeTime){
		t=System.currentTimeMillis();
		this.maxLifeTime=maxLifeTime;
	}
	
	public synchronized void reset(){
		t=System.currentTimeMillis();
	}
	
	public boolean isTooMuch(){
		return ((System.currentTimeMillis()-t)>maxLifeTime);
	}
	
}
