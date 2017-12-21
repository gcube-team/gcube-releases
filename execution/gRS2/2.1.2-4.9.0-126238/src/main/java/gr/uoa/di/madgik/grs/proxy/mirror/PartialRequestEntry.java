package gr.uoa.di.madgik.grs.proxy.mirror;

import java.io.Serializable;

import gr.uoa.di.madgik.grs.buffer.IBuffer.TransportOverride;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;

/**
 * This class holds information on a request made by a reader for more data to be provided over a partially
 * transfered record {@link Field}. These records are internally managed by the {@link PartialRequestManager}
 * and the respective managing {@link IMirror} instance
 * 
 * @author gpapanikos
 *
 */
public class PartialRequestEntry implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private long recordIndex;
	private int fieldIndex;
	transient private Object notify;
	private TransportOverride override;

	/**
	 * Creates a new instance for the request based on its details
	 * 
	 * @param recordIndex The {@link Record} index
	 * @param fieldIndex The {@link Field} index
	 * @param override The {@link TransportOverride} directive
	 * @param notify The synchronization object to use to notify the requester
	 */
	public PartialRequestEntry(long recordIndex, int fieldIndex, TransportOverride override, Object notify)
	{
		this.recordIndex=recordIndex;
		this.fieldIndex=fieldIndex;
		this.notify=notify;
		this.override=override;
	}

	/**
	 * Retrieved the {@link Record} index
	 * 
	 * @return the {@link Record} index
	 */
	public long getRecordIndex()
	{
		return recordIndex;
	}

	/**
	 * Sets the {@link Record} index
	 * 
	 * @param recordIndex the {@link Record} index
	 */
	public void setRecordIndex(long recordIndex)
	{
		this.recordIndex = recordIndex;
	}

	/**
	 * Retrieved the {@link Field} index
	 * 
	 * @return the {@link Field} index
	 */
	public int getFieldIndex()
	{
		return fieldIndex;
	}

	/**
	 * Sets the {@link Field} index
	 * 
	 * @param fieldIndex the {@link Field} index
	 */
	public void setFieldIndex(int fieldIndex)
	{
		this.fieldIndex = fieldIndex;
	}

	/**
	 * Retrieves the synchronization object
	 * 
	 * @return the synchronization object
	 */
	public Object getNotify()
	{
		return notify;
	}

	/**
	 * Sets the synchronization object
	 * 
	 * @param notify the synchronization object
	 */
	public void setNotify(Object notify)
	{
		this.notify = notify;
	}

	/**
	 * Retrieves the {@link TransportOverride} directive
	 * 
	 * @return the {@link TransportOverride} directive
	 */
	public TransportOverride getOverride()
	{
		return override;
	}

	/**
	 * Sets the {@link TransportOverride} directive
	 * 
	 * @param override the {@link TransportOverride} directive
	 */
	public void setOverride(TransportOverride override)
	{
		this.override = override;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * the equality operation is based in the {@link Record} index and the {@link Field} index 
	 * </p>
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof PartialRequestEntry)) return false;
		if(this.recordIndex!=((PartialRequestEntry)obj).recordIndex) return false;
		if(this.fieldIndex!=((PartialRequestEntry)obj).fieldIndex) return false;
		return true;
	}
	
	/**
	 * Creates a new instance of the {@link PartialRequestEntry} with the same values as the 
	 * ones available in this instance
	 * 
	 * @return the new instance
	 */
	public PartialRequestEntry copy()
	{
		return new PartialRequestEntry(this.recordIndex, this.fieldIndex, this.override, this.notify);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Since the two key values used for equality are the {@link Record} and {@link Field} index, the 
	 * hash code created is based in the Cantor function to create a unique hash value
	 * </p>
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		//http://stackoverflow.com/questions/919612/mapping-two-integers-to-one-in-a-unique-and-deterministic-way/919661#919661
		
		//even though record index may be long, the modulo applied should suffice as the possible
		//input can only include record indexes that have a difference of Integer.Max_Value. The 
		//toPositive invocation is also a not needed precaution as the indexes are always positive
		return cantorPairing(this.toPositive(toInt(this.recordIndex)), this.toPositive(this.fieldIndex));
	}
	
	private int toInt(long n)
	{
		if(n<Integer.MIN_VALUE) return (int)(n%Integer.MIN_VALUE);
		else if (n>Integer.MAX_VALUE) return (int)(n%Integer.MAX_VALUE);
		else return (int)n;
	}
	
	private int cantorPairing(int k1, int k2)
	{
		return ((k1 + k2)*(k1 + k2 + 1) + k2)/2;
	}
	
	private int toPositive(int n)
	{
		if(n>=0) return n * 2;
		else return (-n * 2) - 1;
	}
}
