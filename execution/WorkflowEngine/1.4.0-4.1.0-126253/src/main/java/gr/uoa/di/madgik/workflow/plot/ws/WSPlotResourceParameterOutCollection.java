package gr.uoa.di.madgik.workflow.plot.ws;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.Method;
import gr.uoa.di.madgik.environment.is.elements.invocable.PojoInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.WSInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotMethod;
import gr.uoa.di.madgik.environment.is.elements.plot.WSPlotInfo;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter.ParameterDirectionType;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResource;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceOutCollection;
import java.util.HashMap;
import java.util.Map;

public class WSPlotResourceParameterOutCollection implements IPlotResourceOutCollection
{
	public InvocablePlotInfo plotInfo;
	public InvocableProfileInfo invocableInfo;
	public Map<Integer, WSPlotResourceMethodOutput> Parameters=new HashMap<Integer, WSPlotResourceMethodOutput>();

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
		if(param instanceof WSPlotResourceMethodOutput) this.Parameters.put(((WSPlotResourceMethodOutput)param).MethodOrder, (WSPlotResourceMethodOutput)param);
		else throw new WorkflowValidationException("Invalid type provided");
	}
	
	public WSPlotResourceMethodOutput Get(int methodOrder)
	{
		return this.Parameters.get(methodOrder);
	}

	public void Validate() throws WorkflowValidationException
	{
		if(!(plotInfo instanceof WSPlotInfo)) throw new WorkflowValidationException("Incompatible type provided");
		if(!(invocableInfo instanceof WSInvocableProfileInfo)) throw new WorkflowValidationException("Incompatible type provided");
		for(PlotMethod pm : ((WSPlotInfo)plotInfo).Methods)
		{
			if(!pm.UseReturnValue) continue;
			int key=pm.Order;
			if(!this.Parameters.containsKey(key)) throw new WorkflowValidationException("Needed runtime return value parameter of method "+pm.Signature+", order "+pm.Order+", not provided");
			Method m=((PojoInvocableProfileInfo)invocableInfo).Get(pm.Signature);
			if(m==null) throw new WorkflowValidationException("Method with signature "+pm.Signature+" not found in invocable profile");
			WSPlotResourceMethodOutput provParam = this.Parameters.get(key);
			if(provParam.Output==null || (!provParam.Output.GetDirectionType().equals(ParameterDirectionType.Out) && !provParam.Output.GetDirectionType().equals(ParameterDirectionType.InOut))) throw new WorkflowValidationException("Provided parameter "+pm.Signature+"# has no associated value or is not of the correct direction for return value");
		}
	}
}
