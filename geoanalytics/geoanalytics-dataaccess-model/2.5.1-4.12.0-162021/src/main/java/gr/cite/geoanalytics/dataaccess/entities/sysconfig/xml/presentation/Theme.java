package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.presentation;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.sun.xml.txw2.annotation.XmlElement;

@XmlRootElement
@XmlSeeAlso(GeoStyle.class)
public class Theme 
{
	private String title;
	private GeoStyle geoStyle;
	
	public String getTitle() 
	{
		return title;
	}
	
	@XmlElement
	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	public GeoStyle getGeoStyle() 
	{
		return geoStyle;
	}
	
	@XmlElement
	public void setGeoStyle(GeoStyle geoStyle) 
	{
		this.geoStyle = geoStyle;
	}
	
	@Override
	 public boolean equals(Object other) 
	 {
		if (other == this) return true;
		if (other == null || other.getClass() != this.getClass()) return false;

		if(!title.equals(((Theme)other).getTitle())) return false;
		return true;
	 }
 
	@Override
	public int hashCode() 
	{
		return title.hashCode();
	}
}
