package gr.uoa.di.madgik.grs.store.buffer.multiplex;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreEntry;
import gr.uoa.di.madgik.grs.store.buffer.GRS2BufferStoreAccessException;
import gr.uoa.di.madgik.grs.store.buffer.GRS2BufferStoreException;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore;
import gr.uoa.di.madgik.grs.store.buffer.BufferStoreEntry.EntryStatus;
import gr.uoa.di.madgik.grs.store.buffer.IBufferStore.MultiplexType;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the {@link IDemultiplex} interface for the {@link MultiplexType#FIFO} type of multiplexing
 * 
 * @author gpapanikos
 *
 */
public class FifoDemultiplex implements IDemultiplex
{
	private ArrayList<BufferStoreEntry> entries=null;
	private ArrayList<RecordDefinition[]> definitionsList=null;
	private RecordWriter<Record> writer=null;
	private IBufferStore store=null;
	private Object modificationNotify=null;
	private long sendEvents=0;
	
	/**
	 * Create a new instance
	 */
	public FifoDemultiplex(){}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IDemultiplex#setModificationNotify(java.lang.Object)
	 */
	public void setModificationNotify(Object notify)
	{
		this.modificationNotify=notify;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IDemultiplex#setEntries(java.util.ArrayList)
	 */
	public void setEntries(ArrayList<BufferStoreEntry> entries)
	{
		this.entries=entries;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IDemultiplex#setDefinitionsList(java.util.ArrayList)
	 */
	public void setDefinitionsList(ArrayList<RecordDefinition[]> definitionsList)
	{
		this.definitionsList=definitionsList;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IDemultiplex#setWriter(gr.uoa.di.madgik.grs.writer.RecordWriter)
	 */
	public void setWriter(RecordWriter<Record> writer)
	{
		this.writer=writer;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IDemultiplex#setBufferStore(gr.uoa.di.madgik.grs.store.buffer.IBufferStore)
	 */
	public void setBufferStore(IBufferStore store)
	{
		this.store=store;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IDemultiplex#dispose()
	 */
	public void dispose()
	{
		//nothing to clear. readers and manager is cleared further up the stack
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IDemultiplex#demultiplex()
	 */
	public void demultiplex() throws GRS2BufferStoreException
	{
		try
		{
			this.store.markActivity();
			for(int i=0;(i<this.entries.size() && this.writer.getStatus()==Status.Open);i+=1)
			{
				long timeout=this.entries.get(i).getReaderTimeout();
				TimeUnit unit=this.entries.get(i).getReaderTimeoutTimeUnit();
				long recordCount=0;
				while(true)
				{
					this.store.markActivity();
					for(long se=this.sendEvents;se<this.entries.get(i).getEventManager().getEventCount();se+=1)
					{
						BufferEvent event=this.entries.get(i).getEventManager().retrieveByIndex(se);
						this.sendEvents+=1;
						this.writer.emit(event);
					}
					if(recordCount==this.entries.get(i).getRecordManager().getRecordCount() && 
							this.entries.get(i).getStatus()==EntryStatus.Close)
					{
						break;
					}
					Record record=this.entries.get(i).getRecordManager().retrieveByIndex(recordCount,true);
					if(record==null)
					{
						synchronized (modificationNotify) 
						{
//							long start=System.currentTimeMillis();
//							System.out.println("blocking to be notified");
							try{this.modificationNotify.wait(unit.toMillis(timeout));}catch(Exception ex){}
//							System.out.println("blocked to be notified for "+(System.currentTimeMillis()-start));
						}
					}
					else
					{
						int defCount=0;
						for(int d=0;d<i;d+=1) defCount+=this.definitionsList.get(d).length;
						record.setDefinitionIndex(defCount+record.getDefinitionIndex());
//						System.out.println("demultiplexing record");
						if(!this.writer.put(record,timeout,unit)) break;
						recordCount+=1;
					}
				}
				this.store.markActivity();
			}
	//		System.out.println("closing writer");
			this.store.markActivity();
			if(writer.getStatus()!=Status.Dispose) writer.close();
		}catch(GRS2Exception ex)
		{
			throw new GRS2BufferStoreAccessException("Could not complete multiplexing operation",ex);
		}
	}

}
