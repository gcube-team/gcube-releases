package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FileField;
import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Reader test class
 * 
 * @author gpapanikos
 *
 */
public class ReaderFileThread extends Thread
{
	private URI locator=null;
	private int readCount;
	
	/**
	 * Create reader
	 * 
	 * @param locator what to read
	 * @param readCount how much to read
	 */
	public ReaderFileThread(URI locator, int readCount)
	{
		this.locator=locator;
		this.readCount=readCount;
	}
	
	public void run()
	{
		try
		{
			ForwardReader<GenericRecord> reader=new ForwardReader<GenericRecord>(this.locator);
			reader.setIteratorTimeout(60);
			reader.setIteratorTimeUnit(TimeUnit.SECONDS);
			System.out.println("starting reading");
			int count=0;
			Iterator<GenericRecord> iter=reader.iterator();
			while(true)
			{
				long startRec=System.currentTimeMillis();
				if(!iter.hasNext())
				{
					System.out.println("no more after "+(System.currentTimeMillis()-startRec));
					break;
				}
				if(count==this.readCount) break;
				GenericRecord rec=iter.next();
				long endRec=System.currentTimeMillis();
				if(rec==null) throw new TimeoutException("timeout while waiting for next");
				count+=1;
				Field f=rec.getField("ThisIsTheField");
				long startAvail=System.currentTimeMillis();
				rec.makeAvailable();
				System.out.println(((FileField)f).getPayload().toString() +" in "+(endRec-startRec)+" with available "+reader.availableRecords()+" and total "+reader.totalRecords() +" localization took "+(System.currentTimeMillis()-startAvail));
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
