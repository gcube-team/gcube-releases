package gr.uoa.di.madgik.grs.buffer;

import java.util.concurrent.TimeUnit;

import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.BufferEvent.EventSource;
import gr.uoa.di.madgik.grs.proxy.mirror.IMirror;
import gr.uoa.di.madgik.grs.record.GRS2RecordException;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;

/**
 * The {@link IBuffer} interface is the main enabling component acting as the {@link Record} buffer that is
 * populated by a writer and read by a reader. All implementations of this class must declare a default constructor
 * with no arguments since they may be instantiated using reflection under the default no argument constructor  
 * 
 * @author gpapanikos
 *
 */
public interface IBuffer
{
	/**
	 * The current status of the {@link IBuffer}. Depending on the status different operations
	 * can be performed on the {@link IBuffer}
	 * 
	 * @author gpapanikos
	 *
	 */
	public enum Status
	{
		/**
		 * The buffer has not yet been initialized and configuration parameters can be modified. This
		 * is the only state in which configuration values can be modified
		 */
		Init,
		/**
		 * The buffer is open for editing and consumption
		 */
		Open,
		/**
		 * The buffer is closed for authoring but open for reading
		 */
		Close,
		/**
		 * The buffer is in dispose and cannot be used for any operation
		 */
		Dispose
	}

	/**
	 * The transport directive set for the {@link IBuffer} or its contained items. All the directives
	 * declared for the items supporting a transport directive use this enumeration. This directives
	 * are used in cases of non same JVM hosted instances of writer and reader
	 * 
	 * @author gpapanikos
	 *
	 */
	public enum TransportDirective
	{
		/**
		 * The transport is inherited by the element that is higher up the item hierarchy. For the 
		 * top most element, {@link IBuffer}, this value should not be used. If used, then the {@link IBuffer}
		 * implementation should override it.
		 */
		Inherit,
		/**
		 * The full payload should be transfered whenever first requested 
		 */
		Full,
		/**
		 * Initially no payload is transfered, but it is gradually send with subsequent requests
		 */
		Partial
	}
	
	/**
	 * During remote transport of an item payload, the defined {@link TransportDirective} can be overridden
	 * to request a different behavior. This enumeration defines when this override needs to take place.
	 * 
	 * @author gpapanikos
	 *
	 */
	public enum TransportOverride
	{
		/**
		 * The component should behave as if the {@link TransportDirective} defined for it
		 * was {@link TransportDirective#Full}
		 */
		Override,
		/**
		 * No override to the originally defined {@link TransportDirective} is needed
		 */
		Defined
	}
	
	/**
	 * If the current {@link IBuffer} is served by a {@link IBufferStore}, the {@#link IBufferStore} is set
	 * so that its last activity time is set everytime the {@link IBuffer}'s activity time is also
	 * updated
	 * 
	 * @param store the {@link IBufferStore} that provides the data that the {@link IBuffer} is serving
	 * @throws GRS2BufferException the status of the {@link IBuffer} does not allow for the definitions to be retrieved
	 */
	public void setBufferStore(IBufferStore store) throws GRS2BufferException;
	
	/**
	 * Sets the definitions of the {@link Record}s that are to be placed in the {@link IBuffer}. All
	 * records that are then inserted in the {@link IBuffer} must point to one of these definitions. 
	 * This is a configuration parameter that can be set only before {@link IBuffer} initialization.
	 * The order by which these definitions are provided is maintained and can be used for reference
	 * 
	 * @param definitions the record definitions
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow the definitions to be set
	 */
	public void setRecordDefinitions(RecordDefinition[] definitions) throws GRS2BufferException;
	/**
	 * Retrieves the definitions of the {@link Record}s provided using {@link IBuffer#setRecordDefinitions(RecordDefinition[])}
	 * 
	 * @return the record definitions
	 * @throws GRS2BufferException the status of the {@link IBuffer} does not allow for the definitions to be retrieved
	 */
	public RecordDefinition[] getRecordDefinitions() throws GRS2BufferException;
	
