package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.utils.Locators;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DTSExceptionWrapper;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.StrDataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;

/**
 * @author Dimitris Katris, NKUA
 *
 * <p>
 * This <tt>DataSink</tt> stores <tt>DataElements</tt> to a result set with xml elements.
 * </p>
 */
public class RS2DataSink implements DataSink {
	
	private static Logger log = LoggerFactory.getLogger(RS2DataSink.class);
	
	
	private RecordDefinition[] defs = null; 
	private int wroterecs = 0; 
	private RecordWriter<GenericRecord> writer = null;
	
	/**
	 * This constructor is used when DTS is instantiating a new AlternativeRepresentationDataSink.
	 * 
	 * @param output The output value of the <tt>DataSink</tt>.
	 * @param outputParameters The output parameters of the <tt>DataSink</tt>.
	 * @throws Exception If the result set could not be created.
	 */
	public RS2DataSink(String output, Parameter[] outputParameters) throws Exception {
		//TODO: OutputParameter that denotes if the payload of the result elements should be wrapped with MM envelope...

		defs = new RecordDefinition[]{          //A gRS can contain a number of different record definitions
		        new GenericRecordDefinition((new FieldDefinition[] {
				        new StringFieldDefinition("Rowset")
		        }))
		        };

		writer = new RecordWriter<GenericRecord>(
		        new LocalWriterProxy(), //The proxy that defines the way the writer can be accessed
		        defs,   //The definitions of the records the gRS handles
		        RecordWriter.DefaultBufferCapacity,
		        RecordWriter.DefaultConcurrentPartialCapacity,
		        RecordWriter.DefaultMirrorBufferFactor,
		        1,
		        TimeUnit.DAYS		        
		      );
	}
	
	private static String stringFromInputStream (InputStream in) throws IOException {
	    StringBuffer out = new StringBuffer();
	    byte[] b = new byte[4096];
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    in.close();
	    return out.toString();
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#append(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement)
	 * @param dataElement {@link DataElement} to be appended to this <tt>DataSink</tt>
	 */
	public void append(DataElement dataElement) {
		if (dataElement instanceof DTSExceptionWrapper) {
			int minutes = 1;
			try {
				while (!writer.put(((DTSExceptionWrapper) dataElement).getThrowable(), 1, TimeUnit.MINUTES)) {
					Status s = writer.getStatus();
					minutes++;
					log.debug("RS buffer full.... looping... " + dataElement.getId() + " status " + s.toString());
					if (minutes > 10) {
						log.warn("Transformation has remained idle for a long. Closing writer.");
						close();
						writer = null;
						return;
					}
				}
			} catch (GRS2WriterException e) {
				log.error("could not append exception", e);
			}
			return;
		}

		/* Wrap the element's payload in a new ResultElementGeneric */
		GenericRecord rec=new GenericRecord();
		
		try {
			String payload;
			if(dataElement instanceof StrDataElement){
				payload = ((StrDataElement)dataElement).getStringContent();
			}else{
				payload = stringFromInputStream(dataElement.getContent());
			}
			
	        rec.setFields(new Field[]{new StringField(payload)});

	        int minutes = 1;
	        while(!writer.put(rec, 60, TimeUnit.SECONDS)) {
	        	Status s =writer.getStatus();
	        	minutes++;
				log.debug("RS buffer full.... looping... " + dataElement.getId()+ " status "+s.toString());	        	
				if (minutes > 10){
					log.info("Transformation has remained idle for too long. Closing writer.");
					close();
					writer = null;
				}
	        };
	        
	        wroterecs++;
			log.debug("Wrote record ("+ wroterecs +") #" + dataElement.getId());
		} catch (Exception e) {
			close();
			log.error("Failed to append RS2 element", e);
			
			return;
//			throw new Exception("Failed to create ResultSet element.", e);
		}

	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	public void close() {
		if (isClosed ==false)
			try {
				isClosed=true;
				// TODO there is a double close of the ResultSet
				// one from the executor and one from the program
				// The correct behavour is to have only the program close the RS
				log.debug("Total records added: "+writer.totalRecords());
				writer.close();
				ReportManager.closeReport();
			} catch (Exception e) {
				log.error("Could not close RSXMLWriter ", e);
			}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#getOutput()
	 * @return The output of the transformation.
	 */
	public String getOutput() {
		try {
			URI TCPLocator = Locators.localToTCP(writer.getLocator()); 
			return TCPLocator.toString();
		} catch (Exception e) {
			log.error("Did not manage to create the RS Locator", e);
			return null;
		}
	}
	private boolean isClosed=false;
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the <tt>DataHandler</tt> has been closed.
	 */
	public boolean isClosed() {
		return isClosed;
	}

}
