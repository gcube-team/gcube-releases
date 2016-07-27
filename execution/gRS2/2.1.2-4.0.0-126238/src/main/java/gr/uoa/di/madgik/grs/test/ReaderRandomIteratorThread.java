package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import java.net.URI;
import java.util.ListIterator;
import java.util.concurrent.TimeoutException;

/**
 * Reader test class
 * 
 * @author gpapanikos
 *
 */
public class ReaderRandomIteratorThread extends Thread
{
	private URI locator=null;
	
	/**
	 * Create reader
	 * 
	 * @param locator what to read
	 */
	public ReaderRandomIteratorThread(URI locator)
	{
		this.locator=locator;
	}
	
	public void run()
	{
		try
		{
			RandomReader<GenericRecord> reader=new RandomReader<GenericRecord>(this.locator);
			System.out.println("starting reading");
			ListIterator<GenericRecord> iter= reader.listIterator();
			System.out.println("going forward");
			while(iter.hasNext())
			{
				GenericRecord rec=iter.next();
				if(rec==null) throw new TimeoutException("Exception while waiting for record");
				Field f=rec.getField("ThisIsTheField");
				if(f!=null) System.out.println(((StringField)f).getPayload());
			}
			System.out.println("going backward");
			while(iter.hasPrevious())
			{
				GenericRecord rec=iter.previous();
				if(rec==null) throw new TimeoutException("Exception while waiting for record");
				Field f=rec.getField("ThisIsTheField");
				if(f!=null) System.out.println(((StringField)f).getPayload());
			}
			System.out.println("going forward");
			while(iter.hasNext())
			{
				GenericRecord rec=iter.next();
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
