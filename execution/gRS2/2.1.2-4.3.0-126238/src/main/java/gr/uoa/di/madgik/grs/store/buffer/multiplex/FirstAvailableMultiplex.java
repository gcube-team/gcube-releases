package gr.uoa.di.madgik.grs.store.buffer.multiplex;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreEntry;
import gr.uoa.di.madgik.grs.store.buffer.GRS2BufferStoreAccessException;
import gr.uoa.di.madgik.grs.store.buffer.GRS2BufferStoreException;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreEntry.EntryStatus;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore.MultiplexType;

import java.util.ArrayList;

/**
 * Implementation of the {@link IMultiplex} interface for the {@link MultiplexType#FirstAvailable} type of multiplexing
 * 
 * @author gpapanikos
 *
 */
public class FirstAvailableMultiplex implements IMultiplex
{
	private ArrayList<BufferStoreEntry> entries=null;
	private ArrayList<BufferStoreEntry> activeEntries=null;
	private TimeStruct timeout=new TimeStruct(ForwardReader.DefaultIteratorTimeout, ForwardReader.DefaultIteratorTimeUnit);
	private IBufferStore bufferStore=null;
	private Object modificationNotify=null;
	
	/**
	 * Creates a new instance
	 */
	public FirstAvailableMultiplex() {}

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
		this.activeEntries=new ArrayList<BufferStoreEntry>(entries);
		for(BufferStoreEntry entry: this.entries)
		{
			TimeStruct str=new TimeStruct(entry.getReaderTimeout(), entry.getReaderTimeoutTimeUnit());
			if(this.timeout.compareTo(str)>0) this.timeout=str;
		}
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
		//readers and manager is cleared further up the stack
		if(this.activeEntries!=null)
		{
			this.activeEntries.clear();
			this.activeEntries=null;
		}
		this.timeout=null;
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
			ArrayList<BufferStoreEntry> inactive=new ArrayList<BufferStoreEntry>();
			while(this.activeEntries.size()!=0)
			{
				inactive.clear();
				boolean foundSome=false;
				for(BufferStoreEntry entry : this.activeEntries)
				{
					if(entry.getReader().getStatus()==Status.Dispose || 
							(entry.getReader().getStatus()==Status.Close && 
							entry.getReader().availableRecords()==0))
					{
						while(true && entry.getReader().getStatus()!=Status.Dispose)
						{
							BufferEvent event = entry.getReader().receive();
							if(event!=null) entry.getEventManager().persist(event);
							else break;
						}
						inactive.add(entry);
					}
					else
					{
						while(true)
						{
							this.bufferStore.markActivity();
							Record rec=entry.getReader().get();
							if(rec==null) break;
							foundSome=true;
							entry.getRecordManager().persist(rec);
							while(true)
							{
								BufferEvent event = entry.getReader().receive();
								if(event!=null) entry.getEventManager().persist(event);
								else break;
							}
							synchronized (this.modificationNotify) { this.modificationNotify.notifyAll(); }
						}
					}
				}
				for(BufferStoreEntry entry : inactive)
				{
					while(true && entry.getReader().getStatus()!=Status.Dispose)
					{
						BufferEvent event = entry.getReader().receive();
						if(event!=null) entry.getEventManager().persist(event);
						else break;
					}
					this.bufferStore.markActivity();
					this.activeEntries.remove(entry);
					entry.getReader().close();
					entry.setStatus(EntryStatus.Close);
				}
				synchronized (this.modificationNotify) { this.modificationNotify.notifyAll(); }
				if(!foundSome && this.activeEntries.size()>0)
				{
					//getting randomly the first available to wait for next. To do proper check there
					//should be a thread for each active reader calling the waitAvailable
					//long start=System.currentTimeMillis();
					this.activeEntries.get(0).getReader().waitAvailable(this.timeout.timeout, this.timeout.unit);
					//System.out.println("BLOCKED "+(System.currentTimeMillis()-start));
				}
				this.bufferStore.markActivity();
				synchronized (this.modificationNotify) { this.modificationNotify.notifyAll(); }
			}
			this.bufferStore.markActivity();
			synchronized (this.modificationNotify) { this.modificationNotify.notifyAll(); }
			//System.out.println("EXITING MULTIPLEX");
		}catch(GRS2Exception ex)
		{
			throw new GRS2BufferStoreAccessException("Could not complete multiplexing operation",ex);
		}
	}
}
