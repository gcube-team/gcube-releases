package gr.uoa.di.madgik.workflow.adaptor.utils.hadoop;

public class HadoopInOutDirectoryInfo
{
	public enum OutStoreMode 
	{
		StorageSystem,
		Url
	}
	
	public static class AccessInfo
	{
		public String userId;
		public String password;
		public int port = -1;
	}
	
	public boolean CleanUp;
	public String Directory;
	public OutStoreMode OutputStoreMode = OutStoreMode.StorageSystem;
	public String OutputStoreLocation;
	public AccessInfo accessInfo = new AccessInfo();
}
