package gr.uoa.di.madgik.grs.store.buffer.multiplex;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreEntry;
import gr.uoa.di.madgik.grs.store.buffer.GRS2BufferStoreAccessException;
import gr.uoa.di.madgik.grs.store.buffer.GRS2BufferStoreException;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreEntry.EntryStatus;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore.MultiplexType;

import java.util.ArrayList;

/**
 * Implementation of the {@link IMultiplex} interface for the {@link MultiplexType#FIFO} type of multiplexing
 * 
 * @author gpapanikos
 *
 */
public class FifoMultiplex implements IMultiplex
{
	private ArrayList<BufferStoreEntry> entries=null;
	private IBufferStore bufferStore=null;
	private Object modificationNotify=null;
	
	/**
	 * Create a new instance
	 */
	public FifoMultiplex() {}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IMultiplex#setModificationNotify(java.lang.Object)
	 */
	public void setModificationNotify(Object notify)
	{
		this.modificationNotify=notify;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IMultiplex#setEntries(java.util.ArrayList)
	 */
	public void setEntries(ArrayList<BufferStoreEntry> entries)
	{
		this.entries=entries;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IMultiplex#setBufferStore(gr.uoa.di.madgik.grs.store.buffer.IBufferStore)
	 */
	public void setBufferStore(IBufferStore bufferStore)
	{
		this.bufferStore=bufferStore;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IMultiplex#dispose()
	 */
	public void dispose()
	{
		//nothing to clear. readers and manager is cleared further up the stack
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IMultiplex#multiplex()
	 */
	public void multiplex() throws GRS2BufferStoreException
	{
		try
		{
			for(BufferStoreEntry entry : this.entries)
			{
				for(Record rec : entry.getReader())
				{
					this.bufferStore.markActivity();
					entry.getRecordManager().persist(rec);
					while(true)
					{
						BufferEvent event = entry.getReader().receive();
						if(event!=null) entry.getEventManager().persist(event);
						else break;
					}
					synchronized (this.modificationNotify) { this.modificationNotify.notifyAll(); }
				}
				while(true)
				{
					BufferEvent event = entry.getReader().receive();
					if(event!=null) entry.getEventManager().persist(event);
					else break;
				}
				this.bufferStore.markActivity();
				entry.getReader().close();
				entry.setStatus(EntryStatus.Close);
				synchronized (this.modificationNotify) { this.modificationNotify.notifyAll(); }
			}
			this.bufferStore.markActivity();
			synchronized (this.modificationNotify) { this.modificationNotify.notifyAll(); }
		}catch(GRS2Exception ex)
		{
			throw new GRS2BufferStoreAccessException("Could not complete multiplexing operation",ex);
		}
	}
}
