package org.gcube.datatransfer.common.messaging.messages;

import java.util.ArrayList;

import org.gcube.common.core.monitoring.GCUBEMessage;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.messaging.utils.Utils;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TreeTransferOutcome;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TransferResponseMessage extends GCUBEMessage{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final MessageLabels messageType=MessageLabels.DataTransferResponse;

	//for filtering
	private String sourceEndpoint;	
	private String destEndpoint;	
	private String transferId;
	
	//status
	private String transferStatus;
	//response during the transfer
	private MonitorTransferReportMessage monitorResponse;
	//response at the end of the transfer
	private ArrayList<FileTransferOutcome> outcomesResponse;
	//response at the end of the transfer (for tree-based transfer)
	private TreeTransferOutcome treeOutcomeResponse;
	
	//q label
	public static final String dataTransferLabel=MessageLabels.DataTransferResponse.toString();
	
	/**
	 * creates the topic name for this message
	 * @param scope the message scope
	 */
	public void createTopicName(GCUBEScope scope){
		if (scope.isInfrastructure()){
			this.topic = Utils.replaceUnderscore(scope.getName())+
			"."+dataTransferLabel +
			"."+Utils.replaceUnderscore(sourceEndpoint);
		}
		else if (scope.getType().compareTo(GCUBEScope.Type.VO) == 0)
		{
			String voName =scope.getName();
			this.topic = Utils.replaceUnderscore(scope.getInfrastructure().getName())+
			"."+Utils.replaceUnderscore(voName)+
			"."+dataTransferLabel +
			"."+Utils.replaceUnderscore(sourceEndpoint);
		}
	}
	
	
	public String getSourceEndpoint() {
		return sourceEndpoint;
	}

	public String getDestEndpoint() {
		return destEndpoint;
	}

	public String getTransferId() {
		return transferId;
	}

	public MonitorTransferReportMessage getMonitorResponse() {
		return monitorResponse;
	}

	public ArrayList<FileTransferOutcome> getOutcomesResponse() {
		return outcomesResponse;
	}

	public void setSourceEndpoint(String sourceEndpoint) {
		this.sourceEndpoint = sourceEndpoint;
	}

	public void setDestEndpoint(String destEndpoint) {
		this.destEndpoint = destEndpoint;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}

	public void setMonitorResponse(MonitorTransferReportMessage monitorResponse) {
		this.monitorResponse = monitorResponse;
	}

	public void setOutcomesResponse(ArrayList<FileTransferOutcome> outcomesResponse) {
		this.outcomesResponse = outcomesResponse;
	}

	public String getTransferStatus() {
		return transferStatus;
	}

	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}


	public TreeTransferOutcome getTreeOutcomeResponse() {
		return treeOutcomeResponse;
	}


	public void setTreeOutcomeResponse(TreeTransferOutcome treeOutcomeResponse) {
		this.treeOutcomeResponse = treeOutcomeResponse;
	}
	
	
	

}
