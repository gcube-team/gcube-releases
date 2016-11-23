package gr.uoa.di.madgik.workflow.plan;

import java.util.Set;

public class WorklfowElementGroup
{
	public enum GroupType
	{
		Sequential,
		Parallel
	}
	
	public GroupType Type=GroupType.Sequential;
	public String GroupName=null;
	public Set<String> Elements;
	public Set<String> SubGroups;
}
