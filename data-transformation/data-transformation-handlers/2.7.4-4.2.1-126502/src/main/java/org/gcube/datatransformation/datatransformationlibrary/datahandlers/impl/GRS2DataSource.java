package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GRS2ExceptionWrapper;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.io.File;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.utils.RSDataElementUtil;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link DataSource} fetches {@link DataElement}s from a result set.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class GRS2DataSource implements DataSource, ContentTypeDataSource {

	private static Logger log = LoggerFactory.getLogger(GRS2DataSource.class);

	private Boolean hideRecs = false;
	private ForwardReader<GenericRecord> reader = null;
	private boolean isClosed = false;
	
	/**
	 * This constructor for {@link GRS2DataSource}
	 * 
	 * @param input
	 *            The input value of the {@link DataSource}.
	 * @param inputParameters
	 *            The output parameters of the <tt>DataSink</tt>.
	 * @throws Exception
	 *             If the result set could not be created.
	 */
	public GRS2DataSource(String input, Parameter[] inputParameters) throws Exception {
		reader = new ForwardReader<GenericRecord>(URI.create(input));

		if(inputParameters!=null){
			for(Parameter param: inputParameters){
				if(param!=null && param.getName()!=null && param.getValue()!=null){
					if(param.getName().equalsIgnoreCase("hideRecs")){
						try {
							hideRecs = Boolean.parseBoolean(param.getValue());
							log.debug("GRS2DataSource will be set with hideRecs set to " + hideRecs);
					} catch (Exception e) { }
					}

				}
			}
		}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	public void close() {
		if (!isClosed)
			try {
				isClosed = true;
				log.debug("Total records read: " + reader.totalRecords());
				reader.close();
				ReportManager.closeReport();
			} catch (Exception e) {
				log.error("Could not close ForwardReader ", e);
			}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the <tt>DataHandler</tt> has been closed.
	 */
	public boolean isClosed() {
		return isClosed;
	}

	@Override
	public boolean hasNext() {
		
		try {
			return (!(reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0)));
		} catch (GRS2ReaderException e) {
			log.warn("Got Exception", e);
			return false;
		}
	}

	@Override
	public DataElement next() throws Exception {
		GenericRecord rec = null;
		try {
			rec = reader.get(60, TimeUnit.SECONDS); // XXX TIMEOUT 10mins?
		} catch (GRS2ReaderException e) {
			log.error("Did not manage to read result set element", e);
		}

		if (rec == null) {
			if (hasNext())
				log.warn("Result set returned null object");
			return null;
		}
		
		if (rec instanceof GRS2ExceptionWrapper) {
			Throwable t = ((GRS2ExceptionWrapper)rec).getEx();
			throw new Exception(t);
		}
		
		if(hideRecs) {
			rec.hide();
		}
		
		String metadata = null;
		File f = null;
		try {
			metadata = ((StringField) rec.getField(0)).getPayload();
			try {
				f = ((FileField) rec.getField(1)).getPayload();
				if (hideRecs) {
					f.deleteOnExit();
				}
			} catch (Exception e) {
				f = null;
				log.warn("Data element has no file field.");
			}
		} catch (Exception e) {
			log.error("Can't read fields from result set element", e);
		}

		DataElement de = null;
		try {
			de = RSDataElementUtil.dataElementFromRS(metadata, f);
		} catch (Exception e) {
			log.error("Can't create data element with the given fields.", e);
		}

		return de;
	}

	@Override
	public ContentType nextContentType() {
		GenericRecord rec = null;
		try {
			rec = reader.get(60, TimeUnit.SECONDS); // XXX TIMEOUT 10mins?
		} catch (GRS2ReaderException e) {
			log.error("Did not manage to read result set element", e);
		}

		if (rec == null) {
			if (hasNext())
				log.warn("Result set returned null object");
			return null;
		}

		String metadata = null;
		try {
			metadata = ((StringField) rec.getField(0)).getPayload();
		} catch (Exception e) {
			log.error("Can't read fields from result set element", e);
		}

		DataElement de = null;
		try {
			de = RSDataElementUtil.dataElementFromRS(metadata, null);
		} catch (Exception e) {
			log.error("Can't create data element with the given fields.", e);
		}

		return de.getContentType();
	}
	
//	public static void main(String[] args) throws Exception {
//		TCPConnectionManager.Init(new TCPConnectionManagerConfig("meteora.di.uoa.gr", new ArrayList<PortRange>(), true));
//
//		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
//		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
//		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
//
//		final DataSink sink = new GRS2DataSink(null, null);
//		
//		new Thread() {
//			public void run() {
//				for(int i =0; i < 10; i++){
//					DataElementImpl de = DataElementImpl.getSourceDataElement();
//					de.setAttribute("attrName" + i +".a", "attrVal" + i +".a");
//					de.setAttribute("attrName" + i +".b", "attrVal" + i +".b");
//					de.setAttribute("attrName" + i +".c", "attrVal" + i +".c");
//					de.setContentType(new ContentType("text/plain", null));
//					de.setId("id" + i);
//					
////					try {
////						de.setContent(new FileInputStream("/home/jgerbe/finaldraftedited.txt"));
////					} catch (FileNotFoundException e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					}
//					sink.append(de);
//				}
//				sink.close();
//			}
//		}.start();
//		
//		final DataSource dest = new GRS2DataSource(sink.getOutput(), null);
//		while(dest.hasNext()){
//			DataElement de = dest.next();
//			if (de == null)
//				System.out.println(de);
//			else {
//				System.out.println(de.getId());
//				System.out.println(de.getContentType().toString());
//				System.out.println(RSDataElementUtil.dataElementMetadataToXML(de));
//				System.out.println(RSDataElementUtil.stringFromInputStream(de.getContent()));
//				System.out.println();
//			}
//		}
//	}
}
