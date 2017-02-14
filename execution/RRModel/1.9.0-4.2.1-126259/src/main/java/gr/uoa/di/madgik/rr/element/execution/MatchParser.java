package gr.uoa.di.madgik.rr.element.execution;

import java.util.Hashtable;

public class MatchParser
{
	private String pattern;
	public Hashtable<String,String> requirments=new Hashtable<String, String>();
	
	public MatchParser(String pattern)
	{
		this.pattern=pattern;
		this.parse();
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
