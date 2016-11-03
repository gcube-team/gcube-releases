package gr.uoa.di.madgik.environment.is.elements.plot;

import java.io.Serializable;
import java.util.ArrayList;
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
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.PlotErrorMapping;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentFile;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentVariable;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentFile.LocalEnvironmentFileDirection;

public class ShellPlotInfo extends InvocablePlotInfo implements Serializable
{
	private static final long serialVersionUID = -1572338650766821375L;
	public boolean UseStdIn=false;
	public boolean UseStdOut=false;
	public boolean UseStdErr=false;
	public boolean UseStdExit=false;
	public List<PlotShellParameter> Parameters=new ArrayList<PlotShellParameter>();
	public Set<PlotErrorMapping> ErrorMappings=new HashSet<PlotErrorMapping>();
	
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
			Element inputsElement=XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "inputs", InvocablePlotInfo.PlotProfileNS);
			if(inputsElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			List<Element> inputParameterElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(inputsElement, "parameter", InvocablePlotInfo.PlotProfileNS);
			this.Parameters.clear();
			if(inputParameterElementlst!=null)
			{
				for(Element inputParameterElement : inputParameterElementlst)
				{
					PlotShellParameter p=new PlotShellParameter();
					p.FromXML(inputParameterElement);
					this.Parameters.add(p);
				}
			}
			Element stdinElement=XMLUtils.GetChildElementWithNameAndNamespace(inputsElement, "stdin", InvocablePlotInfo.PlotProfileNS);
			if(stdinElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			if(!XMLUtils.AttributeExists(stdinElement, "use"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
			this.UseStdIn=Boolean.parseBoolean( XMLUtils.GetAttribute(stdinElement, "use"));
			Element outputsElement=XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "outputs", InvocablePlotInfo.PlotProfileNS);
			if(outputsElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			Element stdoutElement=XMLUtils.GetChildElementWithNameAndNamespace(outputsElement, "stdout", InvocablePlotInfo.PlotProfileNS);
			if(stdoutElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			if(!XMLUtils.AttributeExists(stdoutElement, "use"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
			this.UseStdOut=Boolean.parseBoolean( XMLUtils.GetAttribute(stdoutElement, "use"));
			Element stderrElement=XMLUtils.GetChildElementWithNameAndNamespace(outputsElement, "stderr", InvocablePlotInfo.PlotProfileNS);
			if(stderrElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			if(!XMLUtils.AttributeExists(stderrElement, "use"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
			this.UseStdErr=Boolean.parseBoolean( XMLUtils.GetAttribute(stderrElement, "use"));
			Element stdexitElement=XMLUtils.GetChildElementWithNameAndNamespace(outputsElement, "stdexit", InvocablePlotInfo.PlotProfileNS);
			if(stdexitElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			if(!XMLUtils.AttributeExists(stdexitElement, "use"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
			this.UseStdExit=Boolean.parseBoolean( XMLUtils.GetAttribute(stdexitElement, "use"));
			List<Element> errorMappingElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(stdexitElement, "errorMapping", InvocablePlotInfo.PlotProfileNS);
			this.ErrorMappings.clear();
			if(errorMappingElementlst!=null)
			{
				for(Element errorMappingElement : errorMappingElementlst)
				{
					PlotErrorMapping pem=new PlotErrorMapping();
					pem.FromXML(errorMappingElement);
					this.ErrorMappings.add(pem);
				}
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
		buf.append("<wfprf:inputs>");
		for(PlotShellParameter par : this.Parameters) buf.append(par.ToXML());
		buf.append("<wfprf:stdin use=\""+this.UseStdIn+"\" />");
		buf.append("</wfprf:inputs>");
		buf.append("<wfprf:outputs>");
		buf.append("<wfprf:stdout use=\""+this.UseStdOut+"\" />");
		buf.append("<wfprf:stderr use=\""+this.UseStdErr+"\" />");
		buf.append("<wfprf:stdexit use=\""+this.UseStdExit+"\">");
		for(PlotErrorMapping map : this.ErrorMappings) buf.append(map.ToXML());
		buf.append("</wfprf:stdexit>");
		buf.append("</wfprf:outputs>");
		buf.append("</wfprf:plot>");
		buf.append("</wfprf:Plots>");
		return buf.toString();
	}
}
