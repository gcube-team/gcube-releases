package gr.uoa.di.madgik.workflow.plot.commons;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentFile;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.util.HashMap;
import java.util.Map;

public class PlotResourceEnvironmentFileCollection implements IPlotResourceCollection
{
	public Map<String,PlotResourceEnvironmentFile> Parameters=new HashMap<String,PlotResourceEnvironmentFile>();
	public InvocablePlotInfo plotInfo;
	public InvocableProfileInfo invocableInfo;

	public void Add(IPlotResource param) throws WorkflowValidationException
	{
		if(!(param instanceof PlotResourceEnvironmentFile)) throw new WorkflowValidationException("Incompatible type provided");
		this.Parameters.put(((PlotResourceEnvironmentFile)param).FileName, (PlotResourceEnvironmentFile)param);
	}
	
	public PlotResourceEnvironmentFile Get(String fileName)
	{
		return this.Parameters.get(fileName);
	}

	public void SetPlotInfo(InvocablePlotInfo plotInfo)
	{
		this.plotInfo=plotInfo;
	}

	public void SetInvocableInfo(InvocableProfileInfo invocableInfo)
	{
		this.invocableInfo=invocableInfo;
	}
	
	public void Validate() throws WorkflowValidationException
	{
		for(PlotLocalEnvironmentFile f : this.plotInfo.LocalEnvironment.Files)
		{
			if(f.Name==null || f.Name.trim().length()==0 || f.Location==null || f.Location.trim().length()==0) throw new WorkflowValidationException("Needed values not provided");
			PlotResourceEnvironmentFile pf= this.Get(f.Name);
			if(pf==null) throw new WorkflowValidationException("Resource for file "+f.Name+" not provided");
			if(pf.InParameter==null || pf.OutParameter==null) throw new WorkflowValidationException("Input and output parameters not provided");
		}
	}
}
