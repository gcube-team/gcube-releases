package gr.uoa.di.madgik.rr.element.execution;

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

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;

import org.w3c.dom.Element;

@PersistenceCapable(table="EXECUTIONSERVICE", detachable="true")
@Queries(
		{@Query(
				name="all", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.execution.ExecutionServiceDao"
				),
		@Query(
				name="exists", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.execution.ExecutionServiceDao WHERE this.ID == :id"
				),
		}
		
)
public class ExecutionServiceDao implements IDaoElement
{
	@PrimaryKey
	//@Column(name = "gcubeGUID")
	public String ID;
	
	//@Column(name = "gcubeTimestamp")
	public Long timestamp=Calendar.getInstance().getTimeInMillis();
	
	//@Column(name = "gcubeEndpointHost")
	public String endpoint;
	
	//@Column(name = "gcubeFunctionality-ref")
	public String functionality;
	
	//@Column(name = "gcubeHostingNode-ref")
	public String hostingNode;
	
	//@Column(name = "gcubeScope")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	public Set<String> scopes=new HashSet<String>();

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
	
	public String getFunctionality()
	{
		return functionality;
	}

	public void setFunctionality(String functionality)
	{
		this.functionality = functionality;
	}
	
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
	
	public String getEndpoint()
	{
		return this.endpoint;
	}
	
	public void setEndpoint(String endpoint)
	{
		this.endpoint=endpoint;
	}
	
	public void apply(IDaoElement target) throws ResourceRegistryException
	{
		if(!(target instanceof ExecutionServiceDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		this.setID(((ExecutionServiceDao)target).getID());
		this.setFunctionality(((ExecutionServiceDao)target).getFunctionality());
		this.setHostingNode(((ExecutionServiceDao)target).getHostingNode());
		this.setEndpoint(((ExecutionServiceDao)target).getEndpoint());
		this.setScopes(((ExecutionServiceDao)target).getScopes());
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
		buf.append("<executionService id=\""+this.getID()+"\">");
		buf.append("<timestamp>"+new Long(this.getTimestamp()).toString()+"</timestamp>");
		buf.append("<endpoint>"+this.getEndpoint()+"</endpoint>");
		buf.append("<hostingNode>"+this.getHostingNode()+"</hostingNode>");
		buf.append("<functionality>"+this.getFunctionality()+"</functionality>");
		if(!this.getScopes().isEmpty())
		{
			buf.append("<scopes>");
			for(String s : this.getScopes())
				buf.append("<scope>"+s+"</scope>");
			buf.append("</scopes>");
		}
		buf.append("</executionService>");
		return buf.toString();
	}
	
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("ExecutionService ID:"+this.ID+"\n");
		buf.append("Endpoint : "+this.endpoint+"\n");
		buf.append("Hosting Node: "+this.hostingNode+"\n");
		return buf.toString();
	}
}
