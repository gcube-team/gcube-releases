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
public class LaunchException extends ExecutorException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -4805436085852176907L;
	
	private static final String DEFAULT_MESSAGE = "Error trying to launch the plugin execution";
	
	public LaunchException(){
		super(DEFAULT_MESSAGE);
		this.faultInfo = new ExceptionBean(DEFAULT_MESSAGE);
	}

	public LaunchException(String message) {
		super(message);
	}
	
	public LaunchException(Throwable cause) {
		this(DEFAULT_MESSAGE, cause);
	}
	
	public LaunchException(ExceptionBean faultInfo){
		super(faultInfo);
	}
	
	public LaunchException(String message, Throwable cause){
		super(message, cause);
	}
	
	public LaunchException(String message, ExceptionBean faultInfo){
		super(message, faultInfo);

	}
	
	public LaunchException(String message, ExceptionBean faultInfo, Throwable cause){
		super(message, faultInfo, cause);
	}

	@Override
	public ExceptionBean getFaultInfo(){
		return faultInfo;
	}
}
