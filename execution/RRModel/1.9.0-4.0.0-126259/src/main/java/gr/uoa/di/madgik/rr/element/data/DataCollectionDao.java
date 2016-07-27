package gr.uoa.di.madgik.rr.element.data;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Serialized;

import org.w3c.dom.Element;

import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;

@PersistenceCapable(table="DATACOLLECTION", detachable="true")
@Queries(
		{@Query(
				name="allCollections", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.data.DataCollectionDao"
				),
		@Query(
				name="exists", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.data.DataCollectionDao WHERE this.ID == :id"
				),
		}
		
)
public class DataCollectionDao implements IDaoElement
{
	@PrimaryKey
	//@Column(name = "gcubeGUID")
	public String ID;
	
	//@Column(name = "gcubeTimestamp")
	public Long timestamp=Calendar.getInstance().getTimeInMillis();
	
	//@Column(name = "gcubeName")
	public String name;
	
	//@Column(name = "gcubeDescription")
	public String description;
	
	//@Column(name = "gcubeCollectionType")
	public String collectionType;
	
	//@Column(name = "gcubeCreationTime")
	public String creationTime=Long.toString(new Date().getTime());
	
	//@Column(name = "gcubeScope")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	public Set<String> scopes=new HashSet<String>();

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

	public String getCreationTime()
	{
		return creationTime;
	}

	public void setCreationTime(String creationTime)
	{
		this.creationTime = creationTime;
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
	
	public String getCollectionType()
	{
		return collectionType;
	}
	
	public void setCollectionType(String collectionType)
	{
		this.collectionType = collectionType;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public Set<String> getScopes()
	{
		return scopes;
	}

	public void setScopes(Set<String> scopes)
	{
		this.scopes = scopes;
	}

	public void apply(IDaoElement target) throws ResourceRegistryException
	{
		if(!(target instanceof DataCollectionDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		this.setCreationTime(((DataCollectionDao)target).getCreationTime());
		this.setDescription(((DataCollectionDao)target).getDescription());
		this.setCollectionType(((DataCollectionDao)target).getCollectionType());
		this.setID(((DataCollectionDao)target).getID());
		this.setName(((DataCollectionDao)target).getName());
		this.setScopes(((DataCollectionDao)target).getScopes());
	}
	
	public void fromXML(Element element) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("This element does not support serialization operation");
	}

	public String toXML() throws ResourceRegistryException
	{
		throw new ResourceRegistryException("This element does not support serialization operation");
	}
	
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("DataCollection ID : "+this.ID);
		buf.append("DataCollection Name : "+this.name);
		buf.append("DataCollection collectionType : "+this.collectionType);
		buf.append("DataCollection Descrition : "+this.description);
		buf.append("DataCollection Creation Time : "+this.creationTime);
		for(String s : this.scopes)
		{
			buf.append("DataCollection Scope : "+s);
		}
		return buf.toString();
	}
	
	@Override
	public String toString() {
		return this.deepToString();
	}

}
