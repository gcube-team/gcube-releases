package gr.uoa.di.madgik.execution.plan.element.filter;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class ParameterFilterBase implements Comparable<ParameterFilterBase>, Serializable
{
	private static final long serialVersionUID = 1L;

	public enum FilterType
	{
		XPath,
		Xslt,
		XsltT1DoArray,
		XsltFrom1DArray,
		Serialization,
		ObjectConvertable,
		ObjectReflectable,
		ReflectableToTemplate,
		ReflectableFromTemplate,
		Decompose,
		Compose,
		External,
		Emit,
		ArrayEvaluation
	}
	
	public int Order = 0;
	public Map<String,String> TokenMapping=new HashMap<String, String>();
	
	public int GetOrder()
	{
		return this.Order;
	}
	
	public void SetOrder(int Order)
	{
		this.Order=Order;
	}

	public int compareTo(ParameterFilterBase o)
	{
		return Integer.valueOf(this.Order).compareTo(o.GetOrder());
	}
	
	public Map<String,String> GetTokenMappings()
	{
		return this.TokenMapping;
	}
	
	public void SetTokenMappings(Map<String,String> TokenMapping)
	{
		this.TokenMapping=TokenMapping;
	}
	
	public boolean TokensMatch(String Token1,String Token2)
	{
		String tmpToken1=Token1;
		String tmpToken2=Token2;
		if(this.TokenMapping.containsKey(Token1)) tmpToken1=this.TokenMapping.get(Token1);
		if(this.TokenMapping.containsKey(Token2)) tmpToken2=this.TokenMapping.get(Token2);
		return tmpToken1.equals(tmpToken2);
	}
	
	public String GetToken(String Token)
	{
		if(this.TokenMapping.containsKey(Token)) return this.TokenMapping.get(Token);
		return Token;
	}
	
	public void TokenMappingValidate() throws ExecutionValidationException
	{
		if(this.TokenMapping==null) throw new ExecutionValidationException("Token Mapping can be left empty but not null");
	}
	
	public String TokenMappingToXML()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<tokenMap>");
		for(Map.Entry<String, String> entry : this.TokenMapping.entrySet()) buf.append("<map key=\""+entry.getKey()+"\" value=\""+entry.getValue()+"\"/>");
		buf.append("</tokenMap>");
		return buf.toString();
	}

	public void TokenMappingFromXML(String XML) throws ExecutionSerializationException
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
		this.TokenMappingFromXML(doc.getDocumentElement());
	}

	public void TokenMappingFromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			Element tmp=XMLUtils.GetChildElementWithName(XML, "tokenMap");
			if(tmp==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			List<Element> maps=XMLUtils.GetChildElementsWithName(tmp, "map");
			if(maps==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.TokenMapping.clear();
			for(Element m : maps)
			{
				if(!XMLUtils.AttributeExists(m, "key") || !XMLUtils.AttributeExists(m, "value")) throw new ExecutionSerializationException("Provided serialization is not valid");
				this.TokenMapping.put(XMLUtils.GetAttribute(m, "key"), XMLUtils.GetAttribute(m, "value"));
			}
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

	public abstract ParameterFilterBase.FilterType GetFilterType();
	
	public abstract void Validate() throws ExecutionValidationException;
	public abstract void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint)  throws ExecutionValidationException;
	
	public abstract void ValidateForOnlineFiltering() throws ExecutionValidationException;
	public abstract void ValidatePreExecutionForOnlineFiltering(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint)  throws ExecutionValidationException;
	
	public abstract boolean SupportsOnLineFiltering();
	
	public abstract boolean StoreOutput();
	public abstract Set<String> GetStoreOutputVariableName();
	
	public abstract Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException;
	public abstract Object ProcessOnLine(Object OnLineFilteredValue,Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException;
	
	public abstract String ToXML() throws ExecutionSerializationException;
	public abstract void FromXML(String XML) throws ExecutionSerializationException;
	public abstract void FromXML(Node XML) throws ExecutionSerializationException;
	
	public abstract Set<String> GetInputVariableNames();
}
