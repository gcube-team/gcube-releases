package gr.uoa.di.madgik.workflow.adaptor.utils.jdl;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.ss.StorageSystem;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowGridAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.AttachedGridResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource.ResourceType;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AdaptorJDLResources acts as a container for {@link AttachedGridResource} instances that are to be supplied
 * to an {@link WorkflowGridAdaptor}.
 * 
 * @author gpapanikos
 */
public class AdaptorJDLResources implements IAdaptorResources
{
	
	private static Logger logger = LoggerFactory.getLogger(AdaptorJDLResources.class);
	
	/** The contained Resources */
	public Set<AttachedJDLResource> Resources=new HashSet<AttachedJDLResource>();
	
	/**
	 * Checks if a resource exists.
	 * 
	 * @param ResourceKey the resource key
	 * 
	 * @return true, if successful
	 */
	public boolean ResourceExists(String ResourceKey)
	{
		for(AttachedJDLResource res : this.Resources)
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
	 * @return the attached jdl resource
	 */
	public AttachedJDLResource GetResource(String ResourceKey)
	{
		for(AttachedJDLResource res : this.Resources)
		{
			if(res.Key.equals(ResourceKey)) return res;
		}
		return null;
	}
	
	/**
	 * Stores the resource in the storage system
	 * 
	 * @throws Exception the exception
	 */
	public void StoreResources(EnvHintCollection Hints) throws Exception
	{
		for(AttachedJDLResource att : this.Resources)
		{
			if(att.TypeOfResource != ResourceType.InData) continue;
			switch(att.ResourceLocationType)
			{
				case CMSReference:
				{
					if(att.Value==null || att.Value.trim().length()==0) throw new WorkflowValidationException("CMS id not provided for "+att.ResourceLocationType+" provided attribute");
					att.StorageSystemID=att.Value;
					logger.debug("Stored CMSReference resource: key=" + att.Key + " ssid=" + att.StorageSystemID);
					break;
				}
				case Reference:
				{
					if(att.StorageSystemID==null || att.StorageSystemID.trim().length()==0) att.StorageSystemID = StorageSystem.Store(new URL(att.Value),Hints);
					logger.debug("Stored Reference resource: key=" + att.Key + " ref=" + att.Value +  " ssid=" + att.StorageSystemID);
					break;
				}
				case LocalFile:
				{
					if(att.StorageSystemID==null || att.StorageSystemID.trim().length()==0) att.StorageSystemID = StorageSystem.Store(att.Value,Hints);
					logger.debug("Stored Local resource: key=" + att.Key + " ref=" + att.Value + " ssid=" + att.StorageSystemID);
					break;
				}
				default: throw new WorkflowValidationException("Unrecognized resource type");
			}
		}
	}

}
