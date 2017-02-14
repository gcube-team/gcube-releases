package org.gcube.execution.workfloworchestrationlayerservice.utils;

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
	public AccessInfo AccessInfo;
	
	
	public String toString()
	{
		return Value; 
	}
}
