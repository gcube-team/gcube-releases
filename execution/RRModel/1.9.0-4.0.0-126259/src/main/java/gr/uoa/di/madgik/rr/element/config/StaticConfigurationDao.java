package gr.uoa.di.madgik.rr.element.config;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;

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

@PersistenceCapable(table="STATICCONFIG", detachable="true")
@Queries(
		{@Query(
				name="exists", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.config.StaticConfigurationDao WHERE this.ID == :id"
				)
		}
)
public class StaticConfigurationDao implements IDaoElement
{
	@PrimaryKey
	//@Column(name = "gcubeGUID")
	public String ID;
	
	//@Column(name = "gcubeTimestamp")
	@Persistent(defaultFetchGroup="true")
	public Long timestamp;
	
	
	//@Column(name = "gcubePresentationInfoGroups")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> presentationInfoGroups=new HashSet<String>();
	
	//@Column(name = "gcubePresentationInfoKeyword")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> presentationInfoKeywords=new HashSet<String>();
	
	public String getID()
	{
		return ID;
	}

	public void setID(String id)
	{
		ID = id;
	}
	
	public Long getTimestamp()
	{
		if (timestamp ==  null)
			return 0l;
		return timestamp;
	}
	
	public void setTimestamp(Long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public String deepToString(){
		StringBuilder buf=new StringBuilder();
		buf.append("StaticConfiguration ID : "+this.ID);
		if(this.presentationInfoGroups!=null){
			for(String s : this.presentationInfoGroups) if(s!=null) buf.append("Presentation Info Groups "+s+"\n");
		}
		if(this.presentationInfoKeywords!=null){
			for(String p : this.presentationInfoKeywords)if(p!=null) buf.append("Presentation Info Keywords "+p+"\n");
		}
		return buf.toString();
	}

	public Set<String> getPresentationInfoGroups()
	{
		return presentationInfoGroups;
	}
	public void setPresentationInfoGroups(Set<String> presentationInfoGroups)
	{
		this.presentationInfoGroups = presentationInfoGroups;
	}

	public Set<String> getPresentationInfoKeywords()
	{
		return presentationInfoKeywords;
	}
	public void setPresentationInfoKeywords(Set<String> presentationInfoKeywords)
	{
		this.presentationInfoKeywords = presentationInfoKeywords;
	}

	public void apply(IDaoElement target) throws ResourceRegistryException
	{
		if(!(target instanceof StaticConfigurationDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		this.setID(((StaticConfigurationDao)target).getID());
		this.setPresentationInfoGroups(((StaticConfigurationDao)target).getPresentationInfoGroups());
		this.setPresentationInfoKeywords(((StaticConfigurationDao)target).getPresentationInfoKeywords());
		
	}
	
	public void fromXML(Element element) throws ResourceRegistryException
	{
		try
		{
			if(!XMLUtils.AttributeExists(element, "id")) throw new ResourceRegistryException("id attribute not found in serialization");
			this.setID(XMLUtils.GetAttribute(element, "id"));
			this.setTimestamp(Long.parseLong(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "timestamp"))));
			List<Element> presentationInfoGroupsXML = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(element, "presentationInfoGroups"), "presentationInfoGroup");
			for(Element presentationInfoGroup : presentationInfoGroupsXML) this.presentationInfoGroups.add(XMLUtils.GetChildText(presentationInfoGroup));
			List<Element> presentationInfoKeywordsXML = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(element, "presentationInfoKeywords"), "presentationInfoKeyword");
			for(Element presentationInfoKeyword : presentationInfoKeywordsXML) this.presentationInfoKeywords.add(XMLUtils.GetChildText(presentationInfoKeyword));
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("could not complete deserialization",ex);
		}
	}

	public String toXML()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<staticConfiguration id=\""+this.getID()+"\">");
		buf.append("<timestamp>"+new Long(this.getTimestamp()).toString()+"</timestamp>");
		buf.append("<presentationInfoGroups>");
		for(String presentationInfoGroup : this.presentationInfoGroups)
		{
			buf.append("<presentationInfoGroup>");
			buf.append(presentationInfoGroup);
			buf.append("</presentationInfoGroup>");
		}
		buf.append("</presentationInfoGroups>");
	
		buf.append("<presentationInfoKeywords>");
		for(String presentationInfoKeyword : this.presentationInfoKeywords)
		{
			buf.append("<presentationInfoKeyword>");
			buf.append(presentationInfoKeyword);
			buf.append("</presentationInfoKeyword>");
		}
		buf.append("</presentationInfoKeywords>");
		
		buf.append("</staticConfiguration>");
		return buf.toString();
	}
}
