package gr.uoa.di.madgik.grs.writer;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.BufferEvent.EventSource;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreReader;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

public interface IRecordWriter<T extends Record>
{
	
	/**
	 * If this writer is populated by an {@link IBufferStore} this store is provided in order to keep the store
	 * alive and active even after the respective {@link BufferStoreReader} has closed the writer but the client
	 * reader is still using it
	 * 
	 * @param store the store which serves the writer
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public void setBufferStore(IBufferStore store) throws GRS2WriterException;
	
	/**
	 * The underlying {@link IBuffer} capacity used
	 * 
	 * @see IBuffer#getCapacity()
	 * 
	 * @return the buffer capacity
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public int getCapacity() throws GRS2WriterException;
	
	/**
	 * Retrieves the notification threshold defined
	 * 
	 * @see IBuffer#getNotificationThreshold()
	 * 
	 * @return the notification threshold
	 * @throws GRS2WriterException the operation could not be completed
	 */
//	public float getThreshold() throws GRS2WriterException;
	
	/**
	 * The object used to notify writers when the available {@link Record}s drop below some threshold 
	 * 
	 * @see IBuffer#getWriterThresholdNotificationObject()
	 * 
	 * @return the synchronization object
	 */
//	public Object getThresholdNotificationObject();

	/**
	 * Sets the notification threshold
	 * 
	 * @see IBuffer#setNotificationThreshold(float)
	 * 
	 * @param threshold the notification threshold
	 * @throws GRS2WriterException the operation could not be completed
	 */
//	public void setThreshold(float threshold) throws GRS2WriterException;
	
	/**
	 * @return the locator identifying the authored {@link IBuffer} created through the {@link IWriterProxy} provided at
	 * writer initialization
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public URI getLocator() throws GRS2WriterException;
	
	/**
	 * Retrieves the status of the underlying {@link IBuffer}
	 * 
	 * @see IBuffer#getStatus()
	 * 
	 * @return the status of the underlying buffer
	 */
	public IBuffer.Status getStatus();

	
	/**
	 * The number of readily available for consumption {@link Record}s
	 * 
	 * @see IBuffer#availableRecords()
	 * 
	 * @return the number of readily available for consumption records
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public int availableRecords() throws GRS2WriterException;
	
	/**
	 * The number of total {@link Record}s that have passed through the {@link IBuffer} this far
	 * 
	 * @see IBuffer#totalRecords()
	 * 
	 * @return the number of total {@link Record}s that have passed through the {@link IBuffer} this far
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public long totalRecords() throws GRS2WriterException;
	

	/**
	 * Stores the provided {@link Record} to the underlying {@link IBuffer}
	 * 
	 * @see IBuffer#put(Record)
	 * 
	 * @param record the {@link Record} to add
	 * @return true if the addition was successful or false if the {@link IBuffer} has reached its capacity 
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public boolean put(T record) throws GRS2WriterException;

	/**
	 * Stores the provided {@link Record} to the underlying {@link IBuffer}. If the {@link IBuffer} has already reached
	 * its capacity, the method will block for a maximum of <code>timeout</code> <code>unit</code> units of time waiting 
	 * for a record to be consumed or until the {@link IBuffer} uses the {@link IBuffer#getWriterImmediateNotificationObject()}
	 * to notify blocked writers
	 * 
	 * @see IBuffer#put(Record)
	 * 
	 * @param record the {@link Record} to add
	 * @param timeout the timeout to wait for
	 * @param unit the unit of time to use to interpret the timeout value
	 * @return true if the addition was successful or false if the {@link IBuffer} has reached its capacity and the timeout expired
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public boolean put(T record,long timeout, TimeUnit unit) throws GRS2WriterException;

	/**	
	 * Dissociates a {@link Record} from the underlying {@link IBuffer} of a producer and stores it to the {@link IBuffer} of 
	 * this {@link IRecordWriter}. 
	 * Before {@link IRecordWriter#put(Record)} is called, the record is unbound from the buffer to which is associated, and its definition index 
	 * is re-set to the same value it had before.
	 * <p>
	 * Equivalent to calling {@link IRecordWriter#importRecord(Record, int)} with the second parameter being equal to
	 * {@link Record#getDefinitionIndex()}. Used for convenience if this {@link IRecordWriter} is associated with the same definitions as 
	 * the reader from which the record is originating
	 * 
	 * @param record the {@link Record} to import to the {@link RecordWriter}
	 * @return true if the import was successful or false if the {@link IBuffer} has reached its capacity
	 * @throws GRS2Exception an error has occurred
	 * 
	 * @see IRecordWriter#put(Record)
	 */
	public boolean importRecord(T record) throws GRS2Exception;
	
