package org.gcube.datatransfer.scheduler.library.obj;
import java.io.Serializable;

import org.gcube.datatransfer.common.agent.Types.CancelTransferMessage;

import com.thoughtworks.xstream.XStream;



public class InfoCancelSchedulerMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	
	protected CancelTransferMessage cancelTransferMessage;
	protected static XStream xstream = new XStream();

	
	public CancelTransferMessage getCancelTransferMessage() {
		return cancelTransferMessage;
	}
	public void setCancelTransferMessage(CancelTransferMessage cancelTransferMessage) {
		this.cancelTransferMessage = cancelTransferMessage;
	}

	public String toXML(){
		return xstream.toXML(this);
	}

}
