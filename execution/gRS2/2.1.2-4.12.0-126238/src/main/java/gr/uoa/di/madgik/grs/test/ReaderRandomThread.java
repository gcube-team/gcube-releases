package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Reader test class
 * 
 * @author gpapanikos
 *
 */
public class ReaderRandomThread extends Thread
{
	private URI locator=null;
	
	/**
	 * Create reader
	 * 
	 * @param locator what to read
	 */
	public ReaderRandomThread(URI locator)
	{
		this.locator=locator;
	}
	
	public void run()
	{
		try
		{
			RandomReader<GenericRecord> reader=new RandomReader<GenericRecord>(this.locator);
			System.out.println("starting reading");
			System.out.println("will read first 20");
			for(int i=0;i<20;i+=1)
			{
				if(reader.getStatus()==Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0)) break;
				GenericRecord rec=reader.get(60, TimeUnit.SECONDS);
				if(rec==null) throw new TimeoutException("Exception while waiting for record");
				Field f=rec.getField("ThisIsTheField");
				if(f!=null) System.out.println(((StringField)f).getPayload());
			}
			System.out.println("going back 10 records");
			System.out.println("went back "+reader.seek(-10)+" record");
			System.out.println("will read next 9");
			for(int i=0;i<9;i+=1)
			{
				if(reader.getStatus()==Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0)) break;
				GenericRecord rec=reader.get(60, TimeUnit.SECONDS);
				if(rec==null) throw new TimeoutException("Exception while waiting for record");
				Field f=rec.getField("ThisIsTheField");
				if(f!=null) System.out.println(((StringField)f).getPayload());
			}
			System.out.println("going back 5 records");
			System.out.println("went back "+reader.seek(-5)+" record");
			System.out.println("will read next 3");
			for(int i=0;i<3;i+=1)
			{
				if(reader.getStatus()==Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0)) break;
				GenericRecord rec=reader.get(60, TimeUnit.SECONDS);
				if(rec==null) throw new TimeoutException("Exception while waiting for record");
				Field f=rec.getField("ThisIsTheField");
				if(f!=null) System.out.println(((StringField)f).getPayload());
			}
			System.out.println("reader closing");
			reader.close();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
