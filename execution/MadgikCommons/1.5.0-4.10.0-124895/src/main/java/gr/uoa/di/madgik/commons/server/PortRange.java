package gr.uoa.di.madgik.commons.server;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class giving Start and End semantics of a Port range definition
 * 
 * @author gpapanikos
 */
public class PortRange
{
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(PortRange.class.getName());
	
	/** The Start. */
	private int Start = 0;
	
	/** The End. */
	private int End = 0;

	/**
	 * Creates a new Range
	 *
	 * @param Start The start of the range
	 * @param End The inclusive end of the range
	 */
	public PortRange(int Start, int End)
	{
		this.Start = Start;
		this.End = End;
		if (this.Start > this.End)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Cannot have a port range Start grater than its End");
			throw new IllegalArgumentException("Cannot have a port range Start grater than its End");
		}
	}

	/**
	 * The Start of the range
	 *
	 * @return The range start
	 */
	public int GetStart()
	{
		return this.Start;
	}

	/**
	 * The End of the range
	 *
	 * @return The inclusive range end
	 */
	public int GetEnd()
	{
		return this.End;
	}
}