package org.gcube.datatransfer.agent.impl.event;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class TransferOutcome {
	
	GCUBEScope scope;
	TransferType transferType;
	String transferPhase;
	String sourceID;
	String outcome;
	String destID;
	String transferId;
	
	public TransferOutcome(){}
	
	
	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}

	public GCUBEScope getScope() {
		return scope;
	}

	public void setScope(GCUBEScope scope) {
		this.scope = scope;
	}

	public TransferType getTransferType() {
		return transferType;
	}

	public void setTransferType(TransferType transferType) {
		this.transferType = transferType;
	}

	public String getTransferPhase() {
		return transferPhase;
	}

	public void setTransferPhase(String transferPhase) {
		this.transferPhase = transferPhase;
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

	
	

	public String getOutcome() {
		return outcome;
	}
	
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

}
