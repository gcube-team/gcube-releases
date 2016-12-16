package org.gcube.data.harmonization.occurrence.impl.readers;


public class StreamProgress {

	public enum OperationState {
		
		/**
		 * The operation is in progress.
		 */
		INPROGRESS,
		
		/**
		 * The operation is completed.
		 */
		COMPLETED,
		
		/**
		 * The operation is failed. 
		 */
		FAILED;
	}
	
	private long totalLenght=0;
	private long elaboratedLenght=0;
	private OperationState state=OperationState.INPROGRESS;
	private String failureReason="";
	private String failureDetails="";
	/**
	 * @return the totalLenght
	 */
	public long getTotalLenght() {
		return totalLenght;
	}
	/**
	 * @param totalLenght the totalLenght to set
	 */
	public void setTotalLenght(long totalLenght) {
		this.totalLenght = totalLenght;
	}
	/**
	 * @return the elaboratedLenght
	 */
	public long getElaboratedLenght() {
		return elaboratedLenght;
	}
	/**
	 * @param elaboratedLenght the elaboratedLenght to set
	 */
	public void setElaboratedLenght(long elaboratedLenght) {
		this.elaboratedLenght = elaboratedLenght;
	}
	/**
	 * @return the state
	 */
	public OperationState getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(OperationState state) {
		this.state = state;
	}
	/**
	 * @return the failureReason
	 */
	public String getFailureReason() {
		return failureReason;
	}
	/**
	 * @param failureReason the failureReason to set
	 */
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	/**
	 * @return the failureDetails
	 */
	public String getFailureDetails() {
		return failureDetails;
	}
	/**
	 * @param failureDetails the failureDetails to set
	 */
	public void setFailureDetails(String failureDetails) {
		this.failureDetails = failureDetails;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StreamProgress [totalLenght=");
		builder.append(totalLenght);
		builder.append(", elaboratedLenght=");
		builder.append(elaboratedLenght);
		builder.append(", state=");
		builder.append(state);
		builder.append(", failureReason=");
		builder.append(failureReason);
		builder.append(", failureDetails=");
		builder.append(failureDetails);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
