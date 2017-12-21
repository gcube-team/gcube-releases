package org.gcube.data.streams.handlers;


/**
 * A {@link FaultHandler} that silently stops iteration at the first occurrence of any failure.
 * 
 * @author Fabio Simeoni
 *
 */
public class StopFastHandler implements FaultHandler {

	@Override
	public void handle(RuntimeException failure) {
		
		iteration.stop();
	}
}
