/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

import javax.xml.ws.WebFault;

import org.gcube.vremanagement.executor.exception.beans.ExceptionBean;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
@WebFault
public class InputsNullException extends ExecutorException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 7578814354528161119L;

	private static final String DEFAULT_MESSAGE = "Inputs cannot be null. Use an Empty Map instead.";
	
	public InputsNullException(){
		super(DEFAULT_MESSAGE);
		this.faultInfo = new ExceptionBean(DEFAULT_MESSAGE);
	}
	
	public InputsNullException(String message) {
		super(message);
	}
	
	public InputsNullException(Throwable cause) {
		this(DEFAULT_MESSAGE, cause);
	}
	
	public InputsNullException(ExceptionBean faultInfo){
		super(faultInfo);
	}
	
	public InputsNullException(String message, Throwable cause){
		super(message, cause);
	}
	
	public InputsNullException(String message, ExceptionBean faultInfo){
		super(message, faultInfo);

	}
	
	public InputsNullException(String message, ExceptionBean faultInfo, Throwable cause){
		super(message, faultInfo, cause);
	}

	@Override
	public ExceptionBean getFaultInfo(){
		return faultInfo;
	}
	
}
