package org.gcube.datatransfer.agent.library.exceptions;

import javax.xml.ws.WebFault;

import org.gcube.common.clients.delegates.Unrecoverable;


@Unrecoverable
@WebFault(name="GetTransferOutcomesFault")
public class GetTransferOutcomesException extends Exception {
	private static final long serialVersionUID = 1L;
	

	public GetTransferOutcomesException(){
		super();
	}
	
	public GetTransferOutcomesException(String message){
		super(message);
	}
	
	
	public GetTransferOutcomesException(String msg,Throwable cause) {
		super(msg,cause);
		}

	public GetTransferOutcomesException(Throwable cause) {
		super(cause);
	}
	
}
