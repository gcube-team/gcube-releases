package gr.uoa.di.madgik.execution.plan.element.invocable;

import java.io.Serializable;

import gr.uoa.di.madgik.commons.channel.nozzle.INozzleConfig;
import gr.uoa.di.madgik.commons.channel.nozzle.NozzleConfigUtils;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BoundaryConfig implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public String HostName=null;
	public int Port=0;
	public INozzleConfig NozzleConfig=null;
	
	public String ToXML() throws ExecutionSerializationException
	{
		try
		{
			StringBuilder buf=new StringBuilder();
			buf.append("<boundaryConfig hostname=\""+this.HostName+"\" port=\""+this.Port+"\" >");
			buf.append(this.NozzleConfig.ToXML());
			buf.append("</boundaryConfig>");
			return buf.toString();
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not create serialization",ex);
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
	
	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(XML, "hostname")) throw new ExecutionSerializationException("Provided serialization not valid");
			if(!XMLUtils.AttributeExists(XML, "port")) throw new ExecutionSerializationException("Provided serialization not valid");
			this.HostName=XMLUtils.GetAttribute(XML, "hostname");
			this.Port=Integer.parseInt(XMLUtils.GetAttribute(XML, "port"));
			Element nozzleConfigElement=XMLUtils.GetChildElementWithName(XML, "nozzleConfig");
			if(nozzleConfigElement==null) throw new ExecutionSerializationException("Provided serialization not valid");
			this.NozzleConfig=NozzleConfigUtils.GetNozzleConfig(nozzleConfigElement);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}
	
	public void Validate() throws ExecutionValidationException
	{
		if(this.HostName==null || this.HostName.trim().length()==0) throw new ExecutionValidationException("Host name not present");
		if(this.Port<=0) throw new ExecutionValidationException("Port must be a positive number");
		if(this.NozzleConfig==null) throw new ExecutionValidationException("No nozzle config defined");
	}
}
