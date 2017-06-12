package org.gcube.datatransfer.agent.impl.grs;


import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.common.grs.FileOutcomeRecord;
import org.gcube.datatransfer.common.grs.FileOutcomeRecord.Outcome;
import org.gcube.datatransfer.common.utils.Utils;

/**
 * 
 * @author Andrea Manzi(CERN);
 *
 */
public class GRSFileReader extends Thread
{
	GCUBELog logger = new GCUBELog(GRSFileReader.class); 
	
	private ForwardReader<GenericRecord> reader=null;
	private RecordWriter<GenericRecord> writer=null;
	private File outFolder;
	private boolean overwrite;

	public GRSFileReader(URI locator, File outFolder, boolean overwrite) throws GRS2ReaderException, GRS2WriterException
	{
		reader=new ForwardReader<GenericRecord>(locator);
		
		writer=new RecordWriter<GenericRecord>(
			        new  TCPWriterProxy(),
			        FileOutcomeRecord.fileOutcomeRecordDef,
			        reader.getCapacity(),
			        2,
			        0.5f
			      );
		this.outFolder = outFolder;
		this.overwrite = overwrite; 
	}

	public void run()  {
		try {
			for(GenericRecord rec :reader) {
	
					File file = null;
					String fileName = "";
					Exception e = null;
			
					if(rec==null) 
						continue;
					
					long startTime = 0;
					long endTime = 0;
					
					startTime= System.currentTimeMillis();
					
					FileField filefield = ((FileField) rec.getField("FileField"));
					file = filefield.getPayload();
					StringField fileNameField = ((StringField) rec.getField("FileNameField"));
					fileName = fileNameField.getPayload();
					
					logger.debug("outFolder: "+outFolder);
					logger.debug("FileName: "+fileName);
					
					if (!(outFolder.exists()))
						outFolder.mkdirs();
				
					//while the reader hasn't stopped reading
			        if(writer.getStatus()!=Status.Open) return;
			        
			        endTime = System.currentTimeMillis();
			        Long transferTime= endTime - startTime;
			          
			        GenericRecord recWriter=new GenericRecord();
			          
					try {
						if (new File(outFolder.getAbsolutePath()+File.separator+fileName).exists() && (!(overwrite)))
							e = new Exception ("A file with name "+ fileName + " exists in the destination folder and the overwrite option is set to false");
						else Utils.copyfileToFolder(file, outFolder,fileName);
					}catch (IOException ioe){
						ioe.printStackTrace();
						e = ioe;
					}
					
					recWriter.setFields(new Field[]{new StringField(fileName),
							(e!=null)?new StringField(Outcome.N_A.name()):new StringField(fileName),	   
			        		(e!=null)?new StringField(Outcome.ERROR.name()):new StringField(Outcome.DONE.name()),
			        				new StringField(transferTime.toString()),
			        		(e!=null)?new StringField(e.toString()):new StringField(Outcome.N_A.name())
			        				});
			        //if the buffer is in maximum capacity for the specified interval don;t wait any more
			        if (!writer.put(recWriter,60,TimeUnit.SECONDS)) return;
				}
		} catch (Exception ex){
			ex.printStackTrace();
		} 
		finally{
			try {
				reader.close();
				writer.close();
			} catch (GRS2ReaderException e1) {
				e1.printStackTrace();
			} catch (GRS2WriterException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public String getOutcomeLocator() throws GRS2WriterException{
		return writer.getLocator().toString();

	}
}


