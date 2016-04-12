package gr.uoa.di.madgik.environment.is.elements;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NodeInfo implements IInformationSystemElement
{
	private static final long serialVersionUID = 6956949451207441009L;
	
	public String ID=UUID.randomUUID().toString();
	private long timestamp = Calendar.getInstance().getTimeInMillis();
	public Map<String,ExtensionPair> StaticExtensions=new HashMap<String, ExtensionPair>();
	public Map<String,ExtensionPair> DynamicExtensions=new HashMap<String, ExtensionPair>();
	private boolean isLocal = false;
	
	public NodeInfo(){}
	
	public Map<String,ExtensionPair> getStaticInfo()
	{
		return this.StaticExtensions;
	}
	
	public Map<String,ExtensionPair> getDynamicInfo()
	{
		return this.DynamicExtensions;
	}
	
	public String getExtension(String key)
	{
		if(this.StaticExtensions.containsKey(key)) return this.StaticExtensions.get(key).Value;
		else if (this.DynamicExtensions.containsKey(key)) return this.DynamicExtensions.get(key).Value;
		return null;
	}
	
	public void markLocal()
	{
		this.isLocal = true;
	}
	
	public boolean isLocal()
	{
		return this.isLocal;
	}

	public String getTimestamp()
	{
		return new Long(this.timestamp).toString();
	}
	
	public void updateTimestamp()
	{
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}
	
	public String ToXML(boolean includeStatic, boolean includeDynamic) throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<element id=\""+this.ID+"\">");
		buf.append("<timestamp>" + this.timestamp + "</timestamp>");
		buf.append("<static>");
		if(includeStatic)
		{
			for(ExtensionPair entry : this.StaticExtensions.values())
			{
				buf.append("<entry key=\""+entry.Key+"\">"+entry.Value+"</entry>");
			}
		}
		buf.append("</static>");
		buf.append("<dynamic>");
		if(includeDynamic)
		{
			for(ExtensionPair entry : this.DynamicExtensions.values())
			{
				buf.append("<entry key=\""+entry.Key+"\">"+entry.Value+"</entry>");
			}
		}
		buf.append("</dynamic>");
		buf.append("</element>");
		return buf.toString();
	}
	
	public void FromXML(String xml) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			Document doc=XMLUtils.Deserialize(xml);
			this.FromXML(doc.getDocumentElement());
		}
		catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not parse provided info",ex);
		}
	}
	
	public void FromXML(Element xml) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			this.StaticExtensions.clear();
			this.DynamicExtensions.clear();
			if(!XMLUtils.AttributeExists(xml, "id")) throw new EnvironmentInformationSystemSerializationException("No id provided for element");
			this.ID=XMLUtils.GetAttribute(xml, "id");
			Element timestamp=XMLUtils.GetChildElementWithName(xml, "timestamp");
			if(timestamp!=null) 
				this.timestamp = Long.parseLong(XMLUtils.GetChildText(timestamp));
			Element staticElement=XMLUtils.GetChildElementWithName(xml, "static");
			if(staticElement!=null)
			{
				List<Element> mapElem=XMLUtils.GetChildElementsWithName(staticElement, "entry");
				for(Element elem : mapElem)
				{
					ExtensionPair pair=new ExtensionPair();
					pair.Key=XMLUtils.GetAttribute(elem, "key");
					pair.Value=XMLUtils.GetChildText(elem);
					if(pair.Key==null || pair.Key.trim().length()==0) continue;
					this.StaticExtensions.put(pair.Key, pair);
				}
			}
			Element dynamicElement=XMLUtils.GetChildElementWithName(xml, "dynamic");
			if(dynamicElement!=null)
			{
				List<Element> mapElem=XMLUtils.GetChildElementsWithName(dynamicElement, "entry");
				for(Element elem : mapElem)
				{
					ExtensionPair pair=new ExtensionPair();
					pair.Key=XMLUtils.GetAttribute(elem, "key");
					pair.Value=XMLUtils.GetChildText(elem);
					if(pair.Key==null || pair.Key.trim().length()==0) continue;
					this.DynamicExtensions.put(pair.Key, pair);
				}
			}
		}
		catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not parse provided info",ex);
		}
	}
}
