package org.gcube.vremanagement.softwaregateway.impl.exceptions;

public class BadCoordinatesException extends  ServiceNotAvaiableFault {
	
	private static final long serialVersionUID = 1L;

	public BadCoordinatesException()
	  {
	    super("Bad Coordinates");
	  }
	 
	 public BadCoordinatesException(String msg)
	  {
	    super("Bad Coordinates "+msg);
	  }

}
