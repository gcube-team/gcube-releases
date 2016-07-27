package gr.uoa.di.madgik.searchlibrary.operatorlibrary.gmerge;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.Unary;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.OperationMode;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.ReaderHolder;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.ReaderInit;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to create a merged ResultSet output from the contents of
 * multiple ResultSet.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class GradualMergeOp extends Unary {
	/** The Logger in use */
	private Logger loger = LoggerFactory.getLogger(GradualMergeOp.class.getName());
	/**
	 * The default operation mode. Currently set to
	 * {@link OperationMode#FirstAvailable}
	 */
	public static final OperationMode OperationModeDef = OperationMode.FirstAvailable;
	/**
	 * The default capacity of the {@link RecordWriter}s and, if applicable, of
	 * all {@link IRecordReader}s' buffers
	 */
	public static final int BufferCapacityDef = 100;

	/** The unique ID of this operator invocation */
	private String uid = UUID.randomUUID().toString();

	/** Used for Dispatcher synch according to locator reader */
	private Object synchDispatcher = new Object();
	private Object synchMergingStart = new Object();

	private GradualMergeWorker worker;
	private Vector<ReaderHolder> readers;

	/**
	 * Creates a new {@link GradualMergeOp} with the default timeout used both
	 * for readers and the writer
	 * 
	 * @param inLocator
	 *            The locator of the inputs that will be merged
	 * @param operatorParameters oeprator parameters
	 * @param stats
	 *            Statistics
	 * @throws Exception 
	 */
	public GradualMergeOp(URI inLocator, Map<String, String> operatorParameters, StatsContainer stats) throws Exception {
		super(inLocator, operatorParameters, stats);
		
		loger.info("Gradual Merge operator initialised with locator: " + inLocator);
	}

	/**
	 * Creates a new {@link GradualMergeOp} with configurable operation mode and
	 * timeout
	 * 
	 * @param inLocator
	 *            The locator of the inputs that will be merged
	 * @param operatorParameters operator parameters
	 * @param timeout
	 *            The timeout that will be used by the {@link RecordWriter} and
	 *            all {@link ForwardReader}s
	 * @param timeUnit
	 *            The timeout unit that will be used by the {@link RecordWriter}
	 *            and all {@link ForwardReader}s
	 * @param stats
	 *            Statistics
	 * @throws Exception 
	 */
	public GradualMergeOp(URI inLocator, Map<String, String> operatorParameters, long timeout, TimeUnit timeUnit, StatsContainer stats) throws Exception {
		super(inLocator,operatorParameters, stats, timeout, timeUnit);
		
		loger.info("Gradual Merge operator initialised with locator: " + inLocator);
	}

	/**
	 * Performs the merging operation
	 * 
	 * @return The locator of the merged result set
	 * @throws Exception
	 *             An unrecoverable for the operation error occurred
	 */
	public URI compute() throws Exception {

		Thread t = new Thread() {
			public void run() {
				try {
					long mergestart = System.currentTimeMillis();
					readers = new Vector<ReaderHolder>();
					Vector<ReaderInit> initT = new Vector<ReaderInit>();

					GradualLocatorReader inputRetriever = new GradualLocatorReader(inLocator, readers, initT, uid, synchDispatcher);
					inputRetriever.start();

					synchronized (initT) {
						while (initT.size() == 0 && !inputRetriever.hasFinished())
							initT.wait();
					}

					worker = new GradualMergeWorker(readers, stats, timeout, timeUnit, uid, inputRetriever, synchDispatcher, synchMergingStart);
					worker.start();

					for (int i = 0; i < initT.size(); i++) {
						try {
							initT.get(i).join();
						} catch (Exception e) {
						}
					}

					long mergestop = System.currentTimeMillis();
					loger.info("MERGE OPERATOR " + uid + " TOOK " + (mergestop - mergestart));
				} catch (Exception e) {
					loger.error("Could not start background process of merging for operator " + uid + ". Throwing Exception", e);
				}
			}
		};
		t.setDaemon(false);
		t.start();

		synchronized (synchMergingStart) {
			while (worker == null || worker.getWriterLocator() == null) {
				synchMergingStart.wait();
			}
		}

		loger.trace(uid + ": Returns output: " + worker.getWriterLocator());
		return worker.getWriterLocator();
	}
}
