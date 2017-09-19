package gr.uoa.di.madgik.grs.store.buffer.multiplex;

import gr.uoa.di.madgik.grs.GRS2Exception;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
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

/**
 * Implementation of the {@link IDemultiplex} interface for the {@link MultiplexType#FirstAvailable} type of demultiplexing
 * 
 * @author gpapanikos
 *
 */
public class FirstAvailableDemultiplex implements IDemultiplex
{
	private ArrayList<BufferStoreEntry> entries=null;
	private ArrayList<BufferStoreEntry> activeEntries=null;
	private ArrayList<RecordDefinition[]> definitionsList=null;
	private ArrayList<Long> entryRecordCount=null;
	private RecordWriter<Record> writer=null;
	private TimeStruct timeout=new TimeStruct(ForwardReader.DefaultIteratorTimeout, ForwardReader.DefaultIteratorTimeUnit);
	private IBufferStore store=null;
	private Object modificationNotify=null;
	private long sendEvents=0;
	
	/**
	 * Create a new instance
	 */
	public FirstAvailableDemultiplex(){}

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
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IDemultiplex#setDefinitionsList(java.util.ArrayList)
	 */
	public void setDefinitionsList(ArrayList<RecordDefinition[]> definitionsList)
	{
		this.definitionsList=definitionsList;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IDemultiplex#setEntries(java.util.ArrayList)
	 */
	public void setEntries(ArrayList<BufferStoreEntry> entries)
	{
		this.entries=entries;
		this.activeEntries=new ArrayList<BufferStoreEntry>(entries);
		this.entryRecordCount=new ArrayList<Long>();
		for(BufferStoreEntry entry: this.entries)
		{
			TimeStruct str=new TimeStruct(entry.getReaderTimeout(), entry.getReaderTimeoutTimeUnit());
			if(this.timeout.compareTo(str)>0) this.timeout=str;
			this.entryRecordCount.add(new Long(0));
		}
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
		//the rest are cleared further up the stack
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
	 * @see gr.uoa.di.madgik.grs.store.buffer.multiplex.IDemultiplex#demultiplex()
	 */
	public void demultiplex() throws GRS2BufferStoreException
	{
		try
		{
			this.store.markActivity();
			ArrayList<BufferStoreEntry> inactive=new ArrayList<BufferStoreEntry>();
			while(this.activeEntries.size()!=0 && this.writer.getStatus()==Status.Open)
			{
				inactive.clear();
				boolean foundSome=false;
				for(int i=0;i<this.activeEntries.size();i+=1)
				{
					for(long se=this.sendEvents;se<this.entries.get(i).getEventManager().getEventCount();se+=1)
					{
						BufferEvent event=this.entries.get(i).getEventManager().retrieveByIndex(se);
						this.sendEvents+=1;
						this.writer.emit(event);
					}
					if(this.entryRecordCount.get(i)==this.activeEntries.get(i).getRecordManager().getRecordCount() && 
							this.activeEntries.get(i).getStatus()==EntryStatus.Close) inactive.add(this.activeEntries.get(i));
					else
					{
						while(true)
						{
							Record record=this.activeEntries.get(i).getRecordManager().retrieveByIndex(this.entryRecordCount.get(i),true);
							if(record==null) break;
							else
							{
								this.store.markActivity();
								foundSome=true;
								int defCount=0;
								for(int d=0;d<i;d+=1) defCount+=this.definitionsList.get(d).length;
								record.setDefinitionIndex(defCount+record.getDefinitionIndex());
								if(!this.writer.put(record,timeout.timeout,timeout.unit))
								{
									break;
								}
								this.entryRecordCount.set(i, this.entryRecordCount.get(i)+1);
							}
						}
					}
				}
				this.store.markActivity();
				for(BufferStoreEntry entry : inactive) this.activeEntries.remove(entry);
				if(!foundSome)
				{
					synchronized (this.modificationNotify)
					{
						if(this.activeEntries.size()!=0)
						{
							boolean foundavailable=false;
							for(int i=0;i<this.activeEntries.size();i+=1)
							{
								if(this.entryRecordCount.get(i)<this.activeEntries.get(i).getRecordManager().getRecordCount())
								{
									foundavailable=true;
								}
							}
							boolean activeReaders=false;
							for(int i=0;i<this.activeEntries.size();i+=1)
							{
								if(this.activeEntries.get(i).getStatus()==EntryStatus.Open) activeReaders=true;
							}						
							if(!foundavailable && activeReaders)
							{
								//long start=System.currentTimeMillis();
								try{this.modificationNotify.wait(timeout.unit.toMillis(timeout.timeout));}catch(Exception ex){}
								//System.out.println("blocked to be notified for "+(System.currentTimeMillis()-start));
							}
						}
					}
				}
			}
			this.store.markActivity();
			if(writer.getStatus()!=Status.Dispose) writer.close();
		}catch(GRS2Exception ex)
		{
			throw new GRS2BufferStoreAccessException("Could not complete multiplexing operation",ex);
		}
	}

}
