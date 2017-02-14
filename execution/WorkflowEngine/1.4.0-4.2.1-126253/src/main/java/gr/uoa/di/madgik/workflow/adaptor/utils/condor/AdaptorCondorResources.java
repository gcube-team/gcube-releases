package gr.uoa.di.madgik.workflow.adaptor.utils.condor;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.ss.StorageSystem;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;

public class AdaptorCondorResources implements IAdaptorResources
{
	public Set<AttachedCondorResource> Resources=new HashSet<AttachedCondorResource>();
	
	public AttachedCondorResource GetScopeResource()
	{
		for(AttachedCondorResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedCondorResource.ResourceType.Scope)) return att;
		}
		return null;
	}
	
	public void Validate() throws WorkflowValidationException
	{
		int submitCount=0;
		for(AttachedCondorResource att : this.Resources)
		{
			if (att.TypeOfResource.equals(AttachedCondorResource.ResourceType.Submit)) submitCount+=1;
		}
		if(submitCount!=1) throw new WorkflowValidationException("Exactly one submit resource must be defined");
	}
	
	public void StoreResources(Set<AttachedCondorResource.ResourceType> ResourcesToStore,EnvHintCollection Hints) throws Exception
	{
		for(AttachedCondorResource att : this.Resources)
		{
			switch(att.ResourceLocationType)
			{
				case CMSReference:
				{
					if((att.Value==null || att.Value.trim().length()==0) && ResourcesToStore.contains(att.TypeOfResource)) throw new WorkflowValidationException("CMS id not provided for "+att.ResourceLocationType+" provided attribute");
					att.StorageSystemID=att.Value;
					break;
				}
				case Reference:
				{
					if((att.StorageSystemID==null || att.StorageSystemID.trim().length()==0) && ResourcesToStore.contains(att.TypeOfResource)) att.StorageSystemID = StorageSystem.Store(new URL(att.Value),Hints);
					break;
				}
				case LocalFile:
				{
					if((att.StorageSystemID==null || att.StorageSystemID.trim().length()==0) && ResourcesToStore.contains(att.TypeOfResource)) att.StorageSystemID = StorageSystem.Store(att.Value,Hints);
					break;
				}
				default: throw new WorkflowValidationException("Unrecognized resource type");
			}
		}
	}

	public Set<AttachedCondorResource> GetInDataResources()
	{
		Set<AttachedCondorResource> ret=new HashSet<AttachedCondorResource>();
		for(AttachedCondorResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedCondorResource.ResourceType.InData)) ret.add(att);
		}
		return ret;
	}

	public Set<AttachedCondorResource> GetExecutableResources()
	{
		Set<AttachedCondorResource> ret=new HashSet<AttachedCondorResource>();
		for(AttachedCondorResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedCondorResource.ResourceType.Executable)) ret.add(att);
		}
		return ret;
	}

	public Set<AttachedCondorResource> GetCommandResources()
	{
		Set<AttachedCondorResource> ret=new HashSet<AttachedCondorResource>();
		for(AttachedCondorResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedCondorResource.ResourceType.Command)) ret.add(att);
		}
		return ret;
	}

	public AttachedCondorResource GetSubmitResource()
	{
		for(AttachedCondorResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedCondorResource.ResourceType.Submit)) return att;
		}
		return null;
	}
}
