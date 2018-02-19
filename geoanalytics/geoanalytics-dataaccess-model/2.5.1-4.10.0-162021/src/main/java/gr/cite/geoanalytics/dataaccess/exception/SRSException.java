package gr.cite.geoanalytics.dataaccess.exception;

import java.util.Arrays;

public class SRSException extends Exception
{

	private static final long serialVersionUID = 1874432585944997079L;
	
	public SRSException(String message, String offendingSRS, Throwable cause)
	{
		super(message + " The SRS of the geometry (" + offendingSRS + ") is not of the supported types", cause);
	}
	
	public SRSException(String message, String offendingSRS, String[] validSRSs, Throwable cause)
	{
		super(message + " The SRS of the geometry (" + offendingSRS + ") is not of the supported types: " + Arrays.toString(validSRSs), cause);
	}
	
}
