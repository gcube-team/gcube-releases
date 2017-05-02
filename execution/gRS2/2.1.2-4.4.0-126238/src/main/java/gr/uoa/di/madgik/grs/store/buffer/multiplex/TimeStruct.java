package gr.uoa.di.madgik.grs.store.buffer.multiplex;

import java.util.concurrent.TimeUnit;

/**
 * Utility class holding information on both timeout as well as associated time unit information. It can be used to 
 * order a pair of timeout and time unit information
 * 
 * @author gpapanikos
 *
 */
public class TimeStruct implements Comparable<TimeStruct>
{
	/**
	 * The timeout value
	 */
	public long timeout;
	/**
	 * The time unit value
	 */
	public TimeUnit unit;

	/**
	 * Creates a new instance
	 * 
	 * @param timeout the timeout value
	 * @param unit the time unit value
	 */
	public  TimeStruct(long timeout, TimeUnit unit)
	{
		this.timeout=timeout;
		this.unit=unit;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The comparison is performed taking into account firstly the {@link TimeUnit} value and then if needed the timeout value
	 * </p>
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(TimeStruct o)
	{
		int res=this.unit.compareTo(o.unit);
		if(res==0)
		{
			if(this.timeout==o.timeout) return 0;
			else if(this.timeout<o.timeout) return -1;
			else return 1;
		}
		else if(res<0) return -1;
		else return 1;
	}
}
