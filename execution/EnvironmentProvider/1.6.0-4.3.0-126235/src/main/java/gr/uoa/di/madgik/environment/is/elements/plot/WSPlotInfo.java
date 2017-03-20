package gr.uoa.di.madgik.environment.is.elements.plot;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.InvocablePlotContingency;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentFile;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentVariable;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentFile.LocalEnvironmentFileDirection;

public class WSPlotInfo extends InvocablePlotInfo implements Serializable
{
	private static final long serialVersionUID = 3462799634534701356L;
	public Set<PlotMethod> Methods=new HashSet<PlotMethod>();
	
	@Override
	public void FromXML(String XML) throws EnvironmentInformationSystemSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}
	
	@Override
	public void FromXML(Element XML) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			Element plotElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "plot", InvocablePlotInfo.PlotProfileNS);
			if(plotElement==null) throw new EnvironmentInformationSystemSerializationException("Invalid serialization provided");
			if(XMLUtils.AttributeExists(plotElement, "id")) this.ID=XMLUtils.GetAttribute(plotElement, "id");
			if(XMLUtils.AttributeExists(plotElement, "invocableid")) this.InvocabeProfileID=XMLUtils.GetAttribute(plotElement, "invocableid");
			this.Name=InvocablePlotInfo.GetName(plotElement);
			this.Description=InvocablePlotInfo.GetDescription(plotElement);
			this.Triggers=InvocablePlotInfo.GetTriggers(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "contingencyReactions", InvocablePlotInfo.PlotProfileNS));
			this.LocalEnvironment=InvocablePlotInfo.GetEnvironment(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "localEnvironment", InvocablePlotInfo.PlotProfileNS));
			Element plotMethodsElement=XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "methods", InvocablePlotInfo.PlotProfileNS);
			if(plotMethodsElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			List<Element> plotMethodElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(plotMethodsElement, "method", InvocablePlotInfo.PlotProfileNS);
			this.Methods.clear();
			for(Element plotMethodElement : plotMethodElementlst)
			{
				PlotMethod pm=new PlotMethod();
				pm.FromXML(plotMethodElement);
				this.Methods.add(pm);
			}
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize element", ex);
		}
	}
	
	@Override
	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<wfprf:Plots xmlns:wfprf=\""+InvocablePlotInfo.PlotProfileNS+"\">");
		buf.append("<wfprf:plot id=\""+this.ID+"\" invocableid=\""+this.InvocabeProfileID+"\">");
		buf.append("<wfprf:name value=\""+this.Name+"\"/>");
		buf.append("<wfprf:description>"+this.Description+"</wfprf:description>");
		buf.append("<wfprf:contingencyReactions>");
		for(InvocablePlotContingency cont : this.Triggers) buf.append(cont.ToXML());
		buf.append("</wfprf:contingencyReactions>");
		buf.append("<wfprf:localEnvironment>");
		buf.append("<wfprf:files>");
		buf.append("<wfprf:in>");
		for(PlotLocalEnvironmentFile f : this.LocalEnvironment.Files)
		{
			if(f.Direction==LocalEnvironmentFileDirection.In)
			{
				buf.append("<wfprf:file name=\""+f.Name+"\" location=\""+f.Location+"\" isExecutable=\""+f.IsExecutable+"\" cleanup=\""+f.CleanUp+"\" />");
			}
		}
		buf.append("</wfprf:in>");
		buf.append("<wfprf:out>");
		for(PlotLocalEnvironmentFile f : this.LocalEnvironment.Files)
		{
			if(f.Direction==LocalEnvironmentFileDirection.Out)
			{
				buf.append("<wfprf:file name=\""+f.Name+"\" location=\""+f.Location+"\" isExecutable=\""+f.IsExecutable+"\" cleanup=\""+f.CleanUp+"\" />");
			}
		}
		buf.append("</wfprf:out>");
		buf.append("</wfprf:files>");
		buf.append("<wfprf:variables>");
		for(PlotLocalEnvironmentVariable v : this.LocalEnvironment.Variables)
		{
			if(v.IsFixed) buf.append("<wfprf:var name=\""+v.Name+"\" isFixed=\""+v.IsFixed+"\" value=\""+v.Value+"\"/>");
			else buf.append("<wfprf:var name=\""+v.Name+"\" isFixed=\""+v.IsFixed+"\"/>");
		}
		buf.append("</wfprf:variables>");
		buf.append("</wfprf:localEnvironment>");
		buf.append("<wfprf:methods>");
		for(PlotMethod m : this.Methods)
		{
			buf.append(m.ToXML());
		}
		buf.append("</wfprf:methods>");
		buf.append("</wfprf:plot>");
		buf.append("</wfprf:Plots>");
		return buf.toString();
	}
}
