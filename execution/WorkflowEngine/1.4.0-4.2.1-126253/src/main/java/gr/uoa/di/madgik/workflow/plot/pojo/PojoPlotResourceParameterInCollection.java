package gr.uoa.di.madgik.workflow.plot.pojo;

import gr.uoa.di.madgik.environment.is.elements.InvocablePlotInfo;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.Method;
import gr.uoa.di.madgik.environment.is.elements.invocable.Parameter;
import gr.uoa.di.madgik.environment.is.elements.invocable.PojoInvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotMethod;
import gr.uoa.di.madgik.environment.is.elements.plot.PlotParameter;
import gr.uoa.di.madgik.environment.is.elements.plot.PojoPlotInfo;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter.ParameterDirectionType;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResource;
import gr.uoa.di.madgik.workflow.plot.commons.IPlotResourceInCollection;
import java.util.HashMap;
import java.util.Map;

public class PojoPlotResourceParameterInCollection implements IPlotResourceInCollection
{
	public Map<String,PojoPlotResourceParameter> Parameters=new HashMap<String,PojoPlotResourceParameter>();
	public InvocablePlotInfo plotInfo;
	public InvocableProfileInfo invocableInfo;
	
	public static String GetKey(int SignatureOrder, String ArgumentName)
	{
		return SignatureOrder+"#"+ArgumentName;
	}
	
	public void Add(IPlotResource param) throws WorkflowValidationException
	{
		if(!(param instanceof PojoPlotResourceParameter)) throw new WorkflowValidationException("Incompatible type provided");
		String key=PojoPlotResourceParameterInCollection.GetKey(((PojoPlotResourceParameter)param).MethodOrder, ((PojoPlotResourceParameter)param).ParameterName);
		this.Parameters.put(key, (PojoPlotResourceParameter)param);
	}
	
	public PojoPlotResourceParameter Get(int signatureOrder,String parameterName)
	{
		return this.Parameters.get(PojoPlotResourceParameterInCollection.GetKey(signatureOrder, parameterName));
	}

	public void SetPlotInfo(InvocablePlotInfo plotInfo)
	{
		this.plotInfo=plotInfo;
	}

	public void SetInvocableInfo(InvocableProfileInfo invocableInfo)
	{
		this.invocableInfo=invocableInfo;
	}
	
	public void Validate() throws WorkflowValidationException
	{
		if(!(plotInfo instanceof PojoPlotInfo)) throw new WorkflowValidationException("Incompatible type provided");
		if(!(invocableInfo instanceof PojoInvocableProfileInfo)) throw new WorkflowValidationException("Incompatible type provided");
		for(PlotMethod pm : ((PojoPlotInfo)plotInfo).Methods)
		{
			for(PlotParameter pp : pm.Parameters)
			{
				if(!pp.IsFixed)
				{
					String key=PojoPlotResourceParameterInCollection.GetKey(pm.Order, pp.ParameterName);
					if(!this.Parameters.containsKey(key)) throw new WorkflowValidationException("Needed runtime parameter "+pp.ParameterName+" of method "+pm.Signature+" not provided");
					Method m=((PojoInvocableProfileInfo)invocableInfo).Get(pm.Signature);
					if(m==null) throw new WorkflowValidationException("Method with signature "+pm.Signature+" not found in invocable profile");
					Parameter p = m.Get(pp.ParameterName);
					if(p==null) throw new WorkflowValidationException("Parameter with name "+pp.ParameterName+" in method with signature "+pm.Signature+" not found in invocable profile");
					PojoPlotResourceParameter provParam = this.Parameters.get(key);
					if(provParam.Parameter==null || (!provParam.Parameter.GetDirectionType().equals(ParameterDirectionType.In) && !provParam.Parameter.GetDirectionType().equals(ParameterDirectionType.InOut))) throw new WorkflowValidationException("Provided parameter "+pm.Signature+"#"+pp.ParameterName+" has no associated value or is not of the correct direction");
				}
			}
		}
	}
}
