package gr.uoa.di.madgik.workflow.plot;

import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;

public class PlotSelectionCriteria
{
	//currently selection is made only by name
	public String PlotName=null;
	public void Validate() throws WorkflowValidationException
	{
		if(this.PlotName==null || this.PlotName.trim().length()==0) throw new WorkflowValidationException("No plot name defined");
	}
}
