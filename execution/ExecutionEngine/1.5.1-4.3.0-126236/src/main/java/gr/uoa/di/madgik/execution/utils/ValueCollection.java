package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ValueCollection implements Iterable<String>, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Set<String> Collection=new HashSet<String>();
	private final Boolean lockMe = new Boolean(false);
	
	public ValueCollection(){}

	public ValueCollection(String XML) throws ExecutionSerializationException
	{
		this.FromXML(XML);
	}
	
	public void Add(String item)
	{
		synchronized (this.lockMe)
		{
			this.Collection.add(item);
		}
	}
	
	public boolean Contains(String name)
	{
		synchronized (this.lockMe)
		{
			return this.Collection.contains(name);
		}
	}

	public Iterator<String> iterator()
	{
		return this.Collection.iterator();
	}
	
	public void Merge(ValueCollection coll)
	{
		for(String val : coll)
		{
			if(!this.Contains(val)) this.Add(val);
		}
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<valueColl>");
		for (String item : this.Collection)
		{
			buf.append("<item value=\""+item+"\"/>");
		}
		buf.append("</valueColl>");
		return buf.toString();
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		this.Collection.clear();
		try
		{
			List<Element> variables = null;
			variables = XMLUtils.GetChildElementsWithName(XML, "item");
			for (Element var : variables)
			{
				if(!XMLUtils.AttributeExists(var, "value")) throw new ExecutionSerializationException("Invalid serialization");
				this.Add(XMLUtils.GetAttribute(var, "value"));
			}
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not retrieve items", ex);
		}
	}

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
}
