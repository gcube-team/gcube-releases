/**
 * 
 */
package org.gcube.vremanagement.executor.exception;

import javax.xml.ws.WebFault;

import org.gcube.vremanagement.executor.exception.beans.ExceptionBean;
import org.gcube.vremanagement.executor.json.SEMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
@WebFault
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = SEMapper.CLASS_PROPERTY)
public class ExecutorException extends Exception {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 150353533672078736L;

	private static final String DEFAULT_MESSAGE = "Executor Exception";
	
	protected ExceptionBean faultInfo;
	
	public ExecutorException() {
		super(DEFAULT_MESSAGE);
		this.faultInfo = new ExceptionBean(DEFAULT_MESSAGE);
	}
	
	public ExecutorException(Throwable cause) {
		this(DEFAULT_MESSAGE, cause);
	}
	
	public ExecutorException(String message) {
		super(message);
		this.faultInfo = new ExceptionBean(message);
	}
	
	public ExecutorException(ExceptionBean faultInfo){
		super(faultInfo.getMessage(), faultInfo.getCause());
		this.faultInfo = faultInfo;
	}
	
	public ExecutorException(String message, Throwable cause){
		super(message, cause);
		this.faultInfo = new ExceptionBean(message, cause);
	}
	
	public ExecutorException(String message, ExceptionBean faultInfo){
		super(message);
		this.faultInfo = faultInfo;
	}
	
	public ExecutorException(String message, ExceptionBean faultInfo, Throwable cause){
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	@JsonIgnore
	public ExceptionBean getFaultInfo(){
		return faultInfo;
	}
	
}
