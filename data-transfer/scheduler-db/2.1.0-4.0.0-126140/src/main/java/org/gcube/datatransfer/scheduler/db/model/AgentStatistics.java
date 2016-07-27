package org.gcube.datatransfer.scheduler.db.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;




@PersistenceCapable(table="AGENT_STATISTICS")
public class AgentStatistics implements java.io.Serializable {

	private static final long serialVersionUID = -1334765772852309468L;

	@PrimaryKey
	@Persistent(customValueStrategy="uuid")
	private String agentStatisticsId;
	
	// agentIdOfIS is a unique id of agent even if it's deleted from my DB
	// or even if it goes down for a while and then up in IS again
	private String agentIdOfIS;   
	
	//Statistics:
	//present
	private int ongoingTransfers;   // >0
	//past
	private int failedTransfers;  // >0
	private int succeededTransfers;  // >0
	private int canceledTransfers;  // >0
	private int totalFinishedTransfers;     // >0

	
	public AgentStatistics(){
		this.agentIdOfIS=null;
		this.ongoingTransfers=0;
		this.failedTransfers=0;
		this.canceledTransfers=0;
		this.totalFinishedTransfers=0;
	}


	public String getAgentStatisticsId() {
		return agentStatisticsId;
	}


	public void setAgentStatisticsId(String agentStatisticsId) {
		this.agentStatisticsId = agentStatisticsId;
	}


	public String getAgentIdOfIS() {
		return agentIdOfIS;
	}


	public void setAgentIdOfIS(String agentIdOfIS) {
		this.agentIdOfIS = agentIdOfIS;
	}


	public int getOngoingTransfers() {
		return ongoingTransfers;
	}


	public void setOngoingTransfers(int ongoingTransfers) {
		if(ongoingTransfers<0)this.ongoingTransfers=0;
		else this.ongoingTransfers = ongoingTransfers;
	}


	public int getFailedTransfers() {
		return failedTransfers;
	}


	public void setFailedTransfers(int failedTransfers) {
		if(failedTransfers<0)this.failedTransfers=0;
		else this.failedTransfers = failedTransfers;
	}


	public int getSucceededTransfers() {
		return succeededTransfers;
	}


	public void setSucceededTransfers(int succeededTransfers) {
		if(succeededTransfers<0)this.succeededTransfers=0;
		else this.succeededTransfers = succeededTransfers;
	}


	public int getCanceledTransfers() {
		return canceledTransfers;
	}


	public void setCanceledTransfers(int canceledTransfers) {
		if(canceledTransfers<0)this.canceledTransfers=0;
		else this.canceledTransfers = canceledTransfers;
	}


	public int getTotalFinishedTransfers() {
		return totalFinishedTransfers;
	}


	public void setTotalFinishedTransfers(int totalFinishedTransfers) {
		if(totalFinishedTransfers<0)this.totalFinishedTransfers=0;
		else this.totalFinishedTransfers = totalFinishedTransfers;
	}


}
