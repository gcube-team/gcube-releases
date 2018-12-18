/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

import javax.xml.ws.WebFault;

import org.gcube.vremanagement.executor.exception.beans.ExceptionBean;


/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
@WebFault
public class InvalidInputsException extends ExecutorException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 7578814354528161119L;

	private static final String DEFAULT_MESSAGE = "Inputs cannot be null. Use an Empty Map instead.";
	
	public InvalidInputsException(){
		super(DEFAULT_MESSAGE);
		this.faultInfo = new ExceptionBean(DEFAULT_MESSAGE);
	}
	
	public InvalidInputsException(String message) {
		super(message);
	}
	
	public InvalidInputsException(Throwable cause) {
		this(DEFAULT_MESSAGE, cause);
	}
	
	public InvalidInputsException(ExceptionBean faultInfo){
		super(faultInfo);
	}
	
	public InvalidInputsException(String message, Throwable cause){
		super(message, cause);
	}
	
	public InvalidInputsException(String message, ExceptionBean faultInfo){
		super(message, faultInfo);

	}
	
	public InvalidInputsException(String message, ExceptionBean faultInfo, Throwable cause){
		super(message, faultInfo, cause);
	}

	@Override
	public ExceptionBean getFaultInfo(){
		return faultInfo;
	}
	
}
