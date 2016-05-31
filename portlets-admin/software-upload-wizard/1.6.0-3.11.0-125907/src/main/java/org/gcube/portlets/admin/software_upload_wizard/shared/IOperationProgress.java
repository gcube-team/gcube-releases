package org.gcube.portlets.admin.software_upload_wizard.shared;

import java.io.Serializable;

public interface IOperationProgress extends Serializable {

	public long getTotalLenght();

	public long getElaboratedLenght();

	public OperationState getState();

	public void setState(OperationState state);

	public double getProgress();

	public void setProgress(long totalLenght, long elaboratedLenght);

	public String getDetails();

	public void setDetails(String details);

}