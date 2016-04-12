package org.gcube.data.streams.handlers;


/**
 * A {@link FaultHandler} that silently absorbs all failures.
 * 
 * @author Fabio Simeoni
 *
 */
public class IgnoreHandler implements FaultHandler {

	@Override
	public void handle(RuntimeException failure) {}
}
