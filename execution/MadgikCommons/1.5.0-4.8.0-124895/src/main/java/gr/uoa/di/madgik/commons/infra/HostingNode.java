package gr.uoa.di.madgik.commons.infra;

import java.util.HashMap;
import java.util.Map;

public class HostingNode 
{
	public static String HostnameProperty="hostname";
	public static String HostnameFullProperty="hn.hostname";
	public static String PortProperty="hn.port";
	public static String LoadOneHourProperty="hn.load.one_hour";
	public static String LoadOneWeekProperty="hn.load.one_week";
	public static String LoadFiveMinutesProperty="hn.load.five_min";
	public static String LoadFifteenMinutesProperty="hn.load.fifteen_min";
	public static String DiskSizeProperty="hn.disk.size";
	public static String PhysicalMemorySizeProperty="hn.memory.physical.size";
	public static String PhysicalMemoryAvailableProperty="hn.memory.physical.available";
	public static String VirtualMemorySizeProperty="hn.memory.virtual.size";
	public static String VirtualMemoryAvailableProperty="hn.memory.virtual.available";
	public static String ProcessorCountProperty="hn.processor.count";
	public static String ProcessorTotalBogomipsProperty="hn.processor.total_bogomips";
	public static String ProcessorTotalClockSpeedProperty="hn.processor.total_clockspeed";
	public static String DomainProperty="hn.domain";
	
	protected String id = null;
	protected Map<String, String> pairs = new HashMap<String, String>();
	protected boolean isLocal = false;
	
	protected HostingNode()
	{
		
	}
	
	public HostingNode(String id , Map<String, String> pairs)
	{
		this.id = id;
		this.pairs.putAll(pairs);
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getPropertyByName(String propertyName)
	{
		return pairs.get(propertyName);
	}
	
	public Map<String, String> getProperties()
	{
		return new HashMap<String, String>(pairs);
	}
	
	public void addProperty(String name , String value)
	{
		pairs.put(name, value);
	}
	
	public void markLocal()
	{
		isLocal = true;
	}
	
	public boolean isLocal()
	{
		return isLocal;
	}
	
	public String toXML()
	{
		StringBuilder b = new StringBuilder();
		b.append("<hostingNode id=\"" + id + "\">\n");
		b.append(" <isLocal>" + isLocal + "</isLocal>\n");
		for(Map.Entry<String, String> e : pairs.entrySet())
		{
			b.append(" <pair>\n");	
			b.append("  <key>" + e.getKey() + "</key>\n");
			b.append("  <value>" + e.getValue() + "</value>\n");
			b.append(" </pair>\n");
		}
		b.append("</hostingNode>\n");
		return b.toString();
	}
}