	/**	
	 * Dissociates a {@link Record} from the underlying {@link IBuffer} of a producer and stores it to the {@link IBuffer} of 
	 * this {@link RecordWriter}. 
	 * Before {@link RecordWriter#put(Record, long, TimeUnit)} is called, the record is unbound from the buffer to which is associated, 
	 * and its definition index is re-set to the same value it had before.
	 * <p>
	 * Equivalent to calling {@link RecordWriter#importRecord(Record, int, long, TimeUnit)} with the second parameter being equal to
	 * {@link Record#getDefinitionIndex()}. Used for convenience if this {@link RecordWriter} is associated with the same definitions as 
	 * the reader from which the record is originating
	 * 
	 * @param record the {@link Record} to import to the {@link RecordWriter}
	 * @return true if the import was successful or false if the {@link IBuffer} has reached its capacity and the timeout expired
	 * @throws GRS2Exception an error has occurred
	 * 
	 * @see RecordWriter#put(Record, long, TimeUnit)
	 */
	public boolean importRecord(T record, long timeout, TimeUnit unit) throws GRS2Exception;
	
	/**
	 * Dissociates a {@link Record} from the underlying {@link IBuffer} of a producer and stores it to the {@link IBuffer} of 
	 * this {@link RecordWriter}. 
	 * Before {@link RecordWriter#put(Record)} is called, the record is unbound from the buffer to which is associated, and its definition index 
	 * is set to the supplied value
	 * 
	 * @param record the {@link Record} to import to this {@link RecordWriter}
	 * @param newDefinitionIndex the definition index that the {@link Record} will have in this {@link RecordWriter}
	 * @return true if the import was successful or false if the {@link IBuffer} has reached its capacity
	 * @throws GRS2Exception an error has occurred
	 * 
	 * @see RecordWriter#put(Record)
	 */
	public boolean importRecord(T record, int newDefinitionIndex) throws GRS2Exception;
	
	/**
	 * Dissociates a {@link Record} from the underlying {@link IBuffer} of a producer and stores it to the {@link IBuffer} of 
	 * this {@link RecordWriter}. 
	 * Before {@link RecordWriter#put(Record, long, TimeUnit)} is called, the record is unbound from the buffer to which is associated, 
	 * and its definition index is set to the supplied value
	 * 
	 * @param record the {@link Record} to import to this {@link RecordWriter}
	 * @param newDefinitionIndex the definition index that the {@link Record} will have in this {@link RecordWriter}
	 * @return true if the import was successful or false if the {@link IBuffer} has reached its capacity and the timeout expired
	 * @throws GRS2Exception an error has occurred
	 * 
	 * @see RecordWriter#put(Record, long, TimeUnit)
	 */
	public boolean importRecord(T record, int newDefinitionIndex, long timeout, TimeUnit unit) throws GRS2Exception;
	
	/**
	 * Closes the underlying buffer. After this method has been invoked, no more {@link Record}s can be added 
	 * 
	 * @see IBuffer#close()
	 * 
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public void close() throws GRS2WriterException;

	/**
	 * Disposes the underlying buffer. After this method has been invoked, all associated resources are disposed. If 
	 * there are any readers connected through the underlying {@link IBuffer}, their resources will also be disposed
	 * 
	 * @see IBuffer#dispose()
	 */
	public void dispose();
	/**
	 * Emits the provided event to the reader. The source of the event is set to {@link EventSource#Writer}
	 * 
	 * @param event the event to send to the reader
	 * @throws GRS2WriterException the operation could not be completed
	 * @throws GRS2WriterInvalidArgumentException the event provided cannot be null
	 */
	public void emit(BufferEvent event) throws GRS2WriterException, GRS2WriterInvalidArgumentException;
	
	/**
	 * Receives a previously emitted event from a reader
	 * 
	 * @return the event received or null if no pending events exist
	 * @throws GRS2WriterException the operation could not be completed
	 */
	public BufferEvent receive() throws GRS2WriterException;

}
