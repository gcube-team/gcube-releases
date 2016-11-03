package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.element.BagPlanElement;
import gr.uoa.di.madgik.execution.plan.element.BoundaryPlanElement;
import gr.uoa.di.madgik.execution.plan.element.BreakPlanElement;
import gr.uoa.di.madgik.execution.plan.element.CheckpointPlanElement;
import gr.uoa.di.madgik.execution.plan.element.ConditionalPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FileTransferPlanElement;
import gr.uoa.di.madgik.execution.plan.element.FlowPlanElement;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.LoopPlanElement;
import gr.uoa.di.madgik.execution.plan.element.ParameterProcessingPlanElement;
import gr.uoa.di.madgik.execution.plan.element.PojoPlanElement;
import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
import gr.uoa.di.madgik.execution.plan.element.TryCatchFinallyPlanElement;
import gr.uoa.di.madgik.execution.plan.element.WSRESTPlanElement;
import gr.uoa.di.madgik.execution.plan.element.WSSOAPPlanElement;
import gr.uoa.di.madgik.execution.plan.element.WaitPlanElement;
import gr.uoa.di.madgik.execution.plan.element.invocable.ArgumentBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.CallBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExecutionContextConfigBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.SimpleArgument;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.SimpleCall;
import gr.uoa.di.madgik.execution.plan.element.invocable.simple.SimpleExecutionContextConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSExecutionContextConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSRESTArgument;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSRESTCall;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSSOAPArgument;
import gr.uoa.di.madgik.execution.plan.element.invocable.ws.WSSOAPCall;
import org.w3c.dom.Element;

public class PlanElementUtils
{
	public static ExecutionContextConfigBase GetExecutionContextConfig(Element element) throws ExecutionSerializationException
	{
		try
		{
			ExecutionContextConfigBase elem=null;
			if(!XMLUtils.AttributeExists(element, "type")) throw new ExecutionSerializationException("Provided serialization is not valid");
			switch (ExecutionContextConfigBase.ContextConfigType.valueOf(XMLUtils.GetAttribute(element, "type")))
			{
				case Simple:
				{
					elem=new SimpleExecutionContextConfig();
					break;
				}
				case WS:
				{
					elem=new WSExecutionContextConfig();
					break;
				}
				default:
				{
					throw new ExecutionSerializationException("Unrecognized type found");
				}
			}
			elem.FromXML(element);
			return elem;
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not retrieve execution context config element from provided serialization", ex);
		}
	}
	
	public static CallBase GetCall(Element element) throws ExecutionSerializationException
	{
		try
		{
			CallBase elem=null;
			if(!XMLUtils.AttributeExists(element, "type")) throw new ExecutionSerializationException("Provided serialization is not valid");
			switch (CallBase.CallType.valueOf(XMLUtils.GetAttribute(element, "type")))
			{
				case Simple:
				{
					elem=new SimpleCall();
					break;
				}
				case WSSOAP:
				{
					elem=new WSSOAPCall();
					break;
				}
				case WSREST:
				{
					elem=new WSRESTCall();
					break;
				}
				default:
				{
					throw new ExecutionSerializationException("Unrecognized type found");
				}
			}
			elem.FromXML(element);
			return elem;
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not retrieve call element from provided serialization", ex);
		}
	}
	
	public static ArgumentBase GetArgument(Element element) throws ExecutionSerializationException
	{
		try
		{
			ArgumentBase elem=null;
			if(!XMLUtils.AttributeExists(element, "type")) throw new ExecutionSerializationException("Provided serialization is not valid");
			switch (ArgumentBase.ArgumentType.valueOf(XMLUtils.GetAttribute(element, "type")))
			{
				case Simple:
				{
					elem=new SimpleArgument();
					break;
				}
				case WSSOAP:
				{
					elem=new WSSOAPArgument();
					break;
				}
				case WSREST:
				{
					elem=new WSRESTArgument();
					break;
				}
				default:
				{
					throw new ExecutionSerializationException("Unrecognized type found");
				}
			}
			elem.FromXML(element);
			return elem;
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not retrieve argument element from provided serialization", ex);
		}
	}

	public static IPlanElement GetPlanElement(Element element) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(element, "type")) throw new ExecutionSerializationException("Not valid serialization of plan element");
			IPlanElement elem = null;
			switch (IPlanElement.PlanElementType.valueOf(XMLUtils.GetAttribute(element, "type")))
			{
				case Boundary:
				{
					elem=new BoundaryPlanElement();
					break;
				}
				case POJO:
				{
					elem=new PojoPlanElement();
					break;
				}
				case Shell:
				{
					elem=new ShellPlanElement();
					break;
				}
				case WSSOAP:
				{
					elem=new WSSOAPPlanElement();
					break;
				}
				case WSREST:
				{
					elem=new WSRESTPlanElement();
					break;
				}
				case Break:
				{
					elem=new BreakPlanElement();
					break;
				}
				case Wait:
				{
					elem=new WaitPlanElement();
					break;
				}
				case Conditional:
				{
					elem=new ConditionalPlanElement();
					break;
				}
				case TryCatchFinally:
				{
					elem=new TryCatchFinallyPlanElement();
					break;
				}
				case Flow:
				{
					elem=new FlowPlanElement();
					break;
				}
				case Bag:
				{
					elem=new BagPlanElement();
					break;
				}
				case Filter:
				{
					elem=new ParameterProcessingPlanElement();
					break;
				}
				case Loop:
				{
					elem=new LoopPlanElement();
					break;
				}
				case Sequence:
				{
					elem=new SequencePlanElement();
					break;
				}
				case FileTransfer:
				{
					elem=new FileTransferPlanElement();
					break;
				}
				case Checkpoint:
				{
					elem=new CheckpointPlanElement();
					break;
				}
				default:
				{
					throw new ExecutionSerializationException("Unrecognized type found");
				}
			}
			elem.FromXML(element);
			return elem;
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not retrieve plan element from provided serialization", ex);
		}
	}
}
