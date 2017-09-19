package org.gcube.data.streams.dsl;

import static java.util.Arrays.asList;
import gr.uoa.di.madgik.grs.record.Record;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.adapters.IteratorAdapter;
import org.gcube.data.streams.adapters.IteratorStream;
import org.gcube.data.streams.dsl.consume.ConsumeWithClause;
import org.gcube.data.streams.dsl.fold.InClause;
import org.gcube.data.streams.dsl.from.RsOfClause;
import org.gcube.data.streams.dsl.guard.GuardWithClause;
import org.gcube.data.streams.dsl.listen.MonitorWithClause;
import org.gcube.data.streams.dsl.pipe.PipeThroughClause;
import org.gcube.data.streams.dsl.publish.PublishRsUsingClause;
import org.gcube.data.streams.dsl.publish.PublishRsWithClause;
import org.gcube.data.streams.dsl.unfold.UnfoldThroughClause;
import org.gcube.data.streams.generators.LoggingListener;
import org.gcube.data.streams.generators.NoOpGenerator;
import org.gcube.data.streams.handlers.FaultHandler;
import org.gcube.data.streams.handlers.IgnoreHandler;
import org.gcube.data.streams.handlers.RethrowHandler;
import org.gcube.data.streams.handlers.RethrowUnrecoverableHandler;
import org.gcube.data.streams.handlers.StopFastHandler;
import org.gcube.data.streams.publishers.RsStringRecordFactory;
import org.gcube.data.streams.test.FallibleIterator;

/**
 * 
 * The definitions of an eDSL of stream and stream-related expressions. 
 * 
 * @author Fabio Simeoni
 *
 */
public class Streams {

	
	//CONSUME
	
	/**
	 * Starts a sentence to consume a {@link Stream}
	 * @param stream the stream
	 * @return the next clause of the sentence
	 */
	public static <E> ConsumeWithClause<E> consume(Stream<E> stream) {
		return new ConsumeWithClause<E>(stream);
	}
	
	//CONVERT

	/**
	 * Converts an {@link Iterator} to a {@link Stream}.
	 * @param itarator the iterator
	 * @return the stream
	 */
	public static <E> Stream<E> convert(Iterator<E> itarator) {
		return new IteratorStream<E>(itarator);
	}
	
	/**
	 * Converts a custom {@link IteratorAdapter} to a {@link Stream}.
	 * @param adapter the adapter
	 * @return the stream
	 */
	public static <E> IteratorStream<E> convert(IteratorAdapter<E> adapter) {
		return new IteratorStream<E>(adapter);
	}
	
	/**
	 * Converts an {@link Iterable} to a {@link Stream}.
	 * @param iterable the iterable
	 * @return the stream
	 */
	public static <E> Stream<E> convert(Iterable<E> iterable) {
		return convert(iterable.iterator());
	}
	
	/**
	 * Converts one or more elements into a {@link Stream}.
	 * @param elements the elements
	 * @return the stream
	 */
	public static <E> Stream<E> convert(E ...elements) {
		return convert(asList(elements));
	}
	
	/**
	 * Converts a mixture of exceptions and elements of a given type to a {@link Stream} of a that type.
	 * It's the client's responsibility to ensure that the elements that are not exceptions are homogeneously typed as the type indicated in input. 
	 * @param clazz the stream type
	 * @param elements the elements
	 * @return the stream
	 */
	public static <E> Stream<E> convertWithFaults(Class<E> clazz, Object...elements) {
		return convertWithFaults(clazz,asList(elements));
	}
	
	/**
	 * Converts a mixture of exceptions and elements of a given type to a {@link Stream} of a that type.
	 * It's the client's responsibility to ensure that the elements that are not exceptions are homogeneously typed as the type indicated in input. 
	 * @param clazz the stream type
	 * @param elements the elements
	 * @return the stream
	 */
	public static <E> Stream<E> convertWithFaults(Class<E> clazz,List<? extends Object> elements) {
		
		return convert(new FallibleIterator<E>(clazz, elements));
	}
	
	/**
	 * Starts a sentence to convert a resultset into a {@link Stream}.
	 * @param locator the locator of the resultset
	 * @return the next clause of the sentence
	 */
	public static RsOfClause<Record> convert(URI locator) {
		return new RsOfClause<Record>(locator);
	}
	
