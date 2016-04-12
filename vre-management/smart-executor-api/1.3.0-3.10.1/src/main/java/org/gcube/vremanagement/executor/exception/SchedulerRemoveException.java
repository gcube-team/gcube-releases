/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

import java.util.UUID;

import javax.xml.ws.WebFault;

import org.gcube.vremanagement.executor.exception.beans.ExceptionBean;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
@WebFault
public class SchedulerRemoveException extends ExecutorException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7609491801703267843L;

	private static final String DEFAULT_MESSAGE = "Unable to delete SmartExecutor Scheduled Task";
	
	public SchedulerRemoveException(){
		super(DEFAULT_MESSAGE);
		this.faultInfo = new ExceptionBean(DEFAULT_MESSAGE);
	}
	
	public SchedulerRemoveException(UUID taskUUID) {
		super(DEFAULT_MESSAGE + " " + taskUUID.toString());
	}
	
	public SchedulerRemoveException(String message) {
		super(message);
	}
	
	public SchedulerRemoveException(Throwable cause) {
		this(DEFAULT_MESSAGE, cause);
	}
	
	public SchedulerRemoveException(ExceptionBean faultInfo){
		super(faultInfo);
	}
	
	public SchedulerRemoveException(UUID taskUUID, Throwable cause) {
		this(DEFAULT_MESSAGE + " " + taskUUID.toString(), cause);
	}
	
	public SchedulerRemoveException(String message, Throwable cause){
		super(message, cause);
	}
	
	public SchedulerRemoveException(UUID taskUUID, ExceptionBean faultInfo) {
		this(DEFAULT_MESSAGE + " " + taskUUID.toString(), faultInfo);
	}
	
	public SchedulerRemoveException(String message, ExceptionBean faultInfo){
		super(message, faultInfo);

	}
	
	public SchedulerRemoveException(UUID taskUUID, ExceptionBean faultInfo, Throwable cause) {
		this(DEFAULT_MESSAGE + " " + taskUUID.toString(), faultInfo, cause);
	}
	
	public SchedulerRemoveException(String message, ExceptionBean faultInfo, Throwable cause){
		super(message, faultInfo, cause);
	}

	@Override
	public ExceptionBean getFaultInfo(){
		return faultInfo;
	}
	
}
