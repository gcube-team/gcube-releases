package org.gcube.datatransfer.agent.grs.test;


	import java.net.URI;
	import java.util.concurrent.TimeUnit;
	import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
	import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
	import gr.uoa.di.madgik.grs.record.GenericRecord;
	import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
	import gr.uoa.di.madgik.grs.record.RecordDefinition;
	import gr.uoa.di.madgik.grs.record.field.Field;
	import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
	import gr.uoa.di.madgik.grs.record.field.StringField;
	import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
	import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
	import gr.uoa.di.madgik.grs.writer.RecordWriter;
	 
	public class GRSWriter extends Thread
	{
	  private RecordWriter<GenericRecord> writer=null;
	 
	  public GRSWriter(IWriterProxy proxy) throws GRS2WriterException
	  {
	    //The gRS will contain only one type of records which in turn contains only a single field 
	    RecordDefinition[] defs=new RecordDefinition[]{          //A gRS can contain a number of different record definitions
	        new GenericRecordDefinition((new FieldDefinition[] { //A record can contain a number of different field definitions
	        new StringFieldDefinition("payload")          //The definition of the field
	      }))
	    };
	    writer=new RecordWriter<GenericRecord>(
	        proxy, //The proxy that defines the way the writer can be accessed
	        defs   //The definitions of the records the gRS handles
	      );
	    }
	 
	    public URI getLocator() throws GRS2WriterException
	    {
	      return writer.getLocator();
	    }
	 
	    public void run()
	    {
	      try
	      {
	        for(int i=0;i<500;i+=1)
	        {
	          //while the reader hasn't stopped reading
	          if(writer.getStatus()!=Status.Open) break;
	          GenericRecord rec=new GenericRecord();
	          //Only a string field is added to the record as per definition
	          rec.setFields(new Field[]{new StringField("Hello world "+i)});
	          //if the buffer is in maximum capacity for the specified interval don;t wait any more
	          if(!writer.put(rec,60,TimeUnit.SECONDS)) break;
	        }
	        //if the reader hasn't already disposed the buffer, close to notify reader that no more will be provided 
	        if(writer.getStatus()!=Status.Dispose) writer.close();
	      }catch(Exception ex)
	      {
	        ex.printStackTrace();
	      }
	    }
	}

