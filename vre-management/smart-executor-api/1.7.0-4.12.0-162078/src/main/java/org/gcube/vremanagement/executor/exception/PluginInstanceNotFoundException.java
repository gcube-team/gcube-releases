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
public class PluginInstanceNotFoundException extends ExecutorException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -7609491801703267843L;

	private static final String DEFAULT_MESSAGE = "The requested plugin instance does not exists";
	
	public PluginInstanceNotFoundException(){
		super(DEFAULT_MESSAGE);
		this.faultInfo = new ExceptionBean(DEFAULT_MESSAGE);
	}
	
	public PluginInstanceNotFoundException(String message) {
		super(message);
	}
	
	public PluginInstanceNotFoundException(Throwable cause) {
		this(DEFAULT_MESSAGE, cause);
	}
	
	public PluginInstanceNotFoundException(ExceptionBean faultInfo){
		super(faultInfo);
	}
	
	public PluginInstanceNotFoundException(String message, Throwable cause){
		super(message, cause);
	}
	
	public PluginInstanceNotFoundException(String message, ExceptionBean faultInfo){
		super(message, faultInfo);

	}
	
	public PluginInstanceNotFoundException(String message, ExceptionBean faultInfo, Throwable cause){
		super(message, faultInfo, cause);
	}

	@Override
	public ExceptionBean getFaultInfo(){
		return faultInfo;
	}
	
}
