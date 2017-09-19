package org.gcube.data.streams.handlers;


/**
 * A {@link FaultHandler} that rethrows all failures (i.e. does not handle any).
 * 
 * @author Fabio Simeoni
 *
 */
public class RethrowHandler implements FaultHandler {

	@Override
	public void handle(RuntimeException failure) {
		
		throw failure;
	}
}