	/**
	 * Retrieves the capacity of the underlying bounded buffer that can store the {@link Record}s that a writer adds to it 
	 * 
	 * @return the bounded buffer capacity
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public int getCapacity() throws GRS2BufferException;
	/**
	 * Sets the capacity of the underlying bounded buffer that can store the {@link Record}s that a writer adds to it. 
	 * This is a configuration parameter that can be set only before {@link IBuffer} initialization.
	 * 
	 * @param capacity the bounded buffer capacity
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public void setCapacity(int capacity) throws GRS2BufferException;
	/**
	 * Retrieves the number of items that a reader can concurrently request more of its payload in case this payload
	 * is transported in {@link TransportDirective#Partial} mode. This means that even after a reader has accessed the 
	 * {@link Record}, it should remain accessible for further requests to it. Finding a value larger than 1, permits
	 * a willing reader to process in parallel more than one requests
	 * 
	 * @return the concurrent number of partially retrieved records that can be used
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public int getConcurrentPartialCapacity() throws GRS2BufferException;
	/**
	 * Sets the number of items that a reader can concurrently request more of its payload in case this payload
	 * is transported in {@link TransportDirective#Partial} mode. This means that even after a reader has accessed the 
	 * {@link Record}, it should remain accessible for further requests to it. Setting a value larger than 1, will enable
	 * a willing reader to process in parallel more than one requests. This is a configuration parameter that can be set 
	 * only before {@link IBuffer} initialization
	 * 
	 * @param capacity the concurrent number of partially retrieved records that can be used
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public void setConcurrentPartialCapacity(int capacity) throws GRS2BufferException;
	
	/**
	 * Retrieves the notification threshold factor of the buffer capacity below which, a waiting writer should be notified.
	 * This threshold is reached when a consuming reader accessing the produced components will cause the writer's available
	 * records to drop under the specified threshold of the buffere's full capacity
	 * 
	 * @return the threshold of the buffer's capacity under which the writer is notified to produce more
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
//	public float getNotificationThreshold() throws GRS2BufferException;
	/**
	 * Sets the notification threshold factor of the buffer capacity below which, a waiting writer should be notified.
	 * This threshold is reached when a consuming reader accessing the produced components will cause the writer's available
	 * records to drop under the specified threshold of the buffere's full capacity. This is a configuration parameter 
	 * that can be set only before {@link IBuffer} initialization
	 * 
	 * @param threshold the threshold of the buffer's capacity under which the writer is notified to produce more
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
//	public void setNotificationThreshold(float threshold) throws GRS2BufferException;
	
	/**
	 * Retrieves the timeout after which if the buffer has remained inactive is eligible for purging. This value
	 * is interpreted with respect to the {@link TimeUnit} defined through {@link IBuffer#getInactivityTimeUnit()}
	 * 
	 * @return the timeout period
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public long getInactivityTimeout() throws GRS2BufferException;
	/**
	 * Sets the timeout after which if the buffer has remained inactive is eligible for purging. This value
	 * is interpreted with respect to the {@link TimeUnit} defined through {@link IBuffer#getInactivityTimeUnit()}. 
	 * This is a configuration parameter that can be set only before {@link IBuffer} initialization
	 * 
	 * @param timeout the timeout period
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public void setInactivityTimeout(long timeout) throws GRS2BufferException;
	/**
	 * Retrieves the timeout {@link TimeUnit} after which if the buffer has remained inactive is eligible for purging. 
	 * This value is interpreted with respect to the timeout defined through {@link IBuffer#getInactivityTimeout()}
	 * 
	 * @return the timeout {@link TimeUnit}
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public TimeUnit getInactivityTimeUnit() throws GRS2BufferException;
	/**
	 * Sets the timeout {@link TimeUnit} after which if the buffer has remained inactive is eligible for purging. This value
	 * is interpreted with respect to the timeout defined through {@link IBuffer#getInactivityTimeout()}. 
	 * This is a configuration parameter that can be set only before {@link IBuffer} initialization
	 * 
	 * @param unit the timeout {@link TimeUnit}
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public void setInactivityTimeUnit(TimeUnit unit) throws GRS2BufferException;
	/**
	 * Retrieves the last activity time of the {@link IBuffer}. This value is used in conjunction with the
	 * timeout configuration values available through {@link IBuffer#getInactivityTimeout()} and
	 * {@link IBuffer#getInactivityTimeUnit()}
	 * 
	 * @return the last activity time
	 */
	public long getLastActivityTime();
	/**
	 * Sets the simulate activity status and updates last activity time of the {@link IBuffer} to the current time. Used after 
	 * an operation which was not performed directly on the buffer, but should nevertheless be treated as producer/consumer 
	 * interaction has taken place.
	 */
	public void markSimulateActivity();
	/**
	 * Retrieves the simulate activity status of the {@link IBuffer}. The status will be reset after calling this method. 
	 * 
	 * @return the simulate activity status of the {@link IBuffer}
	 */
	public boolean getSimulateActivity();
	/**
	 * Sets the {@link IMirror} implementation that serves this {@link IBuffer}. If null, no mirror is
	 * used between the writer and reader and the share the same {@link IBuffer} instance
	 * 
	 * @param mirror the mirror serving the synchronization of the {@link IBuffer} between the reader and writer
	 */
	public void setMirror(IMirror mirror);
	/**
	 * Retrieves the {@link IMirror} implementation that serves this {@link IBuffer}. If null, no mirror is
	 * used between the writer and reader and the share the same {@link IBuffer} instance
	 * 
	 * @return the {@link IMirror} implementation that serves this {@link IBuffer}
	 */
	public IMirror getMirror();
	
