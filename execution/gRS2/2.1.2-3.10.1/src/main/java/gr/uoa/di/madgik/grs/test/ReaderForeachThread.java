package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import java.net.URI;

/**
 * Reader test class
 * 
 * @author gpapanikos
 *
 */
public class ReaderForeachThread extends Thread
{
	private URI locator=null;
	private int readCount;
	private boolean sendEvents=false;
	
	/**
	 * Create reader
	 * 
	 * @param locator what to read
	 * @param readCount how much to read
	 */
	public ReaderForeachThread(URI locator, int readCount,boolean sendEvents)
	{
		this.locator=locator;
		this.readCount=readCount;
		this.sendEvents=sendEvents;
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
				if(f!=null) System.out.println(((StringField)f).getPayload());
				if(count%20==0 && sendEvents) reader.emit(new KeyValueEvent("key", ""+count));
				BufferEvent ev=reader.receive();
				if(ev!=null) System.out.println("Reader received event "+((KeyValueEvent)ev).getKey()+"-"+((KeyValueEvent)ev).getValue());
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
