package gr.uoa.di.madgik.grs.store.buffer.multiplex;

import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreEntry;
import gr.uoa.di.madgik.grs.store.buffer.GRS2BufferStoreException;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore.MultiplexType;

import java.util.ArrayList;

/**
 * Interfaces implemented by classes that are able to multiplex a number of incoming locators based on a specific
 * order or algorithm. The multiplexing type they perform is one of the ones defined in {@link MultiplexType}.
 * Implementations of this class must define a default no argument constructor
 * 
 * @author gpapanikos
 *
 */
public interface IMultiplex
{
	/**
	 * Set the synchronization object to be used in a standard wait / notify block to notify when a new {@link Record}
	 * has been made available from any of the readers 
	 * 
	 * @param notify the synchronization object
	 */
	public void setModificationNotify(Object notify);
	
	/**
	 * The entries over which the implementation needs to act
	 * 
	 * @param entries the entries to multiplex
	 */
	public void setEntries(ArrayList<BufferStoreEntry> entries);
	
	/**
	 * The {@link IBufferStore} that is receiving the multiplexed {@link Record}s
	 * 
	 * @param bufferStore the {@link IBufferStore} this multiplex implementation is acting for
	 */
	public void setBufferStore(IBufferStore bufferStore);
	
	/**
	 * Dispose all internally managed information but not the externally provided entries
	 */
	public void dispose();

	/**
	 * Perform the multiplexing operation
	 * 
	 * @throws GRS2BufferStoreException there was a problem during the multiplexing procedure
	 */
	public void multiplex() throws GRS2BufferStoreException;
}
