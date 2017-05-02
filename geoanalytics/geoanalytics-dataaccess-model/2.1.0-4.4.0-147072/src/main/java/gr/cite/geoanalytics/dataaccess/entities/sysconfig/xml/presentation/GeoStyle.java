package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlSeeAlso(TermStyle.class)
public class GeoStyle 
{
	private List<TermStyle> termStyles = new ArrayList<TermStyle>();
	
	@XmlTransient private Map<String, TermStyle> lookup = new HashMap<String, TermStyle>();

	public List<TermStyle> getTermStyles() 
	{
		return termStyles;
	}

	@XmlElement
	public void setTermStyles(List<TermStyle> termStyles) 
	{
		this.termStyles = termStyles;
		
		lookup = new HashMap<String, TermStyle>();
		for(TermStyle ts : termStyles)
			lookup.put(ts.getId(), ts);
	}
	
	public TermStyle getTermStyle(String id)
	{
		return lookup.get(id);
	}
	
	public void addTermStyle(TermStyle ts)
	{
		if(lookup.containsKey(ts.getId())) removeTermStyle(ts.getId());
		lookup.put(ts.getId(), ts);
		termStyles.add(ts);
	}
	
	public void removeTermStyle(String id)
	{
		TermStyle ts = lookup.get(id);
		if(ts == null) return;
		
		termStyles.remove(ts);
		lookup.remove(ts);
	}
	
}
