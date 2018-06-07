/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

import java.util.UUID;

import javax.xml.ws.WebFault;

import org.gcube.vremanagement.executor.exception.beans.ExceptionBean;


/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
@WebFault
public class UnableToInterruptTaskException extends ExecutorException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7609491801703267843L;

	private static final String DEFAULT_MESSAGE = "Unable to interrupt SmartExecutor Task";
	
	public UnableToInterruptTaskException(){
		super(DEFAULT_MESSAGE);
		this.faultInfo = new ExceptionBean(DEFAULT_MESSAGE);
	}
	
	public UnableToInterruptTaskException(UUID taskUUID) {
		super(DEFAULT_MESSAGE + " " + taskUUID.toString());
	}
	
	public UnableToInterruptTaskException(String message) {
		super(message);
	}
	
	public UnableToInterruptTaskException(Throwable cause) {
		this(DEFAULT_MESSAGE, cause);
	}
	
	public UnableToInterruptTaskException(ExceptionBean faultInfo){
		super(faultInfo);
	}
	
	public UnableToInterruptTaskException(UUID taskUUID, Throwable cause) {
		this(DEFAULT_MESSAGE + " " + taskUUID.toString(), cause);
	}
	
	public UnableToInterruptTaskException(String message, Throwable cause){
		super(message, cause);
	}
	
	public UnableToInterruptTaskException(UUID taskUUID, ExceptionBean faultInfo) {
		this(DEFAULT_MESSAGE + " " + taskUUID.toString(), faultInfo);
	}
	
	public UnableToInterruptTaskException(String message, ExceptionBean faultInfo){
		super(message, faultInfo);

	}
	
	public UnableToInterruptTaskException(UUID taskUUID, ExceptionBean faultInfo, Throwable cause) {
		this(DEFAULT_MESSAGE + " " + taskUUID.toString(), faultInfo, cause);
	}
	
	public UnableToInterruptTaskException(String message, ExceptionBean faultInfo, Throwable cause){
		super(message, faultInfo, cause);
	}

	@Override
	public ExceptionBean getFaultInfo(){
		return faultInfo;
	}
	
}