	/**
	 * If an {@link IMirror} is used to synchronize the {@link IBuffer} instances accessible to the reader and writer,
	 * this value indicates the number of {@link Record}s that the writer {@link IMirror}} will limit its sending
	 * phase to choke large transfers. This is a configuration parameter that can be set only before {@link IBuffer} 
	 * initialization
	 * 
	 * @param size the size of the mirroring window
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public void setMirrorBuffer(int size) throws GRS2BufferException;
	/**
	 * If an {@link IMirror} is used to synchronize the {@link IBuffer} instances accessible to the reader and writer,
	 * this value indicates the number of {@link Record}s that the writer {@link IMirror}} will limit its sending
	 * phase to choke large transfers
	 * 
	 * @return the size of the mirroring window
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public int getMirrorBuffer() throws GRS2BufferException;
	
	/**
	 * Retrieves the {@link TransportDirective} set for the {@link IBuffer}. This value is used by the contained
	 * items in case they have defined {@link TransportDirective#Inherit} and it is not resolved further down the 
	 * item hierarchy. This value should be permitted to be {@link TransportDirective#Inherit}
	 * 
	 * @return the set transport directive
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public TransportDirective getTransportDirective() throws GRS2BufferException;
	/**
	 * Sets the {@link TransportDirective} set for the {@link IBuffer}. This value is used by the contained
	 * items in case they have defined {@link TransportDirective#Inherit} and it is not resolved further down the 
	 * item hierarchy. This value should be permitted to be {@link TransportDirective#Inherit}. This is a configuration 
	 * parameter that can be set only before {@link IBuffer} initialization
	 * 
	 * @param directive the transport directive
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public void setTransportDirective(TransportDirective directive) throws GRS2BufferException;
	/**
	 * Resolves the set {@link TransportDirective} set for the {@link IBuffer} through 
	 * {@link IBuffer#setTransportDirective(TransportDirective)}. This is the operation that should used by the underlying
	 * items when resolving their {@link TransportDirective} to avoid reaching the indecision point where they
	 * end up with a final {@link TransportDirective#Inherit} directive. This method should never return a value
	 * of {@link TransportDirective#Inherit}
	 * 
	 * @return the transport directive to be used by a component that has not resolved its {#link {@link TransportDirective}
	 * further down the item hierarchy
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public TransportDirective resolveTransportDirective() throws GRS2BufferException;
	
	/**
	 * Using this object, a writer can be blocked using a standard synchronized / wait block to be notified
	 * by the {@link IBuffer} when the reader has left its available {@link Record}s under a threshold calculated
	 * using the {@link IBuffer#getCapacity()} and {@link IBuffer#getNotificationThreshold()}
	 * 
	 * @return the object that can be used for the synchronization
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
//	public Object getWriterThresholdNotificationObject() throws GRS2BufferException;
	/**
	 * Using this object, a reader can be blocked using a standard synchronized / wait block to be notified
	 * by the {@link IBuffer} when the writer has increased its available {@link Record}s over a threshold calculated
	 * using the {@link IBuffer#getCapacity()} and {@link IBuffer#getNotificationThreshold()}
	 * 
	 * @return the object that can be used for the synchronization
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
//	public Object getReaderThresholdNotificationObject() throws GRS2BufferException;
	
	/**
	 * Using this object, a writer can be blocked using a standard synchronized / wait block to be notified
	 * by the {@link IBuffer} when the reader has reduced its available {@link Record}s by one
	 * 
	 * @return the object that can be used for the synchronization
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public Object getWriterImmediateNotificationObject() throws GRS2BufferException;
	/**
	 * Using this object, a reader can be blocked using a standard synchronized / wait block to be notified
	 * by the {@link IBuffer} when the writer has increased its available {@link Record}s by one
	 * 
	 * @return the object that can be used for the synchronization
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public Object getReaderImmediateNotificationObject() throws GRS2BufferException;
	
	/**
	 * After all configuration values have been set, this method performs all the initialization needed to get the
	 * {@link IBuffer} ready to receive {@link Record}s from a writer and serve them to a reader. After this point no
	 * configuration parameters can be set and the {@link Status} of the buffer is changed from {@link Status#Init}
	 * to {@link Status#Open}. Initialization must only take place once per each {@link IBuffer} instance
	 * 
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public void initialize() throws GRS2BufferException;
	/**
	 * Retrieves the {@link Status} of the {@link IBuffer}
	 * 
	 * @return the status
	 */
	public Status getStatus();
	/**
	 * Closes the authoring side of the {@link IBuffer} after this invocation, no more {@link Record}s can be added
	 * to the {@link IBuffer} but a reader can still consume any {@link Record}s that are still available. The status
	 * of the {@link IBuffer} is changed to {@link Status#Close}
	 * 
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public void close() throws GRS2BufferException;
	/**
	 * Disposes the {@link IBuffer} and all its underlying resources. After an invocation to this method, no operations
	 * can be performed on the {@link IBuffer}. The status is changed to {@link Status#Dispose}. The implementations 
	 * of this method should make sure that they dispose all resources relevant to the {@link IBuffer}
	 */
	public void dispose();

