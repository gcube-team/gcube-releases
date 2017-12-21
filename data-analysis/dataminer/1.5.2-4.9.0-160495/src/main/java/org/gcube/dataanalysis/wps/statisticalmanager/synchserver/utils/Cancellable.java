package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils;

public interface Cancellable {
	
	boolean cancel();
	
	boolean isCancelled();
	
}
