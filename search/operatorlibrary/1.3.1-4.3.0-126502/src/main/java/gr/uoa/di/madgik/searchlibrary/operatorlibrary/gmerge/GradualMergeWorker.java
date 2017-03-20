package gr.uoa.di.madgik.searchlibrary.operatorlibrary.gmerge;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.EventEntry;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.ReaderHolder;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.ReaderScan;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.RecordBufferEntry;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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
public class GradualMergeWorker extends Thread {
	/** The Logger the class uses */
	private Logger log = LoggerFactory.getLogger(GradualMergeWorker.class.getName());

	/** The readers to use */
	private Vector<ReaderHolder> readers = null;
	/**
	 * The writer to use
	 */
	private RecordWriter<Record> writer = null;
	/**
	 * Synchronization object for writer initialization
	 */
	private Object synchWriterInit = new Object();

	/** Synchronization object for locators addition */
	private Object synchDispatcher;

	/** A unique identifier for this operation */
	private String uid = null;

	/**
	 * The timeout that will be used by {@link RecordWriter} and all
	 * {@link IRecordReader}s involved in the merging operation
	 */
	private long timeout;
	/**
	 * The timeout unit that will be used by {@link RecordWriter} and all
	 * {@link IRecordReader}s involved in the merging operation
	 */
	private TimeUnit timeUnit;

	/** The number of records merged */
	private int count = 0;

	/** Used for timing */
	private long firststop = 0;

	/** Used to see when finished */
	private GradualLocatorReader inputRetriever;

	/** Used for synchronization of termination */
	private SynchFinished synchFinished = new SynchFinished();
	/**
	 * statistics
	 */
	private StatsContainer stats;

	private Object synchMergingStart;

