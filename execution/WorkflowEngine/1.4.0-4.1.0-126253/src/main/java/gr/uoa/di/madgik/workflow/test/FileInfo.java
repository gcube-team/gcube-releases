package gr.uoa.di.madgik.workflow.test;

public class FileInfo
{
	public enum LocationType
	{
		local,
		ss,
		url
	}
	public LocationType TypeOfLocation=LocationType.local;
	public String Value;
}
