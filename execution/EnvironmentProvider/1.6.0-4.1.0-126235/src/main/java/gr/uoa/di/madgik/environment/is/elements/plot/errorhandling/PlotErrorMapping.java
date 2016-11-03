package gr.uoa.di.madgik.environment.is.elements.plot.errorhandling;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import java.io.Serializable;
import org.w3c.dom.Element;

public class PlotErrorMapping implements Serializable
{
	private static final long serialVersionUID = 2555226048649304051L;
	public int ExitCode=0;
	public String FullErrorName=null;
	public String SimpleErrorName=null;
	public String Message=null;
	
	public void FromXML(Element XML) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(XML, "exitCode"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
			if(!XMLUtils.AttributeExists(XML, "fullName"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
			if(!XMLUtils.AttributeExists(XML, "simpleName"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
			this.ExitCode=Integer.parseInt(XMLUtils.GetAttribute(XML, "exitCode"));
			this.FullErrorName=XMLUtils.GetAttribute(XML, "fullName");
			this.SimpleErrorName=XMLUtils.GetAttribute(XML, "simpleName");
			this.Message=XMLUtils.GetChildText(XML);
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize element", ex);
		}
	}
	
	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<wfprf:errorMapping exitCode=\""+this.ExitCode+"\" fullName=\""+this.FullErrorName+"\" simpleName=\""+this.SimpleErrorName+"\">"+this.Message+"</wfprf:errorMapping>");
		return buf.toString();
	}
}
