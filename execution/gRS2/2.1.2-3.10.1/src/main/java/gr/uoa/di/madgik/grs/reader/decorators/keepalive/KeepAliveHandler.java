package gr.uoa.di.madgik.grs.reader.decorators.keepalive;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderInvalidArgumentException;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;

/**
 * Utility class which periodically retrieves records from a {@link IRecordReader}
 * A queue is kept internally into which the records are stored.
 * The records are retrieved in FIFO order via the {@link KeepAliveHandler#get()} method
 * 
 * @author gerasimos.farantatos
 *
 * @param <T> the type of {@link Record} specialization that is to be returned by the <code>get</code> operations
 */
class KeepAliveHandler<T extends Record> implements Runnable {
	
	private static Logger logger = Logger.getLogger(KeepAliveHandler.class.getName());
	private IRecordReader<T> reader = null;
	private LinkedList<T> queue = new LinkedList<T>();
	private long keepAliveFrequency = 0;
	private TimeUnit keepAliveFrequencyUnit = null;
	private long inactivityTimeout = 0;
	private TimeUnit inactivityTimeUnit = null;
	private Integer inactivityRecordThreshold = null;
	private Integer sizeUponPreviousRetrieval = 0;
	private Object synchQueue = new Object();
	private Object synch = null;
	
	/**
	 * Creates a new instance
	 * 
	 * @param reader the {@link IRecordReader} from which the records will be read
	 * @param keepAliveFrequency how often to read records from the {@link IRecordReader}
	 * @param keepAliveFrequencyUnit the time unit of the keep-alive frequency
	 * @param synch object to synchronize with a {@link KeepAliveReader} in order to ensure that the records will
	 * be retrieved in the correct order
	 * @throws GRS2ReaderInvalidArgumentException one of the arguments is not valid
	 */
	public KeepAliveHandler(IRecordReader<T> reader, long keepAliveFrequency, TimeUnit keepAliveFrequencyUnit, Object synch) throws GRS2ReaderInvalidArgumentException {
		if(reader == null) throw new GRS2ReaderInvalidArgumentException("Invalid reader reference");
		if(keepAliveFrequency < 1) throw new GRS2ReaderInvalidArgumentException("Keep-alive frequency should be positive");
		if(synch == null) throw new GRS2ReaderInvalidArgumentException("Invalid synchronization object reference");
		this.reader = reader;
		this.keepAliveFrequency = keepAliveFrequency;
		this.keepAliveFrequencyUnit = keepAliveFrequencyUnit;	
		this.synch = synch;
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param reader the {@link IRecordReader} from which the records will be read
	 * @param keepAliveFrequency how often to read records from the {@link IRecordReader}
	 * @param keepAliveFrequencyUnit the time unit of the keep-alive frequency
	 * @param synch object to synchronize with a {@link KeepAliveReader} in order to ensure that the records will
	 * be retrieved in the correct order
	 * @param inactivityTimeout A timeout after which the underlying reader will be closed
	 * @param inactivityTimeUnit The time unit of the inactivity timeout
	 * 
	 * @throws GRS2ReaderInvalidArgumentException one of the arguments is not valid
	 */
	public KeepAliveHandler(IRecordReader<T> reader, long keepAliveFrequency, TimeUnit keepAliveFrequencyUnit, 
			long inactivityTimeout, TimeUnit inactivityTimeUnit, Object synch) throws GRS2ReaderInvalidArgumentException {
		this(reader, keepAliveFrequency, keepAliveFrequencyUnit, synch);
		
		if(inactivityTimeout < 1) throw new GRS2ReaderInvalidArgumentException("Inactivity timeout should be positive");
		this.inactivityTimeout = inactivityTimeout;
		this.inactivityTimeUnit = inactivityTimeUnit;
		this.inactivityRecordThreshold = (int)(inactivityTimeUnit.toMillis(inactivityTimeout) / keepAliveFrequencyUnit.toMillis(keepAliveFrequency));
		logger.log(Level.FINE, "Computed inactivity record threshold to " + inactivityRecordThreshold);
	}
	
	/**
	 * Returns the number of records read from the {@link IRecordReader}
	 * @return the number of records available
	 */
	public int getAvailableRecords() {
		synchronized(synchQueue) {
			return queue.size();
		}
	}
	
	/**
	 * Returns the first available (oldest) record read from the {@link IRecordReader}
	 * @return the first available record
	 */
	public T get() {
		synchronized(synchQueue) {
		   T rec =  queue.pollFirst();
		   if(inactivityRecordThreshold != null) {
			   sizeUponPreviousRetrieval = queue.size();
			   logger.log(Level.FINE, "Queue size on past retrieval is " + sizeUponPreviousRetrieval);
		   }
		   rec.show();
		   return rec;
		}
	}
	
	/**
	 * Clears the underlying queue
	 */
	public void clear() {
		synchronized(synchQueue) {
			queue.clear();
		}
	}
	
	/**
	 * Performs the keep-alive operation
	 */
	public void run() {
		try {
			while(true) {
				try { keepAliveFrequencyUnit.sleep(keepAliveFrequency); }
				catch(InterruptedException e) { }
				if(this.reader.getStatus() != Status.Open)
					break;
				synchronized(synch) {
					T record;
					if((record = reader.get()) != null)
						synchronized(synchQueue) {
							record.hide();
							queue.add(record);
						}
					if(inactivityRecordThreshold != null && queue.size() > sizeUponPreviousRetrieval + inactivityRecordThreshold) {
						logger.log(Level.FINE, "Closing keep-alive reader due to inactivity");
						reader.close();
						break;
					}
				}
			}
		}catch(Exception e) {
			logger.log(Level.WARNING, "Keep alive handler halted", e);
		}
	}
}
