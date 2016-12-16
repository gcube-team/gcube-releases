package gr.uoa.di.madgik.environment.is.elements.plot;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import java.io.Serializable;
import org.w3c.dom.Element;

public class PlotShellParameter extends PlotParameter implements Comparable<PlotShellParameter>, Serializable
{
	private static final long serialVersionUID = 1523180179231852775L;
	public int Order=0;
	public boolean IsFile=false;

	public int compareTo(PlotShellParameter o)
	{
		return Integer.valueOf(this.Order).compareTo(o.Order);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
	
	public void FromXML(Element XML) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(XML, "isFixed")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			if(!XMLUtils.AttributeExists(XML, "name")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			if(!XMLUtils.AttributeExists(XML, "order")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			if(!XMLUtils.AttributeExists(XML, "isFile")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			this.IsFixed=Boolean.parseBoolean(XMLUtils.GetAttribute(XML, "isFixed"));
			this.ParameterName =XMLUtils.GetAttribute(XML, "name");
			this.Order =Integer.parseInt(XMLUtils.GetAttribute(XML, "order"));
			this.IsFile =Boolean.parseBoolean(XMLUtils.GetAttribute(XML, "isFile"));
			if(this.IsFixed) this.FixedValue=XMLUtils.GetChildText(XML);
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize element", ex);
		}
	}
	
	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		if(!this.IsFixed) buf.append("<wfprf:parameter isFixed=\""+this.IsFixed+"\" name=\""+this.ParameterName+"\" order=\""+this.Order+"\" isFile=\""+this.IsFile+"\"/>");
		else
		{
			buf.append("<wfprf:parameter isFixed=\""+this.IsFixed+"\" name=\""+this.ParameterName+"\" order=\""+this.Order+"\" isFile=\""+this.IsFile+"\">");
			buf.append(this.FixedValue);
			buf.append("</wfprf:parameter>");
		}
		return buf.toString();
	}
}