	/**
	 * Retrieves the number of {@link Record}s that have in total passed through the {@link IBuffer}. If the {@link Status} of
	 * the {@link IBuffer} is not {@link gr.uoa.di.madgik.grs.buffer.IBuffer.Status#Close}, then this number is not the final number of records. Once the 
	 * status has been set to {@link Status#Close}, this is the final number of {@link Record}s moved though the 
	 * {@link IBuffer} 
	 * 
	 * @return the total number of {@link Record}s
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public long totalRecords() throws GRS2BufferException;
	/**
	 * Retrieves the number of {@link Record}s that are currently available to be read
	 * 
	 * @return the number of available {@link Record}s
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public int availableRecords() throws GRS2BufferException;
	
	/**
	 * Attempts to place a {@link Record} to the {@link IBuffer}. If the operation was successful, true will be returned.
	 * If the {@link IBuffer} has reached its capacity, false will be returned. This operation should be successful if
	 * the number of available records, {@link IBuffer#availableRecords()}, is less than the {@link IBuffer}'s capacity, 
	 * {@link IBuffer#getCapacity()}. After a successful invocation, the available records are increased by one.
	 * 
	 * @param record the {@link Record} to add
	 * @return true on successful addition, false otherwise
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 * @throws GRS2RecordException The status of the {@link Record} does not allow for the operation to be executed
	 */
	public boolean put(Record record) throws GRS2BufferException,GRS2RecordException;
	
	/**
	 * Attempts to retrieve a {@link Record} from the {@link IBuffer}. If the operation is successful, the {@link Record}
	 * will be returned. If the number of available records is 0, then null will be returned. This operation should return
	 * successfully with the {@link Record} if the number of available records, {@link IBuffer#availableRecords()} is greater
	 * than 0. After a successful invocation, the available records are decreased by one.
	 * 
	 * @return the retrieved {@link Record} or null of none is available
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public Record get() throws GRS2BufferException;
	
	/**
	 * Attempts to locate the {@link Record} with the provided id in the underlying {@link IBuffer} structures. If this 
	 * {@link Record} is available and not yet served to the reader or it has already been served but remains in the 
	 * overflow buffers the size of which is defined by {@link IBuffer#getConcurrentPartialCapacity()} then it must be
	 * made available  
	 * 
	 * @param recordIndex the {@link Record} id which as documented in the {@link Record} component must coincide with 
	 * the order by which it was placed to the {@link IBuffer}
	 * @return the {@link Record} if found, null otherwise
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public Record locate(long recordIndex) throws GRS2BufferException;
	
	/**
	 * In case this {@link IBuffer} is registered to be served to a reader, the key by which it is registered is provided
	 * 
	 * @param key the registry key
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public void setKey(String key) throws GRS2BufferException;
	/**
	 * Retrieves the key under which the {@link IBuffer} is registered and accessible through the registry
	 * 
	 * @return the registry key
	 */
	public String getKey();
	
	/**
	 * Emits a {@link BufferEvent} targeted to the other end of the {@link IBuffer} usage. A {@link BufferEvent}
	 * emitted by the writer will only be received by a reader and vice versa.
	 * 
	 * @param event the event to emit
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public void emit(BufferEvent event) throws GRS2BufferException;
	
	/**
	 * Receives a {@link BufferEvent} emitted by the other end of the {@link IBuffer} usage. A {@link BufferEvent}
	 * emitted by the writer will only be received by a reader and vice versa.
	 * 
	 * @param source the events that are of interest are the ones of the defined source
	 * @return A previously emitted event or null if none is available
	 * @throws GRS2BufferException The status of the {@link IBuffer} does not allow for the operation to be executed
	 */
	public BufferEvent receive(EventSource source) throws GRS2BufferException;
}
