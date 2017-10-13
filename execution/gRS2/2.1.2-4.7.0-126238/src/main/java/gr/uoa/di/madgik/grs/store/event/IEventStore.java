package gr.uoa.di.madgik.grs.store.event;

import gr.uoa.di.madgik.grs.events.BufferEvent;

/**
 * This interface defines a way a persistency manager can be interfaced to enabling persistency of {@link BufferEvent}s
 * and retrieval based on the order by which they were stored. The {@link BufferEvent} persistency
 * must be handled by the respective {@link BufferEvent#deflate(java.io.DataOutput)} operation while the retrieval
 * by the respective {@link BufferEvent#inflate(java.io.DataInput)} method. The storage medium over which the 
 * {@link BufferEvent}s are persisted is left to the implementation specifics 
 * 
 * @author gpapanikos
 *
 */
public interface IEventStore
{
	/**
	 * Retrieves the number of {@link BufferEvent}s stored using this {@link IEventStore}
	 * 
	 * @return the number of {@link BufferEvent}s stored
	 */
	public long getEventCount();

	/**
	 * Persists the provided {@link BufferEvent}
	 * 
	 * @param event the {@link BufferEvent} to persist
	 * @throws GRS2EventStoreException the state of the {@link IEventStore} does not allow for this operation to be completed
	 */
	public void persist(BufferEvent event) throws GRS2EventStoreException;

	/**
	 * Retrieve a previously stored {@link BufferEvent} based on the index by which it was stored
	 * 
	 * @param eventIndex The index by which the {@link BufferEvent} to be retrieved was stored
	 * @return the {@link BufferEvent} retrieved 
	 * @throws GRS2EventStoreException the state of the {@link IEventStore} does not allow for this operation to be completed
	 */
	public BufferEvent retrieveByIndex(long eventIndex) throws GRS2EventStoreException;

	/**
	 * Disposes the {@link IEventStore} instance as well as any permanent storage resources occupied
	 * 
	 * @throws GRS2EventStoreException the state of the {@link IEventStore} does not allow for this operation to be completed
	 */
	public void dispose() throws GRS2EventStoreException;
}
