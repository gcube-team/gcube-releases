package gr.uoa.di.madgik.workflow.adaptor.search.utils;

import gr.uoa.di.madgik.execution.plan.element.IPlanElement;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class NodeExecutionInfo 
{
	public IPlanElement element;
	public WrapperNode wrapperNode;
	
	public NodeExecutionInfo(IPlanElement element, WrapperNode wrapperNode) 
	{
		this.element = element;
		this.wrapperNode = wrapperNode;
	}
}
