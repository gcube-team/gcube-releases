//package gr.uoa.di.madgik.is.test;
//
//import java.io.File;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import gr.uoa.di.madgik.commons.utils.XMLUtils;
//import gr.uoa.di.madgik.is.InformationSystem;
//import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
//import gr.uoa.di.madgik.environment.is.elements.*;
//import gr.uoa.di.madgik.environment.is.elements.invocable.*;
//import gr.uoa.di.madgik.environment.is.elements.invocable.context.*;
//import gr.uoa.di.madgik.environment.is.elements.invocable.context.InvocableContext.*;
//import gr.uoa.di.madgik.environment.is.elements.plot.*;
//import gr.uoa.di.madgik.environment.is.elements.plot.errorhandling.*;
//import gr.uoa.di.madgik.environment.is.elements.plot.localenv.*;
//import gr.uoa.di.madgik.environment.is.elements.plot.localenv.PlotLocalEnvironmentFile.*;
//
//public class SetupEnvironment
//{
//	private static final String ExecutionProfileNS="http://profile.execution.madgik.di.uoa.gr";
//	
//	public static String RegisterNode(String HostName) throws EnvironmentInformationSystemException
//	{
//		NodeInfo info=new NodeInfo(HostName);
//		InformationSystem.RegisterNode(info,null);
//		return info.ID;
//	}
//	
//	public static String RegisterBoundaryListener(String NodeID, int port) throws EnvironmentInformationSystemException
//	{
//		BoundaryListenerInfo info = new BoundaryListenerInfo(NodeID, port);
//		InformationSystem.RegisterBoundaryListener(info,null);
//		return info.ID;
//	}
//	
//	public static void RegisterInvocableInstance(String NodeID, String InvocableID) throws EnvironmentInformationSystemException
//	{
//		InformationSystem.RegisterInvocableInstance(NodeID, InvocableID,null);
//	}
//	
//	public static String RegisterInvocable(File profile) throws EnvironmentInformationSystemException
//	{
//		InvocableProfileInfo info=SetupEnvironment.ParseInvocableProfile(profile);
//		InformationSystem.RegisterInvocable(info,null);
//		return info.ID;
//	}
//	
//	public static Set<String> RegisterPlots(String InvocableProfileID,File profile) throws EnvironmentInformationSystemException
//	{
//		Set<String> ids=new HashSet<String>();
//		InvocableProfileInfo info = InformationSystem.GetInvocableProfile(InvocableProfileID,null);
//		if(info==null) throw new EnvironmentInformationSystemException("Referenced Invocable profile not present");
//		Set<InvocablePlotInfo> plots=SetupEnvironment.ParseInvocablePlots(profile,info);
//		for(InvocablePlotInfo plot : plots)
//		{
//			InformationSystem.RegisterPlotOfInvocable(InvocableProfileID, plot,null);
//			ids.add(plot.ID);
//		}
//		return ids;
//	}
//	
//	private static Set<InvocablePlotInfo> ParseInvocablePlots(File profile,InvocableProfileInfo InvocableProfile) throws EnvironmentInformationSystemException
//	{
//		try
//		{
//			Set<InvocablePlotInfo> plots=new HashSet<InvocablePlotInfo>();
//			Document doc= XMLUtils.Deserialize(profile);
//			List<Element> plotElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(doc.getDocumentElement(), "plot", "http://profile.workflow.madgik.di.uoa.gr");
//			for(Element plotElement : plotElementlst)
//			{
//				InvocablePlotInfo ipi=null;
//				if(InvocableProfile instanceof PojoInvocableProfileInfo)
//				{
//					PojoPlotInfo pp=new PojoPlotInfo();
//					pp.InvocabeProfileID=InvocableProfile.ID;
//					pp.Name=SetupEnvironment.GetName(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "name", "http://profile.workflow.madgik.di.uoa.gr"));
//					pp.Description=SetupEnvironment.GetDescription(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "description", "http://profile.workflow.madgik.di.uoa.gr"));
//					pp.Triggers=SetupEnvironment.GetTriggers(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "contingencyReactions", "http://profile.workflow.madgik.di.uoa.gr"));
//					pp.LocalEnvironment=SetupEnvironment.GetEnvironment(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "localEnvironment", "http://profile.workflow.madgik.di.uoa.gr"));
//					Element plotMethodsElement=XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "methods", "http://profile.workflow.madgik.di.uoa.gr");
//					if(plotMethodsElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					List<Element> plotMethodElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(plotMethodsElement, "method", "http://profile.workflow.madgik.di.uoa.gr");
//					for(Element plotMethodElement : plotMethodElementlst)
//					{
//						PlotMethod pm=new PlotMethod();
//						if(!XMLUtils.AttributeExists(plotMethodElement, "order")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//						pm.Order=Integer.parseInt(XMLUtils.GetAttribute(plotMethodElement, "order"));
//						if(!XMLUtils.AttributeExists(plotMethodElement, "isConstructor")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//						pm.IsConstructor=Boolean.parseBoolean(XMLUtils.GetAttribute(plotMethodElement, "isConstructor"));
//						if(!pm.IsConstructor)
//						{
//							if(!XMLUtils.AttributeExists(plotMethodElement, "useReturn")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//							pm.UseReturnValue=Boolean.parseBoolean(XMLUtils.GetAttribute(plotMethodElement, "useReturn"));
//						}
//						Element plotMethodSignature=XMLUtils.GetChildElementWithNameAndNamespace(plotMethodElement, "signature", "http://profile.workflow.madgik.di.uoa.gr");
//						if(plotMethodSignature==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//						pm.Signature=XMLUtils.GetChildText(plotMethodSignature);
//						Element plotMethodParametersElement=XMLUtils.GetChildElementWithNameAndNamespace(plotMethodElement, "parameters", "http://profile.workflow.madgik.di.uoa.gr");
//						if(plotMethodParametersElement!=null)
//						{
//							List<Element> plotMethodParameterslst=XMLUtils.GetChildElementsWithNameAndNamespace(plotMethodParametersElement, "parameter", "http://profile.workflow.madgik.di.uoa.gr");
//							for(Element plotMethodParameterElement : plotMethodParameterslst)
//							{
//								PlotParameter ppar=new PlotParameter();
//								if(!XMLUtils.AttributeExists(plotMethodParameterElement, "isFixed"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//								ppar.IsFixed=Boolean.parseBoolean(XMLUtils.GetAttribute(plotMethodParameterElement, "isFixed"));
//								if(ppar.IsFixed) ppar.FixedValue=XMLUtils.GetChildText(plotMethodParameterElement);
//								if(!XMLUtils.AttributeExists(plotMethodParameterElement, "name"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//								ppar.ParameterName=XMLUtils.GetAttribute(plotMethodParameterElement, "name");
//								pm.Parameters.add(ppar);
//							}
//						}
//						pp.Methods.add(pm);
//					}
//					ipi=pp;
//				}
//				else if(InvocableProfile instanceof WSInvocableProfileInfo)
//				{
//					WSPlotInfo pp=new WSPlotInfo();
//					pp.InvocabeProfileID=InvocableProfile.ID;
//					pp.Name=SetupEnvironment.GetName(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "name", "http://profile.workflow.madgik.di.uoa.gr"));
//					pp.Description=SetupEnvironment.GetDescription(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "description", "http://profile.workflow.madgik.di.uoa.gr"));
//					pp.Triggers=SetupEnvironment.GetTriggers(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "contingencyReactions", "http://profile.workflow.madgik.di.uoa.gr"));
//					pp.LocalEnvironment=SetupEnvironment.GetEnvironment(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "localEnvironment", "http://profile.workflow.madgik.di.uoa.gr"));
//					Element plotMethodsElement=XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "methods", "http://profile.workflow.madgik.di.uoa.gr");
//					if(plotMethodsElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					List<Element> plotMethodElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(plotMethodsElement, "method", "http://profile.workflow.madgik.di.uoa.gr");
//					for(Element plotMethodElement : plotMethodElementlst)
//					{
//						PlotMethod pm=new PlotMethod();
//						if(!XMLUtils.AttributeExists(plotMethodElement, "order")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//						pm.Order=Integer.parseInt(XMLUtils.GetAttribute(plotMethodElement, "order"));
//						pm.IsConstructor=false;
//						if(!XMLUtils.AttributeExists(plotMethodElement, "useReturn")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//						pm.UseReturnValue=Boolean.parseBoolean(XMLUtils.GetAttribute(plotMethodElement, "useReturn"));
//						Element plotMethodSignature=XMLUtils.GetChildElementWithNameAndNamespace(plotMethodElement, "signature", "http://profile.workflow.madgik.di.uoa.gr");
//						if(plotMethodSignature==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//						pm.Signature=XMLUtils.GetChildText(plotMethodSignature);
//						Element plotMethodParametersElement=XMLUtils.GetChildElementWithNameAndNamespace(plotMethodElement, "parameters", "http://profile.workflow.madgik.di.uoa.gr");
//						if(plotMethodParametersElement!=null)
//						{
//							List<Element> plotMethodParameterslst=XMLUtils.GetChildElementsWithNameAndNamespace(plotMethodParametersElement, "parameter", "http://profile.workflow.madgik.di.uoa.gr");
//							for(Element plotMethodParameterElement : plotMethodParameterslst)
//							{
//								PlotParameter ppar=new PlotParameter();
//								if(!XMLUtils.AttributeExists(plotMethodParameterElement, "isFixed"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//								ppar.IsFixed=Boolean.parseBoolean(XMLUtils.GetAttribute(plotMethodParameterElement, "isFixed"));
//								if(ppar.IsFixed) ppar.FixedValue=XMLUtils.GetChildText(plotMethodParameterElement);
//								if(!XMLUtils.AttributeExists(plotMethodParameterElement, "name"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//								ppar.ParameterName=XMLUtils.GetAttribute(plotMethodParameterElement, "name");
//								pm.Parameters.add(ppar);
//							}
//						}
//						pp.Methods.add(pm);
//					}
//					ipi=pp;
//				}
//				else if(InvocableProfile instanceof ShellInvocableProfileInfo)
//				{
//					ShellPlotInfo pp=new ShellPlotInfo();
//					pp.InvocabeProfileID=InvocableProfile.ID;
//					pp.Name=SetupEnvironment.GetName(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "name", "http://profile.workflow.madgik.di.uoa.gr"));
//					pp.Description=SetupEnvironment.GetDescription(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "description", "http://profile.workflow.madgik.di.uoa.gr"));
//					pp.Triggers=SetupEnvironment.GetTriggers(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "contingencyReactions", "http://profile.workflow.madgik.di.uoa.gr"));
//					pp.LocalEnvironment=SetupEnvironment.GetEnvironment(XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "localEnvironment", "http://profile.workflow.madgik.di.uoa.gr"));
//					Element inputsElement=XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "inputs", "http://profile.workflow.madgik.di.uoa.gr");
//					if(inputsElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					List<Element> inputParameterElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(inputsElement, "parameter", "http://profile.workflow.madgik.di.uoa.gr");
//					if(inputParameterElementlst!=null)
//					{
//						for(Element inputParameterElement : inputParameterElementlst)
//						{
//							PlotShellParameter p=new PlotShellParameter();
//							if(!XMLUtils.AttributeExists(inputParameterElement, "isFixed")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//							if(!XMLUtils.AttributeExists(inputParameterElement, "name")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//							if(!XMLUtils.AttributeExists(inputParameterElement, "order")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//							if(!XMLUtils.AttributeExists(inputParameterElement, "isFile")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//							p.IsFixed=Boolean.parseBoolean(XMLUtils.GetAttribute(inputParameterElement, "isFixed"));
//							p.ParameterName =XMLUtils.GetAttribute(inputParameterElement, "name");
//							p.Order =Integer.parseInt(XMLUtils.GetAttribute(inputParameterElement, "order"));
//							p.IsFile =Boolean.parseBoolean(XMLUtils.GetAttribute(inputParameterElement, "isFile"));
//							if(p.IsFixed) p.FixedValue=XMLUtils.GetChildText(inputParameterElement);
//							pp.Parameters.add(p);
//						}
//					}
//					Element stdinElement=XMLUtils.GetChildElementWithNameAndNamespace(inputsElement, "stdin", "http://profile.workflow.madgik.di.uoa.gr");
//					if(stdinElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					if(!XMLUtils.AttributeExists(stdinElement, "use"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					pp.UseStdIn=Boolean.parseBoolean( XMLUtils.GetAttribute(stdinElement, "use"));
//					Element outputsElement=XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "outputs", "http://profile.workflow.madgik.di.uoa.gr");
//					if(outputsElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					Element stdoutElement=XMLUtils.GetChildElementWithNameAndNamespace(outputsElement, "stdout", "http://profile.workflow.madgik.di.uoa.gr");
//					if(stdoutElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					if(!XMLUtils.AttributeExists(stdoutElement, "use"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					pp.UseStdOut=Boolean.parseBoolean( XMLUtils.GetAttribute(stdoutElement, "use"));
//					Element stderrElement=XMLUtils.GetChildElementWithNameAndNamespace(outputsElement, "stderr", "http://profile.workflow.madgik.di.uoa.gr");
//					if(stderrElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					if(!XMLUtils.AttributeExists(stderrElement, "use"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					pp.UseStdErr=Boolean.parseBoolean( XMLUtils.GetAttribute(stderrElement, "use"));
//					Element stdexitElement=XMLUtils.GetChildElementWithNameAndNamespace(outputsElement, "stdexit", "http://profile.workflow.madgik.di.uoa.gr");
//					if(stdexitElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					if(!XMLUtils.AttributeExists(stdexitElement, "use"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					pp.UseStdExit=Boolean.parseBoolean( XMLUtils.GetAttribute(stdexitElement, "use"));
//					List<Element> errorMappingElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(stdexitElement, "errorMapping", "http://profile.workflow.madgik.di.uoa.gr");
//					if(errorMappingElementlst!=null)
//					{
//						for(Element errorMappingElement : errorMappingElementlst)
//						{
//							PlotErrorMapping pem=new PlotErrorMapping();
//							if(!XMLUtils.AttributeExists(errorMappingElement, "exitCode"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//							if(!XMLUtils.AttributeExists(errorMappingElement, "fullName"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//							if(!XMLUtils.AttributeExists(errorMappingElement, "simpleName"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//							pem.ExitCode=Integer.parseInt(XMLUtils.GetAttribute(errorMappingElement, "exitCode"));
//							pem.FullErrorName=XMLUtils.GetAttribute(errorMappingElement, "fullName");
//							pem.SimpleErrorName=XMLUtils.GetAttribute(errorMappingElement, "simpleName");
//							pem.Message=XMLUtils.GetChildText(errorMappingElement);
//							pp.ErrorMappings.add(pem);
//						}
//					}
//					ipi=pp;
//				}
//				else
//				{
//					throw new EnvironmentInformationSystemException("Invalid serialization provided");
//				}
//				plots.add(ipi);
//			}
//			return plots;
//		}
//		catch(Exception ex)
//		{
//			throw new EnvironmentInformationSystemException("Could not parse profile",ex);
//		}
//	}
//	
//	private static String GetName(Element plotElement) throws Exception
//	{
//		Element plotNameElement=XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "name", "http://profile.workflow.madgik.di.uoa.gr");
//		if(plotNameElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//		if(!XMLUtils.AttributeExists(plotNameElement, "value")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//		return XMLUtils.GetAttribute(plotNameElement, "value");
//	}
//	
//	private static String GetDescription(Element plotElement) throws Exception
//	{
//		Element plotDescriptionElement=XMLUtils.GetChildElementWithNameAndNamespace(plotElement, "description", "http://profile.workflow.madgik.di.uoa.gr");
//		if(plotDescriptionElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//		return XMLUtils.GetChildText(plotDescriptionElement);
//	}
//	
//	private static Set<InvocablePlotContingency> GetTriggers(Element plotTriggersElement) throws Exception
//	{
//		Set<InvocablePlotContingency> triggers=new HashSet<InvocablePlotContingency>();
//		if(plotTriggersElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//		List<Element> plotTriggersElementslst=XMLUtils.GetChildElementsWithNameAndNamespace(plotTriggersElement, "contingency", "http://profile.workflow.madgik.di.uoa.gr");
//		for(Element plotContingencyElement : plotTriggersElementslst)
//		{
//			InvocablePlotContingency cont=new InvocablePlotContingency();
//			Element triggElement=XMLUtils.GetChildElementWithNameAndNamespace(plotContingencyElement, "trigger", "http://profile.workflow.madgik.di.uoa.gr");
//			if(triggElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//			cont.Trigger=new InvocablePlotContingencyTrigger();
//			if(XMLUtils.AttributeExists(triggElement, "isFullName"))
//			{
//				cont.Trigger.IsFullName=Boolean.parseBoolean(XMLUtils.GetAttribute(triggElement, "isFullName"));
//				cont.Trigger.ErrorName=XMLUtils.GetChildText(triggElement);
//				if(cont.Trigger.ErrorName.trim().length()==0) cont.Trigger.ErrorName=null;
//			}
//			Element reactionElement=XMLUtils.GetChildElementWithNameAndNamespace(plotContingencyElement, "reaction", "http://profile.workflow.madgik.di.uoa.gr");
//			if(reactionElement==null) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//			if(!XMLUtils.AttributeExists(reactionElement, "type")) throw new EnvironmentInformationSystemException("Invalid serialization provided");
//			IInvocablePlotContingencyReaction.ReactionType react=IInvocablePlotContingencyReaction.ReactionType.valueOf(XMLUtils.GetAttribute(reactionElement, "type"));
//			switch(react)
//			{
//				case None:
//				{
//					cont.Reaction=new InvocablePlotContingencyReactionNone();
//					break;
//				}
//				case Retry:
//				{
//					cont.Reaction=new InvocablePlotContingencyReactionRetry();
//					if(!XMLUtils.AttributeExists(reactionElement, "retries"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					if(!XMLUtils.AttributeExists(reactionElement, "interval"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					((InvocablePlotContingencyReactionRetry)cont.Reaction).NumberOfRetries=Integer.parseInt(XMLUtils.GetAttribute(reactionElement, "retries"));
//					((InvocablePlotContingencyReactionRetry)cont.Reaction).RetryInterval=Long.parseLong(XMLUtils.GetAttribute(reactionElement, "interval"));
//					break;
//				}
//				case Pick:
//				{
//					cont.Reaction=new InvocablePlotContingencyReactionPick();
//					if(!XMLUtils.AttributeExists(reactionElement, "exhaust"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					((InvocablePlotContingencyReactionPick)cont.Reaction).ExhaustLists=Boolean.parseBoolean(XMLUtils.GetAttribute(reactionElement, "exhaust"));
//					Element defaultListElement=XMLUtils.GetChildElementWithNameAndNamespace(reactionElement, "defaultList", "http://profile.workflow.madgik.di.uoa.gr");
//					if(defaultListElement!=null)
//					{
//						List<Element> defaultListlst=XMLUtils.GetChildElementsWithNameAndNamespace(defaultListElement, "item", "http://profile.workflow.madgik.di.uoa.gr");
//						if(defaultListlst!=null)
//						{
//							for(Element defaultElement : defaultListlst) ((InvocablePlotContingencyReactionPick)cont.Reaction).DefaultList.add(XMLUtils.GetChildText(defaultElement));
//						}
//					}
//					Element queryElement=XMLUtils.GetChildElementWithNameAndNamespace(reactionElement, "query", "http://profile.workflow.madgik.di.uoa.gr");
//					if(queryElement!=null)
//					{
//						((InvocablePlotContingencyReactionPick)cont.Reaction).Query=XMLUtils.GetChildText(queryElement);
//					}
//					break;
//				}
//				default:
//				{
//					throw new EnvironmentInformationSystemException("Invalid serialization provided");
//				}
//			}
//			triggers.add(cont);
//		}
//		return triggers;
//	}
//	
//	private static PlotLocalEnvironment GetEnvironment(Element plotLocalEnvironmentElement) throws Exception
//	{
//		PlotLocalEnvironment env=new PlotLocalEnvironment();
//		if(plotLocalEnvironmentElement==null)throw new EnvironmentInformationSystemException("Invalid serialization provided");
//		Element plotLocalEnvironmentFilesElement=XMLUtils.GetChildElementWithNameAndNamespace(plotLocalEnvironmentElement, "files", "http://profile.workflow.madgik.di.uoa.gr");
//		if(plotLocalEnvironmentFilesElement==null)throw new EnvironmentInformationSystemException("Invalid serialization provided");
//		List<Element> plotLocalEnvironmentInputFilesElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(plotLocalEnvironmentFilesElement, "in", "http://profile.workflow.madgik.di.uoa.gr");
//		if(plotLocalEnvironmentInputFilesElementlst!=null)
//		{
//			for(Element plotLocalEnvironmentInputFilesElement : plotLocalEnvironmentInputFilesElementlst)
//			{
//				PlotLocalEnvironmentFile f=new PlotLocalEnvironmentFile();
//				f.Direction=LocalEnvironmentFileDirection.In;
//				if(!XMLUtils.AttributeExists(plotLocalEnvironmentInputFilesElement, "name"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//				if(!XMLUtils.AttributeExists(plotLocalEnvironmentInputFilesElement, "location"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//				if(!XMLUtils.AttributeExists(plotLocalEnvironmentInputFilesElement, "cleanup"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//				f.Location=XMLUtils.GetAttribute(plotLocalEnvironmentInputFilesElement, "location");
//				f.CleanUp=Boolean.parseBoolean(XMLUtils.GetAttribute(plotLocalEnvironmentInputFilesElement, "cleanup"));
//				f.Name=XMLUtils.GetAttribute(plotLocalEnvironmentInputFilesElement, "name");
//				env.Files.add(f);
//			}
//		}
//		List<Element> plotLocalEnvironmentOutputFilesElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(plotLocalEnvironmentFilesElement, "out", "http://profile.workflow.madgik.di.uoa.gr");
//		if(plotLocalEnvironmentOutputFilesElementlst!=null)
//		{
//			for(Element plotLocalEnvironmentOutputFilesElement : plotLocalEnvironmentOutputFilesElementlst)
//			{
//				PlotLocalEnvironmentFile f=new PlotLocalEnvironmentFile();
//				f.Direction=LocalEnvironmentFileDirection.Out;
//				if(!XMLUtils.AttributeExists(plotLocalEnvironmentOutputFilesElement, "name"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//				if(!XMLUtils.AttributeExists(plotLocalEnvironmentOutputFilesElement, "location"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//				if(!XMLUtils.AttributeExists(plotLocalEnvironmentOutputFilesElement, "isExecutable"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//				if(!XMLUtils.AttributeExists(plotLocalEnvironmentOutputFilesElement, "cleanup"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//				f.Location=XMLUtils.GetAttribute(plotLocalEnvironmentOutputFilesElement, "location");
//				f.CleanUp=Boolean.parseBoolean(XMLUtils.GetAttribute(plotLocalEnvironmentOutputFilesElement, "cleanup"));
//				f.Name=XMLUtils.GetAttribute(plotLocalEnvironmentOutputFilesElement, "name");
//				f.IsExecutable=Boolean.parseBoolean(XMLUtils.GetAttribute(plotLocalEnvironmentOutputFilesElement, "isExecutable"));
//				env.Files.add(f);
//			}
//		}
//		Element plotLocalEnvironmentVariablesElement=XMLUtils.GetChildElementWithNameAndNamespace(plotLocalEnvironmentElement, "variables", "http://profile.workflow.madgik.di.uoa.gr");
//		if(plotLocalEnvironmentVariablesElement==null)throw new EnvironmentInformationSystemException("Invalid serialization provided");
//		List<Element> plotLocalEnvironmentVariablesElementlst=XMLUtils.GetChildElementsWithNameAndNamespace(plotLocalEnvironmentFilesElement, "var", "http://profile.workflow.madgik.di.uoa.gr");
//		if(plotLocalEnvironmentVariablesElementlst!=null)
//		{
//			for(Element plotLocalEnvironmentVarElement : plotLocalEnvironmentVariablesElementlst)
//			{
//				PlotLocalEnvironmentVariable v=new PlotLocalEnvironmentVariable();
//				if(!XMLUtils.AttributeExists(plotLocalEnvironmentVarElement, "name"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//				if(!XMLUtils.AttributeExists(plotLocalEnvironmentVarElement, "isFixed"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//				v.Name=XMLUtils.GetAttribute(plotLocalEnvironmentVarElement, "name");
//				v.IsFixed=Boolean.parseBoolean(XMLUtils.GetAttribute(plotLocalEnvironmentVarElement, "isFixed"));
//				if(v.IsFixed)
//				{
//					if(!XMLUtils.AttributeExists(plotLocalEnvironmentVarElement, "value"))throw new EnvironmentInformationSystemException("Invalid serialization provided");
//					v.Value=XMLUtils.GetAttribute(plotLocalEnvironmentVarElement, "value");
//				}
//				env.Variables.add(v);
//			}
//		}
//		return env;
//	}
//	
//	private static InvocableProfileInfo ParseInvocableProfile(File profile) throws EnvironmentInformationSystemException
//	{
//		try
//		{
//			InvocableProfileInfo invProfile=null;
//			Document doc= XMLUtils.Deserialize(profile);
//			if(!XMLUtils.AttributeExists(doc.getDocumentElement(), "type")) throw new EnvironmentInformationSystemException("Not valid serialization");
//			if(XMLUtils.GetAttribute(doc.getDocumentElement(), "type").equalsIgnoreCase("Pojo"))
//			{
//				PojoInvocableProfileInfo pojoProfile=new PojoInvocableProfileInfo();
//				invProfile=pojoProfile;
//				Element itemElem=XMLUtils.GetChildElementWithNameAndNamespace(doc.getDocumentElement(), "item",SetupEnvironment.ExecutionProfileNS);
//				if(itemElem==null) throw new EnvironmentInformationSystemException("Invalid serialization");
//				Element classNameElem=XMLUtils.GetChildElementWithNameAndNamespace(itemElem, "className", SetupEnvironment.ExecutionProfileNS);
//				if(classNameElem==null) throw new EnvironmentInformationSystemException("Invalid serialization");
//				if(!XMLUtils.AttributeExists(classNameElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
//				pojoProfile.ClassName=XMLUtils.GetAttribute(classNameElem, "value");
//				Element contextElement=XMLUtils.GetChildElementWithNameAndNamespace(itemElem, "context",SetupEnvironment.ExecutionProfileNS);
//				if(contextElement==null) throw new EnvironmentInformationSystemException("Invalid serialization");
//				if(!XMLUtils.AttributeExists(contextElement, "supported")) throw new EnvironmentInformationSystemException("Not valid serialization");
//				pojoProfile.ExecutionContext=new InvocableContext();
//				pojoProfile.ExecutionContext.Supported=Boolean.parseBoolean(XMLUtils.GetAttribute(contextElement, "supported"));
//				if(pojoProfile.ExecutionContext.Supported)
//				{
//					Element contextKeepAliveElem=XMLUtils.GetChildElementWithNameAndNamespace(contextElement, "keepAlive", SetupEnvironment.ExecutionProfileNS);
//					if(contextKeepAliveElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//					if(!XMLUtils.AttributeExists(contextKeepAliveElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
//					pojoProfile.ExecutionContext.KeepAlive=Boolean.parseBoolean(XMLUtils.GetAttribute(contextKeepAliveElem, "value"));
//					Element contextReportsProgressElem=XMLUtils.GetChildElementWithNameAndNamespace(contextElement, "reportsProgress", SetupEnvironment.ExecutionProfileNS);
//					if(contextReportsProgressElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//					if(!XMLUtils.AttributeExists(contextReportsProgressElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
//					pojoProfile.ExecutionContext.ReportsProgress=Boolean.parseBoolean(XMLUtils.GetAttribute(contextReportsProgressElem, "value"));
//					pojoProfile.ExecutionContext.ProgressProvider=ProgressReportingProvider.Local;
//					Element contextgRSProxyElem=XMLUtils.GetChildElementWithNameAndNamespace(contextElement, "gRSProxy", SetupEnvironment.ExecutionProfileNS);
//					if(contextgRSProxyElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//					if(!XMLUtils.AttributeExists(contextgRSProxyElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
//					pojoProfile.ExecutionContext.ProxygRS=new InvocableContextgRSProxy();
//					pojoProfile.ExecutionContext.ProxygRS.SupplyProxy=Boolean.parseBoolean(XMLUtils.GetAttribute(contextgRSProxyElem, "value"));
//					if(pojoProfile.ExecutionContext.ProxygRS.SupplyProxy)
//					{
//						Element contextgRSProxyEncryptElem=XMLUtils.GetChildElementWithNameAndNamespace(contextgRSProxyElem, "encrypt",SetupEnvironment.ExecutionProfileNS);
//						if(contextgRSProxyEncryptElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//						if(!XMLUtils.AttributeExists(contextgRSProxyEncryptElem, "value")) throw new EnvironmentInformationSystemException("not valie serialization");
//						pojoProfile.ExecutionContext.ProxygRS.Encrypt=Boolean.parseBoolean(XMLUtils.GetAttribute(contextgRSProxyEncryptElem, "value"));
//						Element contextgRSProxyAuthenticateElem=XMLUtils.GetChildElementWithNameAndNamespace(contextgRSProxyElem, "authenticate",SetupEnvironment.ExecutionProfileNS);
//						if(contextgRSProxyAuthenticateElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//						if(!XMLUtils.AttributeExists(contextgRSProxyAuthenticateElem, "value")) throw new EnvironmentInformationSystemException("not valie serialization");
//						pojoProfile.ExecutionContext.ProxygRS.Authenticate=Boolean.parseBoolean(XMLUtils.GetAttribute(contextgRSProxyAuthenticateElem, "value"));
//					}
//				}
//				Element callsElem=XMLUtils.GetChildElementWithNameAndNamespace(doc.getDocumentElement(), "calls", SetupEnvironment.ExecutionProfileNS);
//				if(callsElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//				List<Element> calllst=XMLUtils.GetChildElementsWithNameAndNamespace(callsElem, "call", SetupEnvironment.ExecutionProfileNS);
//				for(Element callElem : calllst)
//				{
//					Method m=new Method();
//					Element callMethodElement =XMLUtils.GetChildElementWithNameAndNamespace(callElem, "method", SetupEnvironment.ExecutionProfileNS);
//					if(callMethodElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					if(!XMLUtils.AttributeExists(callMethodElement, "name")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					m.Name=XMLUtils.GetAttribute(callMethodElement, "name");
//					if(pojoProfile.ClassName.equals(m.Name)) m.IsConstructor=true;
//					else m.IsConstructor=false;
//					Element callSignatureElement =XMLUtils.GetChildElementWithNameAndNamespace(callElem, "signature", SetupEnvironment.ExecutionProfileNS);
//					if(callSignatureElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					m.Signature=XMLUtils.GetChildText(callSignatureElement);
//					List<Element> callMethodArgumentsElementslst=XMLUtils.GetChildElementsWithNameAndNamespace(callElem, "argument", SetupEnvironment.ExecutionProfileNS);
//					for(Element callMethodArgumentElement : callMethodArgumentsElementslst)
//					{
//						Parameter p=new Parameter();
//						Element callMethodArgumentOrderElement=XMLUtils.GetChildElementWithNameAndNamespace(callMethodArgumentElement, "order", SetupEnvironment.ExecutionProfileNS);
//						if(callMethodArgumentOrderElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//						if(!XMLUtils.AttributeExists(callMethodArgumentOrderElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//						p.Order=Integer.parseInt(XMLUtils.GetAttribute(callMethodArgumentOrderElement, "value"));
//						Element callMethodArgumentNameElement=XMLUtils.GetChildElementWithNameAndNamespace(callMethodArgumentElement, "name", SetupEnvironment.ExecutionProfileNS);
//						if(callMethodArgumentNameElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//						if(!XMLUtils.AttributeExists(callMethodArgumentNameElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//						p.Name=XMLUtils.GetAttribute(callMethodArgumentNameElement, "value");
//						Element callMethodArgumentTokenElement=XMLUtils.GetChildElementWithNameAndNamespace(callMethodArgumentElement, "token", SetupEnvironment.ExecutionProfileNS);
//						if(callMethodArgumentTokenElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//						if(!XMLUtils.AttributeExists(callMethodArgumentTokenElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//						p.Token=XMLUtils.GetAttribute(callMethodArgumentTokenElement, "value");
//						p.Type=SetupEnvironment.GetParameterType(callMethodArgumentElement,false);
//						m.Add(p);
//					}
//					Element returnElem=XMLUtils.GetChildElementWithNameAndNamespace(callElem, "return", SetupEnvironment.ExecutionProfileNS);
//					if(returnElem!=null)
//					{
//						m.ReturnValue=SetupEnvironment.GetParameterType(returnElem,false);
//					}
//					pojoProfile.Add(m);
//				}
//			}
//			else if(XMLUtils.GetAttribute(doc.getDocumentElement(), "type").equalsIgnoreCase("WS"))
//			{
//				WSInvocableProfileInfo wsProfile=new WSInvocableProfileInfo();
//				invProfile=wsProfile;
//				Element itemElem=XMLUtils.GetChildElementWithNameAndNamespace(doc.getDocumentElement(), "item",SetupEnvironment.ExecutionProfileNS);
//				if(itemElem==null) throw new EnvironmentInformationSystemException("Invalid serialization");
//				Element classNameElem=XMLUtils.GetChildElementWithNameAndNamespace(itemElem, "className", SetupEnvironment.ExecutionProfileNS);
//				if(classNameElem==null) throw new EnvironmentInformationSystemException("Invalid serialization");
//				if(!XMLUtils.AttributeExists(classNameElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
//				wsProfile.ClassName=XMLUtils.GetAttribute(classNameElem, "value");
//				Element contextElement=XMLUtils.GetChildElementWithNameAndNamespace(itemElem, "context",SetupEnvironment.ExecutionProfileNS);
//				if(contextElement==null) throw new EnvironmentInformationSystemException("Invalid serialization");
//				if(!XMLUtils.AttributeExists(contextElement, "supported")) throw new EnvironmentInformationSystemException("Not valid serialization");
//				wsProfile.ExecutionContext=new InvocableContext();
//				wsProfile.ExecutionContext.Supported=Boolean.parseBoolean(XMLUtils.GetAttribute(contextElement, "supported"));
//				if(wsProfile.ExecutionContext.Supported)
//				{
//					Element contextKeepAliveElem=XMLUtils.GetChildElementWithNameAndNamespace(contextElement, "keepAlive", SetupEnvironment.ExecutionProfileNS);
//					if(contextKeepAliveElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//					if(!XMLUtils.AttributeExists(contextKeepAliveElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
//					wsProfile.ExecutionContext.KeepAlive=Boolean.parseBoolean(XMLUtils.GetAttribute(contextKeepAliveElem, "value"));
//					Element contextReportsProgressElem=XMLUtils.GetChildElementWithNameAndNamespace(contextElement, "reportsProgress", SetupEnvironment.ExecutionProfileNS);
//					if(contextReportsProgressElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//					if(!XMLUtils.AttributeExists(contextReportsProgressElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
//					wsProfile.ExecutionContext.ReportsProgress=Boolean.parseBoolean(XMLUtils.GetAttribute(contextReportsProgressElem, "value"));
//					if(wsProfile.ExecutionContext.ReportsProgress)
//					{
//						Element contextProviderTypeElement=XMLUtils.GetChildElementWithNameAndNamespace(contextReportsProgressElem, "progressProvider", SetupEnvironment.ExecutionProfileNS);
//						if(contextProviderTypeElement==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//						if(!XMLUtils.AttributeExists(contextProviderTypeElement, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
//						wsProfile.ExecutionContext.ProgressProvider=ProgressReportingProvider.valueOf(XMLUtils.GetAttribute(contextProviderTypeElement, "value"));
//					}
//					Element contextgRSProxyElem=XMLUtils.GetChildElementWithNameAndNamespace(contextElement, "gRSProxy", SetupEnvironment.ExecutionProfileNS);
//					if(contextgRSProxyElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//					if(!XMLUtils.AttributeExists(contextgRSProxyElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
//					wsProfile.ExecutionContext.ProxygRS=new InvocableContextgRSProxy();
//					wsProfile.ExecutionContext.ProxygRS.SupplyProxy=Boolean.parseBoolean(XMLUtils.GetAttribute(contextgRSProxyElem, "value"));
//					if(wsProfile.ExecutionContext.ProxygRS.SupplyProxy)
//					{
//						Element contextgRSProxyEncryptElem=XMLUtils.GetChildElementWithNameAndNamespace(contextgRSProxyElem, "encrypt",SetupEnvironment.ExecutionProfileNS);
//						if(contextgRSProxyEncryptElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//						if(!XMLUtils.AttributeExists(contextgRSProxyEncryptElem, "value")) throw new EnvironmentInformationSystemException("not valie serialization");
//						wsProfile.ExecutionContext.ProxygRS.Encrypt=Boolean.parseBoolean(XMLUtils.GetAttribute(contextgRSProxyEncryptElem, "value"));
//						Element contextgRSProxyAuthenticateElem=XMLUtils.GetChildElementWithNameAndNamespace(contextgRSProxyElem, "authenticate",SetupEnvironment.ExecutionProfileNS);
//						if(contextgRSProxyAuthenticateElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//						if(!XMLUtils.AttributeExists(contextgRSProxyAuthenticateElem, "value")) throw new EnvironmentInformationSystemException("not valie serialization");
//						wsProfile.ExecutionContext.ProxygRS.Authenticate=Boolean.parseBoolean(XMLUtils.GetAttribute(contextgRSProxyAuthenticateElem, "value"));
//					}
//				}
//				Element callsElem=XMLUtils.GetChildElementWithNameAndNamespace(doc.getDocumentElement(), "calls", SetupEnvironment.ExecutionProfileNS);
//				if(callsElem==null) throw new EnvironmentInformationSystemException("Not valid serialization");
//				List<Element> calllst=XMLUtils.GetChildElementsWithNameAndNamespace(callsElem, "call", SetupEnvironment.ExecutionProfileNS);
//				for(Element callElem : calllst)
//				{
//					WSMethod m=new WSMethod();
//					Element callMethodElement =XMLUtils.GetChildElementWithNameAndNamespace(callElem, "method", SetupEnvironment.ExecutionProfileNS);
//					if(callMethodElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					if(!XMLUtils.AttributeExists(callMethodElement, "name")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					m.Name=XMLUtils.GetAttribute(callMethodElement, "name");
//					m.IsConstructor=false;
//					Element callMethodURNElement=XMLUtils.GetChildElementWithNameAndNamespace(callElem, "methodURN", SetupEnvironment.ExecutionProfileNS);
//					if(callMethodURNElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					if(!XMLUtils.AttributeExists(callMethodURNElement, "name")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					m.MethodURN=XMLUtils.GetAttribute(callMethodURNElement, "name");
//					Element callSignatureElement =XMLUtils.GetChildElementWithNameAndNamespace(callElem, "signature", SetupEnvironment.ExecutionProfileNS);
//					if(callSignatureElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					m.Signature=XMLUtils.GetChildText(callSignatureElement);
//					Element callEnvelopeTemplateElement=XMLUtils.GetChildElementWithNameAndNamespace(callElem, "envelopeTemplate", SetupEnvironment.ExecutionProfileNS);
//					if(callEnvelopeTemplateElement==null) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					m.EnvelopeTemplate=XMLUtils.GetChildCDataText(callEnvelopeTemplateElement);
//					if(m.EnvelopeTemplate==null || m.EnvelopeTemplate.trim().length()==0) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					Element callExecutionContextTokenElement=XMLUtils.GetChildElementWithNameAndNamespace(callElem, "executionContextToken", SetupEnvironment.ExecutionProfileNS);
//					if(callExecutionContextTokenElement==null && wsProfile.ExecutionContext.Supported) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					if(callExecutionContextTokenElement!=null)
//					{
//						m.ExecutioContextToken=XMLUtils.GetChildText(callExecutionContextTokenElement);
//						if(m.ExecutioContextToken==null || m.ExecutioContextToken.trim().length()==0) throw new EnvironmentInformationSystemException("Not Valid serialization");
//					}
//					List<Element> callMethodArgumentsElementslst=XMLUtils.GetChildElementsWithNameAndNamespace(callElem, "argument", SetupEnvironment.ExecutionProfileNS);
//					for(Element callMethodArgumentElement : callMethodArgumentsElementslst)
//					{
//						Parameter p=new Parameter();
//						Element callMethodArgumentOrderElement=XMLUtils.GetChildElementWithNameAndNamespace(callMethodArgumentElement, "order", SetupEnvironment.ExecutionProfileNS);
//						if(callMethodArgumentOrderElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//						if(!XMLUtils.AttributeExists(callMethodArgumentOrderElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//						p.Order=Integer.parseInt(XMLUtils.GetAttribute(callMethodArgumentOrderElement, "value"));
//						Element callMethodArgumentNameElement=XMLUtils.GetChildElementWithNameAndNamespace(callMethodArgumentElement, "name", SetupEnvironment.ExecutionProfileNS);
//						if(callMethodArgumentNameElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//						if(!XMLUtils.AttributeExists(callMethodArgumentNameElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//						p.Name=XMLUtils.GetAttribute(callMethodArgumentNameElement, "value");
//						Element callMethodArgumentTokenElement=XMLUtils.GetChildElementWithNameAndNamespace(callMethodArgumentElement, "token", SetupEnvironment.ExecutionProfileNS);
//						if(callMethodArgumentTokenElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//						if(!XMLUtils.AttributeExists(callMethodArgumentTokenElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//						p.Token=XMLUtils.GetAttribute(callMethodArgumentTokenElement, "value");
//						p.Type=SetupEnvironment.GetParameterType(callMethodArgumentElement,true);
//						m.Add(p);
//					}
//					Element returnElem=XMLUtils.GetChildElementWithNameAndNamespace(callElem, "return", SetupEnvironment.ExecutionProfileNS);
//					if(returnElem!=null)
//					{
//						m.ReturnValue=SetupEnvironment.GetParameterType(returnElem,true);
//					}
//					wsProfile.Add(m);
//				}
//			}
//			else if(XMLUtils.GetAttribute(doc.getDocumentElement(), "type").equalsIgnoreCase("Shell"))
//			{
//				ShellInvocableProfileInfo shellProfile=new ShellInvocableProfileInfo();
//				invProfile=shellProfile;
//				Element itemElem=XMLUtils.GetChildElementWithNameAndNamespace(doc.getDocumentElement(), "item",SetupEnvironment.ExecutionProfileNS);
//				if(itemElem==null) throw new EnvironmentInformationSystemException("Invalid serialization");
//				Element executableNameElem=XMLUtils.GetChildElementWithNameAndNamespace(itemElem, "executableName", SetupEnvironment.ExecutionProfileNS);
//				if(executableNameElem==null) throw new EnvironmentInformationSystemException("Invalid serialization");
//				if(!XMLUtils.AttributeExists(executableNameElem, "value")) throw new EnvironmentInformationSystemException("Not valid serialization");
//				shellProfile.ExecutableName=XMLUtils.GetAttribute(executableNameElem, "value");
//				Element argumentsElem=XMLUtils.GetChildElementWithNameAndNamespace(itemElem, "arguments", SetupEnvironment.ExecutionProfileNS);
//				if(argumentsElem==null) throw new EnvironmentInformationSystemException("Invalid serialization");
//				if(!XMLUtils.AttributeExists(argumentsElem, "bound")) throw new EnvironmentInformationSystemException("Not valid serialization");
//				shellProfile.AreParametersBound=Boolean.parseBoolean(XMLUtils.GetAttribute(argumentsElem, "bound"));
//				if(shellProfile.AreParametersBound)
//				{
//					List<Element> shellArgumentsElementslst=XMLUtils.GetChildElementsWithNameAndNamespace(argumentsElem, "argument", SetupEnvironment.ExecutionProfileNS);
//					for(Element shellArgumentElement : shellArgumentsElementslst)
//					{
//						Parameter p=new Parameter();
//						Element shellArgumentOrderElement=XMLUtils.GetChildElementWithNameAndNamespace(shellArgumentElement, "order", SetupEnvironment.ExecutionProfileNS);
//						if(shellArgumentOrderElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//						if(!XMLUtils.AttributeExists(shellArgumentOrderElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//						p.Order=Integer.parseInt(XMLUtils.GetAttribute(shellArgumentOrderElement, "value"));
//						Element shellArgumentNameElement=XMLUtils.GetChildElementWithNameAndNamespace(shellArgumentElement, "name", SetupEnvironment.ExecutionProfileNS);
//						if(shellArgumentNameElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//						if(!XMLUtils.AttributeExists(shellArgumentNameElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//						p.Name=XMLUtils.GetAttribute(shellArgumentNameElement, "value");
//						Element shellArgumentTokenElement=XMLUtils.GetChildElementWithNameAndNamespace(shellArgumentElement, "token", SetupEnvironment.ExecutionProfileNS);
//						if(shellArgumentTokenElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//						if(!XMLUtils.AttributeExists(shellArgumentTokenElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//						p.Token=XMLUtils.GetAttribute(shellArgumentTokenElement, "value");
//						p.Type=SetupEnvironment.GetParameterType(shellArgumentElement,false);
//						shellProfile.Parameters.add(p);
//					}
//				}
//			}
//			else
//			{
//				throw new EnvironmentInformationSystemException("Not valid serialization");
//			}
//			return invProfile;
//		}
//		catch(Exception ex)
//		{
//			throw new EnvironmentInformationSystemException("Could not parse profile",ex);
//		}
//	}
//	
//	private static ParameterType GetParameterType(Element parentElement,boolean extendedParameter) throws Exception
//	{
//		ParameterType pt=null;
//		if(!extendedParameter) pt=new ParameterType();
//		else pt=new WSParameterType();
//		Element tmpTypeElement=XMLUtils.GetChildElementWithNameAndNamespace(parentElement, "type", SetupEnvironment.ExecutionProfileNS);
//		if(tmpTypeElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//		if(!XMLUtils.AttributeExists(tmpTypeElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//		pt.Type=XMLUtils.GetAttribute(tmpTypeElement, "value");
//		Element tmpConverterElement=XMLUtils.GetChildElementWithNameAndNamespace(parentElement, "converter", SetupEnvironment.ExecutionProfileNS);
//		if(tmpConverterElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//		if(!XMLUtils.AttributeExists(tmpConverterElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//		pt.Converter=XMLUtils.GetAttribute(tmpConverterElement, "value");
//		Element tmpEngineTypeElement=XMLUtils.GetChildElementWithNameAndNamespace(parentElement, "engineType", SetupEnvironment.ExecutionProfileNS);
//		if(tmpEngineTypeElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
//		if(!XMLUtils.AttributeExists(tmpEngineTypeElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//		pt.EngineType=XMLUtils.GetAttribute(tmpEngineTypeElement, "value");
//		if(extendedParameter)
//		{
//			Element returnExtractElem=XMLUtils.GetChildElementWithNameAndNamespace(parentElement, "extract", SetupEnvironment.ExecutionProfileNS);
//			((WSParameterType)pt).ExtractExpression=null;
//			if(returnExtractElem!=null)
//			{
//				if(!XMLUtils.AttributeExists(returnExtractElem, "type")) throw new EnvironmentInformationSystemException("Not Valid serialization");
//				((WSParameterType)pt).ExpressionExtractType=WSMethod.ExtractType.valueOf(XMLUtils.GetAttribute(returnExtractElem, "type"));
//				((WSParameterType)pt).ExtractExpression=XMLUtils.GetChildText(returnExtractElem);
//				if(((WSParameterType)pt).ExtractExpression==null || ((WSParameterType)pt).ExtractExpression.trim().length()==0) throw new EnvironmentInformationSystemException("Not Valid serialization");
//			}
//		}
//		return pt;
//	}
//}
