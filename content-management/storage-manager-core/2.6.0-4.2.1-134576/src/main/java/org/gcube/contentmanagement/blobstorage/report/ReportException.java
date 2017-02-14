package org.gcube.contentmanagement.blobstorage.report;


/**
 * Report Exception class
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class ReportException extends Exception {
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = -7852250665598838026L;
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
	        this.setExc(e);
	    }

		public Exception getExc() {
			return exc;
		}

		public void setExc(Exception exc) {
			this.exc = exc;
		}

}
