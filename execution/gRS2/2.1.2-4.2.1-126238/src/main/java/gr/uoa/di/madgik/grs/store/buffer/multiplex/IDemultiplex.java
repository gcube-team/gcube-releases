package gr.uoa.di.madgik.grs.store.buffer.multiplex;

import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreEntry;
import gr.uoa.di.madgik.grs.store.buffer.GRS2BufferStoreException;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.util.ArrayList;

/**
 * Interfaces implemented by classes that are able to demultiplex a number of stored locator data based on a specific
 * order or algorithm. The demultiplexing type they perform is one of the ones defined in {@link gr.uoa.di.madgik.grs.store.buffer.IBufferStore.MultiplexType}. 
 * Implementations of this class must define a default no argument constructor
 * 
 * @author gpapanikos
 *
 */
public interface IDemultiplex
{
	/**
	 * Set the synchronization object to be used in a standard wait / notify block to be notified when a new {@link Record}
	 * has been made available from the respective {@link IMultiplex} 
	 * 
	 * @param notify the synchronization object
	 */
	public void setModificationNotify(Object notify);
	
	/**
	 * The entries over which the implementation needs to act
	 * 
	 * @param entries the entries to demultiplex
	 */
	public void setEntries(ArrayList<BufferStoreEntry> entries);
	
	/**
	 * Sets the list of {@link RecordDefinition}s in the order of the managed {@link BufferStoreEntry} list
	 * 
	 * @param definitionsList the list of record definitions
	 */
	public void setDefinitionsList(ArrayList<RecordDefinition[]> definitionsList);
	
	/**
	 * Sets the {@link RecordWriter} that will receive the restored {@link Record}s
	 * 
	 * @param writer the writer to store the restored {@link Record}s
	 */
	public void setWriter(RecordWriter<Record> writer);
	
	/**
	 * The {@link IBufferStore} over which the demultiplexing is performed
	 * 
	 * @param store the {@link IBufferStore} over which the demultiplexing is performed
	 */
	public void setBufferStore(IBufferStore store);
	
	/**
	 * Dispose all internally managed information but not the externally provided entries
	 */
	public void dispose();

	/**
	 * Perform the demultiplexing operation
	 * 
	 * @throws GRS2BufferStoreException there was a problem during the demultiplexing procedure
	 */
	public void demultiplex() throws GRS2BufferStoreException;
}
