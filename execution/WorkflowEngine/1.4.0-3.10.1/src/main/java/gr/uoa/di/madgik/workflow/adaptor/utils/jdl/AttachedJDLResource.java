package gr.uoa.di.madgik.workflow.adaptor.utils.jdl;

import gr.uoa.di.madgik.workflow.adaptor.WorkflowJDLAdaptor;

/**
 * The Class AttachedJDLResource holds information on a resource that is needed by the {@link WorkflowJDLAdaptor}
 * that requires this information.
 * ok
 * @author gpapanikos
 */
public class AttachedJDLResource
{
	public enum AttachedResourceType
	{
		LocalFile,
		Reference,
		CMSReference
	}
	
	public enum ResourceType
	{
		InData,
		OutData
	}
	
	public static class AccessInfo
	{
		public String userId;
		public String password;
		public int port = -1;
	}
	
	/** The Key by which this resource is identified. This is the base of the name 
	 * that will be used for the resource once transfered to the Grid UI node as
	 * describe by the {@link AttachedJDLResource#GetNameToUse()}*/
	public String Key=null;
	
	/** The location of the resource in the node that the resource is submitted */
	public String Value=null;
	
	public AccessInfo accessInfo = new AccessInfo();
	
	public AttachedResourceType ResourceLocationType=AttachedResourceType.LocalFile;
	
	/** The Type of resource. */
	public ResourceType TypeOfResource=ResourceType.InData;
	
	/** The ID of the resource once it is submitted to the  */
	public String StorageSystemID=null;
	
//	/** Flag indicating how this resource will be be used as described 
//	 * by the {@link AttachedJDLResource#GetNameToUse()}*/
//	public boolean UseFullPath=false;
	
	/**
	 * Instantiates a new attached jdl resource.
	 * 
	 * @param Key the key
	 * @param Value the value
	 */
	public AttachedJDLResource(String Key, ResourceType ResourceType, String Value,AttachedResourceType ResourceLocationType)
	{
		this.Key=Key;
		this.TypeOfResource = ResourceType;
		this.Value=Value;
		this.ResourceLocationType=ResourceLocationType;
		if(this.TypeOfResource==null) this.TypeOfResource=ResourceType.InData;
		if(this.ResourceLocationType==null) this.ResourceLocationType=ResourceLocationType.CMSReference;
	}
//	/**
//	 * Instantiates a new attached jdl resource.
//	 * 
//	 * @param Key the key
//	 * @param Value the value
//	 * @param UseFullPath whether the full path should be used
//	 */
//	public AttachedJDLResource(String Key, String Value,boolean UseFullPath)
//	{
//		this.Key=Key;
//		this.Value=Value;
//		this.UseFullPath=UseFullPath;
//	}
	
//	/**
//	 * Gets the name to use for this resource. If {@link AttachedJDLResource#UseFullPath} is set,
//	 * then the content of the {@link AttachedJDLResource#Key} is used. Otherwise any path
//	 * info on the key content is omitted and only the last part is used
//	 * 
//	 * @return the name this resource should be named
//	 */
//	public String GetNameToUse()
//	{
//		if(this.UseFullPath) return Key;
//		else return new File(Key).getName();
//	}
}
