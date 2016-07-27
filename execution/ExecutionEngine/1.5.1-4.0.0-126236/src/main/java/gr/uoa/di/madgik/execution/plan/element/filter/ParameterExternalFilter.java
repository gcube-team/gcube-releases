package gr.uoa.di.madgik.execution.plan.element.filter;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParameterExternalFilter extends ParameterFilterBase
{
	public IExternalFilter ExternalFilter=null;

	@Override
	public FilterType GetFilterType()
	{
		return ParameterFilterBase.FilterType.External;
	}

	@Override
	public Set<String> GetInputVariableNames()
	{
		return this.ExternalFilter.GetInputVariableNames();
	}

	@Override
	public Set<String> GetStoreOutputVariableName()
	{
		return this.ExternalFilter.GetStoreOutputVariableName();
	}

	@Override
	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		return this.ExternalFilter.Process(Handle);
	}

	@Override
	public Object ProcessOnLine(Object OnLineFilteredValue, Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		return this.ExternalFilter.ProcessOnLine(OnLineFilteredValue, AdditionalValueProviders,Handle);
	}

	@Override
	public boolean StoreOutput()
	{
		return this.ExternalFilter.StoreOutput();
	}

	@Override
	public boolean SupportsOnLineFiltering()
	{
		return this.ExternalFilter.SupportsOnLineFiltering();
	}

	@Override
	public void Validate() throws ExecutionValidationException
	{
		if(this.ExternalFilter==null) throw new ExecutionValidationException("No external filter defined");
		this.ExternalFilter.Validate();
		this.TokenMappingValidate();
	}

	@Override
	public void ValidateForOnlineFiltering() throws ExecutionValidationException
	{
		this.ExternalFilter.ValidateForOnlineFiltering();
	}

	@Override
	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.ExternalFilter.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
	}

	@Override
	public void ValidatePreExecutionForOnlineFiltering(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.ExternalFilter.ValidatePreExecutionForOnlineFiltering(Handle, ExcludeAvailableConstraint);
	}

	@Override
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<filter type=\""+this.GetFilterType().toString()+"\" order=\""+this.Order+"\">");
		buf.append(this.ExternalFilter.ToXML());
		buf.append(this.TokenMappingToXML());
		buf.append("</filter>");
		return buf.toString();
	}
	
	@Override
	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	@Override
	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists((Element)XML, "type") ||
					!XMLUtils.AttributeExists((Element)XML, "order")) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!ParameterFilterBase.FilterType.valueOf(XMLUtils.GetAttribute((Element)XML, "type")).equals(this.GetFilterType())) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Order=Integer.parseInt(XMLUtils.GetAttribute((Element)XML, "order"));
			Element externalElement=XMLUtils.GetChildElementWithName(XML, "external");
			if(externalElement==null) throw new ExecutionSerializationException("Invalid serialization");
			if(!XMLUtils.AttributeExists(externalElement, "type")) throw new ExecutionSerializationException("Invalid serialization");
			String t=XMLUtils.GetAttribute(externalElement, "type");
			if(t==null || t.trim().length()==0) throw new ExecutionSerializationException("Invalid serialization");
			Object o=Class.forName(t).newInstance();
			if(!(o instanceof IExternalFilter))throw new ExecutionSerializationException("Invalid serialization");
			this.ExternalFilter=(IExternalFilter)o;
			this.ExternalFilter.FromXML(externalElement);
			this.TokenMappingFromXML(XML);
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

}
