package org.gcube.data.streams;

import java.net.URI;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.gcube.data.streams.adapters.IteratorStream;
import org.gcube.data.streams.adapters.ResultsetStream;
import org.gcube.data.streams.delegates.FoldedStream;
import org.gcube.data.streams.delegates.GuardedStream;
import org.gcube.data.streams.delegates.MonitoredStream;
import org.gcube.data.streams.delegates.PipedStream;
import org.gcube.data.streams.delegates.UnfoldedStream;
import org.gcube.data.streams.dsl.Faults;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.streams.exceptions.StreamOpenException;
import org.gcube.data.streams.handlers.FaultHandler;
import org.gcube.data.streams.publishers.RsPublisher;
import org.gcube.data.streams.publishers.StreamPublisher;

/**
 * An {@link Iterator} over the elements of a dataset of arbitrary origin, including memory, secondary storage, and
 * network.
 * <p>
 * 
 * <h3>Properties</h3><br>
 * 
 * Streams are:
 * <p>
 * 
 * <ul>
 * <li><em>addressable</em>: clients may invoke {@link #locator()} to obtain a reference to their address. The use and
 * syntax of locators is implementation-dependent.
 * <li><em>closeable</em>: clients may invoke {@link #close()} to allow implementations to release resources. Clients
 * <em>should</em> invoke {@link #close()} if they do not consume streams in their entirety. Implementations
 * <em>must</em> automatically release their resources when they have been consumed in their entirety.
 * <li><em>fallible</em>: invoking {@link #next()} over streams that originate from secondary storage and remote
 * locations may raise a wide range failures. Some failures may be <em>recoverable</em>, in that subsequent invocations
 * of {@link #next()} <em>may</em> still succeed. Other failures may be <em>unrecoverable</em>, in that subsequent
 * invocations of {@link #next()} are guaranteed to fail too.
 * </ul>
 * 
 * <h3>Implementations</h3><br>
 * 
 * There are predefined implementations that adapt the {@link Stream} interface to existing {@link Iterator}s and remote
 * gRS2 resultsets (cf. {@link IteratorStream} and {@link ResultsetStream}).
 * <p>
 * 
 * Other predefined implementations transform, fold, and unfold the elements of existing streams (cf.
 * {@link PipedStream}, {@link FoldedStream}, {@link UnfoldedStream}).
 * <p>
 * 
 * Additional implementations allow modular handling of stream faults and notify interested listeners of stream
 * iteration events (cf. {@link GuardedStream}, {@link MonitoredStream}).
 * <p>
 * 
 * Finally, streams may be published outside the current runtime by implementations of the {@link StreamPublisher}
 * interface. A predefined implementation supports publication of streams as gRS2 resultsets (cf. {@link RsPublisher}).
 * 
 * <p>
 * 
 * All the available implementations can be fluently instantiated and configured with an embedded DSL (cf.
 * {@link Streams}).
 * 
 * <h3>Fault Handling</h3><br>
 * 
 * Clients can implement {@link FaultHandler}s to specify fault handling policies over streams, and then wrap streams in
 * {@link GuardedStream}s that apply they policies:
 * 
 * <pre>
 * import static ....Streams.*; 
 * ...
 * Stream&lt;T&gt; stream = ...
 * 
 * FaultHandler handler = new FaultHandler() {
 *   public void handle(RuntimeException fault) {
 *    ...
 *   }
 * };
 * 
 * Stream&lt;T&gt; guarded = guard(stream).with(handler);
 * </pre>
 * 
 * 
 * {@link FaultHandler}s can ignore faults, rethrow them, rethrow different faults, or use the constant
 * {@link FaultHandler#iteration} to stop the iteration of the underlying stream (cf. {@link Iteration#stop()})
 * <p>
 * 
 * 
 * Faults are unchecked exceptions thrown by {@link #next()}, often wrappers around an original cause.
 * {@link FaultHandler}s can use a fluent API to simplify the task of analysing fault causes (cf. {@link Faults}):
 * 
 * <pre>
 * FaultHandler handler = new FaultHandler() {
 *  	public void handle(RuntimeException fault) {
 *           try {
 *           	throw causeOf(fault).as(SomeException.class,SomeOtherException.class);
 *           }
 *           catch(SomeException e) {...}
 *           catch(SomeOtherException e) {...}
 *        }
 * };
 * </pre>
 * 
 * <h3>Consumption</h3><br>
 * 
 * Clients may consume streams by explicitly iterating over their elements. Since streams are fallible and closeable,
 * the recommended idiom is the following:
 * 
 * <pre>
 * Stream&lt;T&gt; stream = ...
 * try {
 *   while (stream.hasNext())
 *     ....stream.next()...
 * }
 * finally {
 *  stream.close();
 * }
 * </pre>
 * 
 * Alternatively, clients may provide {@link Callback}s to generic {@link StreamConsumer}s that iterate on
 * behalf of clients. Using the simplifications of the DSL:
 * 
 * <pre>
 * Stream&lt;T&gt; stream = ...
 * 
 * Callback&lt;T&gt; callback = new Callback&lt;T&gt;() {
 *  	public void consume(T element) {
 *           ...element...
 *        }
 * };
 * 
 * consume(stream).with(callback);
 * </pre>
 * 
 * {@link Callback}s can control iteration through the {@link Iteration} constant (cf. {@link Callback#iteration}):
 * 
 * <pre>
 * Callback&lt;T&gt; callback = new Callback&lt;T&gt;() {
 *  	public void consume(T element) {
 *  	    ...iteration.stop()...
 *  		...
 *        }
 * };
 * </pre>
 * 
 * 
 * 
 * @param <E> the type of elements iterated over
 * 
 * @author Fabio Simeoni
 * 
 */
public interface Stream<E> extends Iterator<E> {

	boolean hasNext();

	/**
	 * @throws NoSuchElementException if the stream has no more elements or it has been closed
	 * @throws StreamOpenException if the stream cannot be opened
	 * @throws RuntimeException if the element cannot be returned
	 */
	E next();

	/**
	 * Returns the stream locator.
	 * 
	 * @return the locator
	 * @throws IllegalStateException if the stream is no longer addressable at the time of invocation.
	 */
	URI locator();

	/**
	 * Closes the stream unconditionally, releasing any resources that it may be using.
	 * <p>
	 * Subsequent invocations of this method have no effect.<br>
	 * Subsequents invocations of {@link #hasNext()} return {@code false}.<br>
	 * Subsequent invocations of {@link #next()} throw {@link NoSuchElementException}s.
	 * <p>
	 * Failures are logged by implementations and suppressed otherwise.
	 */
	void close();

	/**
	 * Returns <code>true</code> if the stream has been closed.
	 * 
	 * @return <code>true</code> if the stream has been closed
	 */
	boolean isClosed();

}