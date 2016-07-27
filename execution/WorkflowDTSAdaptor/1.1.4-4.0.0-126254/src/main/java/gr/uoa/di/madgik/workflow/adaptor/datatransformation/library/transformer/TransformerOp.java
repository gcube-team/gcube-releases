package gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.transformer;

import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.gcube.datatransformation.datatransformationlibrary.DTSScope;
import org.gcube.datatransformation.datatransformationlibrary.ProgramExecutor;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DTSExceptionWrapper;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.GRS2DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.GRS2DataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.RS2DataSink;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class responsible for retrieving data elements from a ResultSet,
 * performs a simple transform on them and forward the outcome to a ResultSet.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class TransformerOp {
	/** The logger that class uses. */
	private Logger log = LoggerFactory.getLogger(TransformerOp.class.getName());

	/** The unique ID of this operator invocation. */
	private String uid = UUID.randomUUID().toString();

	/**
	 * The data source from which data elements are read. Typically, a
	 * {@link RS2DataSource}.
	 */
	private DataSource source = null;

	/**
	 * The data sink that data elements are written. Typically, a
	 * {@link RS2DataSink}.
	 */
	private DataSink sink = null;

	/** The content type that data elements have to be transformed to. */
	private ContentType targetContentType = null;

	/** The TransformationUnit that will be used. */
	private TransformationUnit tUnit;

	/**
	 * The default timeout. Currently set to 180.
	 */
	public static final long TimeoutDef = 180;

	/**
	 * The default timeout unit. The current default unit is seconds.
	 */
	public static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;

	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;

	/**
	 * Creates a new {@link TransformerOp} with the default timeout for the
	 * writer.
	 * 
	 * @param input
	 *            The locator of the input that will be transformed.
	 * @param tUnit
	 *            The {@link TransformationUnit} that will be used.
	 * @param mimeType
	 *            The mimeType of the transformation's outcome.
	 * @throws Exception
	 *             If underlying components could not be initialized.
	 */
	public TransformerOp(URI input, TransformationUnit tUnit, ContentType contentType, String scope) throws Exception {
		DTSScope.setScope(scope);

		this.tUnit = tUnit;
		targetContentType = contentType;

		try {
			sink = new GRS2DataSink(null, new Parameter[] { new Parameter("deleteOnDispose", "true") });
		} catch (Exception e) {
			log.error("Trans: " + uid + " Could not create GRS2DataSink", e);
			throw new Exception("Could not create GRS2DataSink", e);
		}

		try {
			source = new GRS2DataSource(input.toASCIIString(), null);
		} catch (Exception e) {
			log.error("Trans: " + uid + " Could not create GRS2DataSource", e);
			try {
				sink.append(new DTSExceptionWrapper(e));
				sink.close();
			} catch (Exception e1) {
			}
			throw new Exception("Could not create GRS2DataSource", e);
		}
		log.debug("TransformationOP with input RS: " + input.toASCIIString() + " from mimetype: " + tUnit.getSources().get(0).getContentType().getMimeType()
				+ " to mimetype: " + targetContentType.getMimeType() + " and output RS: " + sink.getOutput());
	}

	/**
	 * Creates a new {@link TransformerOp} with custom timeout set.
	 * 
	 * @param input
	 *            The locator of the input that will be transformed.
	 * @param tUnit
	 *            The {@link TransformationUnit} that will be used.
	 * @param mimeType
	 *            The mime type that input will be transformed to.
	 * @param timeout
	 *            The timeout that will be used.
	 * @param timeUnit
	 *            The timeout unit that will be used.
	 * @throws Exception
	 *             If underlying components could not be initialized.
	 */
	public TransformerOp(URI input, TransformationUnit tUnit, ContentType contentType, String scope, long timeout, TimeUnit timeUnit) throws Exception {
		this(input, tUnit, contentType, scope);
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}

	/**
	 * Performs the reading of a {@link DataSource}, transforms the
	 * {@link DataElement}s to a corresponding mime type and appends the
	 * outcome.
	 * 
	 * @return The locator of the result set.
	 * @throws Exception
	 *             An unrecoverable error for the operation occurred.
	 */
	public URI compute() throws Exception {
		long readstart = System.currentTimeMillis();

		ArrayList<DataSource> sources = new ArrayList<DataSource>();
		sources.add(source);

		ProgramExecutor.transformDataWithProgram(sources, tUnit.getTransformationProgram().getTransformer(), tUnit.mergeProgramParameters(), targetContentType,
				sink);

		long readend = System.currentTimeMillis();

		log.info("Trans: " + uid + " Transformation process has been assigned after: " + (readend - readstart) + " msecs");

		return URI.create(sink.getOutput());
	}

	/**
	 * For testing only
	 * 
	 * @param args
	 */

//	public static void main(String args[]) throws Exception {
//		TCPConnectionManager.Init(new TCPConnectionManagerConfig("meteora.di.uoa.gr", new ArrayList<PortRange>(), true));
//		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
//		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
//
//		// Add-hoc dependency
//		LocalInfoManager imanager = new LocalInfoManager();
//		imanager.setProgramsFile("programs.xml");
//		try {
//			ProgramExecutor.initializeDeployer("dts_libs_path");
//		} catch (Exception e3) {
//			// TODO Auto-generated catch block
//			e3.printStackTrace();
//		}
//		TransformationsGraphImpl graph = new TransformationsGraphImpl(imanager);
//		//
//
//		Input in = new Input();
//		in.setInputType("Local");
//		in.setInputValue("/home/jgerbe/testArea/src");
//		in.setInputparameters(null);
//
//		ContentType sct = new ContentType();
//		sct.setMimeType("image/gif");
//
//		gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.datasource.DataSourceOp ds = null;
//		ds = new gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.datasource.DataSourceOp(in, sct.getMimeType());
//
//		URI uri = null;
//		uri = ds.compute();
//
//		ContentType tct = new ContentType();
//		tct.setMimeType("image/png");
//
//		ArrayList<TransformationUnit> tus = graph.findApplicableTransformationUnits(sct, tct, false);
//		TransformationUnit tuo = graph.findApplicableTransformationUnits(sct, tct, false).get(0);
//		System.out.println("Trans Units: " + tus.size());
//		System.out.println("Parameterss: " + tuo.getProgramParameters().size());
//
//		//
//		String s = null;
//		TransformationUnitConverter conv = new TransformationUnitConverter();
//		s = conv.Convert(tuo);
//		// System.out.println(s);
//
//		TransformationUnit tu = null;
//		tu = (TransformationUnit) conv.Convert(s);
//
//		System.out.println(s + "\n" + conv.Convert(tu));
//		TransformerOp ts = null;
//		ts = new TransformerOp(uri, tu, tct);
//
//		URI out = null;
//		out = ts.compute();
//		System.out.println("URI: " + out.toASCIIString());
//
//		DataSource res = null;
//		res = new GRS2DataSource(out.toASCIIString(), null);
//
//		DataSink sink = new PathDataSink("/home/jgerbe/testArea/dest", null);
//		while (res.hasNext()) {
//			DataElement de = res.next();
//			if (de != null) {
//				System.out.println("Got data element with id: " + de.getId());
//				sink.append(de);
//			} else
//				System.out.println("null");
//		}
//	}
}
