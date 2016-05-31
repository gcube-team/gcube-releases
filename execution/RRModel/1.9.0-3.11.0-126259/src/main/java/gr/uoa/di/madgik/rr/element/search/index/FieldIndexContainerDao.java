package gr.uoa.di.madgik.rr.element.search.index;

import java.util.Calendar;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.w3c.dom.Element;

import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;

@PersistenceCapable(table="FIELDINDEXCONTAINER", detachable="true")
@Queries(
		{
		@Query(
				name="exists", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainerDao WHERE this.ID == :id"
				),
		@Query(
				name="queryByFieldIDAndType", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainerDao WHERE this.field == :id && this.fieldType == :type"
				),
		@Query(
				name="queryByFieldIDAndTypeAndCollection", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainerDao WHERE this.field == :id && this.fieldType == :type && this.collection == :collection"
				),
		@Query(
				name="queryByFieldIDAndTypeAndLanguage", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainerDao WHERE this.field == :id && this.fieldType == :type && this.language == :language"
				),
		@Query(
				name="queryByFieldIDAndTypeAndCollectionAndLanguage", 
				language="JDOQL", 
				value="SELECT this.ID FROM gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainerDao WHERE this.field == :id && this.fieldType == :type && this.collection == :collection && this.language == :language"
				),		
		}
		
)
public class FieldIndexContainerDao implements IDaoElement
{
	@PrimaryKey
	//@Column(name = "gcubeGUID")
	private String ID;
	
	//@Column(name = "gcubeTimestamp")
	private Long timestamp = Calendar.getInstance().getTimeInMillis();
	
	//@Column(name = "gcubeCollection")
	private String collection;
	
	//@Column(name = "gcubeLanguage")
	private String language;
	
	//@Column(name = "gcubeFieldRef")
	private String field;
	
	//@Column(name = "gcubeFieldType")
	private String fieldType="s";
	
	//@Column(name = "gcubeExpression")
	private String expression;

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
	
	public String getFieldType()
	{
		return fieldType;
	}

	public void setFieldType(String fieldType)
	{
		if(fieldType==null) this.fieldType="s";
		else if(fieldType.equalsIgnoreCase("s")) this.fieldType="s";
		else this.fieldType = "p";
	}

	public String getCollection()
	{
		return collection;
	}

	public void setCollection(String collection)
	{
		this.collection = collection;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
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
	
	public void apply(IDaoElement target) throws ResourceRegistryException
	{
		if(!(target instanceof FieldIndexContainerDao)) throw new ResourceRegistryException("cannot apply to target of "+target);
		this.setID(((FieldIndexContainerDao)target).getID());
		this.setCollection(((FieldIndexContainerDao)target).getCollection());
		this.setField(((FieldIndexContainerDao)target).getField());
		this.setFieldType(((FieldIndexContainerDao)target).getFieldType());
		this.setLanguage(((FieldIndexContainerDao)target).getLanguage());
		this.setExpression(((FieldIndexContainerDao)target).getExpression());
	}
	
	public void fromXML(Element element) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("This element does not support serialization operation");
	}

	public String toXML() throws ResourceRegistryException
	{
		throw new ResourceRegistryException("This element does not support serialization operation");
	}

}
