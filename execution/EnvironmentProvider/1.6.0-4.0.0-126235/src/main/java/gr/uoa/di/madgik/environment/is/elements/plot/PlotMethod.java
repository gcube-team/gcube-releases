package gr.uoa.di.madgik.environment.is.elements.plot;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Element;

public class PlotMethod implements Serializable
{
	private static final long serialVersionUID = -4686237646751510182L;
	public int Order=0;
	public boolean IsConstructor;
	public boolean UseReturnValue;
	public String Signature;
	public Set<PlotParameter> Parameters=new HashSet<PlotParameter>();
	
	public void FromXML(Element XML) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(XML, "order")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			this.Order=Integer.parseInt(XMLUtils.GetAttribute(XML, "order"));
			if(!XMLUtils.AttributeExists(XML, "isConstructor")) this.IsConstructor=false;
			else this.IsConstructor=Boolean.parseBoolean(XMLUtils.GetAttribute(XML, "isConstructor"));
			if(!this.IsConstructor)
			{
				if(!XMLUtils.AttributeExists(XML, "useReturn")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
				this.UseReturnValue=Boolean.parseBoolean(XMLUtils.GetAttribute(XML, "useReturn"));
			}
			Element plotMethodSignature=XMLUtils.GetChildElementWithNameAndNamespace(XML, "signature", InvocablePlotInfo.PlotProfileNS);
			if(plotMethodSignature==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			this.Signature=XMLUtils.GetChildText(plotMethodSignature);
			Element plotMethodParametersElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "parameters", InvocablePlotInfo.PlotProfileNS);
			if(plotMethodParametersElement!=null)
			{
				List<Element> plotMethodParameterslst=XMLUtils.GetChildElementsWithNameAndNamespace(plotMethodParametersElement, "parameter", InvocablePlotInfo.PlotProfileNS);
				this.Parameters.clear();
				for(Element plotMethodParameterElement : plotMethodParameterslst)
				{
					PlotParameter ppar=new PlotParameter();
					ppar.FromXML(plotMethodParameterElement);
					this.Parameters.add(ppar);
				}
			}
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize element", ex);
		}
	}
	
	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<wfprf:method order=\""+this.Order+"\" isConstructor=\""+this.IsConstructor+"\" useReturn=\""+this.UseReturnValue+"\">");
		buf.append("<wfprf:signature>"+this.Signature+"</wfprf:signature>");
		buf.append("<wfprf:parameters>");
		for(PlotParameter par : this.Parameters) buf.append(par.ToXML());
		buf.append("</wfprf:parameters>");
		buf.append("</wfprf:method>");
		return buf.toString();
	}
}
