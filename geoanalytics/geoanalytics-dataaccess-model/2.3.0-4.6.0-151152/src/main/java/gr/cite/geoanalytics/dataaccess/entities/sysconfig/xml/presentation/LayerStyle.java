package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import gr.cite.geoanalytics.dataaccess.xml.CDATAAdapter;

@XmlRootElement
public class LayerStyle 
{
	private String name = null;
	private String style = null;
	
	public String getName() 
	{
		return name;
	}
	
	@XmlElement
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getStyle() 
	{
		return style;
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(value=CDATAAdapter.class)
	public void setStyle(String style) 
	{
		this.style = style;
	}
	
	@Override
	public boolean equals(Object other) 
	{
		if (other == this) return true;
		if (other == null || other.getClass() != this.getClass()) return false;

		if(!name.equals(((LayerStyle)other).getName())) return false;
		return true;
	}
 
	@Override
	public int hashCode() 
	{
		return name.hashCode();
	}
}
