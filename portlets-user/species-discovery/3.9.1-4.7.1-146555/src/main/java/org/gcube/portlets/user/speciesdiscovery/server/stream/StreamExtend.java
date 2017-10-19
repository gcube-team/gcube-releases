package org.gcube.portlets.user.speciesdiscovery.server.stream;

import java.net.URI;
import java.util.Iterator;

import org.gcube.data.streams.Stream;

public class StreamExtend<I> implements Stream<I> {

	private Iterator<I> iterator;

	public StreamExtend(Iterator<I> iterator){
		this.iterator = iterator;
	}
	
	@Override
	public void remove() {
		iterator.remove();
	}

	@Override
	public void close() {
	}

	@Override
	public boolean hasNext() {
		if (iterator.hasNext())
			return true;
		return false;
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public URI locator() {
		return null;
	}

	@Override
	public I next() {
		I item = iterator.next();
		if(item!=null)
			return item;
		
		return null;
	}
}
