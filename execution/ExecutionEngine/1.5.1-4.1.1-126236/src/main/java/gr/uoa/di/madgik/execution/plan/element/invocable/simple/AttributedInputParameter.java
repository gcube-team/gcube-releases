package gr.uoa.di.madgik.execution.plan.element.invocable.simple;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IParameter;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import java.util.Set;
import org.w3c.dom.Element;

public class AttributedInputParameter
{
	public IInputParameter Parameter=null;
	public boolean IsFile=false;
	
	public AttributedInputParameter(){}
	
	public AttributedInputParameter(IInputParameter Parameter)
	{
		this.Parameter=Parameter;
		this.IsFile=false;
	}
	
	public AttributedInputParameter(IInputParameter Parameter,boolean IsFile)
	{
		this.Parameter=Parameter;
		this.IsFile=IsFile;
	}

	public Set<String> GetModifiedVariableNames()
	{
		return this.Parameter.GetModifiedVariableNames();
	}

	public Set<String> GetNeededVariableNames()
	{
		return this.Parameter.GetNeededVariableNames();
	}
	
	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		this.Parameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
	}
	
	public void Validate() throws ExecutionValidationException
	{
		if(this.Parameter==null) throw new ExecutionValidationException("Needed input parameter not set");
		this.Parameter.Validate();
	}
	
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<attrParam isFile=\""+this.IsFile+"\">");
		buf.append(this.Parameter.ToXML());
		buf.append("</attrParam>");
		return buf.toString();
	}
	
	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists(XML, "isFile")) throw new ExecutionSerializationException("Not valid serialization");
			this.IsFile=Boolean.parseBoolean(XMLUtils.GetAttribute(XML, "isFile"));
			Element paramElem = XMLUtils.GetChildElementWithName(XML, "param");
			IParameter param=ParameterUtils.GetParameter(paramElem);
			if(!(param instanceof IInputParameter)) throw new ExecutionSerializationException("Not valid serialization of element");
			this.Parameter=(IInputParameter)param;
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Not valid serialization",ex);
		}
	}
}
