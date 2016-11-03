package gr.uoa.di.madgik.workflow.adaptor.utils.hadoop;

public class AttachedHadoopResource implements Comparable<AttachedHadoopResource>
{
	public enum AttachedResourceType
	{
		LocalFile,
		Reference,
		CMSReference
	}
	
	public enum ResourceType
	{
		Jar,
		MainClass,
		Argument,
		Configuration,
		Property,
		File,
		Lib,
		Archive,
		Input,
		Output,
		Scope
	}
	
	public static class AccessInfo
	{
		public String userId;
		public String password;
		public int port = -1;
	}
	
	public String Key=null;
	public String Value=null;
	public AccessInfo accessInfo = new AccessInfo();
	public String StorageSystemID=null;
	public ResourceType TypeOfResource=ResourceType.Jar;
	public boolean IsHDFSPresent=false;
	public int Order=0;
	public boolean CleanUp=true;
	public AttachedResourceType ResourceLocationType=AttachedResourceType.LocalFile;
	
	public AttachedHadoopResource() {}
	
	public AttachedHadoopResource(String Key, String Value, ResourceType Type)
	{
		this.Key=Key;
		this.Value=Value;
		this.TypeOfResource=Type;
	}
	
	public int compareTo(AttachedHadoopResource o)
	{
		return new Integer(this.Order).compareTo(o.Order);
	}
	
	public static AttachedHadoopResource NewJarResource(String Key, String Value, boolean IsHDFSPresent)
	{
		AttachedHadoopResource res=new AttachedHadoopResource();
		res.Key=Key;
		res.IsHDFSPresent=IsHDFSPresent;
		res.TypeOfResource=ResourceType.Jar;
		res.Value=Value;
		return res;
	}
	
	public static AttachedHadoopResource NewConfigurationResource(String Key, String Value, boolean IsHDFSPresent)
	{
		AttachedHadoopResource res=new AttachedHadoopResource();
		res.Key=Key;
		res.IsHDFSPresent=IsHDFSPresent;
		res.TypeOfResource=ResourceType.Configuration;
		res.Value=Value;
		return res;
	}
	
	public static AttachedHadoopResource NewLibResource(String Key, String Value, boolean IsHDFSPresent)
	{
		AttachedHadoopResource res=new AttachedHadoopResource();
		res.Key=Key;
		res.IsHDFSPresent=IsHDFSPresent;
		res.TypeOfResource=ResourceType.Lib;
		res.Value=Value;
		return res;
	}
	
	public static AttachedHadoopResource NewArchiveResource(String Key, String Value, boolean IsHDFSPresent)
	{
		AttachedHadoopResource res=new AttachedHadoopResource();
		res.Key=Key;
		res.IsHDFSPresent=IsHDFSPresent;
		res.TypeOfResource=ResourceType.Archive;
		res.Value=Value;
		return res;
	}
	
	public static AttachedHadoopResource NewFileResource(String Key, String Value, boolean IsHDFSPresent)
	{
		AttachedHadoopResource res=new AttachedHadoopResource();
		res.Key=Key;
		res.IsHDFSPresent=IsHDFSPresent;
		res.TypeOfResource=ResourceType.File;
		res.Value=Value;
		return res;
	}
	
	public static AttachedHadoopResource NewArgumentResource(int Order, String Value)
	{
		AttachedHadoopResource res=new AttachedHadoopResource();
		res.TypeOfResource=ResourceType.Argument;
		res.Key=Value;
		res.Value=Value;
		res.Order=Order;
		return res;
	}
	
	public static AttachedHadoopResource NewMainClassResource(String Value)
	{
		AttachedHadoopResource res=new AttachedHadoopResource();
		res.TypeOfResource=ResourceType.MainClass;
		res.Value=Value;
		res.Key=Value;
		return res;
	}
	
	public static AttachedHadoopResource NewPropertyResource(String Value)
	{
		AttachedHadoopResource res=new AttachedHadoopResource();
		res.TypeOfResource=ResourceType.Property;
		res.Value=Value;
		res.Key=Value;
		return res;
	}
	
	public static AttachedHadoopResource NewInputResource(String Key, String Value, boolean CleanUp)
	{
		AttachedHadoopResource res=new AttachedHadoopResource();
		res.Key=Key;
		res.CleanUp=CleanUp;
		res.TypeOfResource=ResourceType.Input;
		res.Value=Value;
		return res;
	}
	
	public static AttachedHadoopResource NewOutputResource(String Value, boolean CleanUp)
	{
		AttachedHadoopResource res=new AttachedHadoopResource();
		res.Key=Value;
		res.CleanUp=CleanUp;
		res.TypeOfResource=ResourceType.Output;
		res.Value=Value;
		return res;
	}
}
