package org.gcube.data.access.queueManager;

import javax.jms.ExceptionListener;

import org.gcube.data.access.queueManager.model.QueueItem;

public interface QueueItemHandler<T extends QueueItem> extends ExceptionListener{

	public void handleQueueItem(T item)throws Exception;
	
	public void close();
}
