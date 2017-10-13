/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class FileUploadMonitor implements Serializable {

	private static final long serialVersionUID = -1150111422206443617L;

	protected long totalLenght;
	protected long elaboratedLenght;
	protected FileUploadState state;
	protected String failureReason;
	protected String failureDetails;
	protected float percentDone;

	public FileUploadMonitor() {
		state = FileUploadState.INPROGRESS;
	}

	public FileUploadMonitor(long totalLenght, long elaboratedLenght, FileUploadState state, String failureReason) {
		this.totalLenght = totalLenght;
		this.elaboratedLenght = elaboratedLenght;
		this.state = state;
		this.failureReason = failureReason;
		this.percentDone = 0;
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

	public FileUploadState getState() {
		return state;
	}

	/**
	 * @return the failureDetails
	 */
	public String getFailureDetails() {
		return failureDetails;
	}

	public float getPercentDone() {
		return percentDone;
	}

	public void setPercentDone(float percentDone) {
		this.percentDone = percentDone;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public void setFailureDetails(String failureDetails) {
		this.failureDetails = failureDetails;
	}

	public void setState(FileUploadState state) {
		this.state = state;
	}

	/**
	 * 
	 * @return failureReason
	 */
	public String getFailureReason() {
		return failureReason;
	}

	/**
	 * @param totalLenght
	 *            the totalLenght to set
	 */
	public void setTotalLenght(long totalLenght) {
		this.totalLenght = totalLenght;
	}

	/**
	 * 
	 * @param elaboratedLenght
	 *            elaborated lenght
	 */
	public void setElaboratedLenght(long elaboratedLenght) {
		this.elaboratedLenght = elaboratedLenght;
	}

	/**
	 * 
	 * @param failureReason
	 *            failure reason
	 * @param failureDetails
	 *            failure details
	 */
	public void setFailed(String failureReason, String failureDetails) {
		this.state = FileUploadState.FAILED;
		this.failureReason = failureReason;
		this.failureDetails = failureDetails;
	}

	@Override
	public String toString() {
		return "FileUploadMonitor [totalLenght=" + totalLenght + ", elaboratedLenght=" + elaboratedLenght + ", state="
				+ state + ", failureReason=" + failureReason + ", failureDetails=" + failureDetails + ", percentDone="
				+ percentDone + "]";
	}

}
