package org.gcube.data.streams.publishers;

import static gr.uoa.di.madgik.grs.writer.RecordWriter.*;
import static org.gcube.data.streams.Utils.*;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.Utils;
import org.gcube.data.streams.exceptions.StreamPublishException;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.streams.handlers.FaultHandler;
import org.gcube.data.streams.handlers.RethrowHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Publishes {@link Stream}s as resultsets.
 * 
 * @author Fabio Simeoni
 * 
 * @param <E> the type of stream element
 * 
 */
public class RsPublisher<E> implements StreamPublisher {

	private static Logger log = LoggerFactory.getLogger(RsPublisher.class);

	private final Stream<E> stream;
	private final RecordFactory<E> factory;

	private RsTransport transport;
	
	// resultset writer parameters
	private int bufferSize = DefaultBufferCapacity;
	private long timeout = DefaultInactivityTimeout;
	private TimeUnit timeoutUnit = DefaultInactivityTimeUnit;
	private boolean onDemand = true;

	// default thread provider
	private ThreadProvider provider = new ThreadProvider() {
		@Override
		public Thread newThread(Runnable task) {
			return new Thread(task);
		}
	};

	// by default, we re-throw failures and handle them in publishing loop
	private FaultHandler handler = new RethrowHandler();

	/**
	 * Creates an instance for a given {@link Stream} and with a given element serialiser.
	 * 
	 * @param stream the stream
	 * @param serialiser the serialiser
	 */
	public RsPublisher(Stream<E> stream, Generator<E, String> serialiser) {
		this(stream, new RsStringRecordFactory<E>(serialiser));
	}

	/**
	 * Creates an instance for a given {@link Stream} and with a given {@link RecordFactory}.
	 * 
	 * @param stream the stream
	 * @param factory the factory
	 */
	public RsPublisher(Stream<E> stream, RecordFactory<E> factory) {

		if (stream == null || factory == null || factory.definitions() == null)
			throw new IllegalArgumentException("invalid or null inputs");

		this.stream = stream;
		this.factory = factory;
	}

	/**
	 * Sets the size of the write buffer.
	 * 
	 * @param size the size in bytes.
	 * @throws IllegalArgumentException if the size is not a positive number
	 */
	public void setBufferSize(int size) throws IllegalArgumentException {

		if (size <= 0)
			throw new IllegalArgumentException("invalid empty buffer");

		this.bufferSize = size;
	}

	/**
	 * Sets the time to wait on a full write buffer. After the time has elapsed publication stops.
	 * 
	 * @param timeout the timeout
	 * @param timeoutUnit the timeout unit
	 * @throws IllegalArgumentException if the timeout is not a positive number or the timeout unit is <code>null</code>
	 */
	public void setTimeout(long timeout, TimeUnit timeoutUnit) throws IllegalArgumentException {

		if (timeout <= 0 || timeoutUnit == null)
			throw new IllegalArgumentException("invalid  timeout or time unit");

		this.timeout = timeout;
		this.timeoutUnit = timeoutUnit;
	}

	/**
	 * Sets the resultset transport.
	 * 
	 * @param transport the transport
	 * @throws IllegalArgumentException if the transport is <code>null</code>
	 */
	public void setTransport(RsTransport transport) {

		if (transport == null)
			throw new IllegalArgumentException("invalid null transport");

		this.transport = transport;
	}

	/**
	 * Sets the production mode of the publiher
	 * 
	 * @param onDemand <code>true</code> if the stream ought to be consumed only when clients require it,
	 *            <code>false</code> if it should be consumed continuously.
	 */
	public void setOnDemand(boolean onDemand) {
		this.onDemand = onDemand;
	}

	/**
	 * Sets the {@link ThreadProvider} used to populate the resultset.
	 * 
	 * @param provider the provider
	 * @throws IllegalArgumentException if the provider is <code>null</code>.
	 */
	public void setThreadProvider(ThreadProvider provider) {

		if (provider == null)
			throw new IllegalArgumentException("invalid null provider");

		this.provider = provider;
	}

