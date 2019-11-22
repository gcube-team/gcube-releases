package org.gcube.datatransfer.common.messaging.messages;
/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public enum MessageLabels {	
	DataTransferRequest{
		public String toString(){return "DATA.TRANSFER.REQUEST";}
	},
	DataTransferResponse{
		public String toString(){return "DATA.TRANSFER.RESPONSE";}
	}	
}
