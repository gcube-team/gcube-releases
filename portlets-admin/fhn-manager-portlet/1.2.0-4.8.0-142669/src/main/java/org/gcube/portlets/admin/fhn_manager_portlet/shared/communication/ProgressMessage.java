package org.gcube.portlets.admin.fhn_manager_portlet.shared.communication;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProgressMessage implements IsSerializable{

	private Double progressCount=0.0d;
	private String message="";
	private ProgressStatus status=ProgressStatus.PENDING;
	
	private IsSerializable result=null;
	private OperationTicket ticket=null;
	/**
	 * @return the progressCount
	 */
	public Double getProgressCount() {
		return progressCount;
	}
	/**
	 * @param progressCount the progressCount to set
	 */
	public void setProgressCount(Double progressCount) {
		this.progressCount = progressCount;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the status
	 */
	public ProgressStatus getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(ProgressStatus status) {
		this.status = status;
	}
	/**
	 * @return the result
	 */
	public IsSerializable getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(IsSerializable result) {
		this.result = result;
	}
	/**
	 * @return the ticket
	 */
	public OperationTicket getTicket() {
		return ticket;
	}
	/**
	 * @param ticket the ticket to set
	 */
	public void setTicket(OperationTicket ticket) {
		this.ticket = ticket;
	}
	
	public ProgressMessage() {
		// TODO Auto-generated constructor stub
	}
	public ProgressMessage(Double progressCount, String message,
			ProgressStatus status, OperationTicket ticket,IsSerializable result) {
		super();
		this.progressCount = progressCount;
		this.message = message;
		this.status = status;
		this.ticket = ticket;
		this.result=result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProgressMessage [progressCount=");
		builder.append(progressCount);
		builder.append(", message=");
		builder.append(message);
		builder.append(", status=");
		builder.append(status);
		builder.append(", result=");
		builder.append(result);
		builder.append(", ticket=");
		builder.append(ticket);
		builder.append("]");
		return builder.toString();
	}
	
		
}
