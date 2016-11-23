package gr.uoa.di.madgik.rr.element.functionality;

import java.util.Calendar;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.w3c.dom.Element;

import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;

@PersistenceCapable(table="FUNCTIONALITY", detachable="true")
@Queries(
		{@Query(
				name="allFunctionalities", 
				language="JDOQL", 
				value="SELECT this.name FROM gr.uoa.di.madgik.rr.element.functionality.FunctionalityDao"
				),
		@Query(
				name="exists", 
				language="JDOQL", 
				value="SELECT this.name FROM gr.uoa.di.madgik.rr.element.functionality.FunctionalityDao WHERE this.name == :id"
				),
		}
		
)
public class FunctionalityDao implements IDaoElement
{
	@PrimaryKey
	//@Column(name = "gcubeGUID")
	public String name;
	
	//@Column(name = "gcubeTimestamp")
	public Long timestamp = Calendar.getInstance().getTimeInMillis();

	public String getName()
	{
		return name;
	}
	
	public String getID()
	{
		return name;
	}
	
	public Long getTimestamp()
	{
		if (timestamp ==  null)
			return 0l;
		return timestamp;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void apply(IDaoElement target) throws ResourceRegistryException
	{
		if(!(target instanceof FunctionalityDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		this.setName(((FunctionalityDao)target).getName());
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
		buf.append("Functionality Name : "+this.name);
		return buf.toString();
	}

}
