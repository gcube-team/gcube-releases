package gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge;

import gr.uoa.di.madgik.grs.reader.ForwardReader;

import java.net.URI;
import java.util.Vector;

import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.GRS2DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper thread class that initializes an {@link DataSource}
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class ReaderInit extends Thread {
	/** The Logger this class uses */
	private Logger log = LoggerFactory.getLogger(ReaderInit.class.getName());

	/** The readers to use */
	private Vector<ReaderHolder> readers = null;

	/** The index of the reader */
	private int index = 0;
	
	/** The locator to use */
	private URI locator = null;
	
	/** A unique identifier for this operation */
	private String uid = null;

	/**
	 * Creates a new {@link ForwardReader}
	 * 
	 * @param readers
	 *            The {@link ReaderHolder} vector to update
	 * @param index
	 *            The index of the holder this thread should update
	 * @param locator
	 *            The locator to the ResultSet
	 * @param uid
	 *            A unique identifier for this operation
	 */
	public ReaderInit(Vector<ReaderHolder> readers, int index, URI locator, String uid) {
		this.readers = readers;
		this.index = index;
		this.locator = locator;
		this.uid = uid;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			this.setName("Reader Init #" + this.index);
			long initstart = System.currentTimeMillis();
			log.info(uid + ": Initializing reader #" + index + " with locator " + locator);
			
			Parameter[] sourcePars = new Parameter[]{new Parameter("hideRecs", "true")};
			if (locator == null || locator.toASCIIString().trim().length() == 0)
				throw new Exception("Got null locator");
			
			GRS2DataSource reader = new GRS2DataSource(locator.toASCIIString(), sourcePars);
			
			readers.get(index).setReader(reader);
			long initstop = System.currentTimeMillis();
			log.info("MERGE OPERATOR " + this.uid + " THREAD INIT TOOK " + (initstop - initstart));
		} catch (Exception e) {
			log.warn("Could not initialize reader #" + index + " of operation " + this.uid + ". setting null", e);
			readers.get(index).setReader(null);
		} finally {
			readers.get(index).setWaitingForInit(false);
			Object synchReader = readers.get(index).getSynchReader();
			synchronized (synchReader) {
				synchReader.notify();
			}
		}
	}
}
