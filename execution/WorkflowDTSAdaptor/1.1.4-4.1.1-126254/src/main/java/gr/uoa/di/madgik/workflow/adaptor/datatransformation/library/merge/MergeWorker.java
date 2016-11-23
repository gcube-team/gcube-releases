package gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge;

import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DTSExceptionWrapper;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.LocalFileDataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to create a merged ResultSet output from the contents of
 * multiple ResultSet, not all known at initialization time. The number of
 * ResultSet to be merged may grow dynamically.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class MergeWorker extends Thread {
	/** The Logger the class uses */
	private Logger log = LoggerFactory.getLogger(MergeWorker.class.getName());

	/** The readers to use */
	private Vector<ReaderHolder> readers = null;

	/** The {@link DataSink} that will be used for output ResultSet. */
	private DataSink sink = null;

	/** Synchronization object for writer initialization */
	private Object synchWriterInit = new Object();

	/** Synchronization object for locators addition */
	private Object synchDispatcher;

	/** A unique identifier for this operation */
	private String uid = null;

	/** The number of records merged */
	private AtomicInteger count = new AtomicInteger(0);

	/** Used for timing */
	private long firststop = 0;

	/** Used to see when finished */
	private LocatorReader inputRetriever;

	/** Used for synchronization of termination */
	private SynchFinished synchFinished = new SynchFinished();
	
	private boolean finished = false;

	/**
	 * Creates a new {@link MergeWorker}
	 * 
	 * @param readers
	 *            Vector holding info on the input ResultSet
	 * @param sink
	 *            The output {@link DataSink} to place the records to
	 * @param uid
	 *            A unique identifier for this operation
	 * @param inputRetriever
	 * @param synchDispatcher
	 */
	public MergeWorker(Vector<ReaderHolder> readers, DataSink sink, String uid, LocatorReader inputRetriever, Object synchDispatcher) {
		this.readers = readers;
		this.sink = sink;
		this.uid = uid;
		this.inputRetriever = inputRetriever;
		this.synchDispatcher = synchDispatcher;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		long start = System.currentTimeMillis();
		this.setName("Merge Worker");
		BlockingQueue<DataElement> des = null;
		Queue<EventEntry> events = null;

		try {
			int capacity = 100; // Estimated optimal capacity. appr 100
			des = new ArrayBlockingQueue<DataElement>(capacity);
			log.debug("Queue capacity for " + this.uid + " is " + capacity);

			// events = new ConcurrentLinkedQueue<EventEntry>();
			Vector<ReaderScan> scan = new Vector<ReaderScan>(readers.size());

			ScanDispatcher dispatcher = new FirstAvailableScanDispatcher(scan, readers, des, events, uid, synchDispatcher, synchWriterInit, synchFinished);

			dispatcher.start();

			// Wait for writer to become available. This will happen once all
			// readers are initialized and, consequently, all record definitions
			// are retrieved

			synchronized (synchWriterInit) {
				while (scan.size() == 0 && !synchFinished.isFinished())
					synchWriterInit.wait();
			}

			// EventHandler<Record> eventHandler = new
			// EventHandler<Record>(sink, events, readers.size(), 100);


			int time = 1; 
			while (!finished) {

				if (this.count.get() == 0) {
					firststop = System.currentTimeMillis();
					log.debug("First stop: " + (firststop - start));
				} else if (this.count.get() == 1){
					synchronized (count) {
						count.notify();
					}
				}
				// avoid continual checking of reader status when queue becomes
				// empty in exchange for a 500ms delay on completion
				DataElement de = des.poll(time < 500? time *= 2 : 500, TimeUnit.MILLISECONDS);
				// eventHandler.propagateEvents();

				if (de != null) {
					if (de instanceof DTSExceptionWrapper) {
						log.error("received execption",((DTSExceptionWrapper) de).getThrowable());
						sink.append(de);
						continue;
					} else {
						sink.append(de);
						this.count.incrementAndGet();
						de.destroy();
						if (de instanceof LocalFileDataElement) {
							((LocalFileDataElement) de).getFileContent().delete();
						}
						// eventHandler.increaseProducedRecordCount();
					}
				} else {
					int count = 0;
					for (int i = 0; i < scan.size(); i++) {
						if (!readers.get(i).hasFinished())
							count++;
					}
					if (count == 0 && inputRetriever.hasFinished())
						finished = true;
				}
				if (sink.isClosed())
					finished = true;
			}
			
			synchFinished.setFinished(true);

			synchronized (readers) {
				for(int i = 0; i < readers.size(); i++)
				readers.get(i).setFinished(true);
			}

			Exception ex = null;
			for (int i = 0; i < scan.size(); i++) {
				do {
					try {
						scan.get(i).join();
						ex = null;
					} catch (InterruptedException e) {
						ex = e;
						log.error("received exception" , ex);
						sink.append(new DTSExceptionWrapper(ex));
					}
				} while (ex != null);
			}

			// eventHandler.sendPendingFinalEvents(this.count);

		} catch (Exception e) {
			log.error("Could not complete background merging for " + this.uid + ". Closing", e);
			sink.append(new DTSExceptionWrapper(e));
		} finally {
			synchronized (count) {
				count.notify();
			}
			des.clear();
			sink.close();
			for (int i = 0; i < readers.size(); i++) {
				if (readers.get(i).getReader() != null && !readers.get(i).getReader().isClosed()){
					log.info("Closing GRS2DataSource #" + i);
					readers.get(i).getReader().close();
				}
			}
			synchronized (synchDispatcher) {
				synchDispatcher.notify();
			}
			synchronized (synchWriterInit) {
				synchWriterInit.notify();
			}
			long closestop = System.currentTimeMillis();
			log.info("MERGE OPERATOR " + this.uid + ":\nProduced first result in " + (firststop - start) + " milliseconds\n" + "Produced last result in "
					+ (closestop - start) + " milliseconds\n" + "Produced " + count.get() + " data elements\n" + "Production rate was "
					+ (((float) this.count.get() / (float) (closestop - start)) * 1000) + " records per second");
		}
	}
	
	AtomicInteger getCounter() {
		return count;
	}
	
	boolean hasFinished() {
		return finished;
	}
}

class SynchFinished {
	private Boolean finished = false;

	/**
	 * @return the hasFinished
	 */
	public synchronized boolean isFinished() {
		return Boolean.valueOf(finished);
	}

	/**
	 * @param hasFinished the hasFinished to set
	 */
	public synchronized void setFinished(boolean hasFinished) {
		this.finished = hasFinished;
	}
}
