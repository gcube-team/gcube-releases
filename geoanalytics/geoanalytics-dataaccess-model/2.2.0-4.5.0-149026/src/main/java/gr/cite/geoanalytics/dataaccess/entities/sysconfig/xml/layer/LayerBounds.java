package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LayerBounds
{
	private double minX;
	private double minY;
	private double maxX;
	private double maxY;
	
	public LayerBounds() { }
	
	public LayerBounds(LayerBounds other)
	{
		this.minX = other.minX;
		this.minY = other.minY;
		this.maxX = other.maxX;
		this.maxY = other.maxY;
	}
	
	public double getMinX()
	{
		return minX;
	}
	
	@XmlElement
	public void setMinX(double minX)
	{
		this.minX = minX;
	}
	
	public double getMinY()
	{
		return minY;
	}
	
	@XmlElement
	public void setMinY(double minY)
	{
		this.minY = minY;
	}
	
	public double getMaxX()
	{
		return maxX;
	}
	
	@XmlElement
	public void setMaxX(double maxX)
	{
		this.maxX = maxX;
	}
	
	public double getMaxY()
	{
		return maxY;
	}
	
	@XmlElement
	public void setMaxY(double maxY)
	{
		this.maxY = maxY;
	}
	
	public void mergeWith(LayerBounds b)
	{
		if(b.getMinX() < minX) minX = b.getMinX();
		if(b.getMinY() < minY) minY = b.getMinY();
		if(b.getMaxX() > maxX) maxX = b.getMaxX();
		if(b.getMaxY() > maxY) maxY = b.getMaxY();
	}
	
	@Override
	public String toString() {
		return "minX: " + minX + " minY: " + minY + " maxX: " + maxX + " minY" + minY + " maxY" + maxY;
	}
}
