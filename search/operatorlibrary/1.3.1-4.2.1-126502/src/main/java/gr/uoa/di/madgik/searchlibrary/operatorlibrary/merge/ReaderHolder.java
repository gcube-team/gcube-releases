package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;

/**
 * Placeholder for input ResultSet
 * 
 * @author UoA
 */
public class ReaderHolder {
	/**
	 * The Reader used
	 */
	private IRecordReader<Record> reader=null;

	/**
	 * Whether the writer has finished authoring
	 */
	private boolean finished = false;
	
	private boolean waitingForInit = true;
	
	private Object synchReader = null;
	
	/**
	 * Creates a new {@link ReaderHolder}
	 */
	public ReaderHolder(){
		this.reader = null;
		this.finished = false;
		this.synchReader = new Object();
	}

	/**
	 * Retrieves the {@link ForwardReader}
	 * 
	 * @return The {@link ForwardReader}
	 */
	public IRecordReader<Record> getReader() {
		return reader;
	}

	/**
	 * Retrieves the {@link ForwardReader}'s synchronization object
	 * 
	 * @return The synchronization object
	 */
	public Object getSynchReader() {
		return synchReader;
	}
	
	/**
	 * Sets the {@link ForwardReader}
	 * 
	 * @param reader The {@link ForwardReader}
	 */
	public void setReader(IRecordReader<Record> reader) {
		this.reader = reader;
	}

	/**
	 * Checks if the associated {@link ForwardReader} still has records to read
	 * 
	 * @return <code>true</code> if there are unread records, <code>false</code> otherwise
	 */
	public boolean hasFinished() {
		return finished;
	}
	
	/**
	 * Sets whether or not the associated {@link ForwardReader} still has records to read from
	 * 
	 * @param ready <code>true</code> if it is, <code>false</code> otherwise
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
