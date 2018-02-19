package org.gcube.data.streams.handlers;



/**
 * A partial implementation for {@link FaultHandler} that count failures and keep track of the last observed failure.
 * 
 * @author Fabio Simeoni
 * 
 */
public abstract class CountingHandler implements FaultHandler {

	private int count;
	private Exception lastFailure;

	@Override
	public final void handle(RuntimeException failure) {
		handle(failure, lastFailure, count);
		count++;
		lastFailure = failure;
	}

	/**
	 * Indicates whether iteration should continue or stop the iteration on the occurrence of an iteration failure.
	 * 
	 * @param failure the failure
	 * @param lastFailure the failure observed previously, or <code>null</code> if this is the first observed failure
	 * @param failureCount the number of failures counted so far, <code>0</code> if this is the first observed failure
	 * @throws RuntimeException if no element can be yielded from the input element
	 */
	protected abstract void handle(Exception failure, Exception lastFailure, int failureCount);
}
