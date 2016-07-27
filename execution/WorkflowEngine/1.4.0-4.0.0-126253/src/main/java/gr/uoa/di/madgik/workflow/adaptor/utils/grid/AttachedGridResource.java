package gr.uoa.di.madgik.workflow.adaptor.utils.grid;

import gr.uoa.di.madgik.workflow.adaptor.WorkflowGridAdaptor;

/**
 * The Class AttachedGridResource holds information on a resource that is needed by the {@link WorkflowGridAdaptor}
 * that requires this information. The resources that can be attached to the adaptor are of specific types and some 
 * restrictions apply to those that are considered mandatory or optional as well as their cardinality. These are described
 * in the resources container, {@link AdaptorGridResources}
 * 
 * @author gpapanikos
 */
public class AttachedGridResource
{
	public enum AttachedResourceType
	{
		LocalFile,
		Reference,
		CMSReference
	}
	
	/**
	 * The type of resource that is provided
	 */
	public enum ResourceType
	{
		
		/** Workflow Output */
		OutData,
		
		/** Workflow Input */
		InData,
		
		/** The JDL file that describes the job */
		JDL,
		
		/** Configuration to override the default one */
		Config,
		
		/** The user proxy certificate */
		UserProxy,
		Scope
	}
	
	public static class AccessInfo
	{
		public String userId;
		public String password;
		public int port = -1;
	}
	
	/** The Key by which this resource is identified. This is the name 
	 * that will be used for the resource once transfered to the Grid UI node */
	public String Key=null;
	
	/** The location of the resource in the node that the resource is submitted */
	public String Value=null;
	
	public AccessInfo accessInfo = new AccessInfo();
	
	/** The ID of the resource once it is submitted to the  */
	public String StorageSystemID=null;
	
	/** The Type of resource. */
	public ResourceType TypeOfResource=ResourceType.InData;
	
	public AttachedResourceType ResourceLocationType=AttachedResourceType.LocalFile;

	
	/**
	 * Instantiates a new attached grid resource.
	 * 
	 * @param Key the key
	 * @param Value the value
	 */
	public AttachedGridResource(String Key, String Value)
	{
		this.Key=Key;
		this.Value=Value;
	}
	
	/**
	 * Instantiates a new attached grid resource.
	 * 
	 * @param Key the key
	 * @param Value the value
	 * @param TypeOfResource the type of resource
	 */
	public AttachedGridResource(String Key, String Value,ResourceType TypeOfResource)
	{
		this.Key=Key;
		this.Value=Value;
		this.TypeOfResource=TypeOfResource;
	}

	public AttachedGridResource(String Key, String Value,ResourceType TypeOfResource,AttachedResourceType ResourceLocationType)
	{
		this.Key=Key;
		this.Value=Value;
		this.TypeOfResource=TypeOfResource;
		this.ResourceLocationType=ResourceLocationType;
	}
}
