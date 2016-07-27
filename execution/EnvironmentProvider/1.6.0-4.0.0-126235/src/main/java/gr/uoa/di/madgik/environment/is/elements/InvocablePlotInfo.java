package gr.uoa.di.madgik.environment.is.elements;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.IInvocablePlotContingencyReaction;
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.InvocablePlotContingency;
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.InvocablePlotContingencyReactionNone;
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.InvocablePlotContingencyReactionPick;
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.InvocablePlotContingencyReactionRetry;
import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.InvocablePlotContingencyTrigger;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironment;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentFile;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentVariable;
import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentFile.LocalEnvironmentFileDirection;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.w3c.dom.Element;

public abstract class InvocablePlotInfo implements Serializable
{
	public static final String PlotProfileNS="http://profile.workflow.madgik.di.uoa.gr";
	
	private static final long serialVersionUID = 7114124605350886660L;
	public String ID=UUID.randomUUID().toString();
	public String Name=null;
	public String Description=null;
	public String InvocabeProfileID=null;
	public Set<InvocablePlotContingency> Triggers=new HashSet<InvocablePlotContingency>();
	public PlotLocalEnvironment LocalEnvironment=new PlotLocalEnvironment();
	
	public abstract String ToXML() throws EnvironmentInformationSystemSerializationException;
	public abstract void FromXML(String XML) throws EnvironmentInformationSystemSerializationException;
	public abstract void FromXML(Element XML) throws EnvironmentInformationSystemSerializationException;
	
	
	protected static String GetName(Element plotElement) throws Exception
	{
		Element plotNameElement=XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "name", InvocablePlotInfo.PlotProfileNS);
		if(plotNameElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
		if(!XMLUtils.AttributeExists(plotNameElement, "value")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
		return XMLUtils.GetAttribute(plotNameElement, "value");
	}
	
	protected static String GetDescription(Element plotElement) throws Exception
	{
		Element plotDescriptionElement=XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "description", InvocablePlotInfo.PlotProfileNS);
		if(plotDescriptionElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
		return XMLUtils.GetChildText(plotDescriptionElement);
	}
	
	protected static Set<InvocablePlotContingency> GetTriggers(Element plotTriggersElement) throws Exception
	{
		Set<InvocablePlotContingency> triggers=new HashSet<InvocablePlotContingency>();
		if(plotTriggersElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
		List<Element> plotTriggersElementslst=XMLUtils.GetChildElementsWithNameAndNamespace(plotTriggersElement, "contingency", InvocablePlotInfo.PlotProfileNS);
		for(Element plotContingencyElement : plotTriggersElementslst)
		{
			InvocablePlotContingency cont=new InvocablePlotContingency();
			Element triggElement=XMLUtils.GetChildElementWithNameAndNamespace(plotContingencyElement, "trigger", InvocablePlotInfo.PlotProfileNS);
			if(triggElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			cont.Trigger=new InvocablePlotContingencyTrigger();
			if(XMLUtils.AttributeExists(triggElement, "isFullName"))
			{
				cont.Trigger.IsFullName=Boolean.parseBoolean(XMLUtils.GetAttribute(triggElement, "isFullName"));
				cont.Trigger.ErrorName=XMLUtils.GetChildText(triggElement);
				if(cont.Trigger.ErrorName.trim().length()==0) cont.Trigger.ErrorName=null;
			}
			Element reactionElement=XMLUtils.GetChildElementWithNameAndNamespace(plotContingencyElement, "reaction", InvocablePlotInfo.PlotProfileNS);
			if(reactionElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			if(!XMLUtils.AttributeExists(reactionElement, "type")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
			IInvocablePlotContingencyReaction.ReactionType react=IInvocablePlotContingencyReaction.ReactionType.valueOf(XMLUtils.GetAttribute(reactionElement, "type"));
			switch(react)
			{
				case None:
				{
					cont.Reaction=new InvocablePlotContingencyReactionNone();
					break;
				}
				case Retry:
				{
					cont.Reaction=new InvocablePlotContingencyReactionRetry();
					if(!XMLUtils.AttributeExists(reactionElement, "retries"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
					if(!XMLUtils.AttributeExists(reactionElement, "interval"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
					((InvocablePlotContingencyReactionRetry)cont.Reaction).NumberOfRetries=Integer.parseInt(XMLUtils.GetAttribute(reactionElement, "retries"));
					((InvocablePlotContingencyReactionRetry)cont.Reaction).RetryInterval=Long.parseLong(XMLUtils.GetAttribute(reactionElement, "interval"));
					break;
				}
				case Pick:
				{
					cont.Reaction=new InvocablePlotContingencyReactionPick();
					if(!XMLUtils.AttributeExists(reactionElement, "exhaust"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
					((InvocablePlotContingencyReactionPick)cont.Reaction).ExhaustLists=Boolean.parseBoolean(XMLUtils.GetAttribute(reactionElement, "exhaust"));
					Element defaultListElement=XMLUtils.GetChildElementWithNameAndNamespace(reactionElement, "defaultList", InvocablePlotInfo.PlotProfileNS);
					if(defaultListElement!=null)
					{
						List<Element> defaultListlst=XMLUtils.GetChildElementsWithNameAndNamespace(defaultListElement, "item", InvocablePlotInfo.PlotProfileNS);
						if(defaultListlst!=null)
						{
							for(Element defaultElement : defaultListlst) ((InvocablePlotContingencyReactionPick)cont.Reaction).DefaultList.add(XMLUtils.GetChildText(defaultElement));
						}
					}
					Element queryElement=XMLUtils.GetChildElementWithNameAndNamespace(reactionElement, "query", InvocablePlotInfo.PlotProfileNS);
					if(queryElement!=null)
					{
						((InvocablePlotContingencyReactionPick)cont.Reaction).Query=XMLUtils.GetChildText(queryElement);
					}
					break;
				}
				default:
				{
					throw new EnvironmentInformationSystemException("Invalid serialization provided");
				}
			}
			triggers.add(cont);
		}
		return triggers;
	}
	
	protected static PlotLocalEnvironment GetEnvironment(Element plotLocalEnvironmentElement) throws Exception
	{
		PlotLocalEnvironment env=new PlotLocalEnvironment();
		if(plotLocalEnvironmentElement==null)throw new EnvironmentInformationSystemException("Invalid serialization provided");
		Element plotLocalEnvironmentFilesElement=XMLUtils.GetChildElementWithNameAndNamespace(plotLocalEnvironmentElement, "files", InvocablePlotInfo.PlotProfileNS);
		if(plotLocalEnvironmentFilesElement!=null)
		{
			Element plotLocalEnvironmentInputFiles=XMLUtils.GetChildElementWithNameAndNamespace(plotLocalEnvironmentFilesElement, "in", InvocablePlotInfo.PlotProfileNS);
			if(plotLocalEnvironmentInputFiles!=null)
			{
				List<Element> plotLocalEnvironmentInputFilesElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(plotLocalEnvironmentInputFiles, "file", InvocablePlotInfo.PlotProfileNS);
				if(plotLocalEnvironmentInputFilesElementlst!=null)
				{
					for(Element plotLocalEnvironmentInputFilesElement : plotLocalEnvironmentInputFilesElementlst)
					{
						PlotLocalEnvironmentFile f=new PlotLocalEnvironmentFile();
						f.Direction=LocalEnvironmentFileDirection.In;
						if(!XMLUtils.AttributeExists(plotLocalEnvironmentInputFilesElement, "name"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
						if(!XMLUtils.AttributeExists(plotLocalEnvironmentInputFilesElement, "location"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
						if(!XMLUtils.AttributeExists(plotLocalEnvironmentInputFilesElement, "cleanup"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
						f.Location=XMLUtils.GetAttribute(plotLocalEnvironmentInputFilesElement, "location");
						f.CleanUp=Boolean.parseBoolean(XMLUtils.GetAttribute(plotLocalEnvironmentInputFilesElement, "cleanup"));
						f.Name=XMLUtils.GetAttribute(plotLocalEnvironmentInputFilesElement, "name");
						env.Files.add(f);
					}
				}
			}
			Element plotLocalEnvironmentOutputFiles=XMLUtils.GetChildElementWithNameAndNamespace(plotLocalEnvironmentFilesElement, "out", InvocablePlotInfo.PlotProfileNS);
			if(plotLocalEnvironmentOutputFiles!=null)
			{
				List<Element> plotLocalEnvironmentOutputFilesElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(plotLocalEnvironmentOutputFiles, "file", InvocablePlotInfo.PlotProfileNS);
				if(plotLocalEnvironmentOutputFilesElementlst!=null)
				{
					for(Element plotLocalEnvironmentOutputFilesElement : plotLocalEnvironmentOutputFilesElementlst)
					{
						PlotLocalEnvironmentFile f=new PlotLocalEnvironmentFile();
						f.Direction=LocalEnvironmentFileDirection.Out;
						if(!XMLUtils.AttributeExists(plotLocalEnvironmentOutputFilesElement, "name"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
						if(!XMLUtils.AttributeExists(plotLocalEnvironmentOutputFilesElement, "location"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
						if(!XMLUtils.AttributeExists(plotLocalEnvironmentOutputFilesElement, "isExecutable"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
						if(!XMLUtils.AttributeExists(plotLocalEnvironmentOutputFilesElement, "cleanup"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
						f.Location=XMLUtils.GetAttribute(plotLocalEnvironmentOutputFilesElement, "location");
						f.CleanUp=Boolean.parseBoolean(XMLUtils.GetAttribute(plotLocalEnvironmentOutputFilesElement, "cleanup"));
						f.Name=XMLUtils.GetAttribute(plotLocalEnvironmentOutputFilesElement, "name");
						f.IsExecutable=Boolean.parseBoolean(XMLUtils.GetAttribute(plotLocalEnvironmentOutputFilesElement, "isExecutable"));
						env.Files.add(f);
					}
				}
			}
		}
		Element plotLocalEnvironmentVariablesElement=XMLUtils.GetChildElementWithNameAndNamespace(plotLocalEnvironmentElement, "variables", InvocablePlotInfo.PlotProfileNS);
		if(plotLocalEnvironmentVariablesElement!=null)
		{
			List<Element> plotLocalEnvironmentVariablesElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(plotLocalEnvironmentFilesElement, "var", InvocablePlotInfo.PlotProfileNS);
			if(plotLocalEnvironmentVariablesElementlst!=null)
			{
				for(Element plotLocalEnvironmentVarElement : plotLocalEnvironmentVariablesElementlst)
				{
					PlotLocalEnvironmentVariable v=new PlotLocalEnvironmentVariable();
					if(!XMLUtils.AttributeExists(plotLocalEnvironmentVarElement, "name"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
					if(!XMLUtils.AttributeExists(plotLocalEnvironmentVarElement, "isFixed"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
					v.Name=XMLUtils.GetAttribute(plotLocalEnvironmentVarElement, "name");
					v.IsFixed=Boolean.parseBoolean(XMLUtils.GetAttribute(plotLocalEnvironmentVarElement, "isFixed"));
					if(v.IsFixed)
					{
						if(!XMLUtils.AttributeExists(plotLocalEnvironmentVarElement, "value"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
						v.Value=XMLUtils.GetAttribute(plotLocalEnvironmentVarElement, "value");
					}
					env.Variables.add(v);
				}
			}
		}
		return env;
	}

}
