package org.gcube.datatransfer.common.messaging.messages;

import java.net.URI;
import java.util.ArrayList;

import org.gcube.common.core.monitoring.GCUBEMessage;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.datatransfer.common.agent.Types.TransferOptions;
import org.gcube.datatransfer.common.messaging.utils.Utils;



/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TransferRequestMessage extends GCUBEMessage{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//for filtering
	private String sourceEndpoint;	
	private String destEndpoint;	
	private String transferId;
	
	//transfer inputs
	private ArrayList<URI> inputUris;
	//transfer source (in case of tree based transfer)
	private String treeSourceID;
	//transfer outputs (array list or just a string depends on the type)
	private ArrayList<URI> outputUris;
	//transfer dest (in case of tree based transfer)
	private String treeDestID;
	private String destination;
	//transfer options
	private TransferOptions transferOptions ;
	private String treePattern;
	
	//q label
	public static final String dataTransferLabel=MessageLabels.DataTransferRequest.toString();
	
	/**
	 * creates the topic name for this message
	 * @param scope the message scope
	 */
	public void createTopicName(GCUBEScope scope){
		
		String destination="";
		if(destEndpoint!=null)destination="."+destEndpoint;
		
		if (scope.isInfrastructure()){
			this.topic = Utils.replaceUnderscore(scope.getName())+
			"."+dataTransferLabel +
			destination+
			"."+Utils.replaceUnderscore(sourceEndpoint);
		}
		else if (scope.getType().compareTo(GCUBEScope.Type.VO) == 0)
		{
			String voName =scope.getName();
			this.topic = Utils.replaceUnderscore(scope.getInfrastructure().getName())+
			"."+Utils.replaceUnderscore(voName)+
			"."+dataTransferLabel +
			destination+
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

	public ArrayList<URI> getInputUris() {
		return inputUris;
	}

	public ArrayList<URI> getOutputUris() {
		return outputUris;
	}

	public String getDestination() {
		return destination;
	}

	public TransferOptions getTransferOptions() {
		return transferOptions;
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

	public void setInputUris(ArrayList<URI> inputUris) {
		this.inputUris = inputUris;
	}

	public void setOutputUris(ArrayList<URI> outputUris) {
		this.outputUris = outputUris;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setTransferOptions(TransferOptions transferOptions) {
		this.transferOptions = transferOptions;
	}



	public String getTreeSourceID() {
		return treeSourceID;
	}



	public String getTreeDestID() {
		return treeDestID;
	}



	public String getTreePattern() {
		return treePattern;
	}



	public void setTreeSourceID(String treeSourceID) {
		this.treeSourceID = treeSourceID;
	}



	public void setTreeDestID(String treeDestID) {
		this.treeDestID = treeDestID;
	}



	public void setTreePattern(String treePattern) {
		this.treePattern = treePattern;
	}

}
