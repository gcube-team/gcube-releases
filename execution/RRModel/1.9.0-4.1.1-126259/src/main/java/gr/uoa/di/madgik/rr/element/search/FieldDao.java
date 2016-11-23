package gr.uoa.di.madgik.rr.element.search;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Serialized;

import org.w3c.dom.Element;

@PersistenceCapable(table="FIELD", detachable="true", identityType= IdentityType.APPLICATION)
@Queries(
		{@Query(
				name="allFields", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.FieldDao"
				),
		@Query(
				name="exists", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.FieldDao WHERE this.ID == :id"
				),
		@Query(
				name="fieldName", 
				language="JDOQL", 
				value="SELECT this.name FROM gr.uoa.di.madgik.rr.element.search.FieldDao WHERE this.ID == :id"
				),
			
		@Query(
				name="presentableFieldsOfCollection", 
				language="JDOQL", 
				value=
					"SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.FieldDao WHERE" +
					" ( " + 
					"	SELECT s.field FROM gr.uoa.di.madgik.rr.element.search.PresentableDao s WHERE s.collection == :collection " +
					" ).contains(this.ID)" 
				),
		@Query(
				name="searchableFieldsOfCollection", 
				language="JDOQL", 
				value=
						"SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.FieldDao WHERE " + 
						"(" + 
						"	SELECT s.field FROM gr.uoa.di.madgik.rr.element.search.SearchableDao s WHERE s.collection == :collection" + 
						").contains(this.ID)"
				),
		@Query(
				name="fields", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.FieldDao WHERE colls.contains(this.ID) PARAMETERS Set colls import java.util.Set"
				),
		@Query(
				name="fieldsWithName", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.FieldDao WHERE this.name == :name"
				),		
		}
		
)
public class FieldDao implements IDaoElement
{
	@PrimaryKey
	//@Column(name = "gcubeGUID")
	private String ID;
	
	//@Column(name = "gcubeTimestamp")
	private Long timestamp=Calendar.getInstance().getTimeInMillis();
	
	//@Column(name = "gcubeName")
	private String name;
	
	//@Column(name = "gcubeDescription")
	private String description;
	
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> searchables=new HashSet<String>();
	
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> presentables=new HashSet<String>();
	
	public String deepToString(){
		StringBuilder buf=new StringBuilder();
		buf.append("ID "+ID+"\n");
		buf.append("Name "+name+"\n");
		buf.append("Description "+description+"\n");
		if(this.searchables!=null){
			for(String s : this.searchables) if(s!=null) buf.append("Searchable "+s+"\n");
		}
		if(this.presentables!=null){
			for(String p : this.presentables)if(p!=null) buf.append("Presentable "+p+"\n");
		}
		return buf.toString();
	}
	
	public String getID()
	{
		return ID;
	}
	public Long getTimestamp()
	{
		if (timestamp ==  null)
			return 0l;
		return timestamp;
	}
	public Set<String> getPresentables()
	{
		return presentables;
	}
	public void setPresentables(Set<String> presentables)
	{
		this.presentables = presentables;
	}
	public void setID(String iD)
	{
		ID = iD;
	}
	public void setTimestamp(Long timestamp)
	{
		this.timestamp = timestamp;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public Set<String> getSearchables()
	{
		return searchables;
	}
	public void setSearchables(Set<String> searchables)
	{
		this.searchables = searchables;
	}

	public void apply(IDaoElement target) throws ResourceRegistryException
	{
		if(!(target instanceof FieldDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		//if(this.isEqual(target,applyDetails)) return;
		this.setDescription(((FieldDao)target).getDescription());
		this.setName(((FieldDao)target).getName());
		this.setPresentables(((FieldDao)target).getPresentables());
		this.setSearchables(((FieldDao)target).getSearchables());
		
		for(String item : this.searchables)
		{
			if(!((FieldDao)target).getSearchables().contains(item)) this.getSearchables().remove(item);
			
		}
		for(String item : ((FieldDao)target).searchables)
		{
			if(!this.getSearchables().contains(item)) this.getSearchables().add(item);
		}
		
		for(String item : this.presentables)
		{
			if(!((FieldDao)target).getPresentables().contains(item)) this.getPresentables().remove(item);
			
		}
		for(String item : ((FieldDao)target).presentables)
		{
			if(!this.getPresentables().contains(item)) this.getPresentables().add(item);
		}

	}
	
	public void fromXML(Element element) throws ResourceRegistryException
	{
		try
		{
			if(!XMLUtils.AttributeExists(element, "id")) throw new ResourceRegistryException("id attribute not found in serialization");
			this.setID(XMLUtils.GetAttribute(element, "id"));
			this.setName(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "name")));
			this.setDescription(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "description")));
			List<Element> ssXML=XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(element, "searchables"), "searchable");
			for(Element item : ssXML) this.searchables.add(XMLUtils.GetChildText(item));
			List<Element> psXML=XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(element, "presentables"), "presentable");
			for(Element item : psXML) this.presentables.add(XMLUtils.GetChildText(item));
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("could not complete deserialization",ex);
		}
	}

	public String toXML()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<field id=\""+this.getID()+"\">");
		buf.append("<name>"+this.getName()+"</name>");
		if(description!=null) buf.append("<description>"+this.getDescription()+"</description>");
		buf.append("<searchables>");
		for(String s : this.getSearchables()) buf.append("<searchable>"+s+"</searchable>");
		buf.append("</searchables>");
		buf.append("<presentables>");
		for(String s : this.getPresentables()) buf.append("<presentable>"+s+"</presentable>");
		buf.append("</presentables>");
		buf.append("</field>");
		return buf.toString();
	}
	
}
