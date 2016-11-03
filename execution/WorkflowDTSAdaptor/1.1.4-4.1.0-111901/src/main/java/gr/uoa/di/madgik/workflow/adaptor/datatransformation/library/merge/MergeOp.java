package gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.net.URI;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DTSExceptionWrapper;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.IOHandler;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to create a merged ResultSet output from the contents of
 * multiple ResultSet.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class MergeOp {
	/**
	 * The default timeout used by the {@link IRecordWriter} and all
	 * {@link IRecordReader}s. Currently set to 180.
	 */
	public static final long TimeoutDef = 180;

	/**
	 * The default timeout unit used by the {@link RecordWriter} and all
	 * {@link IRecordReader}s. The current default unit is seconds.
	 */
	public static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;

	/** The Logger in use */
	private Logger log = LoggerFactory.getLogger(MergeOp.class.getName());

	/** The locator of the inputs */
	private URI locator = null;

	/** Sink for the output */
	private DataSink sink;

	/** The unique ID of this operator invocation */
	private String uid = UUID.randomUUID().toString();

	private long timeout = TimeoutDef;
	private TimeUnit timeUnit = TimeUnitDef;

	/** Used for Dispatcher synch according to locator reader */
	private Object synchDispatcher = new Object();

	/** Used for initialization waiting */
	private Object synchInit = new Object();

	/** True when locator gets closed */
	private boolean locatorClosed = false;

	private MergeWorker worker;
	boolean isInit = false;

	/**
	 * Creates a new {@link MergeOp} with the default timeout used both for
	 * readers and the writer
	 * 
	 * @param locator
	 *            The locator of the inputs that will be merged
	 * @throws Exception
	 */
	public MergeOp(URI locator, Output output) throws Exception {
		try {
			IOHandler.init(null);
		} catch (Exception e) {
			log.error("DS: " + uid + " Could not initialize IOHandler", e);
			throw new Exception("Could not initialize IOHandler", e);
		}

		this.locator = locator;

		// Get the data source
		try {
			sink = IOHandler.getDataSink(output);
		} catch (Exception e) {
			log.error("DS: " + uid + " Could not create data source.", e);
			try {
				sink.close();
			} catch (Exception e1) {
			}
			throw new Exception("Could not create DataSource from the given Input", e);
		}
	}

	/**
	 * Creates a new {@link MergeOp} with configurable operation mode and
	 * timeout
	 * 
	 * @param locator
	 *            The locator of the inputs that will be merged
	 * @param timeout
	 *            The timeout that will be used by the {@link RecordWriter} and
	 *            all {@link ForwardReader}s
	 * @param timeUnit
	 *            The timeout unit that will be used by the {@link RecordWriter}
	 *            and all {@link ForwardReader}s
	 * @throws Exception
	 */
	public MergeOp(URI locator, Output output, long timeout, TimeUnit timeUnit) throws Exception {
		this(locator, output);
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}

	/**
	 * Performs the merging operation
	 * 
	 * @return The locator of the merged result set
	 * @throws Exception
	 *             An unrecoverable for the operation error occurred
	 */
	public String compute() throws Exception {
		Thread t = new Thread() {
			public void run() {
				Thread.currentThread().setName("Merge Operator");
				try {
					long mergestart = System.currentTimeMillis();
					Vector<ReaderHolder> readers = new Vector<ReaderHolder>();
					Vector<ReaderInit> initT = new Vector<ReaderInit>();

					log.debug("Creating Forward Reader for locator: " + locator);
					LocatorReader inputRetriever = new LocatorReader(locator, readers, initT, uid, synchDispatcher);
					inputRetriever.start();

					synchronized (initT) {
						while (initT.size() == 0 && !inputRetriever.hasFinished())
							initT.wait(10000);
					}
					
					if (inputRetriever.hasFinished() && initT.size() == 0) {
						throw new Exception("input has been closed unexpectedly. "
								+ (inputRetriever.hasFinished()? "input retrieve has finished" : "input retrieve didn't finish")
								+ " and number of inputs was " + initT.size());
					}

					worker = new MergeWorker(readers, sink, uid, inputRetriever, synchDispatcher);
					worker.start();
					
					synchronized (synchInit) {
						isInit = true;
						synchInit.notify();
					}
					
					for (int i = 0; i < initT.size(); i++) {
						try {
							initT.get(i).join();
						} catch (Exception e) {
						}
					}

					long mergestop = System.currentTimeMillis();
					log.info("MERGE OPERATOR " + uid + " TOOK " + (mergestop - mergestart));
				} catch (Exception e) {
					synchronized (synchInit) {
						isInit = true;
						synchInit.notify();
					}
					if (sink != null)
						sink.append(new DTSExceptionWrapper(e));
					log.error("Could not start background process of merging for operator " + uid + ". Throwing Exception", e);
					sink.close();
				}
			}
		};
		t.setDaemon(false);
		t.start();
		
		synchronized (synchInit) {
			while (isInit == false)
				synchInit.wait();
		}
		if (worker == null)
			throw new Exception("DTS could not be initialised");
		
		synchronized (worker.getCounter()) {
			while(worker.getCounter().get() == 0 && !worker.hasFinished()) {
				worker.getCounter().wait(DTSCore.TIMEOUT);
			}
			if (worker.getCounter().get() == 0 && worker.hasFinished()) {
				return null;
			}
		}

		log.trace(uid + ": Returns output: " + sink.getOutput());
		return sink.getOutput();
	}

//	private static List<URI> uri = Collections.synchronizedList(new ArrayList<URI>());
//	static final String DIR = "directory";
//	static final String USER = "username";
//	static final String PASS = "password";
//
//	public static void main(String[] args) throws Exception {
//		TCPConnectionManager.Init(new TCPConnectionManagerConfig("meteora.di.uoa.gr", new ArrayList<PortRange>(), true));
//		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
//		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
//
//		RecordDefinition[] defs = new RecordDefinition[] { new GenericRecordDefinition((new FieldDefinition[] { new StringFieldDefinition("ThisIsTheField") })) };
//		RecordWriter<GenericRecord> writer = new RecordWriter<GenericRecord>(new LocalWriterProxy(), defs);
//
//		DummyGRS dum;
//		for (int i = 0; i < 100; i++) {
//			dum = new DummyGRS(i);
//			System.out.println(dum.getLocator());
//			uri.add(URI.create(dum.getLocator()));
//		}
//
//		String outputValue = "meteora.di.uoa.gr";
//		String user = "giannis";
//		String pass = "ftpsketo";
//		String sf = "src";
//		String df = "dest";
//
//		Parameter[] outputparameters = new Parameter[]{
//				new Parameter(USER, user),
//				new Parameter(PASS, pass),
//				new Parameter(DIR, df)
//		};
//		Output output = new Output("Local", "/home/jgerbe/testArea/" + df, null);
//		
//		DataSourceOp source = new DataSourceOp(new Input("Local", "/home/jgerbe/testArea/" + sf, null), new ContentType("image/jpeg", null));
//		
//		uri.add(source.compute());
//		
//		MergeOp merger = new MergeOp(writer.getLocator(), output);
//		merger.compute();
//
//		for (URI i : uri) {
//			// while the reader hasn't stopped reading
//			if (writer.getStatus() != Status.Open)
//				break;
//			GenericRecord rec = new GenericRecord();
//			// Only a string field is added to the record as per definition
//			rec.setFields(new Field[] { new StringField(i.toASCIIString()) });
//			System.out.println("Added uri: " + i.toASCIIString());
//			// if the buffer is in maximum capacity for the specified
//			// interval don;t wait any more
//			if (!writer.put(rec, 60, TimeUnit.SECONDS))
//				break;
//		}
//		
//		writer.close();
//	}
//}
//
//class DummyGRS extends Thread {
//	private int seed;
//	GRS2DataSink sink;
//
//	LocalFileDataElement de = new LocalFileDataElement();
//
//	public DummyGRS(int seed) {
//		this.seed = seed;
//		FileWriter fstream;
//		try {
//			File f = new File("/tmp/dts/haha." + seed + ".dts");
//			f.deleteOnExit();
//			de.setContent(f);
//			fstream = new FileWriter("/tmp/dts/haha." + seed + ".dts");
//
//			BufferedWriter out = new BufferedWriter(fstream);
//			out.write("Content with seed: " + seed);
//			// Close the output stream
//			out.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		try {
//			sink = new GRS2DataSink(null, null);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		this.setDaemon(true);
//		this.start();
//	}
//
//	public String getLocator() {
//		return sink.getOutput();
//	}
//
//	@Override
//	public void run() {
//		int i = 0;
//		for (; i < 10; i++) {
//			// while (true) {
//			de.setId(String.valueOf(seed + ":" + i));
//			ContentType ct = new ContentType();
//			ct.setMimeType("text/plain");
//			de.setContentType(ct);
//			sink.append(de);
//		}
//		sink.close();
//	}
}
