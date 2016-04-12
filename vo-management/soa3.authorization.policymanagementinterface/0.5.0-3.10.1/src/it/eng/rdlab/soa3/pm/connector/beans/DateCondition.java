package it.eng.rdlab.soa3.pm.connector.beans;


public class DateCondition extends TimeRelatedCondition 
{

	
	public static String GREATER_EQUAL =  "urn:oasis:names:tc:xacml:1.0:function:date-greater-than-or-equal";
	public static String GREATER = "urn:oasis:names:tc:xacml:1.0:function:date-greater-than";
	public static String LESSER_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:date-less-than-or-equal";
	public static String LESSER = "urn:oasis:names:tc:xacml:1.0:function:date-less-than";
	
	public DateCondition (String condition) throws IllegalArgumentException
	{
		super (condition);

	}

	@Override
	protected boolean checkCondition(String condition) 
	{
	
		return (condition != null && (condition.equals(GREATER_EQUAL) || condition.equals(GREATER) || condition.equals(LESSER_EQUAL) || condition.equals(LESSER)));

	}
	
	


	
}
