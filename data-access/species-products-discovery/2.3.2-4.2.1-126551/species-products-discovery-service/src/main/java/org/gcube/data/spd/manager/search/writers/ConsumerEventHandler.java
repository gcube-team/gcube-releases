package org.gcube.data.spd.manager.search.writers;

import org.gcube.data.spd.model.exceptions.StreamException;

public interface ConsumerEventHandler<T> {

	public boolean onElementReady(T element);
		
	public void onError(StreamException streamException);
	
	public void onClose();
	
	public boolean isConsumerAlive();

}
