package gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;

/**
 * Placeholder for input ResultSet
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class ReaderHolder {
	/** The Reader used */
	private DataSource reader = null;

	/** Whether the writer has finished authoring */
	private boolean finished = false;

	private boolean waitingForInit = true;

	private Object synchReader = null;

	/** Creates a new {@link ReaderHolder} */
	public ReaderHolder() {
		this.reader = null;
		this.finished = false;
		this.synchReader = new Object();
	}

	/**
	 * Retrieves the {@link DataSource}
	 * 
	 * @return The {@link DataSource}
	 */
	public DataSource getReader() {
		return reader;
	}

	/**
	 * Retrieves the {@link DataSource}'s synchronization object
	 * 
	 * @return The synchronization object
	 */
	public Object getSynchReader() {
		return synchReader;
	}

	/**
	 * Sets the {@link DataSource}
	 * 
	 * @param reader
	 *            The {@link DataSource}
	 */
	public void setReader(DataSource reader) {
		this.reader = reader;
	}

	/**
	 * Checks if the associated {@link DataSource} still has {@link DataElement}s to read
	 * 
	 * @return <code>true</code> if there are unread records, <code>false</code>
	 *         otherwise
	 */
	public boolean hasFinished() {
		return finished;
	}

	/**
	 * Sets whether or not the associated {@link DataSource} still has
	 * {@link DataElement}s to read from
	 * 
	 * @param ready
	 *            <code>true</code> if it is, <code>false</code> otherwise
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public void setWaitingForInit(boolean value) {
		this.waitingForInit = value;
	}

	public boolean getWaitingForInit() {
		return this.waitingForInit;
	}
}
