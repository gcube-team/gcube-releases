package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.FileFieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DTSExceptionWrapper;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.utils.RSDataElementUtil;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
import org.gcube.datatransformation.datatransformationlibrary.tmpfilemanagement.TempFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link DataSink} stores {@link DataElement}s in a result set.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class GRS2DataSink implements DataSink {

	private static Logger log = LoggerFactory.getLogger(GRS2DataSink.class);

	private RecordDefinition[] defs = null;
	private int wroterecs = 0;
	private boolean deleteOnDispose = false;
	private RecordWriter<GenericRecord> writer = null;
	
	private static File nullFile = null; // XXX temp fix when content is null

	/**
	 * This constructor is used when DTS is instantiating a new gRS2 Data Sink.
	 * 
	 * @param output
	 *            The output value of the <tt>DataSink</tt>.
	 * @param outputParameters
	 *            The output parameters of the <tt>DataSink</tt>.
	 * @throws Exception
	 *             If the result set could not be created.
	 */
	public GRS2DataSink(String output, Parameter[] outputParameters) throws Exception {
		if(outputParameters!=null){
			for(Parameter param: outputParameters){
				if(param!=null && param.getName()!=null && param.getValue()!=null){
					if(param.getName().equalsIgnoreCase("deleteOnDispose")){
						try {
							deleteOnDispose = Boolean.parseBoolean(param.getValue());
							log.debug("GRS2DataSink will be set with deleteOnDispose set to " + deleteOnDispose);
						} catch (Exception e) { }
					}

				}
			}
		}

		nullFile = new File(TempFileManager.generateTempFileName(null));
		nullFile.createNewFile();
		
		FileFieldDefinition ffd = new FileFieldDefinition("File");
		ffd.setDeleteOnDispose(deleteOnDispose);
		defs = new RecordDefinition[] { new GenericRecordDefinition((new FieldDefinition[] { new StringFieldDefinition("Metadata"), ffd })) };

		writer = new RecordWriter<GenericRecord>(new TCPWriterProxy(), defs, RecordWriter.DefaultBufferCapacity,
				RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor, 1, TimeUnit.DAYS);
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#append(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement)
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
		GenericRecord rec = new GenericRecord();
		try {
			
			File f;
			if (dataElement.getContent() == null){
				if (deleteOnDispose) {
					nullFile = new File(TempFileManager.generateTempFileName(null));
					nullFile.createNewFile();
				} else {
					nullFile.setLastModified(System.currentTimeMillis());
				}
				f = nullFile;
			}
			else
				f = RSDataElementUtil.dataElementContentToFile(dataElement);

			if (f != null){
				if (!f.exists()){
					log.warn(f.getAbsolutePath() + " (No such file or directory)");
					return;
				}
			}
			
			rec.setFields(new Field[] { new StringField(RSDataElementUtil.dataElementMetadataToXML(dataElement)), new FileField(f) });

			int minutes = 1;
			while (!writer.put(rec, 1, TimeUnit.MINUTES)) {
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

			wroterecs++;
			log.debug("Wrote record (" + wroterecs + ") #" + dataElement.getId());
		} catch (Exception e) {
			close();
			log.error("Failed to append RS2 element", e);

			return;
			// throw new Exception("Failed to create ResultSet element.", e);
		}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	public void close() {
		if (isClosed)
			return;
		try {
			isClosed = true;
			try {
				log.debug("Total records added: " + writer.totalRecords());
			} catch(Exception e1) {}
			writer.close();
			ReportManager.closeReport();
		} catch (Exception e) {
			log.error("Could not close GRS2DataSink", e);
		}

	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#getOutput()
	 * @return The output of the transformation.
	 */
	public String getOutput() {
		try {
			return writer.getLocator().toASCIIString();
		} catch (Exception e) {
			log.error("Did not manage to create the RS Locator", e);
			return null;
		}
	}

	private boolean isClosed = false;

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the <tt>DataHandler</tt> has been closed.
	 */
	public boolean isClosed() {
		return isClosed;
	}

//	public static void main(String[] args) throws GRS2WriterException, InterruptedException, GRS2ReaderException, GRS2RecordDefinitionException, GRS2BufferException {
//		TCPConnectionManager.Init(new TCPConnectionManagerConfig("meteora.di.uoa.gr", new ArrayList<PortRange>(), true));
//
//		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
//		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
//		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
//		
//		RecordDefinition[] defs = new RecordDefinition[] { new GenericRecordDefinition((new FieldDefinition[] { new StringFieldDefinition("Metadata") })) };
//		final RecordWriter<GenericRecord> writer = new RecordWriter<GenericRecord>(new TCPWriterProxy(), defs, RecordWriter.DefaultBufferCapacity,
//				RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor, 1, TimeUnit.DAYS);
//
//		new Thread() {
//			public void run() {
//				try {
//					throw new Exception("asdf");
//				} catch (Exception e) {
//					try {
//						writer.put(new Exception(e), 10, TimeUnit.SECONDS);
//					} catch (GRS2WriterException e1) {
//						e1.printStackTrace();
//					}
//				}
//			}
//		}.start();
//		
//		Thread.sleep(1000);
//		
//		ForwardReader<GenericRecord> reader = new ForwardReader<GenericRecord>(writer.getLocator());
//
//		GenericRecord rec = null;
////		try {
////			rec = reader.get(60, TimeUnit.SECONDS); // XXX TIMEOUT 10mins?
////		} catch (GRS2ReaderException e) {
////			log.error("Did not manage to read result set element", e);
////		}
//		
//		rec = reader.iterator().next();
//		System.out.println(((StringField)rec.getField("Metadata")).getPayload());
//	}
}
