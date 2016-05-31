package gr.uoa.di.madgik.execution.plan.element.invocable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class CallBase implements Comparable<CallBase>, Serializable
{
	private static final long serialVersionUID = 1L;

	public enum CallType
	{
		Simple,
		WSSOAP,
		WSREST
	}
	
	public int Order=0;
	public String MethodName=null;
	public List<ArgumentBase> ArgumentList=new ArrayList<ArgumentBase>();
	public IOutputParameter OutputParameter=null;
	
	public void Validate()throws ExecutionValidationException
	{
		if(this.MethodName==null || this.MethodName.trim().length()==0) throw new ExecutionValidationException("Needed invocation Module info not provided");
		if(this.OutputParameter!=null) this.OutputParameter.Validate();
		if(this.ArgumentList==null) throw new ExecutionValidationException("Argument list cannot be null although it can be empty");
		for(ArgumentBase arg : this.ArgumentList) arg.Validate();
	}
	
	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if(this.OutputParameter!=null) this.OutputParameter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
		for(ArgumentBase arg : this.ArgumentList) arg.ValidatePreExecution(Handle,ExcludeAvailableConstraint);
	}
	
	public void EvaluateArguments(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionValidationException
	{
		Collections.sort(this.ArgumentList);
		for(ArgumentBase arg : this.ArgumentList) arg.EvaluateArgument(Handle);
	}
	
	public Class<?>[] GetArgumentTypeList(ExecutionHandle Handle)
	{
		Class<?>[] types=new Class<?>[this.ArgumentList.size()];
		for(int i=0;i<this.ArgumentList.size();i+=1) types[i]=this.ArgumentList.get(i).GetValueClass(Handle);
		return types;
	}
	
	public Object[] GetArgumentValueList()
	{
		Object[] values=new Object[this.ArgumentList.size()];
		for(int i=0;i<this.ArgumentList.size();i+=1) values[i]=this.ArgumentList.get(i).GetValue();
		return values;
	}

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}
	
	public abstract CallType GetCallType();
	
	public abstract String ToXML()throws ExecutionSerializationException;

	public abstract void FromXML(Node XML) throws ExecutionSerializationException;

	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		for(ArgumentBase arg : this.ArgumentList) vars.addAll(arg.GetModifiedVariableNames());
		if(this.OutputParameter!=null) vars.addAll(this.OutputParameter.GetModifiedVariableNames());
		return vars;
	}

	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		for(ArgumentBase arg : this.ArgumentList) vars.addAll(arg.GetNeededVariableNames());
		if(this.OutputParameter!=null) vars.addAll(this.OutputParameter.GetNeededVariableNames());
		return vars;
	}

	public int compareTo(CallBase o)
	{
		return Integer.valueOf(this.Order).compareTo(o.Order);
	}
}
