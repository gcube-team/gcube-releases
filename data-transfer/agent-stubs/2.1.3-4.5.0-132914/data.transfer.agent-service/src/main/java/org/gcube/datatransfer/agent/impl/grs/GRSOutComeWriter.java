package org.gcube.datatransfer.agent.impl.grs;

import java.util.concurrent.TimeUnit;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.common.grs.FileOutcomeRecord;
import org.gcube.datatransfer.common.grs.FileOutcomeRecord.Outcome;
import org.gcube.datatransfer.common.grs.TreeOutcomeRecord;

public class GRSOutComeWriter {
	
	GCUBELog logger = new GCUBELog(this.getClass());

	public boolean isItForTrees;
	public RecordWriter<GenericRecord> writer=null;
	
	public GRSOutComeWriter(int capacity, boolean isItForTrees) throws GRS2WriterException{
		if(isItForTrees){
			this.isItForTrees=true;
			writer=new RecordWriter<GenericRecord>(
			        new  TCPWriterProxy(),
			        TreeOutcomeRecord.treeOutcomeRecordDef,
			        capacity,
			        2,
			        0.5f
			 );
		}
		else{
			this.isItForTrees=false;
			writer=new RecordWriter<GenericRecord>(
			        new  TCPWriterProxy(),
			        FileOutcomeRecord.fileOutcomeRecordDef,
			        capacity,
			        2,
			        0.5f
			 );
		}
		
	}

	public void putField(String sourceUrl, String outURL, Long transferTime,Long transferredBytes,Long total_size,Exception ...e) throws GRS2WriterException {
		if(this.isItForTrees){
			logger.debug("Wrong put field method: This outcome is for trees");
			return;
		}
		
		logger.debug("Writing Outcome for url: " + sourceUrl);

        if(writer.getStatus()!=Status.Open) return;
          
        GenericRecord recWriter=new GenericRecord();
        
		recWriter.setFields(new Field[]{new StringField(sourceUrl.toString()),
				(e.length!= 0)?new StringField(Outcome.N_A.name()):new StringField(outURL),
        		(e.length!= 0)?new StringField(Outcome.ERROR.name()):new StringField(Outcome.DONE.name()),
        		new StringField(transferTime.toString()),
        		new StringField(transferredBytes.toString()),
        		new StringField(total_size.toString()),
        		(e.length!= 0)?new StringField(e[0].getMessage()):new StringField(Outcome.N_A.name())
        				});
        //if the buffer is in maximum capacity for the specified interval don;t wait any more
        if (!writer.put(recWriter,60,TimeUnit.SECONDS)) return;
        else
        	logger.debug("Succesfully written Outcome for url: " + sourceUrl);
   
	}
	
	public void putField(String sourceID, String destID, int readTrees, int writtenTrees,String outcome,Exception ...e) throws GRS2WriterException {
		if(!this.isItForTrees){
			logger.debug("Wrong put field method: This outcome is not for trees");
			return;
		}
		
		logger.debug("Writing Outcome for sourceID: " + sourceID);

        if(writer.getStatus()!=Status.Open) return;
          
        GenericRecord recWriter=new GenericRecord();
        
		recWriter.setFields(new Field[]{new StringField(sourceID),
				new StringField(destID),
				new StringField(Integer.toString(readTrees)),
				new StringField(Integer.toString(writtenTrees)),
				new StringField(outcome),
        		(e.length!= 0)?new StringField(e[0].getMessage()):new StringField(Outcome.N_A.name())
        				});
        //if the buffer is in maximum capacity for the specified interval don;t wait any more
        if (!writer.put(recWriter,60,TimeUnit.SECONDS)) return;
        else
        	logger.debug("Succesfully written Outcome for sourceID: " + sourceID);
   
	}
	public RecordWriter<GenericRecord> getWriter() {
		return writer;
	}

	public void setWriter(RecordWriter<GenericRecord> writer) {
		this.writer = writer;
	}
	
	public void close () throws GRS2WriterException{
		writer.close();
	}
	
}
