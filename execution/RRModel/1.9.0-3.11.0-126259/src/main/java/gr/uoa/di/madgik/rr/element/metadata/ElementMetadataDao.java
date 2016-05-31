package gr.uoa.di.madgik.rr.element.metadata;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Serialized;

import org.w3c.dom.Element;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;

@PersistenceCapable(table="ELEMENTMETADATA", detachable="true")
@Queries(
		{@Query(
				name="all", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.metadata.ElementMetadataDao"
				),
		@Query(
				name="exists", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.metadata.ElementMetadataDao WHERE this.ID == :id"
				),
		@Query(
				name="metadataOfType", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.metadata.ElementMetadataDao where this.type == :type"
				),
		}
		
)
public class ElementMetadataDao implements IDaoElement 
{
	@PrimaryKey
	//@Column(name = "gcubeGUID")
	private String ID;
	
	//@Column(name = "gcubeType")
	private String type;
	
	//@Column(name = "gcubeTimestamp")
	private Long timestamp;
	
	//@Column(name = "gcubeMetadataTimestamp")
	private Long metadataTimestamp=Calendar.getInstance().getTimeInMillis();
	
	//@Column(name = "gcubePropertyKeyRef", jdbcType = "BLOB")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> propertyKeys = new HashSet<String>();
	
	//@Column(name = "gcubePropertyValueRef", jdbcType = "BLOB")
//	@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> propertyValues = new HashSet<String>();
	
	public static String FieldNamePropertyName = "FieldName";
	
	public String getID()
	{
		return ID;
	}

	public void setID(String iD)
	{
		ID = iD;
	}
	
	public String getType()
	{
		return type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public Long getTimestamp()
	{
		if (timestamp ==  null){
			if (metadataTimestamp == null){
				return 0l;
			} else {
				timestamp = metadataTimestamp;
				return timestamp;
			}
		}
		
		return timestamp;
	}
	
	public void setTimestamp(Long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public Long getMetadataTimestamp()
	{
		if (metadataTimestamp ==  null)
			return 0l;
		
		return metadataTimestamp;
	}
	
	public void setMetadataTimestamp(Long timestamp)
	{
		this.metadataTimestamp = timestamp;
	}
	
	public Set<String> getPropertyKeys()
	{
		return propertyKeys;
	}
	
	public void setPropertyKeys(Set<String> propertyKeys)
	{
		this.propertyKeys = propertyKeys;
	}
	
	public Set<String> getPropertyValues()
	{
		return this.propertyValues;
	}
	
	public void setPropertyValues(Set<String> propertyValues)
	{
		this.propertyValues = propertyValues;
	}
	
	public void apply(IDaoElement target) throws ResourceRegistryException
	{
		if(!(target instanceof ElementMetadataDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		this.setID(((ElementMetadataDao)target).getID());
		this.setType(((ElementMetadataDao)target).getType());
		this.setMetadataTimestamp(((ElementMetadataDao)target).getMetadataTimestamp());
		this.setPropertyKeys(((ElementMetadataDao)target).getPropertyKeys());
		this.setPropertyValues(((ElementMetadataDao)target).getPropertyValues());
		
	}
	
	public String deepToString(){
		StringBuilder buf=new StringBuilder();
		buf.append("ID "+ID+"\n");
		buf.append("Type "+type+"\n");
		buf.append("timestamp "+metadataTimestamp+"\n");
		for(String propertyKey : this.getPropertyKeys())
			buf.append("PropertyKey "+propertyKey+"\n");
		for(String propertyValue : this.getPropertyValues())
			buf.append("PropertyValue "+propertyValue+"\n");
		return buf.toString();
	}
	
	@Override
	public void fromXML(Element element) throws ResourceRegistryException
	{
		try
		{
			if(!XMLUtils.AttributeExists(element, "id")) throw new ResourceRegistryException("id attribute not found in serialization");
			this.setID(XMLUtils.GetAttribute(element, "id"));
			this.setType(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "type")));
			this.setMetadataTimestamp(Long.parseLong(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "timestamp"))));
			List<Element> pXML=XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(element, "propertyKeys"), "propertyKey");
			for(Element item : pXML) this.propertyKeys.add(XMLUtils.GetChildText(item));
			pXML=XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(element, "propertyValues"), "propertyValue");
			for(Element item : pXML) this.propertyValues.add(XMLUtils.GetChildText(item));
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("could not complete deserialization",ex);
		}
	}

	@Override
	public String toXML()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<elementMetadata id=\""+this.getID()+"\">");
		buf.append("<type>"+this.getType()+"</type>");
		buf.append("<timestamp>" + this.getMetadataTimestamp() +"</timestamp>");
		buf.append("<propertyKeys>");
		for(String propertyKey : this.getPropertyKeys())
			buf.append("<propertyKey>" + propertyKey + "</propertyKey>");
		buf.append("</propertyKeys>");
		buf.append("<propertyValues>");
		for(String propertyValue : this.getPropertyValues())
			buf.append("<propertyValue>" + propertyValue + "</propertyValue>");
		buf.append("</propertyValues>");
		
		buf.append("</elementMetadata>");
		return buf.toString();
	}

}
