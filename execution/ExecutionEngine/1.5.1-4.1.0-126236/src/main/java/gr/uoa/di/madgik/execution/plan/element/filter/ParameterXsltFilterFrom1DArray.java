package gr.uoa.di.madgik.execution.plan.element.filter;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.DataTypeArray;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParameterXsltFilterFrom1DArray extends ParameterFilterBase
{

	private static Logger logger=LoggerFactory.getLogger(ParameterXsltFilterFrom1DArray.class);
	public String FilteredVariableName = null;
	public String FilterExpressionVariableName = null;
	public String ValueTagVariableName=null;
	public boolean StoreOutput=false;
	public String StoreOutputVariableName=null;
	
	public Set<String> GetInputVariableNames()
	{
		Set<String> vars= new HashSet<String>();
		vars.add(FilteredVariableName);
		vars.add(FilterExpressionVariableName);
		return vars;
	}

	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars= new HashSet<String>();
		vars.add(this.StoreOutputVariableName);
		return vars;
	}

	public boolean StoreOutput()
	{
		return this.StoreOutput;
	}
	
	public void Validate() throws ExecutionValidationException
	{
		if(this.FilteredVariableName==null || this.FilteredVariableName.trim().length()==0) throw new ExecutionValidationException("Expected parameter name not provided");
		if(this.StoreOutput && (this.StoreOutputVariableName==null || this.StoreOutputVariableName.trim().length()==0)) throw new ExecutionValidationException("No output variable name defined to store output");
		if(this.ValueTagVariableName==null || this.ValueTagVariableName.trim().length()==0) throw new ExecutionValidationException("Expected parameter name not provided");
		this.TokenMappingValidate();
	}

	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if (!Handle.GetPlan().Variables.Contains(this.FilteredVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.FilteredVariableName);
		if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.FilteredVariableName)) throw new ExecutionValidationException("Needed variable not available");
		if(this.StoreOutput)
		{
			for(String storeVarName : this.GetStoreOutputVariableName())
			{
				if(!Handle.GetPlan().Variables.Contains(storeVarName))throw new ExecutionValidationException("Needed parameter to store output not present");
			}
		}
