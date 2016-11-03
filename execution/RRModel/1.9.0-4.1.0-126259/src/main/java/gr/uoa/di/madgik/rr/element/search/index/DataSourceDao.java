package gr.uoa.di.madgik.rr.element.search.index;

import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Serialized;

import org.w3c.dom.Element;

@PersistenceCapable(table="DATASOURCE", detachable="true")
//@Inheritance(strategy=InheritanceStrategy.)
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@Queries(
		{@Query(
				name="exists", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.index.DataSourceDao WHERE this.ID == :id"
				),
		@Query(
				name="all", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.index.DataSourceDao"
				),
		@Query(
				name="getType", 
				language="JDOQL", 
				value="SELECT this.type FROM gr.uoa.di.madgik.rr.element.search.index.DataSourceDao WHERE this.ID == :id"
				),
						
		}
)
public abstract class DataSourceDao implements IDaoElement
{	
	@PrimaryKey
	//@Column(name = "gcubeGUID")
	private String ID;
	
	//@Column(name = "gcubeTimestamp")
	private Long timestamp=Calendar.getInstance().getTimeInMillis();
	
	//@Column(name = "gcubeSourceType")
	private String type;
	
	//@Column(name = "gcubeFunctionalityRef")
	private String functionality;
	
	//@Column(name = "gcubeDatasourceServiceRef")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> boundDataSourceServices;
	
	//@Column(name = "gcubeScope")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> scopes=new HashSet<String>();
	
	//@Column(name = "gcubeFieldRef")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> fields=new HashSet<String>();
	
	//@Column(name = "gcubeCapability")
	//@Persistent(defaultFetchGroup="true")
	@Persistent(defaultFetchGroup="true")
	@Column(jdbcType = "BLOB")
	@Serialized
	private Set<String> capabilities=new HashSet<String>();
	
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
		return type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public Set<String> getBoundDataSourceServices()
	{
		return this.boundDataSourceServices;
	}
	
	public void setBoundDataSourceServices(Set<String> boundDataSourceServices)
	{
		this.boundDataSourceServices = boundDataSourceServices;
	}
	
	public Set<String> getScopes()
	{
		return scopes;
	}

	public void setScopes(Set<String> scopes)
	{
		this.scopes = scopes;
	}
	
	public String getFunctionality()
	{
		return functionality;
	}

	public void setFunctionality(String functionality)
	{
		this.functionality = functionality;
	}
	
	public Set<String> getFields()
	{
		return fields;
	}

	public void setFields(Set<String> fields)
	{
		this.fields = fields;
	}

	public Set<String> getCapabilities()
	{
		return capabilities;
	}

	public void setCapabilities(Set<String> capabilities)
	{
		this.capabilities = capabilities;
	}

	public void apply(IDaoElement target) throws ResourceRegistryException
	{
		if(!(target instanceof DataSourceDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		this.setID(((DataSourceDao)target).getID());
		this.setFunctionality(((DataSourceDao)target).getFunctionality());
		this.setType(((DataSourceDao)target).getType());
		this.setFields(((DataSourceDao)target).getFields());
		this.setCapabilities(((DataSourceDao)target).getCapabilities());
		this.setScopes(((DataSourceDao)target).getScopes());
	}
	
	public void fromXML(Element element) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("This element does not support serialization operation");
	}

	public String toXML() throws ResourceRegistryException
	{
		throw new ResourceRegistryException("This element does not support serialization operation");
	}

	@Override
	public String toString()
	{
		return this.ID;
	}
}
