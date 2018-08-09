package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.ObjectField;
import java.net.URI;

/**
 * Reader test class
 * 
 * @author gpapanikos
 *
 */
public class ReaderObjectThread extends Thread
{
	private URI locator=null;
	private int readCount;
	
	/**
	 * Create reader
	 * 
	 * @param locator what to read
	 * @param readCount how much to read
	 */
	public ReaderObjectThread(URI locator, int readCount)
	{
		this.locator=locator;
		this.readCount=readCount;
	}
	
	public void run()
	{
		try
		{
			ForwardReader<GenericRecord> reader=new ForwardReader<GenericRecord>(this.locator);
			System.out.println("starting reading");
			int count=0;
			for(GenericRecord rec : reader)
			{
				if(rec==null) continue;
				if(count==this.readCount) break;
				count+=1;
				Field f=rec.getField("ThisIsTheField");
				if(f!=null) System.out.println(((ObjectField)f).getPayload().toString());
			}
			System.out.println("read total "+count);
			System.out.println("reader closing");
			reader.close();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
