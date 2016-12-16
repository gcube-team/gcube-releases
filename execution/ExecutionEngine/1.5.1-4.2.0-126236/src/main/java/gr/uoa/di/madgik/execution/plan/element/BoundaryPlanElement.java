package gr.uoa.di.madgik.execution.plan.element;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.attachment.ExecutionAttachment;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.contingency.IContingencyReaction;
import gr.uoa.di.madgik.execution.plan.element.invocable.BoundaryConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.BoundaryHandler;
import gr.uoa.di.madgik.execution.plan.element.variable.VariableCollection;
import gr.uoa.di.madgik.execution.utils.BoundaryIsolationInfo;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import gr.uoa.di.madgik.execution.utils.PlanElementUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class represents a requirement that the subplan that is stored in the {@link BoundaryPlanElement#Root} is to
 * be executed in a remote execution container. The access info for this remote container is stored in 
 * {@link BoundaryPlanElement#Config}. Additionally to the subplan moved to the remote host along with the 
 * subset of the variables as defined by {@link BoundaryPlanElement#GetNeededVariableNames()}, the local files included
 * in the {@link BoundaryPlanElement#Attachments} set are also moved using the same connection. The execution in the
 * remote container can be isolated so as any byproducts of the execution do not overlap with other executions
 * that take place in parallel. Files that should be cleaned up after execution can also be declared. This
 * element also supports contingency triggers of {@link IContingencyReaction.ReactionType#Pick} so that in case
 * of error a different execution container can be picked and the execution restarted there. After the execution
 * in the remote execution container is finished, the variables that are modified by the subplan are send back to
 * the caller and are used to update the local variables copy as defined by 
 * {@link BoundaryPlanElement#GetModifiedVariableNames()}. If the execution terminated with an error, the error is 
 * retrieved and re-thrown.
 * 
 * @author gpapanikos
 */
public class BoundaryPlanElement extends PlanElementBase
{
	
	/** The logger. */
	private static final Logger logger=LoggerFactory.getLogger(BoundaryPlanElement.class); 
	
	/** The ID of the element */
	private String ID = UUID.randomUUID().toString();
	
	/** The Name of the element */
	private String Name = BoundaryPlanElement.class.getSimpleName();
	
	/** The Root of the sub plan that is to be executed to the remote execution container. */
	public IPlanElement Root=null;
	
	/** The contingency Triggers that can be applied to this element in case of an error */
	public List<ContingencyTrigger> Triggers=new ArrayList<ContingencyTrigger>();
	
	/** The remote execution container access configuration */
	public BoundaryConfig Config=null;
	
	/** The remote execution container can be isolated to a sub-directory temporarily created for this execution
	 * purposes so that files created during this execution do not overlap with other executions taking place in 
	 * parallel. */
	public BoundaryIsolationInfo Isolation=null;
	
	/** A list of files that will be present in the remote execution container and which after the execution should
	 * be cleaned up */
	public List<String> CleanUpLocalFiles=new ArrayList<String>();
	
	/** The Attachments that are to be moved to the remote execution container directly though the connection established
	 * with the remote container */
	public Set<ExecutionAttachment> Attachments=new HashSet<ExecutionAttachment>();
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#FromXML(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#FromXML(org.w3c.dom.Element)
	 */
	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if (!IPlanElement.PlanElementType.valueOf(XMLUtils.GetAttribute((Element) XML, "type")).equals(this.GetPlanElementType())) throw new ExecutionSerializationException("plan element type missmatch");
			this.ID = XMLUtils.GetAttribute((Element) XML, "id");
			this.Name = XMLUtils.GetAttribute((Element) XML, "name");
			Element rootelem=XMLUtils.GetChildElementWithName(XML, "planElement");
			if(rootelem==null) throw new ExecutionSerializationException("not valid serialization of plan element");
			this.Root=PlanElementUtils.GetPlanElement(rootelem);
			Element cont=XMLUtils.GetChildElementWithName(XML, "triggers");
			this.Triggers.clear();
			if(cont!=null)
			{
				List<Element> trigs = XMLUtils.GetChildElementsWithName(cont, "contingency");
				for(Element trig : trigs)
				{
					ContingencyTrigger t=new ContingencyTrigger();
					t.FromXML(trig);
					this.Triggers.add(t);
				}
			}
			Element confelement=XMLUtils.GetChildElementWithName((Element)XML, "boundaryConfig");
			if(confelement==null) throw new ExecutionSerializationException("not valid serialization of element");
			this.Config=new BoundaryConfig();
			this.Config.FromXML(confelement);
			Element isolationelement=XMLUtils.GetChildElementWithName((Element)XML, "isolation");
			if(isolationelement==null) throw new ExecutionSerializationException("not valid serialization of element");
			this.Isolation=new BoundaryIsolationInfo();
			this.Isolation.FromXML(isolationelement);
			Element cleanupElement=XMLUtils.GetChildElementWithName((Element)XML, "cleanup");
			if(cleanupElement==null) throw new ExecutionSerializationException("not valid serialization of element");
			List<Element> cleanupFilesElementlst=XMLUtils.GetChildElementsWithName(cleanupElement, "file");
			this.CleanUpLocalFiles.clear();
			for(Element cleanupFileElement : cleanupFilesElementlst)
			{
				if(!XMLUtils.AttributeExists(cleanupFileElement, "value")) throw new ExecutionSerializationException("not valid serialization of element");
				this.CleanUpLocalFiles.add(XMLUtils.GetAttribute(cleanupFileElement, "value"));
			}
			Element atts=XMLUtils.GetChildElementWithName(XML, "attachments");
			if(atts==null) throw new ExecutionSerializationException("not valid serialization of element");
			List<Element> attlst=XMLUtils.GetChildElementsWithName(atts, "attachment");
			this.Attachments.clear();
			for(Element at : attlst)
			{
				ExecutionAttachment ato=new ExecutionAttachment();
				ato.FromXML(at);
				this.Attachments.add(ato);
			}
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetID()
	 */
	public String GetID()
	{
		return this.ID;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetName()
	 */
	public String GetName()
	{
		return this.Name;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetPlanElementType()
	 */
	public PlanElementType GetPlanElementType()
	{
		return IPlanElement.PlanElementType.Boundary;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Locate(java.lang.String)
	 */
	public IPlanElement Locate(String ID)
	{
		if (this.ID.equals(ID)) return this;
		if (this.Root != null)
		{
			IPlanElement elem = this.Root.Locate(ID);
			if (elem != null) { return elem; }
		}
		return null;
	}
	
	public Set<IPlanElement> LocateActionElements()
	{
		HashSet<IPlanElement> acts=new HashSet<IPlanElement>();
		if (this.Root != null)
		{
			acts.addAll(this.Root.LocateActionElements());
		}
		return acts;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SetName(java.lang.String)
	 */
	public void SetName(String Name)
	{
		this.Name=Name;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ToXML()
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<planElement type=\""+this.GetPlanElementType().toString()+"\" id=\""+this.GetID()+"\" name=\""+this.GetName()+"\">");
		buf.append(this.Root.ToXML());
		buf.append("<triggers>");
		for(ContingencyTrigger trig : this.Triggers)
		{
			buf.append(trig.ToXML());
		}
		buf.append("</triggers>");
		buf.append(this.Config.ToXML());
		buf.append(this.Isolation.ToXML());
		buf.append("<cleanup>");
		for(String f : this.CleanUpLocalFiles) buf.append("<file name=\""+f+"\"/>");
		buf.append("</cleanup>");
		buf.append("<attachments>");
		for(ExecutionAttachment at : this.Attachments) buf.append(at.ToXML());
		buf.append("</attachments>");
		buf.append("</planElement>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if(this.Root==null) throw new ExecutionValidationException("root of node not set");
		if(this.Config==null) throw new ExecutionValidationException("Boundary config not set");
		if(this.Isolation==null) throw new ExecutionValidationException("Isolation config not set");
		if(this.Attachments==null) throw new ExecutionValidationException("Attachments can be empty but not null");
		for(ExecutionAttachment at : this.Attachments) at.Validate();
		this.Config.Validate();
		this.Isolation.Validate();
		if(this.CleanUpLocalFiles==null) throw new ExecutionValidationException("List of local cleanup files can be empty but not null");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ValidatePreExecution(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
		for(String f : this.CleanUpLocalFiles)
		{
			if(!Handle.GetPlan().Variables.Contains(f)) throw new ExecutionValidationException("Cleanup file not present in available variables");
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SupportedContingencyTriggers()
	 */
	public IContingencyReaction.ReactionType[] SupportedContingencyTriggers()
	{
		return new IContingencyReaction.ReactionType[]{IContingencyReaction.ReactionType.None, IContingencyReaction.ReactionType.Pick,IContingencyReaction.ReactionType.Retry};
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SupportsContingencyTriggers()
	 */
	public boolean SupportsContingencyTriggers()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetContingencyTriggers()
	 */
	public List<ContingencyTrigger> GetContingencyTriggers()
	{
		return this.Triggers;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SetContingencyResourcePick(gr.uoa.di.madgik.execution.engine.ExecutionHandle, java.lang.String)
	 */
	public void SetContingencyResourcePick(ExecutionHandle Handle, String Pick) throws ExecutionRunTimeException
	{
		try
		{
			String []args=Pick.split(":");
			if(args.length!=2) throw new ExecutionSerializationException("Pick resource serialization is not in the expected format");
			this.Config.HostName=args[0];
			this.Config.Port=Integer.parseInt(args[1]);
			this.Config.Validate();
		}catch(Exception ex)
		{
			throw new ExecutionRunTimeException("Could not set provided picked resource ("+Pick+") because of error ("+ex.getMessage()+")",ex);
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetNeededVariableNames()
	 */
	public Set<String> GetNeededVariableNames()
	{
		HashSet<String> vars=new HashSet<String>();
		vars.addAll(this.Root.GetNeededVariableNames());
		vars.addAll(this.Isolation.GetNeededVariableNames());
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetModifiedVariableNames()
	 */
	public Set<String> GetModifiedVariableNames()
	{
		HashSet<String> vars=new HashSet<String>();
		vars.addAll(this.Root.GetModifiedVariableNames());
		vars.addAll(this.Isolation.GetModifiedVariableNames());
		return vars;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.PlanElementBase#GetExtenderLogger()
	 */
	public Logger GetExtenderLogger()
	{
		return logger;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.PlanElementBase#ExecuteExtender(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ExecuteExtender(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionCancelException, ExecutionInternalErrorException,ExecutionBreakException
	{
		this.StartClock(ClockType.Total);
		this.StartClock(ClockType.Init);
		try
		{
			this.RegisterToRunningActionElementsRestriction(Handle);
			logger.debug("Starting");
			this.CheckStatus(Handle);
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),1, 3, "Starting Execution of "+this.Name, this.Name, this.Config.HostName, this.Config.Port));
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),2, 3, "Assembling info to forward to remote boundary element", this.Name, this.Config.HostName, this.Config.Port));
			BoundaryHandler handler=new BoundaryHandler(this.Root, Handle.GetPlan().Variables.Subset(this.GetNeededVariableNames()),Handle.GetPlan().EnvHints, Handle, this.GetID(), this.Name, this.Config,this.Isolation,Handle.GetPlan().Config,this.Attachments);
			this.StopClock(ClockType.Init);
			this.StartClock(ClockType.Children);
			String varsUpdate=handler.EngineSideProtocol();
			this.StopClock(ClockType.Children);
			this.StartClock(ClockType.Finilization);
			VariableCollection varsCollectionUpdate=new VariableCollection(varsUpdate);
			Handle.GetPlan().Variables.Update(varsCollectionUpdate, this.Root.GetModifiedVariableNames());
			if(handler.WeirdCompletion) throw new ExecutionInternalErrorException("Boundary element completed but neither with success or with error...");
			if(!handler.Completed) throw new ExecutionInternalErrorException("Boundary element returned without completing");
			if(handler.Completed && !handler.Successful) throw new ExecutionException("Received error from " + this.Name + "("+this.Config.HostName+":"+this.Config.Port+")", handler.Error);
			this.StopClock(ClockType.Finilization);
		}catch(Exception ex)
		{
			ExceptionUtils.ThrowTransformedException(ex);
		}
		finally
		{
			this.StartClock(ClockType.Finilization);
			this.UnregisterToRunningActionElementsRestriction(Handle);
			this.CleanUpFiles(/*Handle*/);
			this.StopClock(ClockType.Finilization);
		}
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),3, 3, "Finishing Execution of "+this.Name, this.Name, this.Config.HostName, this.Config.Port));
		logger.debug("Exiting");
		this.StopClock(ClockType.Total);
		if(!Handle.GetPlan().Config.ChokePerformanceReporting) Handle.EmitEvent(this.GetPerformanceEvent());
	}
	
	/**
	 * Clean up files.
	 */
	private void CleanUpFiles(/*ExecutionHandle Handle*/)
	{
		for(String s : this.CleanUpLocalFiles)
		{
			try
			{
//				String file=DataTypeUtils.GetValueAsString(Handle.GetPlan().Variables.Get(s).Value.GetValue());
				String file=s;
				File f=new File(file);
				if(f.exists() && f.isFile()) f.delete();
			}
			catch(Exception ex)
			{
//				logger.warn("Could not cleanup file of variable with id "+s);
				logger.warn("Could not cleanup file of variable with location "+s);
			}
		}
	}
}
