package gr.uoa.di.madgik.execution.plan.element;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.contingency.IContingencyReaction;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Element;

/**
 * This interface is implemented by all plan elements that can be used to assemble the {@link ExecutionPlan}
 * 
 * @author gpapanikos
 */
public interface IPlanElement extends Serializable
{
	
	/**
	 * The type of the plan element
	 * 
	 * @author gpapanikos
	 */
	public enum PlanElementType
	{
		
		/** Boundary element of a execution container */
		Boundary,
		
		/** A plain java object invocation */
		POJO,
		
		/** A shell script invocation */
		Shell,
		
		/** A Web Service invocation */
		WSSOAP,

		/** A ReSTful Web Service invocation */
		WSREST,
		
		/** An execution break element */
		Break,
		
		/** A conditional execution element */
		Conditional,
		
		/** A try catch finally element */
		TryCatchFinally,
		
		/** A flow of sub plans */
		Flow,
		
		/** A loop */
		Loop,
		
		/** A sequence of sub plans */
		Sequence,
		
		/** A file transfer element */
		FileTransfer,
		
		/** A checkpoint element */
		Checkpoint,
		
		/** A wait element */
		Wait,
		
		/** A conditional bag of sub plans */
		Bag,
		
		/** A filtering step */
		Filter
	}
	
	/**
	 * Gets the plan element type.
	 * 
	 * @return the plan element type
	 */
	public IPlanElement.PlanElementType GetPlanElementType();
	
	/**
	 * Gets the id.
	 * 
	 * @return the id of the element
	 */
	public String GetID();
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String GetName();
	
	/**
	 * Sets the name.
	 * 
	 * @param Name the name
	 */
	public void SetName(String Name);
	
	/**
	 * Validate the element and any sub element it contains
	 * 
	 * @throws ExecutionValidationException A validation error occurred
	 */
	public void Validate() throws ExecutionValidationException;

	/**
	 * Validate the element and any sub element it contains before it is executed
	 * 
	 * @param Handle the execution handle
	 * 
	 * @throws ExecutionValidationException A validation error occurred
	 */
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException;
	
	/**
	 * Locate the plan element with the provided id. If this is the element requested return this instance.
	 * Otherwise forward the request to any sub elements contained. If the element does not belong to the 
	 * hierarchy under this element, return null
	 * 
	 * @param ID the iD
	 * 
	 * @return the plan element with the provided id. or null if not found
	 */
	public IPlanElement Locate(String ID);
	
	/**
	 * Retrieves the Action elements under this elements
	 * 
	 * @return The action ELements
	 */
	public Set<IPlanElement> LocateActionElements();
	
	/**
	 * Serialize to xml the element and all its contained elements
	 * 
	 * @return the serialization
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public String ToXML() throws ExecutionSerializationException;
	
	/**
	 * Populate the element from its xml serialization as returned by {@link IPlanElement#ToXML()}
	 * 
	 * @param XML the xML serialization
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public void FromXML(String XML) throws ExecutionSerializationException;

	/**
	 * Populate the element from its xml serialization as returned by {@link IPlanElement#ToXML()}
	 * 
	 * @param XML the xML serialization
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public void FromXML(Element XML) throws ExecutionSerializationException;
	
	/**
	 * Checks if the element supports contingency triggers.
	 * 
	 * @return true, if it does
	 */
	public boolean SupportsContingencyTriggers();
	
	/**
	 * Retrieves the supported contingency triggers.
	 * 
	 * @return the supported contingency triggers types.
	 */
	public IContingencyReaction.ReactionType[] SupportedContingencyTriggers();
	
	/**
	 * Gets the contingency triggers.
	 * 
	 * @return the list of contingency trigger that are applicable to the element.
	 */
	public List<ContingencyTrigger> GetContingencyTriggers();
	
	/**
	 * Sets the picked resource in case the {@link IContingencyReaction.ReactionType#Pick} reaction is supported
	 * 
	 * @param Handle the execution handle handle
	 * @param Pick the picked resource
	 * 
	 * @throws ExecutionRunTimeException A runtime error occurred
	 */
	public void SetContingencyResourcePick(ExecutionHandle Handle,String Pick) throws ExecutionRunTimeException;
	
	/**
	 * Gets the needed variable names of the element and of the elements contained. These include the variables that are 
	 * needed to be present for the element and its hierarchy to be executed.
	 * 
	 * @return the set of the variable names that are needed by this element and its contained elements
	 */
	public Set<String> GetNeededVariableNames();
	
	/**
	 * Gets the modified variable names of the element and of the elements contained. These include the variables that are 
	 * modified by the element and its hierarchy when executed.
	 * 
	 * @return the set of the variable names that are modified by this element and its contained elements
	 */
	public Set<String> GetModifiedVariableNames();
	
	/**
	 * Execute the element
	 * 
	 * @param Handle the execution handle
	 * 
	 * @throws ExecutionRunTimeException A runtime error occurred
	 * @throws ExecutionInternalErrorException An internal error occurred
	 * @throws ExecutionCancelException The execution is canceled
	 * @throws ExecutionBreakException The execution was terminated after an explicit request
	 */
	public void Execute(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException,ExecutionBreakException;
}
