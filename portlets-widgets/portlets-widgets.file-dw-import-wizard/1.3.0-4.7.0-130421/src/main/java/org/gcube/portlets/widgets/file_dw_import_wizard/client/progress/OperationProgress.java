/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.progress;

import java.io.Serializable;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class OperationProgress implements Serializable {

	private static final long serialVersionUID = -1150111422206443617L;
	
	protected long totalLenght;
	protected long elaboratedLenght;
	protected OperationState state;
	protected String failureReason;
	protected String failureDetails;
	
	public OperationProgress(){
		state = OperationState.INPROGRESS;
	}

	public OperationProgress(long totalLenght, long elaboratedLenght, OperationState state, String failureReason) {
		this.totalLenght = totalLenght;
		this.elaboratedLenght = elaboratedLenght;
		this.state = state;
		this.failureReason = failureReason;
	}

	/**
	 * @return the totalLenght
	 */
	public long getTotalLenght() {
		return totalLenght;
	}
	
	/**
	 * @return the elaboratedLenght
	 */
	public long getElaboratedLenght() {
		return elaboratedLenght;
	}

	
	public OperationState getState(){
		return state;
	}

	/**
	 * @return the failureDetails
	 */
	public String getFailureDetails() {
		return failureDetails;
	}

	public void setState(OperationState state)
	{
		this.state = state;
	}

	/**
	 * @return the reason
	 */
	public String getFailureReason() {
		return failureReason;
	}

	/**
	 * @param totalLenght the totalLenght to set
	 */
	public void setTotalLenght(long totalLenght) {
		this.totalLenght = totalLenght;
	}

	/**
	 * @param elaboratedLenght the elaboratedLenght to set
	 */
	public void setElaboratedLenght(long elaboratedLenght) {
		this.elaboratedLenght = elaboratedLenght;
	}

	/**
	 * @param failed the failed to set
	 */
	public void setFailed(String failureReason, String failureDetails) {
		this.state = OperationState.FAILED;
		this.failureReason = failureReason;
		this.failureDetails = failureDetails;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OperationProgress [totalLenght=");
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
