package gr.uoa.di.madgik.grs.test;

import java.util.concurrent.TimeUnit;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

/**
 * Writer test class
 * 
 * @author gpapanikos
 *
 */
public class WriterSimpleThread extends Thread
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
	private boolean sendEvents=false;
	
	/**
	 * Create writer
	 * 
	 * @param proxy the proxy to use to publish
	 * @param itemsToProduce the number of items to produce
	 * @param disposePrematurely if the reader should dispose before completing
	 * @param timeoutLifecycle lifecycle timeout
	 * @param timeunitLifecycle lifecycle time unit
	 * @param capacity the buffer capacity
	 */
	public WriterSimpleThread(IWriterProxy proxy,int itemsToProduce,int disposePrematurely,long timeoutLifecycle,TimeUnit timeunitLifecycle,int capacity,boolean sendEvents)
	{
		this.itemsToProduce=itemsToProduce;
		this.disposePrematurely=disposePrematurely;
		this.timeoutLifecycle=timeoutLifecycle;
		this.timeunitLifecycle=timeunitLifecycle;
		this.proxy=proxy;
		this.capacity=capacity;
		this.sendEvents=sendEvents;
	}
	
	/**
	 * Initialize the writer
	 */
	public void prepare()
	{
		try
		{
			System.out.println("preparing writer");
			RecordDefinition[] defs=new RecordDefinition[]{new GenericRecordDefinition((new FieldDefinition[] {new StringFieldDefinition("ThisIsTheField")}))};
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
			for(int i=0;i<this.itemsToProduce;i+=1)
			{
				if(i==this.disposePrematurely)
				{
					System.out.println("writer disposing");
					writer.dispose();
					return;
				}
				if(writer.getStatus()!=Status.Open) break;
				GenericRecord rec=new GenericRecord();
				rec.setFields(new Field[]{new StringField("Hello world "+count)});
				if(writer.put(rec,60,TimeUnit.SECONDS)) count+=1;
				if(count%10==0 && sendEvents) writer.emit(new KeyValueEvent("key", ""+count));
				BufferEvent ev=writer.receive();
				if(ev!=null) System.out.println("Writer received event "+((KeyValueEvent)ev).getKey()+"-"+((KeyValueEvent)ev).getValue());
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
