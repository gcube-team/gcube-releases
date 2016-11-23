package gr.uoa.di.madgik.execution.plan.element.variable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class VariableCollection implements Iterable<NamedDataType>, Serializable
{
	private static final long serialVersionUID = 1L;
	private Hashtable<String, NamedDataType> Variables = new Hashtable<String, NamedDataType>();
	
	
	final Boolean lockMe = new Boolean(false);
	
	public VariableCollection(){}
	
	public VariableCollection(String XML) throws ExecutionSerializationException
	{
		this.FromXML(XML);
	}

	public void Add(NamedDataType item)
	{
		synchronized (this.lockMe)
		{
			this.Variables.put(item.Name, item);
		}
	}

	public void Update(String name, Object value) throws ExecutionValidationException
	{
		synchronized (this.lockMe)
		{
			NamedDataType exist = this.Variables.get(name);
			if (exist == null) { return; }
			exist.Value.SetValue(value);
			exist.IsAvailable=true;
		}
	}

	public NamedDataType Get(String name)
	{
		synchronized (this.lockMe)
		{
			return this.Variables.get(name);
		}
	}
	
	public boolean Contains(String name)
	{
		synchronized (this.lockMe)
		{
			return this.Variables.containsKey(name);
		}
	}

	public Iterator<NamedDataType> iterator()
	{
		return this.Variables.values().iterator();
	}
	
	public VariableCollection Subset(Set<String> NeededVars)
	{
		VariableCollection newCol=new VariableCollection();
		synchronized (this.lockMe)
		{
			for(String setName : NeededVars) if(this.Variables.containsKey(setName)) newCol.Add(this.Variables.get(setName));
		}
		return newCol;
	}
	
	public void Update(VariableCollection UpdateCollection, Set<String> UpdateSubset) throws ExecutionValidationException, ExecutionSerializationException
	{
		synchronized (this.lockMe)
		{
			for(String ShouldUpdate : UpdateSubset)
			{
				if(UpdateCollection.Contains(ShouldUpdate) && this.Variables.containsKey(ShouldUpdate))
				{
					this.Update(UpdateCollection.Get(ShouldUpdate));
				}
			}
		}		
	}

	private void Update(NamedDataType item) throws ExecutionValidationException, ExecutionSerializationException
	{
		NamedDataType exist = this.Variables.get(item.Name);
		if (exist == null) { return; }
		exist.IsAvailable = item.IsAvailable;
		exist.Value.SetStringValue(item.Value.GetStringValue());
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<variables>");
		for (NamedDataType item : this.Variables.values())
		{
			buf.append(item.ToXML());
		}
		buf.append("</variables>");
		return buf.toString();
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		this.Variables.clear();
		List<Element> variables = null;
		try
		{
			variables = XMLUtils.GetChildElementsWithName(XML, "ndt");
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not retrieve variable named data types", ex);
		}
		for (Element var : variables)
		{
			NamedDataType ndt = new NamedDataType();
			ndt.FromXML(var);
			this.Add(ndt);
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
