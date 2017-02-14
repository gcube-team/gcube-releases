package gr.uoa.di.madgik.environment.state.elements;

import java.util.List;

import gr.uoa.di.madgik.commons.utils.XMLUtils;

import org.w3c.dom.Document;

public abstract class StateResource 
{

	protected Document xml = null;
	protected String xmlString = null;
	
	protected StateResource(String xml) throws Exception
	{
		this.xmlString = xml;
		this.xml = XMLUtils.Deserialize(xml);
	}
	
	protected StateResource(Document xml) throws Exception
	{
		this.xml = xml;
	}
	
	public abstract String getKey() throws Exception;
	
	public abstract Endpoint getEndpoint() throws Exception;
	
	public abstract String getElement(String name) throws Exception;
	
	public abstract List<String> evaluate(String expression) throws Exception;
	
	public String serialize() throws Exception
	{
		if(this.xmlString == null)
			this.xmlString = XMLUtils.Serialize(xml);
		return this.xmlString;
	}
}
