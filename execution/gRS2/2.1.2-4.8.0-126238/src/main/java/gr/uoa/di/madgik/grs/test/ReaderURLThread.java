package gr.uoa.di.madgik.grs.test;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.URLField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
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
public class ReaderURLThread extends Thread
{
	private URI locator=null;
	private int readCount;
	
	/**
	 * Create reader
	 * 
	 * @param locator what to read
	 * @param readCount how much to read
	 */
	public ReaderURLThread(URI locator, int readCount)
	{
		this.locator=locator;
		this.readCount=readCount;
	}
	
	//Copied from URL resolution library Helpers just to avoid depending on URL resolution library only because of this test case
	private static String convertStreamToString(InputStream is, int bufferSize, String encoding) throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[bufferSize];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, encoding));
				int n;
				while ((n = reader.read(buffer)) != -1)
					writer.write(buffer, 0, n);

			} finally {
				is.close();
			}
			return writer.toString();
		} else
			return "";
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
				////
//				RecordDefinition rd = rec.getDefinition();
//				for (int i = 0 ; i < rd.getDefinitionSize() ; ++i){
//					System.out.println("\tfield name : " + rd.getDefinition(0).getName());
//				}
				////
				
				long endRec=System.currentTimeMillis();
				if(rec==null) throw new TimeoutException("timeout while waiting for next");
				count+=1;
				Field f=rec.getField("ThisIsTheField");
				long startAvail=System.currentTimeMillis();
				rec.makeAvailable();
				
				System.out.println("rec");
				//String data = Helpers.convertStreamToString(((URLField)f).getInputStream(), 1024, "UTF-8");
				String data = ReaderURLThread.convertStreamToString(((URLField)f).getInputStream(), 1024, "UTF-8"); //Just to avoid depending on URL resolution library only because of this test cases
			//	System.out.println(data);
				
				
				//System.out.println(((FileField)f).getPayload().toString() +" in "+(endRec-startRec)+" with available "+reader.availableRecords()+" and total "+reader.totalRecords() +" localization took "+(System.currentTimeMillis()-startAvail));
				
				
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
