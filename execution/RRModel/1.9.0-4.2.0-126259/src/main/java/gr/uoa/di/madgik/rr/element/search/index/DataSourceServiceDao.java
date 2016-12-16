package gr.uoa.di.madgik.rr.element.search.index;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Serialized;

import org.w3c.dom.Element;

@PersistenceCapable(table="DATASOURCESERVICE", detachable="true")
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@Queries(
		{@Query(
				name="exists", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.index.DataSourceServiceDao WHERE this.ID == :id"
				),
		@Query(
				name="all", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.index.DataSourceServiceDao"
				),
		@Query(
				name="getType", 
				language="JDOQL", 
				value="SELECT this.type FROM gr.uoa.di.madgik.rr.element.search.index.DataSourceServiceDao WHERE this.ID == :id"
				),
				
		}
		
)
public abstract class DataSourceServiceDao implements IDaoElement
{	
	@PrimaryKey
	//@Column(name = "gcubeGUID")
	private String ID;
	
	//@Column(name = "gcubeTimestamp")
	private Long timestamp=Calendar.getInstance().getTimeInMillis();
	
	//@Column(name = "gcubeEndpoint")
	private String endpoint;
	
	//@Column(name = "gcubeHostingNodeRef")
	private String hostingNode;
	
	//@Column(name = "gcubeFunctionalityRef")
	private String functionality;
	
	//@Column(name = "gcubeSourceType")
	private String type;
	
	//@Column(name = "gcubeDatasourceRef")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> dataSources=new HashSet<String>();
	
	//@Column(name = "gcubeScope")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> scopes=new HashSet<String>();
	
	public String getID()
	{
		return ID;
	}

	public void setID(String iD)
	{
		ID = iD;
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
	
	public String getType()
	{
		return this.type;
	}
	
	public String getFunctionality()
	{
		return this.functionality;
	}
	
	public void setFunctionality(String functionality)
	{
		this.functionality = functionality;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public Set<String> getScopes()
	{
		return scopes;
	}

	public void setScopes(Set<String> scopes)
	{
		this.scopes = scopes;
	}

	public String getHostingNode()
	{
		return hostingNode;
	}

	public void setHostingNode(String hostingNode)
	{
		this.hostingNode = hostingNode;
	}
	
	public String getEndpoint()
	{
		return this.endpoint;
	}
	
	public void setEndpoint(String endpoint)
	{
		this.endpoint=endpoint;
	}
	
	public Set<String> getDataSources()
	{
		return this.dataSources;
	}
	
	public void setDataSources(Set<String> dataSources)
	{
		this.dataSources = dataSources;
	}
	
	public void apply(IDaoElement target) throws ResourceRegistryException
	{
		if(!(target instanceof DataSourceServiceDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		this.setID(((DataSourceServiceDao)target).getID());
		this.setEndpoint(((DataSourceServiceDao)target).getEndpoint());
		this.setHostingNode(((DataSourceServiceDao)target).getHostingNode());
		this.setType(((DataSourceServiceDao)target).getType());
		this.setFunctionality(((DataSourceServiceDao)target).getFunctionality());
		this.getDataSources().addAll(((DataSourceServiceDao)target).getDataSources());
		this.setScopes(((DataSourceServiceDao)target).getScopes());
	}
	
	public void fromXML(Element element) throws ResourceRegistryException
	{
		try
		{
			if(!XMLUtils.AttributeExists(element, "id")) throw new ResourceRegistryException("id attribute not found in serialization");
			this.setID(XMLUtils.GetAttribute(element, "id"));
			this.setTimestamp(Long.parseLong(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "timestamp"))));
			this.setEndpoint(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "endpoint")));
			this.setHostingNode(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "hostingNode")));
			this.setFunctionality(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "functionality")));
			this.setType(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "type")));
			List<Element> dsXML=XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(element, "datasources"), "datasource");
			for(Element item : dsXML) this.dataSources.add(XMLUtils.GetChildText(item));
			Element scopesXML = XMLUtils.GetChildElementWithName(element, "scopes");
			if(scopesXML!=null)
			{
				List<Element> sXML = XMLUtils.GetChildElementsWithName(scopesXML, "scope");
				for(Element item : sXML) this.scopes.add(XMLUtils.GetChildText(item));
			}
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("could not complete deserialization",ex);
		}
	}

	public String toXML()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<datasourceService id=\""+this.getID()+"\">");
		buf.append("<timestamp>"+new Long(this.getTimestamp()).toString()+"</timestamp>");
		buf.append("<endpoint>"+this.getEndpoint()+"</endpoint>");
		buf.append("<hostingNode>"+this.getHostingNode()+"</hostingNode>");
		buf.append("<functionality>"+this.getFunctionality()+"</functionality>");
		buf.append("<type>"+this.getType()+"</type>");
		buf.append("<datasources>");
		for(String ds : this.getDataSources())
			buf.append("<datasource>"+ds+"</datasource>");
		buf.append("</datasources>");
		if(!this.getScopes().isEmpty())
		{
			buf.append("<scopes>");
			for(String s : this.getScopes())
				buf.append("<scope>"+s+"</scope>");
			buf.append("</scopes>");
		}
		buf.append("</datasourceService>");
		return buf.toString();
	}

	@Override
	public String toString()
	{
		return this.ID;
	}
}