	/**
	 * Creates a new {@link GradualMergeWorker}
	 * 
	 * @param readers
	 *            Vector holding info on the input ResultSet
	 * @param stats
	 *            Statistics
	 * @param timeout
	 *            The timeout that will be used by the {@link RecordWriter} and
	 *            all {@link ForwardReader}s
	 * @param timeUnit
	 *            The timeout unit that will be used by the {@link RecordWriter}
	 *            and all {@link ForwardReader}s
	 * @param uid
	 *            A unique identifier for this operation
	 * @param inputRetriever
	 *            The retriever of the input locators
	 * @param synchDispatcher
	 *            synchronization object
	 * @param synchMergingStart 
	 */
	public GradualMergeWorker(Vector<ReaderHolder> readers, StatsContainer stats, long timeout, TimeUnit timeUnit, String uid,
			GradualLocatorReader inputRetriever, Object synchDispatcher, Object synchMergingStart) {
		this.readers = readers;
		this.stats = stats;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.uid = uid;
		this.inputRetriever = inputRetriever;
		this.synchDispatcher = synchDispatcher;
		this.synchMergingStart = synchMergingStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		Thread.currentThread().setName(GradualMergeWorker.class.getName());

		long start = System.currentTimeMillis();
		long closestop = System.currentTimeMillis();
		this.setName("Merge Worker");
		BlockingQueue<RecordBufferEntry> recs = null;
		// Queue<EventEntry> events = null;

		try {
			// int capacity = estimateQueueCapacity(readers);
			int capacity = 100;
			log.info("Queue capacity for " + this.uid + " is " + capacity);
			recs = new ArrayBlockingQueue<RecordBufferEntry>(capacity);
			// events = new ConcurrentLinkedQueue<EventEntry>();
			Vector<ReaderScan> scan = new Vector<ReaderScan>(readers.size());

			GradualScanDispatcher dispatcher = new FirstAvailableGradualScanDispatcher(scan, readers, recs, new LinkedList<EventEntry>(), uid, synchDispatcher,
					synchWriterInit, synchFinished);

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

			boolean finished = false;

			long waitTime = 10;
			while (!finished) {

				if (this.count == 0) {
					firststop = System.currentTimeMillis();
					stats.timeToFirstInput(firststop - start);
					log.debug("First stop: " + (firststop - start));
				}
				// avoid continual checking of reader status when queue becomes
				// empty in exchange for a 500ms delay on completion
				RecordBufferEntry rbEntry = recs.poll(waitTime, TimeUnit.MILLISECONDS);
				// eventHandler.propagateEvents();

				if (rbEntry != null) {
//					waitTime = 10;
					Record rec = rbEntry.record;

					if (writer == null) {
						for (int i = 0; i < readers.size(); i++) {
							ReaderHolder reader = readers.get(i);
							if (reader.getWaitingForInit())
								continue;
	
							RecordDefinition[] recordDefinitions = reader.getReader().getRecordDefinitions();
							writer = new RecordWriter<Record>(new LocalWriterProxy(), recordDefinitions, 1000,
									RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor, timeout, timeUnit);
							synchronized (synchMergingStart) {
								synchMergingStart.notify();
							}
							break;
						}
					}
					
					if (writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
						log.info("Consumer side of " + this.uid + " stopped consumption. Notifying all " + readers.size() + " readers to stop.");
//						synchronized (readers) {
							for (int i = 0; i < readers.size(); i++) {
								readers.get(i).setFinished(true);
								try {
									readers.get(i).getReader().close();
								} catch (GRS2ReaderException re) {
									log.warn("Could not close reader #" + i);
								}
							}
//						}
						recs.clear();
						break;
					}
					if (!writer.importRecord(rec, 0, timeout, timeUnit)) {
						if (writer.getStatus() == IBuffer.Status.Open)
							log.warn("Writer of " + this.uid + " has timed out");
						break;
					}
					this.count++;
					// eventHandler.increaseProducedRecordCount();
				} else {
					if (inputRetriever.hasFinished()) {
						int count = 0;
						for (int i = 0; i < scan.size(); i++) {
							if (!readers.get(i).hasFinished())
								count++;
						}
						if (count == 0)
							finished = true;
					}
				}
				waitTime = waitTime < 500? waitTime * 2 : 500;
			}

			synchFinished.setFinished(true);
			closestop = System.currentTimeMillis();

			Exception ex = null;
			for (int i = 0; i < scan.size(); i++) {
				do {
					try {
						scan.get(i).join();
						ex = null;
					} catch (InterruptedException e) {
						ex = e;
					}
				} while (ex != null);
			}

			// eventHandler.sendPendingFinalEvents(this.count);

		} catch (Exception e) {
			log.error("Could not complete background merging for " + this.uid + ". Closing", e);
		} finally {
			recs.clear();
			for (int i = 0; i < readers.size(); i++) {
				readers.get(i).setFinished(true);
				try {
					if (readers.get(i).getReader().getStatus() != Status.Dispose)
						readers.get(i).getReader().close();
				} catch (Exception e) {
					log.warn("Could not close reader #" + i);
				}
			}
			
			try {
				if (writer != null)
					writer.close();
			} catch (Exception e2) {}

			synchronized (synchDispatcher) {
				synchDispatcher.notify();
			}
			synchronized (synchWriterInit) {
				synchWriterInit.notify();
			}
			log.info("MERGE OPERATOR " + this.uid + ":\nProduced first result in " + (firststop - start) + " milliseconds\n" + "Produced last result in "
					+ (closestop - start) + " milliseconds\n" + "Produced " + count + " records\n" + "Production rate was "
					+ (((float) this.count / (float) (closestop - start)) * 1000) + " records per second");
		}
	}

	/**
	 * @return the writer
	 */
	public URI getWriterLocator() {
		if (writer == null)
			return null;
		try {
			return writer.getLocator();
		} catch (GRS2WriterException e) {
			return null;
		}
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
	 * @param hasFinished
	 *            the hasFinished to set
	 */
	public synchronized void setFinished(boolean hasFinished) {
		this.finished = hasFinished;
	}
}
