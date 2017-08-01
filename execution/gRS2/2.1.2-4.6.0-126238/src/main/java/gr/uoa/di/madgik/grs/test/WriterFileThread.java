package gr.uoa.di.madgik.grs.test;

import java.io.File;
import java.util.concurrent.TimeUnit;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportDirective;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.FileFieldDefinition;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

/**
 * Writer test class
 * 
 * @author gpapanikos
 *
 */
public class WriterFileThread extends Thread
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
	private String fileName=null;
	private TransportDirective directive;
	private boolean doCompress=false;
	private int capacity=0;
	
	/**
	 * Create writer
	 * 
	 * @param proxy the proxy to use to publish
	 * @param itemsToProduce the number of items to produce
	 * @param disposePrematurely if the reader should dispose before completing
	 * @param timeoutLifecycle lifecycle timeout
	 * @param timeunitLifecycle lifecycle time unit
	 * @param fileName the file name to include in field
	 * @param directive the record directive
	 * @param doCompress do compression in transport
	 * @param capacity the buffer capacity
	 */
	public WriterFileThread(IWriterProxy proxy,int itemsToProduce,int disposePrematurely,long timeoutLifecycle,TimeUnit timeunitLifecycle,String fileName,TransportDirective directive,boolean doCompress,int capacity)
	{
		this.itemsToProduce=itemsToProduce;
		this.disposePrematurely=disposePrematurely;
		this.timeoutLifecycle=timeoutLifecycle;
		this.timeunitLifecycle=timeunitLifecycle;
		this.proxy=proxy;
		this.fileName=fileName;
		this.directive=directive;
		this.doCompress=doCompress;
		this.capacity=capacity;
	}
	
	/**
	 * Initialize the writer
	 */
	public void prepare()
	{
		try
		{
			System.out.println("preparing writer");
			RecordDefinition[] defs=new RecordDefinition[1];
			FileFieldDefinition ffd=new FileFieldDefinition("ThisIsTheField");
			ffd.setTransportDirective(directive);
			ffd.setCompress(this.doCompress);
			defs[0]=new GenericRecordDefinition((new FieldDefinition[] {ffd}));
			writer=new RecordWriter<GenericRecord>(proxy,defs,capacity,RecordWriter.DefaultConcurrentPartialCapacity, 0.5f,/*RecordWriter.DefaultThreshold,*/this.timeoutLifecycle,this.timeunitLifecycle);
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
				//no definition set so default 0
				rec.setFields(new Field[]{new FileField(new File(fileName))});
				if(writer.put(rec,60,TimeUnit.SECONDS)) count+=1;
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
