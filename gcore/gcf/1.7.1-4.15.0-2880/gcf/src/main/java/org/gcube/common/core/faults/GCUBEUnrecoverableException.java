package org.gcube.common.core.faults;


/**
 * Root of all exceptions with UNRECOVERABLE semantics.
 * 
 * @author University of Strathclyde (USG)
 *
 */
public class GCUBEUnrecoverableException extends GCUBEException {

	/**Serialization version.*/
	private static final long serialVersionUID = 12733171900719931L;
	

	/**
	 * Creates an exception with a no message and no cause.
	 */
	public GCUBEUnrecoverableException() {
		super();
	}
	
	/**
	 * Creates an exception with a given message and cause.
	 * @param msg the message.
	 * @param cause the cause.
	 */
	public GCUBEUnrecoverableException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
	/**
	 * Creates an exception with a given message.
	 * @param msg the message.
	 */
	public GCUBEUnrecoverableException(String msg) {
		super(msg);
	}
	
	/**Create an exception with a given cause.
	 * @param cause the cause.*/
	public GCUBEUnrecoverableException(Throwable cause) {super(cause);}
	
	
	/**{@inheritDoc}*/
	public GCUBEFault getFault() {return new GCUBEUnrecoverableFault(this.getMessage());}
}
