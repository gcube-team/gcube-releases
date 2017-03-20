package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.element.condition.ArrayIterationPlanCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.BagElementDependencyPlanCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.BooleanVariableCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeLeaf;
import gr.uoa.di.madgik.execution.plan.element.condition.ConditionTreeNode;
import gr.uoa.di.madgik.execution.plan.element.condition.CounterPlanCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.DecimalRangePlanCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.IConditionTreeElement;
import gr.uoa.di.madgik.execution.plan.element.condition.IPlanCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.TimeOutPlanCondition;
import gr.uoa.di.madgik.execution.plan.element.condition.VariableIsAvailablePlanCondition;
import org.w3c.dom.Element;

public class ConditionUtils
{
	public static IConditionTreeElement GetConditionTreeElement(Element element) throws ExecutionSerializationException
	{
		try
		{
			IConditionTreeElement elem=null;
			switch(IConditionTreeElement.TreeElementType.valueOf(XMLUtils.GetAttribute(element, "type")))
			{
				case Leaf:
				{
					elem=new ConditionTreeLeaf();
					break;
				}
				case Node:
				{
					elem=new ConditionTreeNode();
					break;
				}
				default:
				{
					throw new ExecutionSerializationException("Unrecognized provided type");
				}
			}
			elem.FromXML(element);
			return elem;
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not create condition tree element from provided serialization",ex);
		}
	}

	public static IPlanCondition GetPlanCondition(Element element) throws ExecutionSerializationException
	{
		try
		{
			IPlanCondition cond=null;
			switch(IPlanCondition.ConditionType.valueOf(XMLUtils.GetAttribute(element, "type")))
			{
				case Counter:
				{
					cond=new CounterPlanCondition();
					break;
				}
				case ArrayIteration:
				{
					cond=new ArrayIterationPlanCondition();
					break;
				}
				case DecimalRange:
				{
					cond=new DecimalRangePlanCondition();
					break;
				}
				case IsAvailable:
				{
					cond=new VariableIsAvailablePlanCondition();
					break;
				}
				case BagDependency:
				{
					cond=new BagElementDependencyPlanCondition();
					break;
				}
				case BooleanVariable:
				{
					cond= new BooleanVariableCondition();
					break;
				}
				case Timeout:
				{
					cond=new TimeOutPlanCondition();
					break;
				}
				default:
				{
					throw new ExecutionSerializationException("Unrecognized provided type");
				}
			}
			cond.FromXML(element);
			return cond;
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not create condition element from provided serialization",ex);
		}
	}
}
