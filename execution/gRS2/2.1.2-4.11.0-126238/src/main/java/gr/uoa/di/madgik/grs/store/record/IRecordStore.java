package gr.uoa.di.madgik.grs.store.record;

import gr.uoa.di.madgik.grs.record.Record;

/**
 * This interface defines a way a persistency manager can be interfaced to enabling persistency of {@link Record}s
 * and retrieval based on either their ids, or the order by which they were stored. The {@link Record} persistency
 * must be handled by the respective {@link Record#deflate(java.io.DataOutput)} operation while the retrieval
 * by the respective {@link Record#inflate(java.io.DataInput, boolean)} method. The storage medium over which the 
 * {@link Record}s are persisted is left to the implementation specifics
 * 
 * @author gpapanikos
 *
 */
public interface IRecordStore
{
	/**
	 * Whether the {@link IRecordStore#retrieveByIndex(long, boolean)} operation should be enabled or not
	 * 
	 * @param enableOrder whether the {@link IRecordStore#retrieveByIndex(long, boolean)} operation should be enabled or not
	 */
	public void enableOrder(boolean enableOrder);
	
	/**
	 * Retrieves the number of {@link Record}s stored using this {@link IRecordStore}
	 * 
	 * @return the number of {@link Record}s stored
	 */
	public long getRecordCount();
	
	/**
	 * Persists the provided {@link Record}
	 * 
	 * @param record the {@link Record} to persist
	 * @throws GRS2RecordStoreException the state of the {@link IRecordStore} does not allow for this operation to be completed
	 */
	public void persist(Record record) throws GRS2RecordStoreException;
	
	/**
	 * Retrieves a previously stored {@link Record} based on its id
	 * 
	 * @param recordID the ID of the {@link Record} to be retrieved
	 * @param reset whether during the {@link Record#inflate(java.io.DataInput, boolean)} invocation
	 * the reset parameter should be set to true or false
	 * @return the {@link Record} retrieved 
	 * @throws GRS2RecordStoreException the state of the {@link IRecordStore} does not allow for this operation to be completed
	 */
	public Record retrieve(long recordID, boolean reset) throws GRS2RecordStoreException;
	
	/**
	 * Retrieve a previously stored {@link Record} based on the index by which it was stored. This method is only
	 * available if before the first time a {@link IRecordStore#persist(Record)} was invoked the method 
	 * {@link IRecordStore#enableOrder(boolean)} has been set to true
	 * 
	 * @param recordIndex The index by which the {@link Record} to be retrieved was stored
	 * @param reset whether during the {@link Record#inflate(java.io.DataInput, boolean)} invocation
	 * the reset parameter should be set to true or false
	 * @return the {@link Record} retrieved 
	 * @throws GRS2RecordStoreException the state of the {@link IRecordStore} does not allow for this operation to be completed
	 */
	public Record retrieveByIndex(long recordIndex, boolean reset) throws GRS2RecordStoreException;
	
	/**
	 * Disposes the {@link IRecordStore} instance as well as any permanent storage resources occupied
	 * 
	 * @throws GRS2RecordStoreException the state of the {@link IRecordStore} does not allow for this operation to be completed
	 */
	public void dispose() throws GRS2RecordStoreException;
}
