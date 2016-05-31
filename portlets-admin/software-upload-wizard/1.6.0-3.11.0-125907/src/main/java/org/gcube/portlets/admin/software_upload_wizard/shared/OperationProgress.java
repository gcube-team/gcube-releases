package org.gcube.portlets.admin.software_upload_wizard.shared;

public class OperationProgress implements IOperationProgress {

	private long totalLenght = 1;
	private long elaboratedLenght = 0;
	private OperationState state;
	private String details = "";

	public OperationProgress() {
		state = OperationState.IN_PROGRESS;
	}

	public OperationProgress(long totalLenght, long elaboratedLenght,
			OperationState state) {
		this.totalLenght = totalLenght;
		this.elaboratedLenght = elaboratedLenght;
		this.state = state;
	}

	@Override
	public long getTotalLenght() {
		return totalLenght;
	}

	@Override
	public long getElaboratedLenght() {
		return elaboratedLenght;
	}

	@Override
	public OperationState getState() {
		return state;
	}

	@Override
	public void setState(OperationState state) {
		this.state = state;
	}

	@Override
	public double getProgress() {
		return totalLenght > 0 ? ((double) elaboratedLenght / (double) totalLenght)
				: 0;
	}

	@Override
	public void setProgress(long totalLenght, long elaboratedLenght) {
		this.totalLenght = totalLenght;
		this.elaboratedLenght = elaboratedLenght;
	}

	@Override
	public String getDetails() {
		return details;
	}

	@Override
	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = "\n";

		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append(" State: " + getState() + NEW_LINE);
		result.append(" Total lenght: " + getTotalLenght() + NEW_LINE);
		result.append(" Elaborated lenght: " + getElaboratedLenght() + NEW_LINE);
		result.append(" Progress: " + getProgress() + NEW_LINE);
		result.append(" Details: " + getDetails() + NEW_LINE);
		result.append("}");

		return result.toString();
	}

}
