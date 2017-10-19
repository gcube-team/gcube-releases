package org.gcube.portlets.admin.fhn_manager_portlet.shared.communication;

import com.google.gwt.user.client.rpc.IsSerializable;


public class OperationTicket implements IsSerializable{

	private String id;
	private Operation operation;
	private long requestTime;
	
	
	public OperationTicket() {
		// TODO Auto-generated constructor stub
	}

	public OperationTicket(String id, Operation operation, long requestTime) {
		super();
		this.id = id;
		this.operation=operation;
		this.requestTime = requestTime;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	/**
	 * @return the requestTime
	 */
	public long getRequestTime() {
		return requestTime;
	}

	/**
	 * @param requestTime the requestTime to set
	 */
	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OperationTicket other = (OperationTicket) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OperationTicket [id=");
		builder.append(id);
		builder.append(", operation=");
		builder.append(operation);
		builder.append(", requestTime=");
		builder.append(requestTime);
		builder.append("]");
		return builder.toString();
	}

	
	
	
}
