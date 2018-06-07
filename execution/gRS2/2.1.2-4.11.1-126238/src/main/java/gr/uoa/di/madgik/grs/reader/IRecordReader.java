package gr.uoa.di.madgik.grs.reader;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.BufferEvent.EventSource;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;


public interface IRecordReader<T extends Record> extends Iterable<T>
{
	
	/**
	 * Retrieves the {@link RecordDefinition}s that define the {@link Record}s that are accessible through the reader.
	 * 
	 * @see IBuffer#getRecordDefinitions()
	 * 
	 * @return the {@link RecordDefinition}s as were provided by the corresponding writer
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public RecordDefinition[] getRecordDefinitions() throws GRS2ReaderException;
	
	/**
	 * Retrieves the Inactivity timeout set for the lifecycle management of the underlying {@link IBuffer}. This value
	 * should be interpreted in conjunction with the value of {@link IRecordReader#getInactivityTimeUnit()}.
	 * 
	 * @see IBuffer#getInactivityTimeout()
	 * 
	 * @return the inactivity timeout
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public long getInactivityTimeout() throws GRS2ReaderException;
	
	/**
	 * Retrieves the Inactivity time unit set for the lifecycle management of the underlying {@link IBuffer}. This value
	 * should be interpreted in conjunction with the value of {@link IRecordReader#getInactivityTimeout()}
	 * 
	 * @see IBuffer#getInactivityTimeUnit()
	 * 
	 * @return the inactivity time unit
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public TimeUnit getInactivityTimeUnit() throws GRS2ReaderException;

	/**
	 * The underlying {@link IBuffer} capacity used.
	 * 
	 * @see IBuffer#getCapacity()
	 * 
	 * @return the buffer capacity
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public int getCapacity() throws GRS2ReaderException;

	/**
	 * The underlying {@link IBuffer} concurrent partial capacity used
	 * 
	 * @see IBuffer#getConcurrentPartialCapacity()
	 * 
	 * @return the concurrency partial record buffer capacity
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public int getConcurrentPartialCapacity() throws GRS2ReaderException;

	/**
	 * Retrieves the notification threshold defined.
	 * 
	 * @see IBuffer#getNotificationThreshold()
	 * 
	 * @return the notification threshold
	 * @throws GRS2ReaderException the operation could not be completed
	 */
//	public float getThreshold() throws GRS2ReaderException;
	
	/**
	 * The object used to notify readers when the available {@link Record}s increase above some threshold 
	 * 
	 * @see IBuffer#getReaderThresholdNotificationObject()
	 * 
	 * @return the synchronization object
	 * @throws GRS2ReaderException
	 */
//	public Object getThresholdNotificationObject() throws GRS2ReaderException;

	/**
	 * Retrieves the status of the underlying {@link IBuffer}.
	 * 
	 * @see IBuffer#getStatus()
	 * 
	 * @return the status of the underlying buffer
	 */
	public IBuffer.Status getStatus();

	/**
	 * Closes and disposes the underlying buffer. After this method has been invoked, all associated resources are disposed.
	 * 
	 * @see IBuffer#close()
	 * @see IBuffer#dispose()
	 * 
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public void close() throws GRS2ReaderException;

	/**
	 * The number of total {@link Record}s that have passed through the {@link IBuffer} this far.
	 * 
	 * @see IBuffer#totalRecords()
	 * 
	 * @return the number of total {@link Record}s that have passed through the {@link IBuffer} this far
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public long totalRecords() throws GRS2ReaderException;
	
	/**
	 * The number of readily available for consumption {@link Record}s.
	 * 
	 * @see IBuffer#availableRecords()
	 * 
	 * @return the number of readily available for consumption records
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public int availableRecords() throws GRS2ReaderException;

	/**
	 * The index of the last {@link Record} retrieved through the reader.
	 * 
	 * @return the index of the last {@link Record} retrieved through the reader
	 * @throws GRS2ReaderException the operation could not be completed because no records were retrieved yet
	 */
	public long currentRecord() throws GRS2ReaderException;
	
