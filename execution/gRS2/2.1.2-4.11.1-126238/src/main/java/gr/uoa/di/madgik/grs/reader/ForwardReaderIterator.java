package gr.uoa.di.madgik.grs.reader;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.record.GRS2ExceptionWrapper;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.exception.GRS2UncheckedException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator implementing class that can be used to iterate over the {@link Record}s accessible through a
 * {@link ForwardReader} instance
 * 
 * @author gpapanikos
 *
 * @param <T> The type of {@link Record}s as defined in the respective {@link ForwardReader}
 */
public class ForwardReaderIterator<T extends Record> implements Iterator<T>
{
	private ForwardReader<T> reader=null;
	private boolean timeoutExpired=false;

	/**
	 * Creates a new instance that will iterate over the {@link Record}s that are accessible through the provided
	 * {@link ForwardReader}. If some records have been consumed through the {@link ForwardReader} provided, then they 
	 * will not be served again from the iterator
	 * 
	 * @param reader the {@link ForwardReader} to use to access the {@link Record}s
	 */
	protected ForwardReaderIterator(ForwardReader<T> reader)
	{
		this.reader=reader;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * If the status of the reader indicates that there may be more {@link Record}s available but have not yet reached the 
	 * current reader through the respective mirroring procedure, then the {@link ForwardReader#waitAvailable(long, java.util.concurrent.TimeUnit)}
	 * is used with the timeout values defined by {@link ForwardReader#getIteratorTimeout()} and {@link ForwardReader#getIteratorTimeUnit()}
	 * </p>
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext()
	{
		try
		{
			if(this.reader.getStatus()==Status.Dispose || (this.reader.getStatus()==Status.Close && this.reader.availableRecords()==0)) return false;
			if(this.reader.availableRecords()==0) 
			{
				if(this.timeoutExpired) return false;
				this.timeoutExpired = !this.reader.waitAvailable(this.reader.getIteratorTimeout(), this.reader.getIteratorTimeUnit());
				if(this.timeoutExpired) reader.close();
			}
			return !this.timeoutExpired;
		} catch (GRS2ReaderException e)
		{
			//throw new GRS2UncheckedException("error reading the records from iterator", e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * the {@link ForwardReader#get(long, java.util.concurrent.TimeUnit)} method is used to retrieve the next available
	 * {@link Record}. The timeout values used are the ones defined by {@link ForwardReader#getIteratorTimeout()} and 
	 * {@link ForwardReader#getIteratorTimeUnit()}. in case there is some error during the {@link Record} retrieval or
	 * the timeout expired, null is returned 
	 * </p>
	 * 
	 * @throws GRS2UncheckedException if exception is written in the RS by the writer when the reader reads it it throws is up as
	 * an unchecked GRS2UncheckedException exception. The reason that this exception is unchecked is to follow the java.util.Iterator
	 * interface
	 * 
	 * @see java.util.Iterator#next()
	 */
	public T next()
	{
		try
		{
			if(!hasNext()) throw new NoSuchElementException();
			T rec = this.reader.get(this.reader.getIteratorTimeout(), this.reader.getIteratorTimeUnit());
			
			if (rec instanceof GRS2ExceptionWrapper) {
				GRS2UncheckedException ex = new GRS2UncheckedException("writer error", ((GRS2ExceptionWrapper) rec).getEx());
				throw ex;
			}
			
			return rec;
		} catch (GRS2ReaderException e)
		{
			throw new GRS2UncheckedException("error reading the records from iterator", e);
		}
		
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Operation is not supported. An {@link IllegalStateException} is thrown upon invocation
	 * </p>
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove()
	{
		throw new IllegalStateException("Operation not supported");
	}

}
