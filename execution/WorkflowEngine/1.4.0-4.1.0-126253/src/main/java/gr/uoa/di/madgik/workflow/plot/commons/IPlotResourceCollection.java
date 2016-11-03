package gr.uoa.di.madgik.workflow.plot.commons;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;

public interface IPlotResourceCollection
{
	public void Add(IPlotResource param) throws WorkflowValidationException;
	
	public void SetPlotInfo(InvocablePlotInfo plotInfo);
	
	public void SetInvocableInfo(InvocableProfileInfo invocableInfo);

	public void Validate() throws WorkflowValidationException;
}
