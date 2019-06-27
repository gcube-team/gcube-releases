package org.gcube.common.core.faults;


/**
 * Root of all exceptions with RETRY-EQUIVALENT semantics.
 * 
 * @author University of Strathclyde (USG)
 *
 */
public class GCUBERetryEquivalentException extends GCUBEException {

	/** Serialization version.*/
	private static final long serialVersionUID = -6986966868023379490L;
	
	
	
	/**
	 * Creates an exception with a no message and no cause.
	 */
	public GCUBERetryEquivalentException() {super();}
	
	/**
	 * Creates an exception with a given message and cause.
	 * @param msg the message.
	 * @param cause the cause.
	 */
	public GCUBERetryEquivalentException(String msg, Throwable cause) {super(msg,cause);}
	
	/**Creates an exception with a given message.
	 * @param msg the message.*/
	public GCUBERetryEquivalentException(String msg) {super(msg);}
	
	/**Create an exception with a given cause.
	 * @param cause the cause.*/
	public GCUBERetryEquivalentException(Throwable cause) {super(cause);}
	
	/**{@inheritDoc}*/
	public GCUBEFault getFault() {return new GCUBERetryEquivalentFault(this.getMessage());}
}
