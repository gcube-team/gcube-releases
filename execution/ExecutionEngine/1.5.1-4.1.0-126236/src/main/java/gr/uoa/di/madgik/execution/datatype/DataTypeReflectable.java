package gr.uoa.di.madgik.execution.datatype;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.ReflectableAnalyzer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class represents data type with a collection of {@link ReflectableItem} payload
 */
public class DataTypeReflectable implements IDataType, Iterable<ReflectableItem>
{
	private static final long serialVersionUID = 1L;
	/** The collection of Items. */
	private ReflectableItem []Items=null;

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<ReflectableItem> iterator()
	{
		return Arrays.asList(this.Items).iterator();
	}

	/**
	 * Always returns false
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#CanSuggestDataTypeClass()
	 */
	public boolean CanSuggestDataTypeClass()
	{
		return false;
	}

	/**
	 * Returns null
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeClass()
	 */
	public Class<?> GetDataTypeClass()
	{
		return null;
	}

	/**
	 * Returns {@link IDataType.DataTypes#Reflectable}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetDataTypeEnum()
	 */
	public DataTypes GetDataTypeEnum()
	{
		return IDataType.DataTypes.Reflectable;
	}

	/**
	 * Returns {@link DataTypeReflectable#ToXML()}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetStringValue()
	 */
	public String GetStringValue() throws ExecutionSerializationException
	{
		return this.ToXML();
	}

	/**
	 * Calls {@link DataTypeReflectable#FromXML(String))}
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetStringValue(java.lang.String)
	 */
	public void SetStringValue(String val) throws ExecutionValidationException, ExecutionSerializationException
	{
		this.FromXML(val);
	}

	/**
	 * Returns this instance 
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#GetValue()
	 */
	public Object GetValue()
	{
		return this;
	}
	
	/**
	 * Retrieves the {@link ReflectableItem}s that make up this instance
	 * 
	 * @return the reflectable item collection
	 */
	public ReflectableItem[] GetItems()
	{
		return this.Items;
	}

	/**
	 * If the provided value is null, the collection of {@link ReflectableItem} is also set to null.
	 * Otherwise, if the provided value is an array of {@link ReflectableItem}, the internal collection
	 * is set to the provided array. If the provided value is a {@link DataTypeReflectable}, the internal
	 * collection is set to the internal collection of the provided {@link DataTypeReflectable}. Otherwise,
	 * if the provided value is a string, the {@link DataTypeReflectable#FromXML(String))} is
	 * called. Otherwise, an instance of a {@link ReflectableAnalyzer} is created, and the produced
	 * {@link DataTypeReflectable} retrieved by {@link ReflectableAnalyzer#ProduceReflectable(Object)}
	 * is used.
	 * 
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#SetValue(java.lang.Object)
	 */
	public void SetValue(Object Value) throws ExecutionValidationException
	{
		String ErrorString=null;
		if (Value == null)
		{
			this.Items= null;
			return;
		}
//		else if (Value.getClass().isArray() && 
//				DataTypeUtils.CountDimentionsOfObjectArrayCode(Value.getClass().getName())==1 && 
//				Value.getClass().getComponentType().getName().equals(ReflectableItem.class.getName()))
		else if (Value instanceof ReflectableItem[])
		{
			this.Items=(ReflectableItem[]) Value;
			return;
		}
		else if(Value instanceof DataTypeReflectable)
		{
			this.Items=((DataTypeReflectable)Value).Items;
			return;
		}
		else if (Value instanceof String)
		{
			try{
				this.FromXML((String)Value);
				return;
			}catch(Exception ex)
			{
				ErrorString=ex.getMessage();
			}
		}
		else
		{
			ReflectableAnalyzer analyzer=new ReflectableAnalyzer(Value.getClass(),null);
			if(analyzer.CanRepresentAsReflectable())
			{
				try
				{
					DataTypeReflectable refl= analyzer.ProduceReflectable(Value);
					if(refl==null) ErrorString="Given value cannot be represented as a reflectable";
					else
					{
						this.Items=refl.Items;
						return;
					}
				}catch(Exception ex)
				{
					ErrorString=ex.getMessage();
				}
			}
			else ErrorString="Given value cannot be represented as a reflectable";
		}
		throw new ExecutionValidationException("Incompatible types. Expecting "+this.GetDataTypeEnum().toString()+", String or something reflectabe and found " + Value.getClass().getName()+" with error from process("+ErrorString+")");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#FromXML(java.lang.String)
	 */
	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#FromXML(org.w3c.dom.Element)
	 */
	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(XML, "type")) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!IDataType.DataTypes.valueOf(XMLUtils.GetAttribute(XML, "type")).equals(this.GetDataTypeEnum())) throw new ExecutionSerializationException("Provided serialization is not valid");
			Element valelem=XMLUtils.GetChildElementWithName(XML, "value");
			if(valelem==null) throw new ExecutionSerializationException("Provided Serialization is not valid");
			Element itemselem = XMLUtils.GetChildElementWithName(valelem, "items");
			if(itemselem==null) this.Items=null;
			else
			{
				List<Element> itemslst=XMLUtils.GetChildElementsWithName(itemselem, "item");
				if(itemslst.size()==0) this.Items=null;
				else
				{
					this.Items=new ReflectableItem[itemslst.size()];
					for(int i=0;i<itemslst.size();i+=1)
					{
						ReflectableItem it=new ReflectableItem();
						it.FromXML(itemslst.get(i));
						this.Items[i]=it;
					}
				}
			}
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.datatype.IDataType#ToXML()
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<dt type=\""+this.GetDataTypeEnum().toString()+"\">");
		buf.append("<value>");
		buf.append("<items>");
		if(this.Items!=null)for(ReflectableItem item : this.Items) buf.append(item.ToXML());
		buf.append("</items>");
		buf.append("</value>");
		buf.append("</dt>");
		return buf.toString();
	}

}
