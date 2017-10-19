package org.gcube.contentmanager.storageserver.accounting;

public class ReportException extends Exception {
	
	  private Exception exc = null;

	    /** The no-arg constructor */
	    public ReportException() {
	    }

	    /**
	     * Construct a ReportException with an error message
	     * @param message the error message
	     */
	    public ReportException(String message) {
	        super(message);
	    }

	    public ReportException (Exception e)
	    {
	        this.exc = e;
	    }

}
