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
public class PluginNotFoundException extends ExecutorException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 6591994140867585229L;

	private static final String DEFAULT_MESSAGE = "The requested plugin does not exist on container";
	
	public PluginNotFoundException(){
		super(DEFAULT_MESSAGE);
		this.faultInfo = new ExceptionBean(DEFAULT_MESSAGE);
	}
	
	public PluginNotFoundException(String message) {
		super(message);
	}
	
	public PluginNotFoundException(Throwable cause) {
		this(DEFAULT_MESSAGE, cause);
	}
	
	public PluginNotFoundException(ExceptionBean faultInfo){
		super(faultInfo);
	}
	
	public PluginNotFoundException(String message, Throwable cause){
		super(message, cause);
	}
	
	public PluginNotFoundException(String message, ExceptionBean faultInfo){
		super(message, faultInfo);

	}
	
	public PluginNotFoundException(String message, ExceptionBean faultInfo, Throwable cause){
		super(message, faultInfo, cause);
	}

	@Override
	public ExceptionBean getFaultInfo(){
		return faultInfo;
	}
	
}
