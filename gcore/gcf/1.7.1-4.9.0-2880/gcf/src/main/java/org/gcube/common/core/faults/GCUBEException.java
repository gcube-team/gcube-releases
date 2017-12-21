package org.gcube.common.core.faults;

import java.rmi.RemoteException;

/**
 * Root of all exceptions with GCUBE semantics.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public abstract class GCUBEException extends RemoteException {
	
		/** Serial Version ID. */
	private static final long serialVersionUID = 1L;

	/**Creates an exception with a no message and no cause.*/
	public GCUBEException() {super();}
	
	/**Creates an exception with a given message and cause.
	 * @param msg the message.
	 * @param cause the cause.*/
	public GCUBEException(String msg, Throwable cause) {super(msg,cause);}
	
	/**Creates an exception with a given message.
	 * @param msg the message. */
	public GCUBEException(String msg) {super(msg);}
	
	/**Create an exception with a given cause.
	 * @param cause the cause.*/
	public GCUBEException(Throwable cause) {super("",cause);}
	
	/**Returns a stub object of the remote fault class which corresponds to the exception. 
	 * @return the stub object.*/
	public abstract GCUBEFault getFault();
	
	/** Converts the exception into a corresponding remote fault.
	 * @param msg an optional message specific to the new fault.
	 * @return the fault.*/
	public GCUBEFault toFault(String ...msg) {
		
		return FaultUtils.newFault(this.getFault(), this);
		
	}
}
