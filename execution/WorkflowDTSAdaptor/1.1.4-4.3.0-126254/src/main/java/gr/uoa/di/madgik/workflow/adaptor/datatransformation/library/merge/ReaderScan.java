package gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge;

import gr.uoa.di.madgik.grs.reader.ForwardReader;

import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DTSExceptionWrapper;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper thread class used to iterate over a ResultSet to retrieve the local
 * payload parts and notify the merging thread when they are available
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class ReaderScan extends Thread {
	/** The Logger the class uses */
	private Logger log = LoggerFactory.getLogger(ReaderScan.class.getName());

	/** The reader to use */
	private Vector<ReaderHolder> readers = null;

	/** The buffer to place records in */
	private BlockingQueue<DataElement> buf = null;

	/** A queue to place events in */
	private Queue<EventEntry> events = null;

	/** The index of the user */
	private int index = 0;

	/** A unique identifier for this operation */
	private String uid = null;

	/**
	 * Creates a new {@link ReaderScan} working on some input
	 * {@link ForwardReader}
	 * 
	 * @param readers
	 *            The {@link ReaderHolder} vector with the available input
	 *            sources
	 * @param index
	 *            The index of the {@link ReaderHolder} this thread should
	 *            operate on
	 * @param buf
	 *            The buffer to place the records read along with the id of the
	 *            reader, which is equal to {@link #index}
	 * @param events
	 *            A queue to place the events read along with the id of the
	 *            reader, which is equal to {@link #index}
	 * @param timeout
	 *            The timeout to use
	 * @param timeUnit
	 *            The timeout unit to use
	 * @param uid
	 *            A unique identifier for this operation
	 */
	public ReaderScan(Vector<ReaderHolder> readers, int index, BlockingQueue<DataElement> buf, Queue<EventEntry> events, String uid) {
		this.readers = readers;
		this.index = index;
		this.buf = buf;
		this.events = events;
		this.uid = uid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		int dc = 0;
		DataSource reader = null;
		try {
			this.setName("Reader Scan #" + this.index);
			// synchronized(readers){
			// readers.get(index).setReady(true);
			// }
			Object synchReader = readers.get(index).getSynchReader();
			synchronized (synchReader) {
				while ((readers.get(index).getWaitingForInit()) == true)
					synchReader.wait();
				reader = readers.get(index).getReader();
			}

			if (reader == null) {
				synchronized (readers) {
					readers.get(index).setFinished(true);
				}
				return;
			}
			while (true) {

				synchronized (readers) {
					if (readers.get(index).hasFinished()) {
						log.info("Reader " + index + " of " + this.uid + " stopping after being notified");
						break;
					}
				}
				if (!reader.hasNext())
					break;

				DataElement de = reader.next();
//				BufferEvent ev = reader.receive();
//				if (ev != null)
//					events.add(new EventEntry(ev, index));

				if (de == null) {
					if (reader.hasNext()) {
						log.warn("Reader " + index + " of " + this.uid + " got null object after reading " + dc + " Records. Continuing to next.");
						continue;
					} else {
						log.warn("Reader " + index + " of " + this.uid + " has timed out after reading " + dc + " Records. Stop reading any more.");
						break;
					}
				}

				dc++;
				// rec.makeAvailable();
				// rec.unbind();
//				rec.hide();
				buf.put(de);
			}
			// readers.get(index).getReader().close();

		} catch (Exception e) {
			log.error("Could not scan entire reader. exiting", e);
			try {
				buf.put(new DTSExceptionWrapper(e));
			} catch (InterruptedException e1) {
				log.error("Could not sent exception", e1);
			}
		} finally {
			if (reader != null)
				reader.close();
			log.info("Closing reader " + index + " of " + this.uid + " after producing " + dc + " records");
			synchronized (readers) {
				readers.get(index).setFinished(true);
			}
		}
	}
}