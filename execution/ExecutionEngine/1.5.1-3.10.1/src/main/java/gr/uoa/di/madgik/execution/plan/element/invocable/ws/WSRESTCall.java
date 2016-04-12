package gr.uoa.di.madgik.execution.plan.element.invocable.ws;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.invocable.ArgumentBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.CallBase;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import gr.uoa.di.madgik.execution.utils.PlanElementUtils;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class WSRESTCall extends CallBase
{
	private static final long serialVersionUID = 1L;
	public String ExecutionContextToken=null;

	public CallType GetCallType()
	{
		return CallType.WSREST;
	}
	
	public void Validate()throws ExecutionValidationException
	{
		for(ArgumentBase arg : this.ArgumentList) if(!(arg instanceof WSRESTArgument)) throw new ExecutionValidationException("Supplied argument type is not one of the supported ones");
		super.Validate();
	}
	
	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		if(this.OutputParameter!=null) this.OutputParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
		for(ArgumentBase arg : this.ArgumentList) arg.ValidatePreExecution(Handle,ExcludeAvailableConstraint);
		super.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
	}

	public String ToXML()throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<call type=\""+this.GetCallType().toString()+"\" order=\""+this.Order+"\">");
		if(this.ExecutionContextToken!=null) buf.append("<executionContextToken>"+this.ExecutionContextToken+"</executionContextToken>");
		buf.append("<methodName value=\""+this.MethodName+"\"/>");
		buf.append("<arguments>");
		for(ArgumentBase a : this.ArgumentList)
		{
			buf.append(a.ToXML());
		}
		buf.append("</arguments>");
		buf.append("<output>");
		if(this.OutputParameter!=null) buf.append(this.OutputParameter.ToXML());
		buf.append("</output>");
		buf.append("</call>");
		return buf.toString();
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists((Element)XML, "order")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Order=Integer.parseInt(XMLUtils.GetAttribute((Element)XML, "order"));
			Element tokenelem=XMLUtils.GetChildElementWithName((Element)XML, "executionContextToken");
			if(tokenelem!=null) this.ExecutionContextToken=XMLUtils.GetChildText(tokenelem);
			Element modnode=XMLUtils.GetChildElementWithName((Element)XML, "methodName");
			if(modnode==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!XMLUtils.AttributeExists(modnode, "value")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.MethodName=XMLUtils.GetAttribute(modnode, "value");
			Element argsnode=XMLUtils.GetChildElementWithName((Element)XML, "arguments");
			if(argsnode==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.ArgumentList.clear();
			List<Element> args=XMLUtils.GetChildElementsWithName(argsnode, "argument");
			for(Element arg : args)
			{
				this.ArgumentList.add(PlanElementUtils.GetArgument(arg));
			}
			Element outputelement=XMLUtils.GetChildElementWithName(XML, "output");
			if(outputelement==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			Element outparam= XMLUtils.GetChildElementWithName(outputelement, "param");
			if(outparam==null) this.OutputParameter=null;
			else this.OutputParameter=(IOutputParameter)ParameterUtils.GetParameter(outparam);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}
}
