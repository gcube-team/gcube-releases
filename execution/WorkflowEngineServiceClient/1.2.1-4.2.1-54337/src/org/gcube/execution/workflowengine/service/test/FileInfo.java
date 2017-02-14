package org.gcube.execution.workflowengine.service.test;

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
}
