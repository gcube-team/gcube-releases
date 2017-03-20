package gr.uoa.di.madgik.workflow.plot.pojo;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.Method;
import gr.uoa.di.madgik.environment.is.elements.invocable.PojoInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotMethod;
import gr.uoa.di.madgik.environment.is.elements.plot.PojoPlotInfo;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter.ParameterDirectionType;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResource;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceOutCollection;
import java.util.HashMap;
import java.util.Map;

public class PojoPlotResourceParameterOutCollection implements IPlotResourceOutCollection
{
	public Map<String,PojoPlotResourceParameter> Parameters=new HashMap<String,PojoPlotResourceParameter>();
	public InvocablePlotInfo plotInfo;
	public InvocableProfileInfo invocableInfo;
	
	private String GetKey(int SignatureOrder, String name)
	{
		return SignatureOrder+"#"+name;
	}
	
	public void Add(IPlotResource param) throws WorkflowValidationException
	{
		if(!(param instanceof PojoPlotResourceParameter)) throw new WorkflowValidationException("Incompatible type provided");
		String key=this.GetKey(((PojoPlotResourceParameter)param).MethodOrder, ((PojoPlotResourceParameter)param).ParameterName);
		this.Parameters.put(key, (PojoPlotResourceParameter)param);
	}

	public void SetPlotInfo(InvocablePlotInfo plotInfo)
	{
		this.plotInfo=plotInfo;
	}

	public void SetInvocableInfo(InvocableProfileInfo invocableInfo)
	{
		this.invocableInfo=invocableInfo;
	}
	
	public PojoPlotResourceParameter Get(String signature,String parameterName)
	{
		return this.Parameters.get(signature+"#"+parameterName);
	}
	
	public void Validate() throws WorkflowValidationException
	{
		if(!(plotInfo instanceof PojoPlotInfo)) throw new WorkflowValidationException("Incompatible type provided");
		if(!(invocableInfo instanceof PojoInvocableProfileInfo)) throw new WorkflowValidationException("Incompatible type provided");
		for(PlotMethod pm : ((PojoPlotInfo)plotInfo).Methods)
		{
			if(!pm.UseReturnValue) continue;
			String key=this.GetKey(pm.Order, null);
			if(!this.Parameters.containsKey(key)) throw new WorkflowValidationException("Needed runtime return value parameter of method "+pm.Signature+" not provided");
			Method m=((PojoInvocableProfileInfo)invocableInfo).Get(pm.Signature);
			if(m==null) throw new WorkflowValidationException("Method with signature "+pm.Signature+" not found in invocable profile");
			PojoPlotResourceParameter provParam = this.Parameters.get(key);
			if(provParam.Parameter==null || (!provParam.Parameter.GetDirectionType().equals(ParameterDirectionType.Out) && !provParam.Parameter.GetDirectionType().equals(ParameterDirectionType.InOut))) throw new WorkflowValidationException("Provided parameter "+pm.Signature+"# has no associated value or is not of the correct direction for return value");
		}
	}
}
