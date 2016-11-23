package org.gcube.datatransfer.agent.library.exceptions;

import javax.xml.ws.WebFault;

import org.gcube.common.clients.delegates.Unrecoverable;

@Unrecoverable
@WebFault(name="TransferFault")
public class TransferException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TransferException(){
		super();
	}
	public TransferException(String message){
		super(message);
	}
	public TransferException(String msg,Throwable cause) {
		super(msg,cause);
		}

	public TransferException(Throwable cause) {
		super(cause);
	}

}
