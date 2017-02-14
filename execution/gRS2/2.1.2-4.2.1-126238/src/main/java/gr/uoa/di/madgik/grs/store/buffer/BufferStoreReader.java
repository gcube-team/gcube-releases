package gr.uoa.di.madgik.grs.store.buffer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.registry.GRSRegistry;
import gr.uoa.di.madgik.grs.store.buffer.multiplex.FifoDemultiplex;
import gr.uoa.di.madgik.grs.store.buffer.multiplex.FirstAvailableDemultiplex;
import gr.uoa.di.madgik.grs.store.buffer.multiplex.IDemultiplex;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

/**
 * This class accesses the content stored through a {@link IBufferStore}, restores the incoming and stored {@link Record}s
 * and publishes them in a new {@link IBuffer} using a {@link RecordWriter} whose locator returns to be used by any interested 
 * reader. The demultiplexing procedure is performed based on the multiplexing type that was set in the respective 
 * {@link IBufferStore}
 * 
 * @author gpapanikos
 *
 */
public class BufferStoreReader extends Thread
{
	private static Logger logger=Logger.getLogger(BufferStoreReader.class.getName());
	private String key=null;
	private IBufferStore store =null;
	private ArrayList<BufferStoreEntry> entries=null;
	private RecordDefinition[] definitions=null;
	private ArrayList<RecordDefinition[]> definitionsList=null;
	private RecordWriter<Record> writer=null;
	private IWriterProxy proxy=null;
	private IDemultiplex algo=null;

	/**
	 * Creates a new instance
	 * 
	 * @param key the key with which the {@link IBufferStore} that is to be demultiplexed is associated
	 * @param proxy the proxy to use for the {@link RecordWriter} that will make the restored {@link Record}s available
	 * @throws GRS2BufferStoreInvalidOperationException the {@link IBufferStore} did not yield any {@link BufferStoreEntry} to use 
	 * @throws GRS2BufferStoreAccessException The {@link RecordDefinition}s of the {@link Record}s could not be found
	 * @throws GRS2BufferStoreInvalidArgumentException No {@link IBufferStore} was found
	 * @throws GRS2WriterException Could nmot initialize and retrieve the locator of the {@link RecordWriter}
	 */
	public BufferStoreReader(String key,IWriterProxy proxy) throws GRS2BufferStoreInvalidOperationException, GRS2BufferStoreAccessException, GRS2BufferStoreInvalidArgumentException, GRS2WriterException
	{
		this.key=key;
		this.proxy=proxy;
		this.initialize();
	}
	
	/**
	 * Starts the background execution of the demultiplex procedure and authors the restored {@link Record}s
	 * using an {@link RecordWriter} whose locator is then returned 
	 * 
	 * @return the locator to the authored {@link IBuffer}
	 * @throws GRS2WriterException the state of the demultiplexing procedure does not permit this operation to be completed
	 */
	public URI populate() throws GRS2WriterException
	{
		this.setName("buffer store access");
		this.setDaemon(true);
		this.start();
		return this.writer.getLocator();
	}
	
	/**
	 * Disposes all managed resources
	 */
	public void dispose()
	{
		if(algo!=null) try{algo.dispose();}catch(Exception ex){}
		if(this.writer!=null) try{this.writer.dispose();}catch(Exception ex){}
		this.entries=null;
		this.definitions=null;
		this.definitionsList=null;
		this.proxy=null;
		this.algo=null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Once the {@link BufferStoreReader#populate()} method is invoked, the execution is performed in a background daemon 
	 * thread that depending on the {@link IBufferStore#getMultiplexType()} value, will forward the execution on the
	 * appropriate {@link IDemultiplex} implementation. Depending on the multiplex type defined, the implementations used
	 * are {@link FifoDemultiplex} and {@link FirstAvailableDemultiplex}
	 * </p>
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		try
		{
			switch(this.store.getMultiplexType())
			{
				case FIFO: { algo=new FifoDemultiplex(); break; }
				case FirstAvailable: { algo=new FirstAvailableDemultiplex(); break; }
				default: throw new GRS2BufferStoreInvalidArgumentException("Not recognizable multiplex type "+this.store.getMultiplexType().toString());
			}
			this.writer.setBufferStore(this.store);
			algo.setDefinitionsList(this.definitionsList);
			algo.setEntries(this.entries);
			algo.setWriter(this.writer);
			algo.setBufferStore(this.store);
			algo.setModificationNotify(this.store.getModificationObject());
			algo.demultiplex();
		}catch(Exception ex)
		{
			try{if(this.writer!=null) this.writer.dispose();}catch(Exception e){}
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING,"Could not complete store access",ex);
		}
	}

	private void initialize() throws GRS2BufferStoreInvalidArgumentException, GRS2BufferStoreInvalidOperationException, GRS2BufferStoreAccessException, GRS2WriterException
	{
		this.store = GRSRegistry.Registry.getStore(this.key);
		if(this.store==null) throw new GRS2BufferStoreInvalidArgumentException("Could not locate buffer store with provided key "+this.key);
		this.store.associateStoreReader(this);
		this.entries=store.getEntries();
		if(this.entries==null) throw new GRS2BufferStoreInvalidOperationException("No available buffer entries");
		this.initDefinitions();
		this.writer=new RecordWriter<Record>(this.proxy, this.definitions);
		this.store.markActivity();
	}
	
	private void initDefinitions() throws GRS2BufferStoreAccessException
	{
		this.definitionsList=new ArrayList<RecordDefinition[]>();
		ArrayList<RecordDefinition> definitions=new ArrayList<RecordDefinition>();
		for(BufferStoreEntry entry : this.entries)
		{
			RecordDefinition[] defs=entry.getDefinitions();
			definitions.addAll(Arrays.asList(defs));
			this.definitionsList.add(defs);
		}
		this.definitions=definitions.toArray(new RecordDefinition[0]);
	}
}
