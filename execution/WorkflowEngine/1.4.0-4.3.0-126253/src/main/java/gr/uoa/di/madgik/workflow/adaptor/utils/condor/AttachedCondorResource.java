package gr.uoa.di.madgik.workflow.adaptor.utils.condor;

public class AttachedCondorResource
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
		Executable,
		Submit,
		Command,
		IsDAG,
		Scope
	}
	public String Key=null;
	public String Value=null;
	public String StorageSystemID=null;
	public ResourceType TypeOfResource=ResourceType.Scope;
	public AttachedResourceType ResourceLocationType=AttachedResourceType.LocalFile;

	public AttachedCondorResource(String Key, String Value,ResourceType TypeOfResource)
	{
		this.Key=Key;
		this.Value=Value;
		this.TypeOfResource=TypeOfResource;
	}

	public AttachedCondorResource(String Key, String Value,ResourceType TypeOfResource,AttachedResourceType ResourceLocationType)
	{
		this.Key=Key;
		this.Value=Value;
		this.TypeOfResource=TypeOfResource;
		this.ResourceLocationType=ResourceLocationType;
	}
}
