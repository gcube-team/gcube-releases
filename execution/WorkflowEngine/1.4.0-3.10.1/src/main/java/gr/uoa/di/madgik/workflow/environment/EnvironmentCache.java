package gr.uoa.di.madgik.workflow.environment;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import java.util.HashMap;

public class EnvironmentCache
{	
	private HashMap<ICacheEntry.CacheType, HashMap<String, ICacheEntry>> Cache=new HashMap<ICacheEntry.CacheType, HashMap<String, ICacheEntry>>();
	
	public InvocablePlotInfo GetPlotInfo(String PlotName,EnvHintCollection Hints) throws WorkflowInternalErrorException, EnvironmentInformationSystemException
	{
		if(!Cache.containsKey(ICacheEntry.CacheType.Plot))
		{
			Cache.put(ICacheEntry.CacheType.Plot, new HashMap<String, ICacheEntry>());
		}
		if(!Cache.get(ICacheEntry.CacheType.Plot).containsKey(PlotName))
		{
			Cache.get(ICacheEntry.CacheType.Plot).put(PlotName, new PlotCacheEntry(PlotName));
		}
		ICacheEntry entry=Cache.get(ICacheEntry.CacheType.Plot).get(PlotName);
		if(entry==null) throw new WorkflowInternalErrorException("Did not find needed cache item");
		if(!(entry instanceof PlotCacheEntry)) throw new WorkflowInternalErrorException("Incompatible chace item found");
		if(!((PlotCacheEntry)entry).GetPlotName().equals(PlotName)) throw new WorkflowInternalErrorException("incompatible plot name found");
		return ((PlotCacheEntry)entry).GetPlotInfo(Hints);
	}
}
