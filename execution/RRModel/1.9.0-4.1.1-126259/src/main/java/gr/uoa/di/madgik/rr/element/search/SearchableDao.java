package gr.uoa.di.madgik.rr.element.search;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;

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

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;


@PersistenceCapable(table="SEARCHABLE", detachable="true")
@Queries(
		{@Query(
				name="exists", 
				language="javax.jdo.query.JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.PresentableDao WHERE this.ID == :id"
				),
		}
)
public class SearchableDao implements IDaoElement
{
	@PrimaryKey
	//@Column(name = "gcubeGUID")
	public String ID;
	
	//@Column(name = "gcubeTimestamp")
	private Long timestamp = Calendar.getInstance().getTimeInMillis();
	
	//@Column(name = "gcubeCollection")
	public String collection;
	
	//@Column(name = "gcubeLocator")
	public String locator;
	
	//@Column(name = "gcubeCapability")
	//@Persistent(defaultFetchGroup="true")
//	@Column(jdbcType = "BLOB")
//	@Persistent(defaultFetchGroup="true")
	
	
	//@Join
	//@javax.jdo.annotations.Element(column="CAPABILITIES")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	public Set<String> capabilities =new HashSet<String>();
	
	//@Column(name = "gcubeOrder")
	public Boolean order;
	
	//@Column(name = "gcubeField-ref")
	public String field;
	
	//@Column(name = "gcubeQueryExpression")
	public String expression;
	
	//@Column(name = "gcubeDatasourceScopes")
	//@Persistent(defaultFetchGroup="true")
//	@Column(jdbcType = "BLOB")
//	@Persistent(defaultFetchGroup="true")
	
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	public Set<String> datasourceScopes = new HashSet<String>();
	
	public String deepToString(){
		StringBuilder buf=new StringBuilder();
		buf.append("searchable ID "+ID+"\n");
		buf.append("searchable collection "+collection+"\n");
		buf.append("searchable locator "+locator+"\n");
		buf.append("searchable order "+order+"\n");
		buf.append("searchable field "+field+"\n");
		if(expression!=null) buf.append("searchable expression " + expression + "\n");
		if(capabilities!=null){
			for(String c : capabilities) buf.append("searchable capability "+c+"\n");
		}
		return buf.toString();
	}

	public String getField()
	{
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}
	
	public String getExpression()
	{
		return expression;
	}

	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	public Boolean isOrder()
	{
		return order;
	}
	public void setOrder(Boolean order)
	{
		this.order = order;
	}
	public String getLocator()
	{
		return locator;
	}
	public void setLocator(String locator)
	{
		this.locator = locator;
	}
	public Set<String> getCapabilities()
	{
		return capabilities;
	}
	public void setCapabilities(Set<String> capabilities)
	{
		this.capabilities = capabilities;
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
	public void setID(String iD)
	{
		ID = iD;
	}
	public void setTimestamp(Long timestamp)
	{
		this.timestamp = timestamp;
	}
	public String getCollection()
	{
		return collection;
	}
	public void setCollection(String collection)
	{
		this.collection = collection;
	}
	public Set<String> getDatasourceScopes()
	{
		return datasourceScopes;
	}
	public void setDatasourceScopes(Set<String> datasourceScopes)
	{
		this.datasourceScopes = datasourceScopes;
	}
	
	public void apply(IDaoElement target) throws ResourceRegistryException
	{
		if(!(target instanceof SearchableDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		this.setCollection(((SearchableDao)target).getCollection());
		this.setField(((SearchableDao)target).getField());
		this.setLocator(((SearchableDao)target).getLocator());
		this.setExpression(((SearchableDao)target).getExpression());
		this.setOrder(((SearchableDao)target).isOrder());
		this.setCapabilities(((SearchableDao)target).getCapabilities());
		this.setDatasourceScopes(((SearchableDao)target).getDatasourceScopes());
	}

	public void fromXML(Element element) throws ResourceRegistryException
	{
		try
		{
			if(!XMLUtils.AttributeExists(element, "id")) throw new ResourceRegistryException("id attribute not found in serialization");
			this.setID(XMLUtils.GetAttribute(element, "id"));
			this.setCollection(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "collection")));
			this.setField(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "field")));
			this.setLocator(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "locator")));
			this.setOrder(Boolean.parseBoolean(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "order"))));
			List<Element> psXML=XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(element, "capabilities"), "capability");
			for(Element item : psXML) 
				this.capabilities.add(StringEscapeUtils.unescapeXml(XMLUtils.GetChildText(item).trim()));
			this.setExpression(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "expression")));
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("could not complete deserialization",ex);
		}
	}

	public String toXML()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<searchable id=\""+this.getID()+"\">");
		buf.append("<collection>"+this.getCollection()+"</collection>");
		buf.append("<field>"+this.getField()+"</field>");
		buf.append("<locator>"+this.getLocator()+"</locator>");
		buf.append("<order>"+this.isOrder()+"</order>");
		buf.append("<capabilities>");
		for(String s : this.getCapabilities()) buf.append("<capability>"+ StringEscapeUtils.escapeXml(s) +"</capability>");
		buf.append("</capabilities>");
		if(this.getExpression() != null) buf.append("<expression>"+this.getExpression()+"</expression>");
		buf.append("</searchable>");
		return buf.toString();
	}
}
