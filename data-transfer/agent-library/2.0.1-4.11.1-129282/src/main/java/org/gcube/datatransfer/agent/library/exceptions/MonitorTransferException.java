package org.gcube.datatransfer.agent.library.exceptions;

import javax.xml.ws.WebFault;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable

@WebFault(name="MonitorTransferFault")
public class MonitorTransferException  extends Exception {
	private static final long serialVersionUID = 1L;
	

	public MonitorTransferException(){
		super();
	}
	
	public MonitorTransferException(String message){
		super(message);
	}
	
	
	public MonitorTransferException(String msg,Throwable cause) {
		super(msg,cause);
		}

	public MonitorTransferException(Throwable cause) {
		super(cause);
	}
}
