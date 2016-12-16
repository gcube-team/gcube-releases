package org.gcube.datatransfer.agent.grs.test;


import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

import org.gcube.common.core.utils.logging.GCUBELog;

public class GRSReader extends Thread
{
	GCUBELog logger = new GCUBELog(GRSReader.class);

	ForwardReader<GenericRecord> reader=null;
	CountDownLatch latch= null;


	public GRSReader(URI locator) throws GRS2ReaderException
	{
		reader=new ForwardReader<GenericRecord>(locator);
		this.latch = new CountDownLatch(1);
	}

	public GRSReader(URI locator,CountDownLatch latch) throws GRS2ReaderException
	{
		reader=new ForwardReader<GenericRecord>(locator);
		this.latch = latch;
	}

	public void run()
	{
		try
		{
			for(GenericRecord rec : reader)
			{
				//In case a timeout occurs while optimistically waiting for more records form an originally open writer
				if(rec==null) break;
				//Retrieve the required field of the type available in the gRS definitions
				logger.debug(((StringField)rec.getField("payload")).getPayload());
			}
			//Close the reader to release and dispose any resources in both reader and writer sides
			reader.close();
			latch.countDown();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}

