package org.gcube.data.streams;

import java.util.NoSuchElementException;

import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;
import org.gcube.data.streams.handlers.FaultHandler;
import org.gcube.data.streams.handlers.RethrowHandler;

/**
 * Partial {@link Stream} implementation based on look-ahead operations over an underlying stream.
 * <p>
 * The implementation attempts to prefetch the output of {@link #next()} in {@link #hasNext()}. If a failure occurs,
 * {@link #hasNext()}:
 * 
 * <li>keeps consuming the underlying stream as long as the failure is a {@link StreamSkipSignal};
 * <li>consults a {@link FaultHandler} for all the other failures. If the {@link FaultHandler} re-throws the same or a
 * different exception, the implementation throws it at the following {@link #next()}.
 * 
 * @author Fabio Simeoni
 * 
 * @param <E> the type of stream elements
 */
public abstract class LookAheadStream<E> implements Stream<E> {

	private FaultHandler handler = new RethrowHandler();

	private static class NullOr<T> {
		T element;
		NullOr(T element) {
			this.element=element;
		}
		T value() {
			return element;
		}
	}
	// iteration state
	protected Boolean hasNext;
	protected NullOr<E> element;
	private RuntimeException failure;
	
	/**
	 * Sets the {@link FaultHandler} for the iteration
	 * 
	 * @param handler the handler
	 * @throws IllegalArgumentException if the handler is <code>null</code>
	 */
	public void setHandler(FaultHandler handler) throws IllegalArgumentException {

		if (handler == null)
			throw new IllegalArgumentException("invalid null handler");

		this.handler = handler;
	}

	@Override
	public final boolean hasNext() {

		if (hasNext==null)
			hasNext = lookAhead();
		
		//auto-close
		if (!hasNext)
			close();
		
		return hasNext;	
	}
	
	private boolean lookAhead() {
						
		if (!delegateHasNext())
			return false;

		try {
			this.element = new NullOr<E>(delegateNext());
			return true;
		}
		catch (RuntimeException failure) {

			try {
				handler.handle(failure);
				return lookAhead();
			}
			catch(StreamSkipSignal skip) {
				return lookAhead();
			}
			catch(StreamStopSignal stop) {
				return false;
			}
			catch(RuntimeException rethrownUnchecked) {	
				this.failure=rethrownUnchecked;
				return true;
			}
		}
		
		
		
	}
	
	@Override
	public final E next() {

		try {
			throwLookAheadFailureIfAny();
			return lookedAheadElementOrGetItNow();
		}
		finally {
			cleanIterationState();
		}
	}
	
	private void throwLookAheadFailureIfAny() {
		if (failure != null)
			throw failure;
	}
	
	private E lookedAheadElementOrGetItNow() {
		
		if (element == null)
			if (hasNext())
				return next();
			else
				throw new NoSuchElementException();
		
		return element.value();
				
	}
	
	private void cleanIterationState() {
		failure=null;
		element=null;
		hasNext = null;
	}
	
	
	
	/**
	 * Returns an element of the underlying stream
	 * 
	 * @return the element
	 */
	protected abstract E delegateNext();

	/**
	 * Returns {@code true} if the underlying stream has more elements.
	 * 
	 * @return {@code true} if the underlying stream has more elements
	 */
	protected abstract boolean delegateHasNext();	
}
