package gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AttributeMappingConfig
{
	private String attributeName = null;
	private String attributeValue = null;
	private String attributeType = null;
	private String termId = null;
	private String layerTermId = null;
	private Boolean presentable = null;
	private Boolean mapValue = null;

	public AttributeMappingConfig() { }
	
	public AttributeMappingConfig(AttributeMappingConfig other)
	{
		this.attributeName = other.attributeName;
		this.attributeValue = other.attributeValue;
		this.attributeType = other.attributeType;
		this.termId = other.termId;
		this.layerTermId = other.layerTermId;
		this.presentable = other.presentable;
		this.mapValue = other.mapValue;
	}

	public String getAttributeName()
	{
		return attributeName;
	}

	@XmlElement
	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}

	public String getAttributeValue()
	{
		return attributeValue;
	}

	@XmlElement
	public void setAttributeValue(String attributeValue)
	{
		this.attributeValue = attributeValue;
	}
	
	public String getAttributeType()
	{
		return attributeType;
	}

	@XmlElement
	public void setAttributeType(String attributeType)
	{
		this.attributeType = attributeType;
	}
	
	public String getTermId()
	{
		return termId;
	}

	@XmlElement
	public void setTermId(String termId)
	{
		this.termId = termId;
	}
	
	public String getLayerTermId()
	{
		return layerTermId;
	}

	@XmlElement
	public void setLayerTermId(String layerTermId)
	{
		this.layerTermId = layerTermId;
	}
	
	public Boolean isPresentable()
	{
		return presentable;
	}
	
	@XmlElement(required = false)
	public void setPresentable(Boolean presentable)
	{
		this.presentable = presentable;
	}
	
	public Boolean isMapValue()
	{
		return mapValue;
	}
	
	@XmlElement(required = false)
	public void setMapValue(Boolean mapValue)
	{
		this.mapValue = mapValue;
	}
	
	 @Override
	 public boolean equals(Object other) 
	 {
		 if (other == this) return true;
		 if (other == null || other.getClass() != this.getClass()) return false;

        if(!attributeName.equals(((AttributeMappingConfig)other).getAttributeName())) return false;
        if(attributeValue == null && ((AttributeMappingConfig)other).getAttributeValue() != null) return false;
        if(attributeValue == null && ((AttributeMappingConfig)other).getAttributeValue() == null)
        {
        	if(!layerTermId.equals(((AttributeMappingConfig)other).getLayerTermId())) return false;
        		return true;
        }
        if(!attributeValue.equals(((AttributeMappingConfig)other).getAttributeValue())) return false;
        if(!layerTermId.equals(((AttributeMappingConfig)other).getLayerTermId())) return false;
		return true;
    }
   
    @Override
    public int hashCode() 
    {
        return termId.hashCode();
    }
}