	/**
	 * Retrieves the next available {@link Record} from the {@link IBuffer}. An explicit cast is performed to the 
	 * type provided in the generic declaration of the reader.
	 * 
	 * @see IBuffer#get()
	 * 
	 * @return the retrieved {@link Record} or null if none was readily available
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public T get() throws GRS2ReaderException;
	
	/**
	 * Retrieves the next available {@link Record} from the {@link IBuffer}. An explicit cast is performed to the 
	 * type provided in the generic declaration of the reader. If no record is readily available the reader will 
	 * block for a maximum of <code>timeout</code> <code>unit</code> units of time waiting for a record to become 
	 * available or until the {@link IBuffer} uses the {@link IBuffer#getReaderImmediateNotificationObject()}
	 * to notify blocked readers.
	 * 
	 * @see IBuffer#get()
	 * 
	 * @param timeout the timeout to wait for
	 * @param unit the unit of time to use to interpret the timeout value
	 * @return the {@link Record} retrieved, or null if the timeout expired without a {@link Record} becoming available
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public T get(long timeout, TimeUnit unit) throws GRS2ReaderException;
	
	/**
	 * Makes sure that within the provided time frame, there is a {@link Record} that can be retrieved without blocking.
	 * If the timeout defined expires and no {@link Record} has been made available, false is returned. Otherwise, or if
	 * there are already available {@link Record}s, true is returned.
	 * 
	 * @param timeout the timeout value interpreted in conjunction with the unit value
	 * @param unit the time unit to use to interpret the timeout value
	 * @return true if there is a {@link Record} available before the timeout has expired, false otherwise
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public boolean waitAvailable(long timeout, TimeUnit unit) throws GRS2ReaderException;
	
	/**
	 * The timeout that should be used by the {@link Iterator}s that are created by this reader. This value
	 * is interpreted in conjunction with the value of {@link IRecordReader#getIteratorTimeUnit()}
	 * 
	 * @param iteratorTimeout the timeout
	 */
	public void setIteratorTimeout(long iteratorTimeout);
	
	/**
	 * The time unit that should be used by the {@link Iterator}s that are created by this reader. This value
	 * is interpreted in conjunction with the value of {@link IRecordReader#getIteratorTimeout()}
	 * 
	 * @param iteratorTimeUnit the time unit
	 */
	public void setIteratorTimeUnit(TimeUnit iteratorTimeUnit);
	
	/**
	 * The timeout that is used by the {@link Iterator}s that are created by this reader. This value
	 * is interpreted in conjunction with the value of {@link IRecordReader#getIteratorTimeUnit()}.
	 * 
	 * @return the timeout
	 */
	public long getIteratorTimeout() throws GRS2ReaderException;
	
	/**
	 * The time unit that is used by the {@link Iterator}s that are created by this reader. This value
	 * is interpreted in conjunction with the value of {@link IRecordReader#getIteratorTimeout()}
	 * 
	 * @return the time unit
	 */
	public TimeUnit getIteratorTimeUnit();
	
	public Iterator<T> iterator();
	
	/**
	 * Seeks the number of {@link Record}s provided in the list of {@link Record}s available.
	 * Depending on the actual {@link IRecordReader} implementation, forward and/or backward seeks may be supported.
	 * 
	 * @param len the number of {@link Record}}s to skip
	 * @return the number of {@link Record}s actually skipped
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public long seek(long len) throws GRS2ReaderException;
	
	/**
	 * Emits the provided event to the writer. The source of the event is set to {@link EventSource#Reader}.
	 * 
	 * @param event the event to send to the writer
	 * @throws GRS2ReaderException the operation could not be completed
	 * @throws GRS2ReaderInvalidArgumentException the event provided cannot be null
	 */
	public void emit(BufferEvent event) throws GRS2ReaderException, GRS2ReaderInvalidArgumentException;
	
	/**
	 * Receives a previously emitted event from a writer.
	 * 
	 * @return the event received or null if no pending events exist
	 * @throws GRS2ReaderException the operation could not be completed
	 */
	public BufferEvent receive() throws GRS2ReaderException;
}
