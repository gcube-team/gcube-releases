package org.gcube.datatransfer.portlets.user.shared.obj;
import java.io.Serializable;

import org.gcube.datatransfer.common.agent.Types.CancelTransferMessage;

public class InfoCancelSchedulerMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	
	protected CancelTransferMessage cancelTransferMessage;

	
	public CancelTransferMessage getCancelTransferMessage() {
		return cancelTransferMessage;
	}
	public void setCancelTransferMessage(CancelTransferMessage cancelTransferMessage) {
		this.cancelTransferMessage = cancelTransferMessage;
	}

}
