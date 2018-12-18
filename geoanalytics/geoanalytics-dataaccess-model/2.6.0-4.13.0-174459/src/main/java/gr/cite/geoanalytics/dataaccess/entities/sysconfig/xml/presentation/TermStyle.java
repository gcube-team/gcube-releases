package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.sun.xml.txw2.annotation.XmlElement;

@XmlRootElement(name = "term")
public class TermStyle 
{
	private String id;
	private String style;
	
	public String getId() 
	{
		return id;
	}
	
	@XmlAttribute
	public void setId(String id) 
	{
		this.id = id;
	}
	
	public String getStyle() 
	{
		return style;
	}
	
	@XmlElement
	public void setStyle(String style) 
	{
		this.style = style;
	}
	
	@Override
	 public boolean equals(Object other) 
	 {
		if (other == this) return true;
		if (other == null || other.getClass() != this.getClass()) return false;

		if(!id.equals(((TermStyle)other).getId())) return false;
       return true;
   }
  
   @Override
   public int hashCode() 
   {
       return id.hashCode();
   }
}
