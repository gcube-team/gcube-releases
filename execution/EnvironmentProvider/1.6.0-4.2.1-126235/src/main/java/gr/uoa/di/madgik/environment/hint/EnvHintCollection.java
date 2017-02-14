package gr.uoa.di.madgik.environment.hint;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentSerializationException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EnvHintCollection implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public Map<String, NamedEnvHint> Hints=new HashMap<String, NamedEnvHint>();
	
	public EnvHintCollection(){}
	
	public EnvHintCollection(EnvHintCollection NewHints)
	{
		this.Hints=new HashMap<String, NamedEnvHint>(NewHints.Hints);
	}
	
	public EnvHintCollection(String Hints) throws EnvironmentSerializationException
	{
		this.FromXML(Hints);
	}
	
	public boolean HintExists(String Name)
	{
		return this.Hints.containsKey(Name);
	}
	
	public NamedEnvHint GetHint(String Name)
	{
		return this.Hints.get(Name);
	}
	
	public void AddHint(NamedEnvHint Hint)
	{
		this.Hints.put(Hint.Name, Hint);
	}
	
	public EnvHintCollection Merge(EnvHintCollection NewHints)
	{
		EnvHintCollection MergedHints=new EnvHintCollection(this);
		for(Map.Entry<String,NamedEnvHint> entry : NewHints.Hints.entrySet()) MergedHints.Hints.put(entry.getKey(), entry.getValue());
		return MergedHints;
	}
	
	public String ToXML() throws EnvironmentSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<nhints>");
		for(Map.Entry<String,NamedEnvHint> entry : this.Hints.entrySet())
		{
			buf.append(entry.getValue().ToXML());
		}
		buf.append("</nhints>");
		return buf.toString();
	}
	
	public void FromXML(String xml) throws EnvironmentSerializationException
	{
		try
		{
			Document doc=XMLUtils.Deserialize(xml);
			this.FromXML(doc.getDocumentElement());
		}
		catch(Exception ex)
		{
			throw new EnvironmentSerializationException("Could not deserialize the environment hint collection",ex);
		}
	}
	
	public void FromXML(Element xml) throws EnvironmentSerializationException
	{
		try
		{
			this.Hints.clear();
			List<Element> lst=XMLUtils.GetChildElementsWithName(xml, "nhint");
			for(Element elem : lst)
			{
				NamedEnvHint h=new NamedEnvHint();
				h.FromXML(elem);
				this.AddHint(h);
			}
		}
		catch(Exception ex)
		{
			throw new EnvironmentSerializationException("Could not deserialize the environment hint collection",ex);
		}
	}
}
