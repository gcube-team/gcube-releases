package org.gcube.common.core.faults;


/**
 * Root of all exceptions with RETRY-SAME semantics.
 * 
 * @author University of Strathclyde (USG)
 *
 */
public class GCUBERetrySameException extends GCUBEException {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 5430841892338912977L;
	

	/**
	 * Creates an exception with a no message and no cause.
	 */
	public GCUBERetrySameException() {
		super();
	}
	
	/**
	 * Creates an exception with a given message and cause.
	 * @param msg the message.
	 * @param cause the cause.
	 */
	public GCUBERetrySameException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
	/**
	 * Creates an exception with a given message.
	 * @param msg the message.
	 */
	public GCUBERetrySameException(String msg) {
		super(msg);
	}
	
	/**
	 * Create an exception with a given cause.
	 * @param cause the cause.
	 */
	public GCUBERetrySameException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Returns the type of the remote fault which corresponds to the exception.
	 * @return the type.
	 */
	public final String getFaultType() {
		return GCUBERetrySameFault.FAULT_TYPE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public GCUBEFault getFault() {
		return new GCUBERetrySameFault(this.getMessage());
	}
	
}
