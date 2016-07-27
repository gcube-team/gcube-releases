package gr.uoa.di.madgik.workflow.plot.ws;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.WSInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.WSMethod;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotMethod;
import gr.uoa.di.madgik.environment.is.elements.plot.WSPlotInfo;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter.ParameterDirectionType;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResource;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceInCollection;
import java.util.HashMap;
import java.util.Map;

public class WSPlotResourceParameterInCollection implements IPlotResourceInCollection
{
	public InvocablePlotInfo plotInfo;
	public InvocableProfileInfo invocableInfo;
	public WSPlotResourceEndpoint EndPoint=null;
	public Map<Integer, WSPlotResourceMethodInput> Parameters=new HashMap<Integer, WSPlotResourceMethodInput>();

	public void SetInvocableInfo(InvocableProfileInfo invocableInfo)
	{
		this.invocableInfo=invocableInfo;
	}

	public void SetPlotInfo(InvocablePlotInfo plotInfo)
	{
		this.plotInfo=plotInfo;
	}

	public void Add(IPlotResource param) throws WorkflowValidationException
	{
		if(param instanceof WSPlotResourceEndpoint) this.EndPoint=(WSPlotResourceEndpoint)param;
		else if(param instanceof WSPlotResourceMethodInput) this.Parameters.put(((WSPlotResourceMethodInput)param).MethodOrder, (WSPlotResourceMethodInput)param);
		else throw new WorkflowValidationException("Incompatible resource type");
	}
	
	public WSPlotResourceEndpoint GetEndPoint()
	{
		return this.EndPoint;
	}
	
	public WSPlotResourceMethodInput GetMethodInput(int methodOrder)
	{
		return this.Parameters.get(methodOrder);
	}

	public void Validate() throws WorkflowValidationException
	{
		if(!(plotInfo instanceof WSPlotInfo)) throw new WorkflowValidationException("Incompatible type provided");
		if(!(invocableInfo instanceof WSInvocableProfileInfo)) throw new WorkflowValidationException("Incompatible type provided");
		if(this.EndPoint==null) throw new WorkflowValidationException("Needed resource end point not provided");
		for(PlotMethod pm : ((WSPlotInfo)plotInfo).Methods)
		{
			int key=pm.Order;
			if(!this.Parameters.containsKey(key)) throw new WorkflowValidationException("Needed runtime parameter of method "+pm.Signature+", order "+pm.Order+", not provided");
			WSMethod m=((WSInvocableProfileInfo)invocableInfo).Get(pm.Signature);
			if(m==null) throw new WorkflowValidationException("Method with signature "+pm.Signature+" not found in invocable profile");
			WSPlotResourceMethodInput provParam = this.Parameters.get(key);
			if(provParam.Input==null || (!provParam.Input.GetDirectionType().equals(ParameterDirectionType.In) && !provParam.Input.GetDirectionType().equals(ParameterDirectionType.InOut))) throw new WorkflowValidationException("Provided parameter "+pm.Signature+" has no associated value or is not of the correct direction");
		}
	}

}
