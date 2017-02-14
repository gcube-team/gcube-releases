package gr.uoa.di.madgik.grs.reader.decorators;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderInvalidArgumentException;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;

/**
 * Class delegating all operations to the underlying {@link IRecordReader}. Used as a base for all decorated
 * {@link IRecordReader}s.
 * 
 * @author gerasimos.farantatos
 *
 * @param <T> The type of {@link Record} specialization that is to be returned by the <code>get</code> operations
 */
public abstract class RecordReaderDelegate<T extends Record> implements IRecordReader<T> {
	
	protected IRecordReader<T> reader = null;
	
	/**
	 * Creates a new reader delegator which forwards all operations to the underlying {@link IRecordReader}
	 * 
	 * @param reader the underlying {@link IRecordReader} to which all operations will be delegated
	 */
	public RecordReaderDelegate(IRecordReader<T> reader) {
		this.reader = reader;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#getRecordDefinitions()}
	 */
	public RecordDefinition[] getRecordDefinitions() throws GRS2ReaderException {
		return reader.getRecordDefinitions();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#getInactivityTimeout()}
	 */
	public long getInactivityTimeout() throws GRS2ReaderException {
		return reader.getInactivityTimeout();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#getInactivityTimeUnit()}
	 */
	public TimeUnit getInactivityTimeUnit() throws GRS2ReaderException {
		return reader.getInactivityTimeUnit();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#setIteratorTimeout(long)}
	 */
	public void setIteratorTimeout(long iteratorTimeout) {
		reader.setIteratorTimeout(iteratorTimeout);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#getIteratorTimeout()}
	 */
	public long getIteratorTimeout() throws GRS2ReaderException {
		return reader.getIteratorTimeout();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#setIteratorTimeUnit(TimeUnit)}
	 */
	public void setIteratorTimeUnit(TimeUnit iteratorTimeUnit) {
		reader.setIteratorTimeUnit(iteratorTimeUnit);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#getIteratorTimeUnit()}
	 */
	public TimeUnit getIteratorTimeUnit() {
		return reader.getIteratorTimeUnit();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#getCapacity()}
	 */
	public int getCapacity() throws GRS2ReaderException {
		return reader.getCapacity();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#getConcurrentPartialCapacity()}
	 */
	public int getConcurrentPartialCapacity() throws GRS2ReaderException {
		return reader.getConcurrentPartialCapacity();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#getStatus()}
	 */
	public synchronized Status getStatus() {
		return reader.getStatus();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#close()}
	 */
	public synchronized void close() throws GRS2ReaderException {
		reader.close();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#totalRecords()}
	 */
	public synchronized long totalRecords() throws GRS2ReaderException {
		return reader.totalRecords();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#availableRecords()
	 */
	public synchronized int availableRecords() throws GRS2ReaderException {
		return reader.availableRecords();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#currentRecord()}
	 */
	public long currentRecord() throws GRS2ReaderException {
		return reader.currentRecord();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#get()}
	 */
	public synchronized T get() throws GRS2ReaderException {
		return reader.get();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#get(long, TimeUnit))}
	 */
	public synchronized T get(long timeout, TimeUnit unit) throws GRS2ReaderException {
		return reader.get(timeout, unit);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#seek(long)}
	 */
	public long seek(long len) throws GRS2ReaderException {
		return reader.seek(len);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#iterator()}
	 */
	public Iterator<T> iterator() {
		return reader.iterator();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#waitAvailable(long, TimeUnit)}
	 */
	public synchronized boolean waitAvailable(long timeout, TimeUnit unit) throws GRS2ReaderException {
		return reader.waitAvailable(timeout, unit);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#emit(BufferEvent)}
	 */
	public synchronized void emit(BufferEvent event) throws GRS2ReaderException, GRS2ReaderInvalidArgumentException {
		reader.emit(event);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link IRecordReader#receive()}
	 */
	public synchronized BufferEvent receive() throws GRS2ReaderException {
		return reader.receive();
	}
	
	/**
	 * 
	 * Changes the window size. It is used only if the underlying reader is instance of {@link RandomReader}, 
	 * otherwise a {@link GRS2ReaderInvalidArgumentException} will be thrown.
	 * 
	 * @param windowSize The new window size
	 */
	public synchronized void changeWindowSize(int windowSize) throws GRS2ReaderInvalidArgumentException{
		if (this.reader instanceof RandomReader) {
			((RandomReader<T>)this.reader).setWindowSize(windowSize);
		} else {
			throw new GRS2ReaderInvalidArgumentException("Reader not instance of RandomReader");
		}
	}
}