	/**
	 * Returns a {@link Stream} of strings extracted from a resultset of {@link RsStringRecordFactory#STRING_RECORD}s.
	 * @param locator the locator of the resultset
	 * @return the stream
	 */
	public static Stream<String> stringsIn(URI locator) {
		return convert(locator).ofStrings().withTimeout(5, TimeUnit.MINUTES);
	}
	
	/**
	 * Returns a {@link Stream} of strings extracted from a resultset of {@link RsStringRecordFactory#STRING_RECORD}s.
	 * @param locator the locator of the resultset
	 * @return the stream
	 */
	public static Stream<String> stringsIn(URI locator, int timeout, TimeUnit timeUnit) {
		return convert(locator).ofStrings().withTimeout(timeout, timeUnit);
	}
	
	// PIPE
	
	/**
	 * Starts a sentence to produce a {@link Stream} generated from another {@link Stream}.
	 * @param stream the input stream.
	 * @return the next clause of the sentence
	 */
	public static <E> PipeThroughClause<E> pipe(Stream<E> stream) {
		return new PipeThroughClause<E>(stream);
	}
	
	
	// FOLD
	
	/**
	 * Starts a sentence to produce a {@link Stream} that groups of elements of another {@link Stream}.
	 * @param stream the input stream.
	 * @return the next clause of the sentence
	 */
	public static <E> InClause<E> fold(Stream<E> stream) {
		return new InClause<E>(stream);
	}
	
	// UNFOLD
	
	/**
	 * Starts a sentence to produce a {@link Stream} that unfolds the elements of another {@link Stream}.
	 * @param stream the input stream.
	 * @return the next clause of the sentence
	 */
	public static <E> UnfoldThroughClause<E> unfold(Stream<E> stream) {
		return new UnfoldThroughClause<E>(stream);
	}
	
	// GUARD
	
	/**
	 * Starts a sentence to produce a {@link Stream} that controls the error raised by another {@link Stream}.
	 * @param stream the input stream.
	 * @return the next clause of the sentence
	 */
	public static <E> GuardWithClause<E> guard(Stream<E> stream) {
		return new GuardWithClause<E>(stream);
	}
	
	/**
	 * Starts a sentence to produce a {@link Stream} that notifies key events in the iteration of another {@link Stream}.
	 * @param stream the input stream.
	 * @return the next clause of the sentence
	 */
	public static <E> MonitorWithClause<E> monitor(Stream<E> stream) {
		return new MonitorWithClause<E>(stream);
	}
	
	// PUBLISH
	
	/**
	 * Starts a sentence to publish a {@link Stream} as a resultset.
	 * @param stream the stream
	 * @return the next clause of the sentence
	 */
	public static <E> PublishRsUsingClause<E> publish(Stream<E> stream) {
		return new PublishRsUsingClause<E>(stream);
	}
	
	/**
	 * Starts a sentence to publish a {@link Stream} as a resultset.
	 * @param stream the stream
	 * @return the next clause of the sentence
	 */
	public static PublishRsWithClause<String> publishStringsIn(Stream<String> stream) {
		return new PublishRsUsingClause<String>(stream).using(no_serialiser);
	}
	
	/**
	 * Returns a {@link Stream} that logs the throughput of an input {@link Stream}.
	 * @param stream the input stream
	 * @return the output stream
	 */
	public static <E> Stream<E> log(Stream<E> stream) {
		LoggingListener<E> listener = new LoggingListener<E>();
		return monitor(pipe(stream).through(listener)).with(listener);
	}
	
	// GENERATORS
	
	/**
	 * A {@link NoOpGenerator}.
	 */
	public static NoOpGenerator<String> no_serialiser = new NoOpGenerator<String>();
	
	/**
	 * Returns a {@link NoOpGenerator}.
	 * @return the generator
	 */
	public static <E> NoOpGenerator<E> no_op(Stream<E> stream) {
		return new NoOpGenerator<E>();
	}
	
	// HANDLERS
	/**
	 * A {@link RethrowHandler} for failure handling.
	 */
	public static FaultHandler RETHROW_POLICY = new RethrowHandler();
	
	/**
	 * A {@link RethrowUnrecoverableHandler} for failure handling.
	 */
	public static FaultHandler RETHROW_UNRECOVERABLE_POLICY = new RethrowUnrecoverableHandler();
	
	/**
	 * A {@link StopFastHandler} for failure handling.
	 */
	public static FaultHandler STOPFAST_POLICY= new StopFastHandler();
	
	/**
	 * A {@link IgnoreHandler} for failure handling.
	 */
	public static FaultHandler IGNORE_POLICY = new IgnoreHandler();
}
