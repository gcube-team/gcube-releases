package gr.uoa.di.madgik.environment.is.elements.plot;

import org.w3c.dom.Element;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;

public class PlotParameter
{
	public String ParameterName=null;
	public boolean IsFixed=false;
	public String FixedValue=null;
	
	public void FromXML(Element XML) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(XML, "isFixed"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
			this.IsFixed=Boolean.parseBoolean(XMLUtils.GetAttribute(XML, "isFixed"));
			if(this.IsFixed) this.FixedValue=XMLUtils.GetChildText(XML);
			if(!XMLUtils.AttributeExists(XML, "name"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
			this.ParameterName=XMLUtils.GetAttribute(XML, "name");
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize element", ex);
		}
	}
	
	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		if(!this.IsFixed) buf.append("<wfprf:parameter isFixed=\""+this.IsFixed+"\" name=\""+this.ParameterName+"\"/>");
		else
		{
			buf.append("<wfprf:parameter isFixed=\""+this.IsFixed+"\" name=\""+this.ParameterName+"\">");
			buf.append(this.FixedValue);
			buf.append("</wfprf:parameter>");
		}
		return buf.toString();
	}
}
