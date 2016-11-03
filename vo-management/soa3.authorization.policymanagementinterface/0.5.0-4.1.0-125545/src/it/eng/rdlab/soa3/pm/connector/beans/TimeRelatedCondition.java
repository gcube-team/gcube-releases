package it.eng.rdlab.soa3.pm.connector.beans;

public abstract class TimeRelatedCondition 
{
	protected String condition;
	
	public TimeRelatedCondition (String condition) throws IllegalArgumentException
	{
		if (!checkCondition(condition))
		{
			throw new IllegalArgumentException("Invalid time condition "+condition);
		}
		else this.condition = condition;
	}
	
	protected abstract boolean checkCondition (String condition);
	

	public String getCondition()
	{
		return condition;
	}
	@Override
	public String toString()
	{
		return condition;
	}

}
