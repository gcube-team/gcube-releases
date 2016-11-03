package org.gcube.datatransfer.agent.library.grs;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.gcube.datatransfer.common.grs.FileRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class GRSFileWriter extends Thread
{
  private RecordWriter<GenericRecord> writer=null;


 private ArrayList<File> files=null;
 

	Logger logger = LoggerFactory.getLogger(this.getClass().toString());
	
 
  public GRSFileWriter(IWriterProxy proxy, ArrayList<File> inputFiles) throws GRS2WriterException
  {
    this.files = inputFiles;
    writer=new RecordWriter<GenericRecord>(
        proxy,
        FileRecord.fileRecordDef,
        inputFiles.size(),
        2,      
        0.5f  
      );
    
    }
 
    public URI getLocator() throws GRS2WriterException
    {
      return writer.getLocator();
    }
 
    public void run()
    {
    	
    	for (File file :files){
    		if (file.isDirectory()){
    			logger.debug("File "+ file +" is a directory and cannot be transferred");
    			continue;
    		}
	      try
	      {
	          //while the reader hasn't stopped reading
	          if(writer.getStatus()!=Status.Open) return;
	          GenericRecord rec=new GenericRecord();
	          
	          rec.setFields(new Field[]{new FileField(file),new StringField(file.getName())});
	          //if the buffer is in maximum capacity for the specified interval don;t wait any more
	          if (!writer.put(rec,60,TimeUnit.SECONDS)) return;
	          logger.debug("Succesfully written outcome");
	          
	         
    	 }catch(Exception ex)
         {
           ex.printStackTrace();
  
         }
    	}
    	logger.debug("All Outcomes Succesfully written");
    	if(writer.getStatus()!=Status.Dispose)
			try {
				writer.close();
				logger.debug("Writer closed");
			} catch (GRS2WriterException e) {
				e.printStackTrace();
			}
 
    }
    
    public RecordWriter<GenericRecord> getWriter() {
    	return writer;
    }

    public void setWriter(RecordWriter<GenericRecord> writer) {
    	this.writer = writer;
    }
}