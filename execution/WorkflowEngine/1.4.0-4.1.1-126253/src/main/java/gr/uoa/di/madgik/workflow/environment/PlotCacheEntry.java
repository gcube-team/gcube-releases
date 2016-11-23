package gr.uoa.di.madgik.workflow.environment;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;

public class PlotCacheEntry implements ICacheEntry
{
	private String PlotName;
	private InvocablePlotInfo PlotInfo=null;
	
	public PlotCacheEntry(String PlotName) {this.PlotName=PlotName;}
	
	public PlotCacheEntry(String PlotName,InvocablePlotInfo PlotInfo) 
	{
		this.PlotName=PlotName;
		this.PlotInfo=PlotInfo;
	}

	public CacheType GetCacheEntryType()
	{
		return CacheType.Plot;
	}
	
	public void SetPlotName(String PlotName) { this.PlotName=PlotName;}
	
	public String GetPlotName() { return this.PlotName; }
	
	public void SetPlotInfo(InvocablePlotInfo PlotInfo) { this.PlotInfo=PlotInfo; }
	
	public InvocablePlotInfo GetPlotInfo(EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
//		Removed the respective method form the Information Provider
//		if(PlotInfo==null) this.PlotInfo=InformationSystem.GetPlotWithName(this.PlotName,Hints);
		return PlotInfo;
	}
}
