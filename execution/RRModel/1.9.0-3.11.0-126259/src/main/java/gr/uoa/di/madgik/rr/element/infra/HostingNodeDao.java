package gr.uoa.di.madgik.rr.element.infra;

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

@PersistenceCapable(table="HOSTINGNODE", detachable="true")
@Queries(
		{@Query(
				name="all", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.infra.HostingNodeDao"
				),
		@Query(
				name="exists", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.infra.HostingNodeDao WHERE this.ID == :id"
				),
		}
		
)
public class HostingNodeDao implements IDaoElement
{
	@PrimaryKey
	//@Column(name = "gcubeGUID")
	private String ID;
	
	//@Column(name = "gcubeTimestamp")
	private Long timestamp;
	
//	@Column(name = "gcubePairKey", jdbcType = "BLOB")
//	@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> pairKeys=new HashSet<String>();
	
//	@Column(name = "gcubePairValue", jdbcType = "BLOB")
//	@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> pairValues=new HashSet<String>();
	
//	@Column(name = "gcubeScope")
//	@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> scopes=new HashSet<String>();
	
	public Set<String> getScopes()
	{
		return scopes;
	}

	public void setScopes(Set<String> scopes)
	{
		this.scopes = scopes;
	}

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
		buf.append("Node ID : "+this.ID);
		if(this.pairKeys!=null){
			for(String s : this.pairKeys) if(s!=null) buf.append("Node Keys "+s+"\n");
		}
		if(this.pairValues!=null){
			for(String p : this.pairValues)if(p!=null) buf.append("Node Values "+p+"\n");
		}
		if(this.scopes!=null){
			for(String p : this.scopes)if(p!=null) buf.append("Node scope "+p+"\n");
		}
		return buf.toString();
	}

	public Set<String> getPairKeys()
	{
		return pairKeys;
	}
	public void setPairKeys(Set<String> pairKeys)
	{
		this.pairKeys = pairKeys;
	}

	public Set<String> getPairValues()
	{
		return pairValues;
	}
	public void setPairValues(Set<String> pairValues)
	{
		this.pairValues= pairValues;
	}

	public void apply(IDaoElement target) throws ResourceRegistryException
	{
		if(!(target instanceof HostingNodeDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		this.setID(((HostingNodeDao)target).getID());
		this.setScopes(((HostingNodeDao)target).getScopes());
		this.setPairKeys(((HostingNodeDao)target).getPairKeys());
		this.setPairValues(((HostingNodeDao)target).getPairValues());
		
	}
	
	public void fromXML(Element element) throws ResourceRegistryException
	{
		try
		{
			if(!XMLUtils.AttributeExists(element, "id")) throw new ResourceRegistryException("id attribute not found in serialization");
			this.setID(XMLUtils.GetAttribute(element, "id"));
			this.setTimestamp(Long.parseLong(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "timestamp"))));
			List<Element> pairKeysXML = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(element, "pairKeys"), "pairKey");
			for(Element pairKey : pairKeysXML) this.pairKeys.add(XMLUtils.GetChildText(pairKey));
			List<Element> pairValuesXML = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(element, "pairValues"), "pairValue");
			for(Element pairValue : pairValuesXML) this.pairValues.add(XMLUtils.GetChildText(pairValue));
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
		buf.append("<hostingNode id=\""+this.getID()+"\">");
		buf.append("<timestamp>"+new Long(this.getTimestamp()).toString()+"</timestamp>");
		buf.append("<pairKeys>");
		for(String pairKey : this.pairKeys)
		{
			buf.append("<pairKey>");
			buf.append(pairKey);
			buf.append("</pairKey>");
		}
		buf.append("</pairKeys>");
	
		buf.append("<pairValues>");
		for(String pairValue : this.pairValues)
		{
			buf.append("<pairValue>");
			buf.append(pairValue);
			buf.append("</pairValue>");
		}
		buf.append("</pairValues>");
		
		if(!this.getScopes().isEmpty())
		{
			buf.append("<scopes>");
			for(String s : this.getScopes())
				buf.append("<scope>"+s+"</scope>");
			buf.append("</scopes>");
		}
		buf.append("</hostingNode>");
		return buf.toString();
	}

}
