package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Reader test class 
 * 
 * @author gpapanikos
 *
 */
public class ReaderFullThread extends Thread
{
	private URI locator=null;
	private int readCount;
	
	/**
	 * Create reader
	 * 
	 * @param locator what to read
	 * @param readCount how much to read
	 */
	public ReaderFullThread(URI locator, int readCount)
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
			while(true)
			{
				if(count==this.readCount) break;
				if(reader.getStatus()==Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0)) break;
				GenericRecord rec=reader.get(60, TimeUnit.SECONDS);
				if(rec==null) continue;
				count+=1;
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
