package gr.uoa.di.madgik.grs.reader;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.record.GRS2ExceptionWrapper;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.exception.GRS2UncheckedException;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A list iterator implementing class that can be used to iterate over the {@link Record}s accessible through a
 * {@link RandomReader} instance
 * 
 * @author gpapanikos
 *
 * @param <T> The type of {@link Record}s as defined in the respective {@link RandomReader}
 */
public class RandomReaderIterator<T extends Record> implements ListIterator<T>
{
	private RandomReader<T> reader=null;
	private boolean timeoutExpired = false;

	/**
	 * Creates a new instance that will iterate over the {@link Record}s that are accessible through the provided
	 * {@link RandomReader}. If some records have been consumed through the {@link RandomReader} provided, then they 
	 * will not be served again from the iterator unless it is rewind
	 * 
	 * @param reader the {@link RandomReader} to use to access the {@link Record}s
	 */
	protected RandomReaderIterator(RandomReader<T> reader)
	{
		this.reader=reader;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * If the status of the reader indicates that there may be more {@link Record}s available but have not yet reached the 
	 * current reader through the respective mirroring procedure, then the {@link ForwardReader#waitAvailable(long, java.util.concurrent.TimeUnit)}
	 * is used with the timeout values defined by {@link RandomReader#getIteratorTimeout()} and {@link RandomReader#getIteratorTimeUnit()}
	 * </p>
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext()
	{
		try
		{
			if(this.reader.canCallNext()) return true;
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
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * the {@link RandomReader#get(long, java.util.concurrent.TimeUnit)} method is used to retrieve the next available
	 * {@link Record}. The timeout values used are the ones defined by {@link RandomReader#getIteratorTimeout()} and 
	 * {@link RandomReader#getIteratorTimeUnit()}. in case there is some error during the {@link Record} retrieval or
	 * the timeout expired, null is returned 
	 * </p>
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
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * If the status of the reader indicates that there are previous {@link Record}s true is returned. Otherwise false 
	 * </p>
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasPrevious()
	{
		return this.reader.canCallPrevious();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The {@link RandomReader#seek(long)} method is used to move back the necessary number of records so that the next call to 
	 * {@link RandomReader#get()} will return the previous record. If some error occurs during the record retrieval, null is returned
	 * </p>
	 * 
	 * @see java.util.ListIterator#previous()
	 */
	public T previous()
	{
		try
		{
			this.reader.seek(-2);
			return this.reader.get();
		} catch (Exception e) 
		{
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Operation is not supported. An {@link IllegalStateException} is thrown upon invocation
	 * </p>
	 * 
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex()
	{
		throw new IllegalStateException("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Operation is not supported. An {@link IllegalStateException} is thrown upon invocation
	 * </p>
	 * 
	 * @see java.util.ListIterator#previousIndex()
	 */
	public int previousIndex()
	{
		throw new IllegalStateException("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Operation is not supported. An {@link IllegalStateException} is thrown upon invocation
	 * </p>
	 * 
	 * @see java.util.ListIterator#remove()
	 */
	public void remove()
	{
		throw new IllegalStateException("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Operation is not supported. An {@link IllegalStateException} is thrown upon invocation
	 * </p>
	 * 
	 * @see java.util.ListIterator#set(java.lang.Object)
	 */
	public void set(T o)
	{
		throw new IllegalStateException("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Operation is not supported. An {@link IllegalStateException} is thrown upon invocation
	 * </p>
	 * 
	 * @see java.util.ListIterator#add(java.lang.Object)
	 */
	public void add(T o)
	{
		throw new IllegalStateException("Operation not supported");
	}
}
