package gr.uoa.di.madgik.grs.utils;

/**
 * Utility class which progressively returns waiting values whose sum amounts to a supplied timeout.
 * Used by readers and writers in order to avoid waiting at once for the full timeout interval when
 * their wait condition is triggered.
 * 
 * @author gerasimos.farantatos
 *
 */
public class ProgressiveTimeoutGenerator 
{
	
	private final float[] FractionsDef = new float[]{35.0f, 12.0f, 6.0f, 2.5f, 1.0f};
	private long timeout;
	private long elapsed;
	private float[] fractions = FractionsDef;
	private boolean init = false;
	private int currentIndex;
	
	/**
	 * Creates a new instance
	 * 
	 * @param timeout The total time to wait
	 */
	public ProgressiveTimeoutGenerator(long timeout) 
	{
		this.timeout = timeout;
		this.elapsed = 0;
		this.currentIndex = 0;
		this.init = false;
	}
	
	
	/**
	 * Sets time fractions for each step. The total steps equal the length of the supplied array.
	 * Each wait period is computed by dividing the time left after the previous step by the corresponding fraction.
	 * Each fraction should be be greater than or equal to 1.0. If the last fraction is not equal to 1.0, its value is ignored and assumed equal to 1.0.
	 * 
	 * @param fractions The fraction of the time to wait at each step. 
	 * @throws Exception If the {@link ProgressiveTimeoutGenerator#next()} or {@link ProgressiveTimeoutGenerator#hasNext()} method has already been called
	 */
	public void setFractions(float[] fractions) throws Exception 
	{
		if(init == true)
			throw new Exception("Timeout fractions cannot be set after initialization");
		for(float fraction : fractions) {
			if(fraction < 1.0)
				throw new Exception("Timeout fractions cannot be less than 1");
		}
		this.fractions = fractions;
	}
	
	/**
	 * Returns the next timeout value based on the time fractions set.
	 * The invocation for the last step returns the total time remaining to reach the total timeout value, regardless of the value of the corresponding fraction. 
	 * 
	 * @return The timeout value for the next waiting step. 0, if the steps
	 */
	public long next()
	{
		if(!hasNext())
			return 0;
		this.init = true;
		long remaining = timeout - elapsed;
		long value = remaining;
		if(currentIndex != fractions.length -1)
			value = (long)Math.ceil(remaining/fractions[currentIndex]);
		currentIndex++;
		elapsed += value;
		return value;
	}
	
	/**
	 * Determines if there are more waiting steps for which a timeout value can be fetched
	 * 
	 * @return true if a timeout value can be fetched through {@link ProgressiveTimeoutGenerator#next()}, false otherwise
	 */
	public boolean hasNext()
	{
		this.init = true;
		return currentIndex != fractions.length;
	}

}