	/**
	 * Sets the {@link FaultHandler} for reading and writing failures.
	 * 
	 * @param handler the handler
	 * @throws IllegalArgumentException if the handler is <code>null</code>
	 */
	public void setFaultHandler(FaultHandler handler) {

		if (handler == null)
			throw new IllegalArgumentException("invalid null handler");

		this.handler = handler;
	}

	@Override
	public URI publish() throws StreamPublishException {

		Utils.initialiseRS();

		if (transport == null)
			transport = RsTransport.TCP;

		URI locator;
		RecordWriter<Record> writer;

		// publish
		try {

			writer = new RecordWriter<Record>(transport.proxy(), // The proxy that defines the way the writer can be
																	// accessed
					factory.definitions(), // The definitions of the records the gRS handles
					bufferSize, // The capacity of the underlying synchronization buffer
					DefaultConcurrentPartialCapacity, // The maximum number of records that can be concurrently accessed
														// on partial transfer
					DefaultMirrorBufferFactor, // The maximum fraction of the buffer that should be transfered during
												// mirroring
					timeout, // The timeout in time units after which an inactive gRS can be disposed
					timeoutUnit // The time unit in timeout after which an inactive gRS can be disposed
			);

			locator = writer.getLocator();

		} catch (GRS2WriterException e) {
			throw new StreamPublishException("cannot publish stream as resultset", e);
		}

		Runnable feeder = newFeeder(writer, locator);

		provider.newThread((feeder)).start();

		return locator;
	}

	// used internally: the task that consumes the stream to publish it in the resultset
	private Runnable newFeeder(final RecordWriter<Record> writer, final URI locator) {

		return new Runnable() {

			@Override
			public void run() {

				while (stream.hasNext()) {

					try {
						publishNextElementOrFailure(writer);
					}
					//stop publishing
					catch (RuntimeException e) {
						
						//also stop consuming if publication was on demand
						if (onDemand)
							break;
						else 
							close(writer,locator); //close as soon as we can
					}
				}

				close(writer,locator);
				stream.close();
			}
		};

	}
	
	private void publishNextElementOrFailure(RecordWriter<Record> writer) {
		
		try {
			
			try {
				publish(writer,nextRecord());
			}
			catch(StreamSkipSignal skip) {//skip this element and continue
				return;
			}
			catch(StreamStopSignal stop) {//rethrow stop
				throw stop;
			}
			catch(RuntimeException failure) {
				
				//publish failure
				publish(writer, failure);
				
				//stop publishing if cannot be recognised as contingency
				if (!isContingency(failure))
					throw failure; 
			}
	
		}
		catch(GRS2WriterException failure) {
			throw new RuntimeException(failure);//stop publishing
		}
	}

	private Record nextRecord() {
		
		try {
			E element = stream.next();
			return factory.newRecord(element);
		}
		catch (RuntimeException e) {	
			try {
				handler.handle(e);
			}
			catch(StreamStopSignal stop) {
				throw e;
			}
			throw new StreamSkipSignal();
		}
	}
	
	//private helper: publish a record
	private void publish(RecordWriter<Record> writer, Record record) throws GRS2WriterException {

		if (writer.getStatus() == Status.Open){
			if (!writer.put(record, timeout, timeoutUnit)) {
				log.trace("client is not consuming resulset, stop publishing");
				throw new GRS2WriterException();
			}
		} else{
			log.warn("Writer not open, actual status is {}",writer.getStatus());
			throw new GRS2WriterException("writer closed or disposed");
		}
			
	}
	
	//private helper: publish a failure
	private void publish(RecordWriter<Record> writer, Throwable failure) throws GRS2WriterException {
		if (writer.getStatus() == Status.Open)
			if (!writer.put(failure, timeout, timeoutUnit)) {
				log.trace("client is not consuming resulset, stop publishing");
				throw new GRS2WriterException();
			}		
	}
	
	
	private void close(RecordWriter<Record> writer, final URI locator) {
		
		if (writer.getStatus() == Status.Open) {
			try {
				writer.close();
			} catch (GRS2WriterException e) {//log anomaly
				log.error("error closing resultset at " + locator, e);
			}
		}
	}
}
