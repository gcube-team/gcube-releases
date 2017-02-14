package gr.uoa.di.madgik.workflow.adaptor.search.utils;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.utils.Locators;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 */
public class LocatorElevationFilter implements IExternalFilter
{
	
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static Logger logger=LoggerFactory.getLogger(LocatorElevationFilter.class);
	
	/** The output of the filter should be stored or not */
	public boolean StoreOutput=false;
	
	/** The variable name containing the locator to elevate */
	public String LocatorVariableName=null;
	
	/** The variable name to store the elevated locator at */
	public String ElevatedLocatorVariableName=null;

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#GetInputVariableNames()
	 */
	public Set<String> GetInputVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(LocatorVariableName);
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#GetStoreOutputVariableName()
	 */
	public Set<String> GetStoreOutputVariableName()
	{
		Set<String> vars=new HashSet<String>();
		vars.add(ElevatedLocatorVariableName);
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#StoreOutput()
	 */
	public boolean StoreOutput()
	{
		return this.StoreOutput;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if(this.LocatorVariableName==null || this.LocatorVariableName.trim().length()==0) throw new ExecutionValidationException("Filtered parameter names cannot be empty or null");
		if(this.StoreOutput) if(this.ElevatedLocatorVariableName==null || this.ElevatedLocatorVariableName.trim().length()==0) throw new ExecutionValidationException("Needed parameter is not provided");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#ValidateForOnlineFiltering()
	 */
	public void ValidateForOnlineFiltering() throws ExecutionValidationException
	{
		throw new ExecutionValidationException("On line filtering is not supported");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#ValidatePreExecution(gr.uoa.di.madgik.execution.engine.ExecutionHandle, java.util.Set)
	 */
	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.Validate();
		if (!Handle.GetPlan().Variables.Contains(this.LocatorVariableName)) throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(this.LocatorVariableName);
		if (!ndt.IsAvailable && ! ExcludeAvailableConstraint.contains(this.LocatorVariableName)) throw new ExecutionValidationException("Needed variable not available");
		if(!Handle.GetPlan().Variables.Contains(this.ElevatedLocatorVariableName))throw new ExecutionValidationException("Needed parameter to store output not present");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#ValidatePreExecutionForOnlineFiltering(gr.uoa.di.madgik.execution.engine.ExecutionHandle, java.util.Set)
	 */
	public void ValidatePreExecutionForOnlineFiltering(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException
	{
		this.ValidateForOnlineFiltering();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#SupportsOnLineFiltering()
	 */
	public boolean SupportsOnLineFiltering()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#Process(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		try {
			URI loc = (URI)Handle.GetPlan().Variables.Get(LocatorVariableName).Value.GetValue();
			if(LocalWriterProxy.isOfType(loc))
				return Locators.localToTCP(loc).toString();
			else
				return loc.toString();
		}catch(Exception e) {
			throw new ExecutionRunTimeException("Could not perform elevation", e);
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#ProcessOnLine(java.lang.Object, java.util.Set)
	 */
	public Object ProcessOnLine(Object OnLineFilteredValue, Set<NamedDataType> AdditionalValueProviders,ExecutionHandle Handle) throws ExecutionRunTimeException
	{
		throw new ExecutionRunTimeException("On line filtering is not supported");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#ToXML()
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		String outputvarString="";
		if(this.ElevatedLocatorVariableName!=null) outputvarString="storeOutputName=\""+this.ElevatedLocatorVariableName+"\"";
		StringBuilder buf=new StringBuilder();
		buf.append("<external type=\""+this.getClass().getName()+"\" storeOutput=\""+Boolean.toString(this.StoreOutput)+"\" "+outputvarString+">");
		buf.append("<filteredVariable name=\""+this.LocatorVariableName+"\"/>");
		buf.append("</external>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter#FromXML(org.w3c.dom.Node)
	 */
	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if(!XMLUtils.AttributeExists((Element)XML, "type") ||
					!XMLUtils.AttributeExists((Element)XML, "storeOutput")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.StoreOutput=Boolean.parseBoolean(XMLUtils.GetAttribute((Element)XML, "storeOutput"));
			if(this.StoreOutput)
			{
				if(!XMLUtils.AttributeExists((Element)XML, "storeOutputName")) throw new ExecutionSerializationException("Provided serialization is not valid");
				this.ElevatedLocatorVariableName=XMLUtils.GetAttribute((Element)XML, "storeOutputName");
			}
			Element tmp=XMLUtils.GetChildElementWithName(XML, "filteredVariable");
			if(!XMLUtils.AttributeExists(tmp, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.LocatorVariableName=XMLUtils.GetAttribute(tmp, "name");
		}catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}
}