//		if(this.StoreOutput() && !Handle.GetPlan().Variables.Contains(this.GetStoreOutputVariableName()))throw new ExecutionValidationException("Needed parameter to store output not present");
		if(this.FilterExpressionVariableName!=null)
		{
			if (!Handle.GetPlan().Variables.Contains(this.FilterExpressionVariableName)) throw new ExecutionValidationException("Needed parameter not found");
			ndt = Handle.GetPlan().Variables.Get(this.FilterExpressionVariableName);
			if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.FilterExpressionVariableName)) throw new ExecutionValidationException("Needed variable not available");
		}
		if (!Handle.GetPlan().Variables.Contains(this.ValueTagVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		ndt = Handle.GetPlan().Variables.Get(this.ValueTagVariableName);
		if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.ValueTagVariableName)) throw new ExecutionValidationException("Needed variable not available");
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
	
	private boolean DirectionIsFromArray(ExecutionHandle Handle) throws ExecutionValidationException
	{
		boolean SourceIsArray=false;
		if(Handle.GetPlan().Variables.Get(this.FilteredVariableName).Value.GetDataTypeEnum().equals(IDataType.DataTypes.Array)) SourceIsArray=true;
		boolean TargetIsNull=(this.StoreOutputVariableName==null);
		boolean TargetIsArray=false;
		if(!TargetIsNull && Handle.GetPlan().Variables.Get(StoreOutputVariableName).Value.GetDataTypeEnum().equals(IDataType.DataTypes.Array)) TargetIsArray=true;
		if(SourceIsArray && !TargetIsNull && !TargetIsArray) return true;
		if(SourceIsArray && !TargetIsNull && TargetIsArray) return true;//this could be false too. Matter of policy
		if(!SourceIsArray && !TargetIsNull && TargetIsArray) return false;
		if(!SourceIsArray && !TargetIsNull && !TargetIsArray) throw new ExecutionValidationException("Cannot apply array transformation on non array source and output when source is array("+SourceIsArray+"), target is not set("+TargetIsNull+"), target is array("+TargetIsArray+")");
		if(SourceIsArray && TargetIsNull) return true;
		if(!SourceIsArray && TargetIsNull) throw new ExecutionValidationException("Cannot apply array transformation on non array source and output when source is array("+SourceIsArray+"), target is not set("+TargetIsNull+"), target is array("+TargetIsArray+")");
		throw new ExecutionValidationException("Could not determin direction of transformation when source is array("+SourceIsArray+"), target is not set("+TargetIsNull+"), target is array("+TargetIsArray+")");
	}

	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try
		{
			NamedDataType ndt = Handle.GetPlan().Variables.Get(this.FilteredVariableName);
			boolean FromArray=this.DirectionIsFromArray(Handle);
			String xsltToApply=null;
			if(this.FilterExpressionVariableName==null)
			{
				if(FromArray) xsltToApply=this.GetDefaultFilterFromArrayDataType((DataTypeArray)Handle.GetPlan().Variables.Get(this.FilteredVariableName).Value, Handle);
				else throw new ExecutionValidationException("The needed transformation is not applicable with an array source");
			}
			else
			{
				NamedDataType ndtx = Handle.GetPlan().Variables.Get(this.FilterExpressionVariableName);
				xsltToApply=ndtx.Value.GetStringValue();
			}
			logger.debug("Xslt filtering on source : "+ndt.Value.GetStringValue());
			logger.debug("Applied xslt is : "+xsltToApply);
			return XMLUtils.Transform(ndt.Value.GetStringValue(), xsltToApply);
		} catch (Exception ex)
		{
			throw new ExecutionRunTimeException("Could not extract value", ex);
		}
	}
	
	private String GetDefaultFilterFromArrayDataType(DataTypeArray dtArray,ExecutionHandle Handle) throws ExecutionValidationException, ExecutionSerializationException
	{
		if(DataTypeUtils.CountDimentionsOfObjectArrayCode(dtArray.GetArrayClassCode())!=1)throw new ExecutionValidationException("Multidimensional arrays are not supported by this filter");
		if(DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(dtArray.GetArrayClassCode()).equals(IDataType.DataTypes.Reflectable))throw new ExecutionValidationException("Arrays of reflectables are not supported by this filter");
		String tagValue=Handle.GetPlan().Variables.Get(this.ValueTagVariableName).Value.GetStringValue();
		StringBuilder buf=new StringBuilder();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buf.append("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
		buf.append("<xsl:template match=\"/\">");
		buf.append("<xsl:for-each select=\"dt/value/array/item/dt/value\">");
		buf.append("<"+tagValue+"><xsl:value-of select=\".\"/></"+tagValue+">");
		buf.append("</xsl:for-each>");
		buf.append("</xsl:template>");
		buf.append("</xsl:stylesheet>");
		return buf.toString();
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
					!XMLUtils.AttributeExists((Element)XML, "order") ||
					!XMLUtils.AttributeExists((Element)XML, "storeOutput")) throw new ExecutionSerializationException("Provided serialization is not valid");
			if(!ParameterFilterBase.FilterType.valueOf(XMLUtils.GetAttribute((Element)XML, "type")).equals(this.GetFilterType())) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Order=Integer.parseInt(XMLUtils.GetAttribute((Element)XML, "order"));
			this.StoreOutput=Boolean.parseBoolean(XMLUtils.GetAttribute((Element)XML, "storeOutput"));
			if(this.StoreOutput)
			{
				if(!XMLUtils.AttributeExists((Element)XML, "storeOutputName")) throw new ExecutionSerializationException("Provided serialization is not valid");
				this.StoreOutputVariableName=XMLUtils.GetAttribute((Element)XML, "storeOutputName");
			}
			Element tmp=XMLUtils.GetChildElementWithName(XML, "filteredVariable");
			if(tmp==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.FilteredVariableName=XMLUtils.GetChildText(tmp);
			tmp=XMLUtils.GetChildElementWithName(XML, "expressionVariable");
			if(tmp!=null) this.FilterExpressionVariableName=XMLUtils.GetChildText(tmp);
			tmp=XMLUtils.GetChildElementWithName(XML, "valueTagVariableName");
			if(tmp==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.ValueTagVariableName=XMLUtils.GetChildText(tmp);
			this.TokenMappingFromXML(XML);
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		String outputvarString="";
		if(this.StoreOutputVariableName!=null) outputvarString="storeOutputName=\""+this.StoreOutputVariableName+"\"";
		buf.append("<filter type=\""+this.GetFilterType().toString()+"\" order=\""+this.GetOrder()+"\" storeOutput=\""+Boolean.toString(this.StoreOutput)+"\" "+outputvarString+">");
		buf.append("<filteredVariable>"+this.FilteredVariableName+"</filteredVariable>");
		if(this.FilterExpressionVariableName!=null) buf.append("<expressionVariable>"+this.FilterExpressionVariableName+"</expressionVariable>");
		buf.append("<valueTagVariableName>"+this.ValueTagVariableName+"</valueTagVariableName>");
		buf.append(this.TokenMappingToXML());
		buf.append("</filter>");
		return buf.toString();
	}

	public FilterType GetFilterType()
	{
		return FilterType.XsltFrom1DArray;
	}
}
