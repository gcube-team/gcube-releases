package org.gcube.datatransfer.common.messaging.messages;

import org.gcube.common.core.monitoring.GCUBEMessage;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.datatransfer.common.messaging.utils.Utils;


/**
 * 
 * @author Andrea Manzi
 *
 */
public class TransferMessage extends GCUBEMessage{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String messageType;
	
	private String transferType;
	
	private String transferPhase;
	
	private String transferOutcome;
	
	private String sourceID;
	
	private String destID;
	
	private String transferId;
	


	/**
	 * queue base
	 */
	public static final String dataTransferLabel="DATA.TRANSFER";
	
	/**
	 * creates the topic name for this message
	 * @param scope the message scope
	 */
	public void createTopicName(GCUBEScope scope){
		
		if (scope.isInfrastructure()){
			this.topic = Utils.replaceUnderscore(scope.getName())+
			"."+dataTransferLabel +
			"."+Utils.replaceUnderscore(sourceGHN);
		}
		else if (scope.getType().compareTo(GCUBEScope.Type.VO) == 0)
		{
			String voName =scope.getName();
			this.topic = Utils.replaceUnderscore(scope.getName())+
			"."+Utils.replaceUnderscore(voName)+
			"."+dataTransferLabel +
			"."+Utils.replaceUnderscore(sourceGHN);
		}
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
	
		builder.append(this.messageType+"\t");
		builder.append(this.scope+"\t");
		builder.append(this.transferType+"\t");
		builder.append(this.transferPhase+"\t");
		builder.append(this.transferOutcome+"\t");
		builder.append(this.sourceGHN+"\t");	
		builder.append(this.time+"\t");
		builder.append(this.topic+"\t");
		
		return builder.toString();
		
	}
	
	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public String getTransferPhase() {
		return transferPhase;
	}

	public void setTransferPhase(String transferPhase) {
		this.transferPhase = transferPhase;
	}

	public String getTransferOutcome() {
		return transferOutcome;
	}

	public void setTransferOutcome(String transferOutcome) {
		this.transferOutcome = transferOutcome;
	}

	
	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public String getDestID() {
		return destID;
	}

	public void setDestID(String destID) {
		this.destID = destID;
	}
	
	
	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}


}
