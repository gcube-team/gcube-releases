package gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.datasource;

import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.net.URI;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.gcube.datatransformation.datatransformationlibrary.DTSScope;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DTSExceptionWrapper;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.IOHandler;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.GRS2DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Input;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class responsible for retrieving data elements from a
 * {@link DataSource} and forward to ResultSet only those of specific content
 * type.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class DataSourceOp {
	/** The logger that class uses. */
	private Logger log = LoggerFactory.getLogger(DataSourceOp.class.getName());

	/** The unique ID of this operator invocation. */
	private String uid = UUID.randomUUID().toString();

	/** The data source from which data elements are read. */
	private DataSource source = null;

	/**
	 * The data sink that data elements are written. Typically, a
	 * {@link GRS2DataSink}.
	 */
	private DataSink sink = null;

	/** The content type of data elements to be forwarded. */
	private ContentType contentType = null;

	/**
	 * The default timeout used by the {@link IRecordWriter}. Currently set to
	 * 180.
	 */
	public static final long TimeoutDef = 180;

	/**
	 * The default timeout unit used by the {@link RecordWriter}. The current
	 * default unit is seconds.
	 */
	public static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;

	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;

	/**
	 * Creates a new {@link DataSourceOp} with the default timeout for the
	 * writer.
	 * 
	 * @param input
	 *            The {@link Input} for the corresponding {@link DataSource}.
	 * @param contentType
	 *            The elements' mimeType that we want to forward.
	 * @throws Exception
	 *             If underlying components could not be initialized.
	 */
	public DataSourceOp(Input input, ContentType contentType) throws Exception {
		Parameter[] inputParameters = input.getInputParameters();
		for (Parameter par : inputParameters) {
			log.debug("parameter (name, value): (" +par.getName() + ", "+ par.getValue() +")");
			if (par.getName().equals("GCubeActionScope")) {
				DTSScope.setScope((par.getValue()));
				break;
			}
		}

		try {
			IOHandler.init(null);
		} catch (Exception e) {
			log.error("DS: " + uid + " Could not initialize IOHandler", e);
			throw new Exception("Could not initialize IOHandler", e);
		}

		// Set desired content type
		if (contentType.getMimeType().trim().length() == 0)
			log.warn("DS: " + uid + " No content type set. Will fetch every data element.");
		this.contentType = contentType;
		
		// Remove useless parameters
		if (this.contentType.getContentTypeParameters() != null) {
			Iterator<Parameter> it = this.contentType.getContentTypeParameters().iterator();
			while(it.hasNext())
				if (it.next().getValue().equals("-"))
					it.remove();
		}
		
		// Getting the sink - Getting the sink first is better because source
		// may start downloading objects
		try {
			sink = new GRS2DataSink(null, new Parameter[] { new Parameter("deleteOnDispose", "true") });
		} catch (Exception e) {
			log.error("DS: " + uid + " Could not create GRS2DataSink", e);
			throw new Exception("Could not create GRS2DataSink", e);
		}

		// Get the data source
		try {
			source = IOHandler.getDataSource(input);
		} catch (Exception e) {
			log.error("DS: " + uid + " Could not create data source.", e);
			try {
				sink.close();
			} catch (Exception e1) {
			}
			throw new Exception("Could not create DataSource from the given Input", e);
		}
		log.debug("DataSourceOP with input: " + input.getInputValue() + " for mimetype: " + contentType.getMimeType() + " and output RS: " + sink.getOutput());
	}

	/**
	 * Creates a new {@link DataSourceOp} with custom timeout set.
	 * 
	 * @param input
	 *            The {@link Input} for the corresponding {@link DataSource}.
	 * @param type
	 *            The elements' {@link ContentType} that we want to forward.
	 * @param timeout
	 *            The timeout that will be used.
	 * @param timeUnit
	 *            The timeout unit that will be used.
	 * @throws Exception
	 *             If underlying components could not be initialized.
	 */
	public DataSourceOp(Input input, ContentType contentType, long timeout, TimeUnit timeUnit) throws Exception {
		this(input, contentType);
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}

	/**
	 * Performs the reading of a {@link DataSource} and append to a ResultSet
	 * the {@link DataElement}s of specific {@link ContentType}.
	 * 
	 * @return The locator of the result set.
	 * @throws Exception
	 *             An unrecoverable error for the operation occurred.
	 */
	public URI compute() throws Exception {

		Thread t = new Thread() {
			@Override
			public void run() {
				Thread.currentThread().setName("DataSource Operator");
				long readstart = System.currentTimeMillis();

				while (!sink.isClosed() && source.hasNext()) {
					DataElement de = null;
					try {
						de = source.next();
					} catch (Exception e) {
						sink.append(new DTSExceptionWrapper(e));
						break;
					}
					if (de == null) {
						log.warn("Got null object. Moving to next");
						continue;
					}
					if (de instanceof DTSExceptionWrapper) {
						log.error("Forwarding exception: " + ((DTSExceptionWrapper)de).getThrowable().getMessage());
						sink.append(de);
					}
					if (de.getContentType() == null) {
						log.warn("DS: " + uid + " Could not evaluate content type.");
						continue;
					}
					log.trace("DS: " + uid + " Data element retrieved with id: " + de.getId() + ". Will try to append it to data sink.");

					if (contentType.getMimeType().trim().length() == 0 || contentType.getMimeType().equalsIgnoreCase(de.getContentType().getMimeType()) || contentType.getMimeType().equalsIgnoreCase("*/*")) {
						sink.append(de);
						log.debug("DS: " + uid + " Data element with compatible content type appended to rs. ID: " + de.getId() + " mime type: "
								+ de.getContentType().getMimeType());
					} else{
						// TODO application/xml and text/xml are considered the same
						if (contentType.getMimeType().equalsIgnoreCase("text/xml") && de.getContentType().getMimeType().equalsIgnoreCase("application/xml") ||
							contentType.getMimeType().equalsIgnoreCase("application/xml") && de.getContentType().getMimeType().equalsIgnoreCase("text/xml")) {
							sink.append(de);
							log.debug("DS: " + uid + " Data element with compatible content type appended to rs. ID: " + de.getId() + " mime type: "
									+ de.getContentType().getMimeType());
						} else
							log.debug("DS: " + uid + " Data element with incompatible content type was not appended to rs. ID: " + de.getId() + " mime type: "
								+ de.getContentType().getMimeType()+". Desired data elements should have type: " + contentType.getMimeType() + "\n" + contentType.toString() + "\n" + de.getContentType().toString());
					}
				}
				source.close();

				long readend = System.currentTimeMillis();

				log.info("DS: " + uid + " Datasource retrieved and appended to RS after: " + (readend - readstart) + " msecs");

				sink.close();
			}
		};
		t.start();

		return URI.create(sink.getOutput());
	}

	/**
	 * For testing only
	 * 
	 * @param args
	 */

//	public static void main(String args[]) {
////		String scope = "/gcube/devNext";
//		String inputMCollectionID = "ca5e67a4-11b4-4925-883e-657bce4b5c95";
////		String xslt = "$BrokerXSLT_FARM_dc_anylanguage_to_ftRowset_anylanguage";
////		String indexType = "FARM_dc";
////		
////		String transformationProgramID = "2";
////		String transformationUnitID = "0";
//		
//		/* INPUT */
//		Input input = new Input();
//		input.setInputType("CMAggregate");
//		input.setInputValue("3781faa0-9d60-11de-8d8f-a04a2d1ca936");
//		Input [] inputs = {input};
//
//		// For collection DS
//		String collID = "2f505dc0-a652-11e0-9d5c-fda94ff03826";
//
//		// For ftp DS
//		final String PARAMETER_DirectoryName = "directory";
//		final String PARAMETER_Username = "username";
//		final String PARAMETER_Password = "password";
//
//		String srv = "meteora.di.uoa.gr";
//		String usr = "giannis";
//		String pass = "ftpsketo";
//		String fold = "src";
//
//		Parameter srcdir = new Parameter(PARAMETER_DirectoryName, fold);
//		Parameter username = new Parameter(PARAMETER_Username, usr);
//		Parameter password = new Parameter(PARAMETER_Password, pass);
//		Parameter[] inputParameters = { srcdir, username, password };
//
//
//		DataSourceOp ds = null;
//		try {
//			ds = new DataSourceOp(input, new ContentType("", new ArrayList<Parameter>()));
//		} catch (GCUBEFault e) {
//			e.printStackTrace();
//		}
////		DataSink sink = new PathDataSink("/home/jgerbe/testArea/dest", null);
//		URI uri = null;
//		try {
//			uri = ds.compute();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		DataSource middle = null;
//		try {
//			middle = new GRS2DataSource(uri.toASCIIString(), null);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		while (middle.hasNext()){
//			DataElement de = middle.next();
//			System.out.println(de.getId());
////			sink.append(middle.next());
//		}
//	}
}
