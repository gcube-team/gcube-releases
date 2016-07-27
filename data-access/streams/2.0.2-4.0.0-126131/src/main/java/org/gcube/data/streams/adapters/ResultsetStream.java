package org.gcube.data.streams.adapters;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GRS2ExceptionWrapper;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.exception.GRS2UncheckedException;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.gcube.data.streams.LookAheadStream;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.exceptions.StreamException;
import org.gcube.data.streams.exceptions.StreamOpenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@link Stream} adapter for gRS2 resultsets.
 * <p>
 * This implementation is not thread safe.
 * 
 * @author Fabio Simeoni
 *
 */
public class ResultsetStream<E extends Record> extends LookAheadStream<E> {

	private static Logger log =LoggerFactory.getLogger(ResultsetStream.class);	

	public static final int default_timeout = 30;
	public static final TimeUnit default_timeout_unit=TimeUnit.SECONDS;

	private final URI locator;

	private long timeout = default_timeout;
	private TimeUnit timeoutUnit = default_timeout_unit;

	private boolean open=false;
	private boolean closed=false;
	private RuntimeException lookAheadFailure;

	private ForwardReader<E> reader;

	private E record;


	/**
	 * Creates a new instance with a result set locator.
	 * @param locator the locator.
	 * @throws IllegalArgumentException if the locator is <code>null</code>.
	 * */
	public ResultsetStream(URI locator) throws IllegalArgumentException {

		if (locator==null)
			throw new IllegalArgumentException("invalid or null locator");

		this.locator=locator;

	}

	public void setTimeout(long timeout, TimeUnit unit) throws IllegalArgumentException {

		if (timeout<=0 || timeoutUnit==null)
			throw new IllegalArgumentException("invalid timeout or null timeout unit");

		this.timeout = timeout;
		this.timeoutUnit = unit;
	}

	@Override
	protected E delegateNext() {

		try {
			if (lookAheadFailure!=null)
				throw lookAheadFailure;
			else if (record instanceof GRS2ExceptionWrapper){
				//get underlying cause
				Throwable cause = ((GRS2ExceptionWrapper)record).getEx().getCause();

				//rewrap checked cause as appropriate to this layer
				if (cause instanceof RuntimeException)
					throw (RuntimeException) cause;
				else
					throw new StreamException(cause);
			} else return record;

		}finally {
			lookAheadFailure=null;
		}
	}

	@Override
	protected boolean delegateHasNext() {

		if (closed)
			return false;

		if (!open) {

			try {
				reader = new ForwardReader<E>(locator);
			}
			catch (Throwable t) { 
				lookAheadFailure= new StreamOpenException("cannot open resultset "+locator,t);
				return true;
			}

			log.info("initialised resultset at "+locator);

			open=true;
		}

		try {
			record = reader.get(timeout, timeoutUnit );
		} catch (GRS2ReaderException e) {
			lookAheadFailure = new RuntimeException(e);
		}

		if (reader.getStatus()!=Status.Close && record == null) {
			if (reader.getStatus()==Status.Open)
				lookAheadFailure = new RuntimeException("Timeout occurred reading the resultSet");
			else if (reader.getStatus()==Status.Dispose)
				lookAheadFailure = new RuntimeException("ResultSet disposed");
			return true;
		} else return record!=null;

	}


	@Override
	public void close() {

		if (open) {
			try {
				reader.close();
				log.info("closed resultset at "+locator);
			}
			catch(GRS2ReaderException e) {
				log.error("could not close resultset",e);
			}
			open=false;
		}
		closed=true;
	}

	@Override
	public URI locator() throws IllegalStateException {

		if (open)
			throw new IllegalStateException("locator is invalid as result set has already been opened");
		else
			return locator;

	}

	@Override
	public void remove() {
		record = null;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}
}
