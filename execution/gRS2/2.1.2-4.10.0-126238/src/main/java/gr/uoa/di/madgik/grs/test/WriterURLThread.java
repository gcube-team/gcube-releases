package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.URLField;
import gr.uoa.di.madgik.grs.record.field.URLFieldDefinition;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Writer test class
 * 
 * @author gpapanikos
 *
 */
public class WriterURLThread extends Thread
{
	/**
	 * The writer that is used
	 */
	public RecordWriter<GenericRecord> writer=null;
	private int itemsToProduce;
	private int disposePrematurely;
	private long timeoutLifecycle;
	private TimeUnit timeunitLifecycle;
	private IWriterProxy proxy=null;
	private int capacity=0;
	private String url;
	private String additionalString;
	
	/**
	 * Create writer
	 * 
	 * @param proxy the proxy to use to publish
	 * @param itemsToProduce the number of items to produce
	 * @param disposePrematurely if the reader should dispose before completing
	 * @param timeoutLifecycle lifecycle timeout
	 * @param timeunitLifecycle lifecycle time unit
	 * @param capacity the buffer capacity
	 * @param url the url of the remote file
	 */
	public WriterURLThread(IWriterProxy proxy,int itemsToProduce,int disposePrematurely,long timeoutLifecycle,TimeUnit timeunitLifecycle,int capacity,String url)
	{
		this.itemsToProduce=itemsToProduce;
		this.disposePrematurely=disposePrematurely;
		this.timeoutLifecycle=timeoutLifecycle;
		this.timeunitLifecycle=timeunitLifecycle;
		this.proxy=proxy;
		this.capacity=capacity;
		this.url = url;
	}
	
	
	/**
	 * Create writer
	 * 
	 * @param proxy the proxy to use to publish
	 * @param itemsToProduce the number of items to produce
	 * @param disposePrematurely if the reader should dispose before completing
	 * @param timeoutLifecycle lifecycle timeout
	 * @param timeunitLifecycle lifecycle time unit
	 * @param capacity the buffer capacity
	 * @param url the url of the remote file
	 * @param additionalString string that contains additional information for the remote file retrieval such as certificate location, ssh keys location, filelist to pick specific files from torrent etc
	 */
	public WriterURLThread(IWriterProxy proxy,int itemsToProduce,int disposePrematurely,long timeoutLifecycle,TimeUnit timeunitLifecycle,int capacity,String url, String additionalString)	{
		this(proxy, itemsToProduce, disposePrematurely, timeoutLifecycle, timeunitLifecycle, capacity, url);
		this.additionalString = additionalString;
	}
	
	/**
	 * Initialize the writer
	 */
	public void prepare()
	{
		try
		{
			System.out.println("preparing writer");
			RecordDefinition[] defs=new RecordDefinition[]{new GenericRecordDefinition((new FieldDefinition[] {new URLFieldDefinition("ThisIsTheField")}))};
			writer=new RecordWriter<GenericRecord>(proxy,defs,capacity,RecordWriter.DefaultConcurrentPartialCapacity,0.5f,/*RecordWriter.DefaultThreshold,*/this.timeoutLifecycle,this.timeunitLifecycle);
			System.out.println("done preparing writer");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void run()
	{
		try
		{
			int count=0;
			for(int i=0;i<10;i+=1)
			{
				if(i==this.disposePrematurely)
				{
					System.out.println("writer disposing");
					writer.dispose();
					return;
				}
				if(writer.getStatus()!=Status.Open) break;
				GenericRecord rec=new GenericRecord();
				rec.setFields(new Field[]{new URLField(new URL(url+(additionalString!=null?"|"+additionalString:"")))});
				if(writer.put(rec,60,TimeUnit.SECONDS)) count+=1;
//				if(count%10==0 && sendEvents) writer.emit(new KeyValueEvent("key", ""+count));
//				BufferEvent ev=writer.receive();
//				if(ev!=null) System.out.println("Writer received event "+((KeyValueEvent)ev).getKey()+"-"+((KeyValueEvent)ev).getValue());
				//Thread.sleep(100);
			}
			System.out.println("writen total "+count);
			System.out.println("writer closing");
			if(writer.getStatus()!=Status.Dispose) writer.close();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
}




