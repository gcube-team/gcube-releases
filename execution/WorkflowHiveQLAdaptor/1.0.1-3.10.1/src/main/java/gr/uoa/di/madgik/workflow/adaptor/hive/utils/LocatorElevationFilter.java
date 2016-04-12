package gr.uoa.di.madgik.workflow.adaptor.hive.utils;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.filter.IExternalFilter;
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
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class LocatorElevationFilter implements IExternalFilter {

	/** The logger */
	private static Logger logger = LoggerFactory.getLogger(LocatorElevationFilter.class.getName());

	/** Whether the output of the filter should be stored or not */
	public boolean StoreOutput = false;

	/** The variable name containing the locator to elevate */
	public String LocatorVariableName = null;

	/** The variable name where to store the elevated locator */
	public String ElevatedLocatorVariableName = null;

	@Override
	public Set<String> GetInputVariableNames() {
		Set<String> vars = new HashSet<String>();
		vars.add(LocatorVariableName);
		return vars;
	}

	@Override
	public Set<String> GetStoreOutputVariableName() {
		Set<String> vars = new HashSet<String>();
		vars.add(ElevatedLocatorVariableName);
		return vars;
	}

	@Override
	public Object Process(ExecutionHandle Handle) throws ExecutionRunTimeException {
		try {
			URI loc = (URI) Handle.GetPlan().Variables.Get(LocatorVariableName).Value.GetValue();
			if (LocalWriterProxy.isOfType(loc)) {
				return Locators.localToTCP(loc).toString();
			} else {
				return loc.toString();
			}
		} catch (Exception e) {
			throw new ExecutionRunTimeException("Could not perform elevation", e);
		}
	}

	@Override
	public Object ProcessOnLine(Object OnLineFilteredValue, Set<NamedDataType> AdditionalValueProviders, ExecutionHandle Handle)
			throws ExecutionRunTimeException {
		throw new ExecutionRunTimeException("On line filtering is not supported");
	}

	@Override
	public boolean StoreOutput() {
		return StoreOutput;
	}

	@Override
	public boolean SupportsOnLineFiltering() {
		return false;
	}

	@Override
	public void Validate() throws ExecutionValidationException {
		if (LocatorVariableName == null || LocatorVariableName.trim().length() == 0) {
			throw new ExecutionValidationException("Filtered parameter names cannot be empty or null");
		}
		if (StoreOutput) {
			if (ElevatedLocatorVariableName == null || ElevatedLocatorVariableName.trim().length() == 0) {
				throw new ExecutionValidationException("Needed parameter is not provided");
			}
		}
	}

	@Override
	public void ValidateForOnlineFiltering() throws ExecutionValidationException {
		throw new ExecutionValidationException("On line filtering is not supported");
	}

	@Override
	public void ValidatePreExecution(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException {
		Validate();
		if (!Handle.GetPlan().Variables.Contains(LocatorVariableName))
			throw new ExecutionValidationException("Needed parameter not found");
		NamedDataType ndt = Handle.GetPlan().Variables.Get(LocatorVariableName);
		if (!ndt.IsAvailable && !ExcludeAvailableConstraint.contains(LocatorVariableName))
			throw new ExecutionValidationException("Needed variable not available");
		if (!Handle.GetPlan().Variables.Contains(ElevatedLocatorVariableName))
			throw new ExecutionValidationException("Needed parameter to store output not present");
	}

	@Override
	public void ValidatePreExecutionForOnlineFiltering(ExecutionHandle Handle, Set<String> ExcludeAvailableConstraint) throws ExecutionValidationException {
		ValidateForOnlineFiltering();
	}

	@Override
	public String ToXML() throws ExecutionSerializationException {
		String outputvarString = "";
		if (ElevatedLocatorVariableName != null) {
			outputvarString = "storeOutputName=\"" + ElevatedLocatorVariableName + "\"";
		}
		StringBuilder buf = new StringBuilder();
		buf.append("<external type=\"" + this.getClass().getName() + "\" storeOutput=\"" + Boolean.toString(StoreOutput) + "\" " + outputvarString + ">");
		buf.append("<filteredVariable name=\"" + LocatorVariableName + "\"/>");
		buf.append("</external>");

		return buf.toString();
	}

	@Override
	public void FromXML(Node XML) throws ExecutionSerializationException {
		try {
			if (!XMLUtils.AttributeExists((Element) XML, "type") || !XMLUtils.AttributeExists((Element) XML, "storeOutput"))
				throw new ExecutionSerializationException("Provided serialization is not valid");
			StoreOutput = Boolean.parseBoolean(XMLUtils.GetAttribute((Element) XML, "storeOutput"));
			if (StoreOutput) {
				if (!XMLUtils.AttributeExists((Element) XML, "storeOutputName"))
					throw new ExecutionSerializationException("Provided serialization is not valid");
				ElevatedLocatorVariableName = XMLUtils.GetAttribute((Element) XML, "storeOutputName");
			}
			Element tmp = XMLUtils.GetChildElementWithName(XML, "filteredVariable");
			if (!XMLUtils.AttributeExists(tmp, "name"))
				throw new ExecutionSerializationException("Provided serialization is not valid");
			LocatorVariableName = XMLUtils.GetAttribute(tmp, "name");
		} catch (Exception ex) {
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

}
