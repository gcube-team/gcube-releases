package gr.uoa.di.madgik.grs.store.buffer;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;
import gr.uoa.di.madgik.grs.store.record.IRecordStore;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This interface defines the way a storage based {@link IBuffer} holder behaves in order to provide permanent storage
 * to a set of incoming locators. Based on the order specified using the {@link MultiplexType}, the data of the incoming 
 * {@link IBuffer}s are persisted using the technology chosen by the interface implementations. The data persisted through
 * this procedure are accessible to recreate a new {@link IBuffer} using the {@link BufferStoreReader} 
 * 
 * @author gpapanikos
 *
 */
public interface IBufferStore
{
	/**
	 * Indicates the way that the incoming locators are read and their data persisted in the underlying storage device
	 * 
	 * @author gpapanikos
	 *
	 */
	public enum MultiplexType
	{
		/**
		 * All locators are used sequentially from beginning to end before the next is used, in the order they were provided
		 */
		FIFO,
		/**
		 * The reader that has readily available {@link Record}s is used first. In cases where none of the readers have
		 * readily available {@link Record}s, it is up to the implementation to select the way ithis is handled
		 */
		FirstAvailable
	}
	
	/**
	 * Associates a {@link BufferStoreReader} with the {@link IBufferStore} that is has been initialized to access. On 
	 * disposal of the {@link IBufferStore}, the associated {@link BufferStoreReader}s are also disposed
	 * 
	 * @param reader the reader to associate
	 */
	public void associateStoreReader(BufferStoreReader reader);
	
	/**
	 * Sets the key by which this {@link IBufferStore} is registered and referenced through a {@link GRSRegistry}
	 * 
	 * @param key the key
	 */
	public void setKey(String key);

	/**
	 * Retrieves the key by which this {@link IBufferStore} is registered and referenced through a {@link GRSRegistry}
	 * 
	 * @return the key
	 */
	public String getKey();
	
	/**
	 * Retrieves a synchronization object that can be used in a standard wait / notify block to notify requesters of when 
	 * an additional object has been made available in the underlying storage from the input readers
	 * 
	 * @return the synchronization object
	 */
	public Object getModificationObject();
	
	/**
	 * Retrieves the {@link MultiplexType} indicating the way the input locators are used and in which order their data is received
	 * 
	 * @return the type of multiplex
	 */
	public MultiplexType getMultiplexType();
	
	/**
	 * Sets the {@link MultiplexType} indicating the way the input locators are used and in which order their data is received
	 * 
	 * @param multiplex the type of multiplex
	 * @throws GRS2BufferStoreException The status of the {@link IBufferStore} does not allow this operation to be completed
	 */
	public void setMultiplexType(MultiplexType multiplex) throws GRS2BufferStoreException;
	
	/**
	 * Retrieves the locators over which the {@link IBufferStore} operates
	 * 
	 * @return the locators
	 */
	public URI[] getLocators();
	
	/**
	 * Sets the incoming locators that should be stored
	 * 
	 * @param locators the locators which data should be stored
	 * @throws GRS2BufferStoreException The status of the {@link IBufferStore} does not allow this operation to be completed
	 */
	public void setLocators(URI[] locators) throws GRS2BufferStoreException;
	
	/**
	 * Retrieves the {@link BufferStoreEntry}s that represent the status for all incoming locators and their respective
	 * readers and status as well as their persistency location and {@link IRecordStore}s
	 * 
	 * @return the list of persistency entries per incoming locator
	 */
	public ArrayList<BufferStoreEntry> getEntries();
	
	/**
	 * Retrieves the timeout that should be used by the readers utilized to access the input locators. This value
	 * is interpreted in conjunction with {@link IBufferStore#getReaderTimeoutTimeUnit()}
	 * 
	 * @return the timeout
	 */
	public long getReaderTimeout();
	
	/**
	 * Sets the timeout that should be used by the readers utilized to access the input locators. This value
	 * is interpreted in conjunction with {@link IBufferStore#setReaderTimeoutTimeUnit(TimeUnit)}
	 * 
	 * @param timeout the timeout
	 */
	public void setReaderTimeout(long timeout);
	
	/**
	 * Retrieves the timeout unit that should be used by the readers utilized to access the input locators. This value
	 * is interpreted in conjunction with {@link IBufferStore#getReaderTimeout()}
	 * 
	 * @return the timeout time unit
	 */
	public TimeUnit getReaderTimeoutTimeUnit();

	/**
	 * Sets the timeout unit that should be used by the readers utilized to access the input locators. This value
	 * is interpreted in conjunction with {@link IBufferStore#setReaderTimeout(long)}
	 * 
	 * @param unit the timeout time unit
	 */
	public void setReaderTimeoutTimeUnit(TimeUnit unit);

	/**
	 * The timeout of the inactivity period after which the {@link IBufferStore} is eligible for disposal. This 
	 * value is to be interpreted in conjunction with the value of {@link IBufferStore#getInactivityTimeUnit()}
	 * 
	 * @return the timeout value
	 */
	public long getInactivityTimeout();

	/**
	 * The time unit used to define the timeout of the inactivity period after which the {@link IBufferStore} is 
	 * eligible for disposal. This value is to be interpreted in conjunction with the value of 
	 * {@link IBufferStore#getInactivityTimeout()}
	 * 
	 * @return the time unit of the timeout
	 */
	public TimeUnit getInactivityTimeUnit();

	/**
	 * Retrieves the last activity time over this {@link IBufferStore}
	 * 
	 * @return the last activity time
	 */
	public long getLastActivityTime();

	/**
	 * Update the last activity time to the current time 
	 */
	public void markActivity();

	/**
	 * Make any needed initialization before the {@link IBufferStore#store()} is called to start the storing procedure
	 * 
	 * @throws GRS2BufferStoreException The initialization could not be performed
	 */
	public void initialize() throws GRS2BufferStoreException;
	
	/**
	 * Start retrieving data from the input locators and storing them according to the specific {@link IBufferStore} implementation 
	 */
	public void store();
	
	/**
	 * Disposes all the resources that are internally managed by the {@link IBufferStore}
	 */
	public void dispose();
}
