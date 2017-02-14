package gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.net.URI;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for reading Readers from a result set
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class LocatorReader extends Thread {

	/** The Logger this class uses */
	private Logger log = LoggerFactory.getLogger(LocatorReader.class.getName());

	/** Locator of the result set containing the readers */
	private URI locator;

	private String uid = null;

	/** Forward reader used for reading from result set locator */
	ForwardReader<GenericRecord> reader = null;

	/** The index of the reader */
	Vector<ReaderHolder> readers;

	/** The index of the initializers */
	private Vector<ReaderInit> initT;
	
	private Boolean hasFinished = false;


	private Object synchDispatcher = null;
	/**
	 * Creates a new {@link LocatorReader}
	 * 
	 * @param locator
	 * @param readers
	 * @param initT
	 * @param uid
	 * @throws GRS2ReaderException
	 */
	public LocatorReader(URI locator, Vector<ReaderHolder> readers, Vector<ReaderInit> initT, String uid, Object synchDispatcher) throws GRS2ReaderException {
		this.uid = uid;
		this.locator = locator;
		this.readers = readers;
		this.initT = initT;
		reader = new ForwardReader<GenericRecord>(locator);
		this.synchDispatcher = synchDispatcher;
	}

	public synchronized  boolean hasFinished() {
			return hasFinished.booleanValue();
	}
	
	@Override
	public void run() {
		if (locator == null || readers == null || initT == null || reader == null) {
			log.error("Not initialized");
			throw new NullPointerException("not initialized");
		}

		int cntLocators = 0;
		try {
			while (!(reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))) {
				GenericRecord rec = reader.get(60, TimeUnit.SECONDS);
				// In case a timeout occurs while optimistically waiting for
				// more records form an originally open writer
				if (rec == null) {
					if (!(reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))){
						log.debug("No Data Element received after 60 secs");
						continue;
					}else {
						log.info("Input Locator closed. status: " + reader.getStatus() + " with " + reader.availableRecords() + " available records");
						break;
					}
				}
				// Retrieve the required field of the type available in the gRS
				// definitions

				String loc = ((StringField) rec.getField(0)).getPayload();
				if (loc == null || loc.trim().isEmpty()) {
					log.warn("Received empty locator");
					continue;
				}
				log.info("Got new locator: " + loc + " that will be added for merge");

				readers.add(new ReaderHolder());
				initT.add(new ReaderInit(readers, cntLocators, URI.create(loc), uid));
				initT.get(cntLocators).start();
				
				if(cntLocators == 0) {
					synchronized (initT) {
						initT.notify();
					}
				}
				cntLocators++;
				
				synchronized (synchDispatcher) {
					synchDispatcher.notify();
				}
			}
			// Close the reader to release and dispose any resources in both
			// reader and writer sides
			synchronized (this) {
				hasFinished = true;
			}
			reader.close();
		} catch (Exception ex) {
			log.error("Locator reader error",ex);
		}
	}
}
