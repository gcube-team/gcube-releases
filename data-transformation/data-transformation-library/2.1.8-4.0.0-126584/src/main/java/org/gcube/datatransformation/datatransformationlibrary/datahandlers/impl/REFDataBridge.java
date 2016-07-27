package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.PropertiesManager;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;

/**
 * @author Dimitris Katris, NKUA
 * 
 *         {@link DataBridge} which keeps references to {@link DataElement}s
 *         when they are appended into it.
 */
public class REFDataBridge implements DataBridge {

	/**
	 * The contents of the <tt>REFDataBridge</tt>.
	 */
	private LinkedList<DataElement> objects = new LinkedList<DataElement>();

	/**
	 * Specifies if the <tt>REFDataBridge</tt> is closed.
	 */
	private boolean isClosed = false;

	/**
	 * Specifies if the <tt>REFDataBridge</tt> is flow controlled.
	 */
	private static boolean flowControled = PropertiesManager.getBooleanPropertyValue("refdatabridge.flowControled", "true");
	/**
	 * The max number of {@link DataElement}s that can be contained in the
	 * <tt>REFDataBridge</tt> in case it is flow controlled.
	 */
	private static int limit = PropertiesManager.getIntPropertyValue("refdatabridge.limit", "10");

	private static Logger log = LoggerFactory.getLogger(REFDataBridge.class);

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#append(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement)
	 * @param object
	 *            <tt>DataElement</tt> to be appended to this <tt>DataSink</tt>
	 */
	public synchronized void append(DataElement object) {
		if (!isClosed) {
			// ////////////////
			if (flowControled) {
				int minutes = 1;
				while (objects.size() >= limit) {
					try {
						log.debug("Size of the objects list has reached the limit (" + limit + "), blocking...");
						wait(60000);
						if (objects.size() >= limit) {
							minutes++;
							if (isClosed) {
								log.debug("RS buffer was closed");
								return;
							}
							log.debug("RS buffer full.... looping... " + object.getId());
							if (minutes > 30) {
								log.warn("Trying several times. Closing writer.");
								close();
								return;
							}
						}
					} catch (InterruptedException e) {
					}
				}
			}// /////////////////

			objects.add(object);
			if (objects.size() == 1)
				notifyAll();
		}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	public synchronized void close() {
		if (this.isClosed == false) {
			this.isClosed = true;
			notifyAll();
		}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
	 * @return true if the <tt>DataSource</tt> has more elements.
	 */
	public synchronized boolean hasNext() {
		/* !!!Attention works only with ONE Consumer */
		while (objects.size() < 1 && !isClosed) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		if (objects.size() == 0 && isClosed)
			return false;
		return true;
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
	 * @return The the next <tt>DataElement</tt> of the <tt>DataSource</tt>.
	 */
	public synchronized DataElement next() {
		if (!hasNext()) {
			return null;
		}
		// ////////////////
		DataElement elm = objects.poll();
		if (flowControled && objects.size() == limit - 1) {
			log.debug("Removed an object and the objects size is " + objects.size() + ", notifying...");
			notifyAll();
		}// /////////////////
		return elm;
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#getOutput()
	 * @return The output of the transformationUnit.
	 */
	public String getOutput() {
		return null;
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the <tt>DataHandler</tt> has been closed.
	 */
	public boolean isClosed() {
		return isClosed;
	}

	@Override
	public ContentType nextContentType() {
		if (!hasNext()) {
			return null;
		}
		// ////////////////
		DataElement elm = objects.poll();
		if (flowControled && objects.size() == limit - 1) {
			log.debug("Removed an object and the objects size is " + objects.size() + ", notifying...");
			notifyAll();
		}// /////////////////
		return elm.getContentType();
	}

}
