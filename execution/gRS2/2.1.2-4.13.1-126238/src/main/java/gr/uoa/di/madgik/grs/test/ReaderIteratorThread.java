package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import java.net.URI;
import java.util.Iterator;

/**
 * Readee test class
 * 
 * @author gpapanikos
 *
 */
public class ReaderIteratorThread extends Thread
{
	private URI locator=null;
	private int readCount;
	private int readerID=-1;
	
	/**
	 * Create reader
	 * 
	 * @param locator what to read
	 * @param readCount how much to read
	 * @param readerID which reader is this
	 */
	public ReaderIteratorThread(URI locator, int readCount,int readerID)
	{
		this.locator=locator;
		this.readCount=readCount;
		this.readerID=readerID;
	}
	
	public void run()
	{
		try
		{
			ForwardReader<GenericRecord> reader=new ForwardReader<GenericRecord>(this.locator);
			System.out.println("starting reading");
			int count=0;
			Iterator<GenericRecord> iter=reader.iterator();
			while(iter.hasNext())
			{
				if(count==this.readCount) break;
				GenericRecord rec=iter.next();
				if(rec==null) continue;
				count+=1;
			}
			System.out.println("Reader "+(readerID<0 ? "" : readerID)+" - read total "+count);
			System.out.println("Reader "+(readerID<0 ? "" : readerID)+" - reader closing");
			reader.close();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		System.out.println("Exiting from reader "+(readerID<0 ? "" : readerID));
	}

}
