package gr.uoa.di.madgik.workflow.adaptor.utils.grid;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.ss.StorageSystem;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowGridAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * The Class AdaptorGridResources acts as a container for {@link AttachedGridResource} instances that are to be supplied
 * to an {@link WorkflowGridAdaptor}. The resources that are provided must comply to some restrictions. These are:
 *  - There must be exactly one resource of type {@link AttachedGridResource.ResourceType#JDL}. In subsequent
 *  	versions there will be an option available to submit more JDL files that will be submitted with the "collection"
 *  	attribute
 *  - There can be at most 1 resource of type {@link AttachedGridResource.ResourceType#Config}
 *  - There must be exactly one resource of type {@link AttachedGridResource.ResourceType#UserProxy}
 * 
 * @author gpapanikos
 */
public class AdaptorGridResources implements IAdaptorResources
{
	
	/** The contained Resources */
	public Set<AttachedGridResource> Resources=new HashSet<AttachedGridResource>();
	
	/**
	 * Validate that the provided resources are the ones that must be present
	 * 
	 * @throws WorkflowValidationException A validation error occurred
	 */
	public void Validate() throws WorkflowValidationException
	{
		int configCount=0;
		int jdlCount=0;
		int proxyCount=0;
		for(AttachedGridResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedGridResource.ResourceType.Config)) configCount+=1;
			else if (att.TypeOfResource.equals(AttachedGridResource.ResourceType.JDL)) jdlCount+=1;
			else if (att.TypeOfResource.equals(AttachedGridResource.ResourceType.OutData))
			{
				File f = new File(att.Key);
				if(f.isAbsolute()) throw new WorkflowValidationException("Key value of OutData resource cannot be absolute path");
			}
			else if (att.TypeOfResource.equals(AttachedGridResource.ResourceType.UserProxy)) proxyCount+=1;
//			if(!att.TypeOfResource.equals(AttachedGridResource.ResourceType.OutData))
//			{
//				if(!new File(att.Value).isFile() || !new File(att.Value).exists()) throw new WorkflowValidationException("resource "+att.Value+" cannot be found");
//			}
		}
		if(configCount>1) throw new WorkflowValidationException("At most one config resource can be defined");
		if(jdlCount!=1) throw new WorkflowValidationException("Exactly one jdl resource must be defined");
		if(proxyCount!=1) throw new WorkflowValidationException("Exactly one user proxy resource must be defined");
	}
	
	/**
	 * Checks if a resource exists.
	 * 
	 * @param ResourceKey the resource key
	 * 
	 * @return true, if successful
	 */
	public boolean ResourceExists(String ResourceKey)
	{
		for(AttachedGridResource res : this.Resources)
		{
			if(res.Key.equals(ResourceKey)) return true;
		}
		return false;
	}
	
	/**
	 * Retrieves the  resource with the provided key
	 * 
	 * @param ResourceKey the resource key
	 * 
	 * @return the attached grid resource
	 */
	public AttachedGridResource GetResource(String ResourceKey)
	{
		for(AttachedGridResource res : this.Resources)
		{
			if(res.Key.equals(ResourceKey)) return res;
		}
		return null;
	}
	
	/**
	 * Stores the resource in the storage system
	 * 
	 * @param ResourcesToStore the resources to store
	 * 
	 * @throws Exception the exception
	 */
	public void StoreResources(Set<AttachedGridResource.ResourceType> ResourcesToStore,EnvHintCollection Hints) throws Exception
	{
		for(AttachedGridResource att : this.Resources)
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
	
	public AttachedGridResource GetScopeResource()
	{
		for(AttachedGridResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedGridResource.ResourceType.Scope)) return att;
		}
		return null;
	}
	
	/**
	 * Gets the JDL resource.
	 * 
	 * @return the attached grid resource
	 */
	public AttachedGridResource GetJDLResource()
	{
		for(AttachedGridResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedGridResource.ResourceType.JDL)) return att;
		}
		return null;
	}
	
	/**
	 * Gets the config resource.
	 * 
	 * @return the attached grid resource
	 */
	public AttachedGridResource GetConfigResource()
	{
		for(AttachedGridResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedGridResource.ResourceType.Config)) return att;
		}
		return null;
	}
	
	/**
	 * Gets the user proxy resource.
	 * 
	 * @return the attached grid resource
	 */
	public AttachedGridResource GetUserProxyResource()
	{
		for(AttachedGridResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedGridResource.ResourceType.UserProxy)) return att;
		}
		return null;
	}
	
	/**
	 * Gets the in data resources.
	 * 
	 * @return the set of attached grid resources
	 */
	public Set<AttachedGridResource> GetInDataResources()
	{
		Set<AttachedGridResource> ret=new HashSet<AttachedGridResource>();
		for(AttachedGridResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedGridResource.ResourceType.InData)) ret.add(att);
		}
		return ret;
	}
	
	/**
	 * Gets the out data resources.
	 * 
	 * @return the set of attached grid resources
	 */
	public Set<AttachedGridResource> GetOutDataResources()
	{
		Set<AttachedGridResource> ret=new HashSet<AttachedGridResource>();
		for(AttachedGridResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(AttachedGridResource.ResourceType.OutData)) ret.add(att);
		}
		return ret;
	}
}
