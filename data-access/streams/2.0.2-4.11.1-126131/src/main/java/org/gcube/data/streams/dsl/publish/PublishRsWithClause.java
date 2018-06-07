/**
 * 
 */
package org.gcube.data.streams.dsl.publish;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.StreamClause;
import org.gcube.data.streams.handlers.FaultHandler;
import org.gcube.data.streams.publishers.ThreadProvider;
import org.gcube.data.streams.publishers.RsPublisher;

/**
 * The clause of {@code publish} sentences in which the output resultset is configured.
 * 
 * @author Fabio Simeoni
 * 
 */
public class PublishRsWithClause<E> extends StreamClause<E, PublishRsEnv<E>> {

	private final RsPublisher<E> publisher;

	/**
	 * Creates an instance with the {@link PublishRsEnv} of the ongoing sentence.
	 * 
	 * @param e the environment
	 */
	public PublishRsWithClause(PublishRsEnv<E> e) {
		super(e);
		this.publisher = new RsPublisher<E>(e.stream(), e.factory);
	}

	/**
	 * Configures the element capacity of the resultset buffer.
	 * 
	 * @param size the number of elements in the buffer
	 * @return this clause
	 */
	public PublishRsWithClause<E> withBufferOf(int size) {
		publisher.setBufferSize(size);
		return this;
	}

	/**
	 * Configures the publishing timeout of the resultset.
	 * <p>
	 * If the timeout expire, elements of the input {@link Stream} will no longer be published, though they may still be
	 * consumed if {@link #nonstop()} has been invoked.
	 * 
	 * @param timeout the timeout
	 * @param unit the time unit for the timeout
	 * @return this clause
	 */
	public PublishRsWithClause<E> withTimeoutOf(int timeout, TimeUnit unit) {
		publisher.setTimeout(timeout, unit);
		return this;
	}

	/**
	 * Configures publication to continue consuming the input {@link Stream} after the expiry of the publishing timeout.
	 * <p>
	 * Typically used for the side-effects of publications.
	 * 
	 * @return this clause
	 */
	public PublishRsWithClause<E> nonstop() {
		publisher.setOnDemand(false);
		return this;
	}

	/**
	 * Configures a {@link ThreadProvider} for the publishing thread.
	 * <p>
	 * Publication occurs asynchronously and a thread provider may be required to make available thread-bound
	 * information in the publishing thread.
	 * 
	 * @param provider the thread provider
	 * @return this clause
	 */
	public PublishRsWithClause<E> with(ThreadProvider provider) {
		publisher.setThreadProvider(provider);
		return this;
	}

	/**
	 * Configures a {@link FaultHandler} for publication and returns the locator of the resultset.
	 * 
	 * @param handler the handler
	 * @return the locator the locator
	 */
	public URI with(FaultHandler handler) {
		publisher.setFaultHandler(handler);
		return publisher.publish();
	}

	/**
	 * Returns the locator of the resultset.
	 * 
	 * @return the locator.
	 */
	public URI withDefaults() {
		return publisher.publish();
	}
}