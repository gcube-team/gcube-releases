package gr.uoa.di.madgik.rr.element.search;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
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

@PersistenceCapable(table="PRESENTABLE", detachable="true", identityType= IdentityType.APPLICATION)
@Queries(
		{@Query(
				name="exists", 
				language="javax.jdo.query.JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.PresentableDao WHERE this.ID == :id"
				),
		}
)
public class PresentableDao implements IDaoElement
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
	
	//@Column(name = "gcubeOrder")
	public Boolean order;
	
	//@Column(name = "gcubeField-ref")
	public String field;
	
	//@Column(name = "gcubeQueryExpression")
	public String expression;
	
	//@Column(name = "gcubePresentationInfo")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	public Set<String> presentationInfo = new HashSet<String>();
	
	//@Column(name = "gcubeDatasourceScopes")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	public Set<String> datasourceScopes = new HashSet<String>();

	public String deepToString(){
		StringBuilder buf=new StringBuilder();
		buf.append("presentable ID "+ID+"\n");
		buf.append("presentable collection "+collection+"\n");
		buf.append("presentable locator "+locator+"\n");
		buf.append("presentable order "+order+"\n");
		buf.append("presentable field "+field+"\n");
		if(expression != null) buf.append("presentable expression "+expression+"\n");
		if(presentationInfo != null) buf.append("presentable presentationInfo "+presentationInfo+"\n");
		buf.append("presentable datasourceScopes "+datasourceScopes+"\n");
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
	
	public Set<String> getPresentationInfo()
	{
		return presentationInfo;
	}

	public void setPresentationInfo(Set<String> presentationInfo)
	{
		this.presentationInfo = presentationInfo;
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
	
		if(!(target instanceof PresentableDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		this.setCollection(((PresentableDao)target).getCollection());
		this.setField(((PresentableDao)target).getField());
		this.setLocator(((PresentableDao)target).getLocator());
		this.setExpression(((PresentableDao)target).getExpression());
		this.setPresentationInfo(((PresentableDao)target).getPresentationInfo());
		this.setOrder(((PresentableDao)target).isOrder());
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
			this.setExpression(XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "expression")));
			//TODO replace with XML elements: <presentationInfo><presentatioInfoElement>...</presentationInfoElement>...</presentationInfo>
			String presInfoPayload = XMLUtils.GetChildText(XMLUtils.GetChildElementWithName(element, "presentationInfo"));
			Set<String> presInfo = new HashSet<String>();
			if(presInfoPayload != null)
			{
				String[] presInfoArray = presInfoPayload.split(" ");
				for(String pi : presInfoArray)
				{
					String trimmedPi = pi.trim();
					if(trimmedPi.isEmpty()) continue;
					presInfo.add(trimmedPi);
				}
			}
			this.setPresentationInfo(presInfo);
			
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("could not complete deserialization",ex);
		}
	}

	public String toXML()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<presentable id=\""+this.getID()+"\">");
		buf.append("<collection>"+this.getCollection()+"</collection>");
		buf.append("<field>"+this.getField()+"</field>");
		buf.append("<locator>"+this.getLocator()+"</locator>");
		buf.append("<order>"+this.isOrder()+"</order>");
		if(this.getExpression() != null) buf.append("<expression>"+this.getExpression()+"</expression>");
		if(this.getPresentationInfo() != null)
		{
			//TODO change when XML representation is updated
			//buf.append("<presentationInfo>"+this.getPresentationInfo()+"</presentationInfo>");
			StringBuilder presInfo = new StringBuilder();
			for(String pi : this.getPresentationInfo()) presInfo.append(pi + " ");
			buf.append("<presentationInfo>"+presInfo.toString().trim()+"</presentationInfo>");
		}
		buf.append("</presentable>");
		return buf.toString();
	}
}
