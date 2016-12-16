package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FileField;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Reader test class
 * 
 * @author gpapanikos
 *
 */
public class ReaderFileMediationThread extends Thread
{
	private URI locator=null;
	private int readCount;
	private String storeBase;
	private boolean doStore=false;
	private int readerID=0;
	
	/**
	 * create reader
	 * 
	 * @param locator what to read
	 * @param readCount how much to read
	 * @param storeBase store directory of what it reads
	 * @param doStore should store what is read
	 */
	public ReaderFileMediationThread(URI locator, int readCount, String storeBase,boolean doStore,int readerID)
	{
		this.locator=locator;
		this.readCount=readCount;
		this.storeBase=storeBase;
		this.doStore=doStore;
		this.readerID=readerID;
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
//				System.out.println(this.readerID+" - available "+reader.availableRecords()+" total "+reader.totalRecords());
				long startRec=System.currentTimeMillis();
				if(!iter.hasNext())
				{
					System.out.println("no more after "+(System.currentTimeMillis()-startRec));
					break;
				}
//				System.out.println(this.readerID+" - available "+reader.availableRecords()+" total "+reader.totalRecords());
				if(count==this.readCount) break;
				GenericRecord rec=iter.next();
				long endRec=System.currentTimeMillis();
				if(rec==null) throw new TimeoutException("timeout while waiting for next");
				count+=1;
				Field f=rec.getField("ThisIsTheField");
				long startMed=System.currentTimeMillis();
				if(this.doStore)
				{
					BufferedInputStream bin=new BufferedInputStream(f.getMediatingInputStream());
					BufferedOutputStream bout=new BufferedOutputStream(new FileOutputStream(new File(this.storeBase, UUID.randomUUID().toString())));
					byte[] buf=new byte[4*1024];
					while(true)
					{
						int read=bin.read(buf);
						if(read<0) break;
						bout.write(buf, 0, read);
					}
					bout.flush();
					bout.close();
					bin.close();
				}
				else
				{
					BufferedInputStream bin=new BufferedInputStream(f.getMediatingInputStream());
					byte[] buf=new byte[4*1024];
					while(true)
					{
						int read=bin.read(buf);
						if(read<0) break;
					}
					bin.close();
				}
				System.out.println(readerID+" - "+((FileField)f).getPayload().toString() +" in "+(endRec-startRec)+" with available "+reader.availableRecords()+" and total "+reader.totalRecords() +" mediation took "+(System.currentTimeMillis()-startMed));
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
