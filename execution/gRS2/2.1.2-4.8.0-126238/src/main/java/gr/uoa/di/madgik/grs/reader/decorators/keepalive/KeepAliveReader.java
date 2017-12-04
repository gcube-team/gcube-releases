package gr.uoa.di.madgik.grs.reader.decorators.keepalive;

import java.util.concurrent.TimeUnit;

import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderInvalidArgumentException;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.reader.decorators.RecordReaderDelegate;
import gr.uoa.di.madgik.grs.record.Record;

/**
 * A {@link KeepAliveReader} adds keep-alive functionality to a {@link IRecordReader}. More specifically, it reads records from
 * the underlying reader periodically, based on a time interval. This functionality is transparent to the client, as each time
 * a <code>get</code> method is called, the record either originates from the set of prefetched records, or it is actually read
 * directly from the underlying reader. Keep-alive functionality is useful in order to keep the communication channel open if 
 * a client wishes to delay the consumption of records for long periods of time and the producer has no indication that this behavior 
 * is not problematic.
 *
 * @author gerasimos.farantatos
 *
 * @param <T> The type of {@link Record} specialization that is to be returned by the <code>get</code> operations
 */
public class KeepAliveReader<T extends Record> extends RecordReaderDelegate<T> {
	
	private KeepAliveHandler<T> handler = null;
	private Object synch = new Object();
	
	/**
	 * Creates a new instance
	 * 
	 * @param reader the {@link IRecordReader} to which keep-alive functionality is to be added
	 * @param keepAliveFrequency the frequency of the keep-alive operation
	 * @param keepAliveFrequencyUnit the time unit of the frequency of the keep-alive operation
	 * @throws GRS2ReaderInvalidArgumentException one of the arguments is not valid
	 */
	public KeepAliveReader(IRecordReader<T> reader, long keepAliveFrequency, TimeUnit keepAliveFrequencyUnit) throws GRS2ReaderInvalidArgumentException {
		super(reader);
		this.handler = new KeepAliveHandler<T>(reader, keepAliveFrequency, keepAliveFrequencyUnit, this.synch);
		Thread handlerThread = new Thread(handler);
		handlerThread.setDaemon(true);
		handlerThread.start();
	}
	
	/**
	 * 
	 * 
	 * @param reader the {@link IRecordReader} to which keep-alive functionality is to be added
	 * @param keepAliveFrequency the frequency of the keep-alive operation
	 * @param keepAliveFrequencyUnit the time unit of the frequency of the keep-alive operation
	 * @param inactivityTimeout A timeout after which the keep-alive reader will close the underlying reader
	 * @param inactivityTimeUnit The time unit of the inactivity timeout
	 * 
	 *  @throws GRS2ReaderInvalidArgumentException one of the arguments is not valid
	 */
	public KeepAliveReader(IRecordReader<T> reader, long keepAliveFrequency, TimeUnit keepAliveFrequencyUnit, 
			long inactivityTimeout, TimeUnit inactivityTimeUnit) throws GRS2ReaderInvalidArgumentException {
		super(reader);
		this.handler = new KeepAliveHandler<T>(reader, keepAliveFrequency, keepAliveFrequencyUnit, inactivityTimeout, inactivityTimeUnit, this.synch);
		Thread handlerThread = new Thread(handler);
		handlerThread.setDaemon(true);
		handlerThread.start();
	}
	
	/**
	 * Retrieves the next available record, which will be either one of the records retrieved via the keep-alive operation or
	 * a record retrieved directly from the underlying {@link IRecordReader}<p><p>
	 * 
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#get()}
	 */
	@Override
	public T get() throws GRS2ReaderException {
		synchronized(synch) {
			if(handler.getAvailableRecords() > 0)
				return handler.get();
			else
				return super.get();
		}
	}
	
	/**
	 * Retrieves the next available record, which will be either one of the records retrieved via the keep-alive operation or
	 * a record retrieved directly from the underlying {@link IRecordReader}<p><p>
	 * 
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#get(long, TimeUnit)}
	 */
	@Override
	public T get(long timeout, TimeUnit unit) throws GRS2ReaderException {
		synchronized(synch) {
			if(handler.getAvailableRecords() > 0)
				return handler.get();
			else
				return super.get(timeout, unit);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#seek(long)}
	 */
	@Override
	public long seek(long len) throws GRS2ReaderException {
		synchronized(synch) {
			int availableRecords = handler.getAvailableRecords();
			if(len > 0) {
				if(len >= availableRecords) {
					handler.clear();
					return super.seek(len-availableRecords);
				}else {
					for(int i = 0; i < len; i++)
						handler.get();
					return len;
				}
			}else {
				handler.clear();
				return super.seek(len-availableRecords);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#currentRecord()}
	 */
	@Override
	public long currentRecord() throws GRS2ReaderException {
		synchronized(synch) {
			return super.currentRecord() - handler.getAvailableRecords();
		}
	}
}
