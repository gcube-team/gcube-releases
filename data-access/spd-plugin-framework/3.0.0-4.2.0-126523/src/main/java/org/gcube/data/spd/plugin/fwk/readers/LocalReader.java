package org.gcube.data.spd.plugin.fwk.readers;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.gcube.data.spd.plugin.fwk.writers.rswrapper.AbstractLocalWrapper;

public class LocalReader<T> extends AbstractLocalReader<T> {

	public LocalReader(AbstractLocalWrapper<T> wrapper) {
		super(wrapper);
	}

	@Override
	public boolean hasNext() {
		if (queue==null)	return false;
		else{
			try {
				element=null;
				if (!this.wrapper.isClosed()){
					while (!this.wrapper.isClosed() && element==null )
						element= queue.poll(timeoutInSeconds, TimeUnit.SECONDS);
				}
				if (element ==null) element=queue.poll();
			} catch (InterruptedException e) {
				logger.warn("the queue is empty",e);
			}
			return element!=null;
		}
	}

	@Override
	public URI locator() {
		return null;
	}

	@Override
	public void close() {
		if (wrapper.isClosed())
			wrapper.disposeBuffer();
		else wrapper.close();
	}

	@Override
	public boolean isClosed() {
		return wrapper.isClosed();
	}

}
