package gr.uoa.di.madgik.grs.store.record;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.UUID;
import gr.uoa.di.madgik.grs.record.Record;

/**
 * Implementation of the {@link IRecordStore} using a {@link RandomAccessFile} over a local file as the persistency medium
 * 
 * @author gpapanikos
 *
 */
public class FileRecordStore implements IRecordStore 
{
	private File recordFile=null;
	private RandomAccessFile rand=null;
	private Hashtable<Long, Long> mappingSimple=null;
	private Hashtable<Long, Long> mappingOrdered=null;
	private boolean enableOrder=false;
	private long recordCount=0;
	
	/**
	 * Creates a new instance
	 * 
	 * @throws IOException
	 */
	public FileRecordStore() throws IOException
	{
		this.recordFile=File.createTempFile(UUID.randomUUID().toString(), null);
		this.recordFile.deleteOnExit();
		this.rand=new RandomAccessFile(this.recordFile, "rw");
		this.mappingSimple=new Hashtable<Long, Long>();
		if(this.enableOrder) this.mappingOrdered=new Hashtable<Long, Long>();
		this.recordCount=0;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.record.IRecordStore#enableOrder(boolean)
	 */
	public void enableOrder(boolean enableOrder)
	{
		this.enableOrder=enableOrder;
		if(this.enableOrder) this.mappingOrdered=new Hashtable<Long, Long>();
		else this.mappingOrdered=null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.record.IRecordStore#getRecordCount()
	 */
	public long getRecordCount()
	{
		return this.recordCount;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.record.IRecordStore#dispose()
	 */
	public synchronized void dispose() throws GRS2RecordStoreException
	{
		try
		{
			if(this.mappingSimple!=null)
			{
				this.mappingSimple.clear();
				this.mappingSimple=null;
			}
			if(this.mappingOrdered!=null)
			{
				this.mappingOrdered.clear();
				this.mappingOrdered=null;
			}
			this.rand.close();
			this.recordFile.delete();
		} catch (Exception e)
		{
			throw new GRS2RecordStoreException("Could not dispose persistency manager resources", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.record.IRecordStore#persist(gr.uoa.di.madgik.grs.record.Record)
	 */
	public synchronized void persist(Record record) throws GRS2RecordStoreException
	{
		try
		{
//			long start=System.currentTimeMillis();
			if(this.mappingSimple.containsKey(record.getID())) return;
			this.rand.seek(this.rand.length());
			long offset=this.rand.getFilePointer();
			this.rand.writeUTF(record.getClass().getName());
			record.deflate(this.rand);
			this.mappingSimple.put(record.getID(), offset);
			if(this.enableOrder) this.mappingOrdered.put(recordCount, record.getID());
			this.recordCount+=1;
//			System.out.println("record persist took "+(System.currentTimeMillis()-start));
		} catch (Exception e)
		{
			throw new GRS2RecordStoreAccessException("Could not persist record", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.record.IRecordStore#retrieve(long, boolean)
	 */
	public synchronized Record retrieve(long recordID, boolean reset) throws GRS2RecordStoreException
	{
		try
		{
//			long start=System.currentTimeMillis();
			if(!this.mappingSimple.containsKey(recordID)) return null;
			this.rand.seek(this.mappingSimple.get(recordID));
			String recordType=this.rand.readUTF();
			Record record = (Record)Class.forName(recordType).newInstance();
			record.inflate(this.rand,reset);
//			System.out.println("record retrieve took "+(System.currentTimeMillis()-start));
			return record;
		} catch (Exception e)
		{
			throw new GRS2RecordStoreAccessException("Could not retrieve record", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see gr.uoa.di.madgik.grs.store.record.IRecordStore#retrieveByIndex(long, boolean)
	 */
	public synchronized Record retrieveByIndex(long recordIndex, boolean reset) throws GRS2RecordStoreException
	{
		try
		{
			if(!this.enableOrder) throw new GRS2RecordStoreAccessException("this operation is only available when ordering is enabled");
			if(!this.mappingOrdered.containsKey(recordIndex)) return null;
			return this.retrieve(this.mappingOrdered.get(recordIndex),reset);
		} catch (Exception e)
		{
			throw new GRS2RecordStoreAccessException("Could not persist record", e);
		}
	}
}
