package org.gcube.datatransfer.common.outcome;

import java.io.Serializable;


public class TreeTransferOutcome extends TransferOutcome implements Serializable{

	private static final long serialVersionUID = 1L;
	private String sourceID;
	private String destID;
	private int totalReadTrees;  //trees been read for the transfer
	private int totalWrittenTrees;  //successfully transferred trees
	
	public TreeTransferOutcome(){}

	public String getSourceID() {
		return sourceID;
	}

	public String getDestID() {
		return destID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public void setDestID(String destID) {
		this.destID = destID;
	}

	public int getTotalReadTrees() {
		return totalReadTrees;
	}

	public int getTotalWrittenTrees() {
		return totalWrittenTrees;
	}

	public void setTotalReadTrees(int totalReadTrees) {
		this.totalReadTrees = totalReadTrees;
	}

	public void setTotalWrittenTrees(int totalWrittenTrees) {
		this.totalWrittenTrees = totalWrittenTrees;
	};
	
	
}
