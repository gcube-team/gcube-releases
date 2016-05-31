package gr.uoa.di.madgik.environment.is.elements.matching;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class MatchParser
{
	private String pattern;
	public Hashtable<String,String> requirments=new Hashtable<String, String>();
	
	public MatchParser(String pattern)
	{
		this.pattern=pattern;
		this.parse();
	}
	
	public static String toRequirement(String key, String value)
	{
		return key + "==" + value;
	}
	
	public static String toRequirements(Map<String, String> reqs)
	{
		StringBuilder r = new StringBuilder();
		int i=0;
		for(Map.Entry<String, String> req : reqs.entrySet())
		{
			r.append(req.getKey()+"=="+req.getValue());
			if(++i<reqs.size()) r.append("&&");
		}
		return r.toString();
	}
	
	public static String toRequirements(Set<String> reqs)
	{
		StringBuilder r = new StringBuilder();
		int i=0;
		for(String req: reqs)
		{
			r.append(req);
			if(++i<reqs.size()) r.append("&&");
		}
		return r.toString();
	}
	
	private void parse()
	{
		if(this.pattern==null || this.pattern.trim().length()==0) return;
		String []pairs=this.pattern.split("&&");
		for(String pair : pairs)
		{
			String []elements=pair.split("==");
			if(elements.length!=2) continue;
			this.requirments.put(elements[0].trim(), elements[1].trim());
		}
	}

}
