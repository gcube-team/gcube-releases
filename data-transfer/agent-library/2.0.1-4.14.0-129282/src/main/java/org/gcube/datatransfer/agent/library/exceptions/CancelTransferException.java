package org.gcube.datatransfer.agent.library.exceptions;

import javax.xml.ws.WebFault;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
@WebFault(name="CancelTransferFault")
public class CancelTransferException  extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public CancelTransferException(){
		super();
	}
	
	public CancelTransferException(String message){
		super(message);
	}
	public CancelTransferException(String msg,Throwable cause) {
		super(msg,cause);
		}

	public CancelTransferException(Throwable cause) {
		super(cause);
	}
}