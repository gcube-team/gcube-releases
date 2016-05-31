package gr.uoa.di.madgik.execution.plan.element.filter;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParameterEmitPayloadFilter extends ParameterFilterBase
{
	private static Logger logger=LoggerFactory.getLogger(ParameterEmitPayloadFilter.class);
	public String PlanNodeID;
	public String EmitVariableName;

	
	public Set<String> GetInputVariableNames()
	{
		Set<String> vars= new HashSet<String>();
		vars.add(EmitVariableName);
		return vars;
	}

	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars= new HashSet<String>();
		return vars;
	}

	public boolean StoreOutput()
	{
		return false;
	}
	
	public void Validate() throws ExecutionValidationException
	{
		if(this.EmitVariableName==null || this.EmitVariableName.trim().length()==0) throw new ExecutionValidationException("Expected parameter name not provided");
		this.TokenMappingValidate();
	}

	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if (!Handle.GetPlan().Variables.Contains(this.EmitVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.EmitVariableName);
		if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.EmitVariableName)) throw new ExecutionValidationException("Needed variable not available");
	}
	
	public boolean SupportsOnLineFiltering()
	{
		return false;
	}

	public void ValidateForOnlineFiltering() throws ExecutionValidationException
	{
		throw new ExecutionValidationException("On line filtering is not supported");
	}

	public void ValidatePreExecutionForOnlineFiltering(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.ValidateForOnlineFiltering();
	}

	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			NamedDataType ndt = Handle.GetPlan().Variables.Get(this.EmitVariableName);
			logger.debug("emiting source : "+ndt.Value.GetStringValue());
			Handle.EmitEvent(new ExecutionExternalProgressReportStateEvent(this.PlanNodeID, "emiting payload", ndt.Value.GetStringValue()));
			return null;
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not extract value", ex);
		}
	}
	
	public Object ProcessOnLine(Object OnLineFilteredValue,Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		throw new ExecutionRunTimeException("On line filtering is not supported");
	}

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

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try{
			if(!XMLUtils.AttributeExists((Element)XML, "type") ||
					!XMLUtils.AttributeExists((Element)XML, "order")) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!ParameterFilterBase.FilterType.valueOf(XMLUtils.GetAttribute((Element)XML, "type")).equals(this.GetFilterType())) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Order=Integer.parseInt(XMLUtils.GetAttribute((Element)XML, "order"));
			Element tmp=XMLUtils.GetChildElementWithName(XML, "emitVariable");
			if(tmp==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.EmitVariableName=XMLUtils.GetChildText(tmp);
			tmp=XMLUtils.GetChildElementWithName(XML, "planNodeID");
			if(tmp==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.PlanNodeID=XMLUtils.GetChildText(tmp);
			this.TokenMappingFromXML(XML);
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<filter type=\""+this.GetFilterType().toString()+"\" order=\""+this.GetOrder()+"\">");
		buf.append("<emitVariable>"+this.EmitVariableName+"</emitVariable>");
		buf.append("<planNodeID>"+this.PlanNodeID+"</planNodeID>");
		buf.append(this.TokenMappingToXML());
		buf.append("</filter>");
		return buf.toString();
	}

	public FilterType GetFilterType()
	{
		return FilterType.Emit;
	}

}
